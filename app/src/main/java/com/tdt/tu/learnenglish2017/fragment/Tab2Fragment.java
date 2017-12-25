package com.tdt.tu.learnenglish2017.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.activity.CourseInfoActivity;
import com.tdt.tu.learnenglish2017.activity.LessonActivity;
import com.tdt.tu.learnenglish2017.helper.Constants;
import com.tdt.tu.learnenglish2017.helper.CourseAdapter;
import com.tdt.tu.learnenglish2017.helper.RequestHandler;
import com.tdt.tu.learnenglish2017.item.Course;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Pham Thanh Tu on 26-Sep-17.
 */

public class Tab2Fragment extends Fragment implements MaterialSearchBar.OnSearchActionListener {
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.searchBar)
    MaterialSearchBar searchBar;

    private View view;
    private List<Course> courseList = new ArrayList<>();
    private ArrayList<String> listCourseId = new ArrayList<>();
    private CourseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment2_layout, container, false);

        init();
        listViewHandler();

        return view;
    }

    private void init() {
        ButterKnife.bind(this, view);
        adapter = new CourseAdapter(view.getContext(), R.layout.course_row_layout, courseList);
        listView.setAdapter(adapter);
        searchBar.setOnSearchActionListener(this);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        courseList.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        searchBar.clearFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
        if (text.toString().equalsIgnoreCase("Free")) {
            loadSearchResults("" + 0);
        } else
            loadSearchResults(text.toString());
    }

    @Override
    public void onButtonClicked(int buttonCode) {
    }

    private void listViewHandler() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String email = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).getString("email", "");
                HashMap<String, String> params = new HashMap<>();
                params.put("email", email);

                LoadUserCourseIds loadUserCourseIds = new LoadUserCourseIds(Constants.URL_GET_USER_COURSE_IDS_BY_EMAIL, params, Constants.CODE_POST_REQUEST, i);
                loadUserCourseIds.execute();
            }
        });
    }

    private void loadSearchResults(String searchQuery) {
        HashMap<String, String> params = new HashMap<>();
        params.put("search_query", searchQuery);

        LoadSearchResults loadSearchResults = new LoadSearchResults(Constants.URL_GET_SEARCH_RESULTS, params, Constants.CODE_POST_REQUEST);
        loadSearchResults.execute();
    }

    private void refreshQuestionList(JSONArray results) throws JSONException {
        courseList.clear();
        for (int i = 0; i < results.length(); i++) {
            JSONObject obj = results.getJSONObject(i);

            courseList.add(new Course(
                    obj.getString("icon"),
                    obj.getString("course_id"),
                    obj.getString("course_name"),
                    obj.getInt("price"),
                    obj.getString("description"),
                    obj.getString("link")
            ));
        }

        adapter.notifyDataSetChanged();
    }

    private void refreshCourseIdList(JSONArray questions) throws JSONException {
        listCourseId.clear();
        for (int i = 0; i < questions.length(); i++) {
            JSONObject obj = questions.getJSONObject(i);
            listCourseId.add(obj.getString("course_id"));
        }
    }

    private void switchActivity(int position) {
        if (checkEnrolledCourse(position)) {
            SharedPreferences.Editor editor = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).edit();
            editor.putString("course_id", courseList.get(position).getCourseId());
            editor.putString("description", courseList.get(position).getDescription());
            editor.commit();

            Intent intent = new Intent(view.getContext(), LessonActivity.class);
            intent.putExtra("course_name", courseList.get(position).getCourseName());
            startActivity(intent);
        } else {
            Intent intent = new Intent(view.getContext(), CourseInfoActivity.class);
            intent.putExtra("course_id", courseList.get(position).getCourseId());
            intent.putExtra("course_name", courseList.get(position).getCourseName());
            intent.putExtra("price", courseList.get(position).getPrice());
            intent.putExtra("description", courseList.get(position).getDescription());
            intent.putExtra("link", courseList.get(position).getLink());

            startActivity(intent);
        }
    }

    private boolean checkEnrolledCourse(int position) {
        for (String courseId : listCourseId) {
            if (courseId.equals(courseList.get(position).getCourseId())) {
                return true;
            }
        }
        return false;
    }

    private class LoadSearchResults extends AsyncTask<Void, Void, String> {

        String url;
        HashMap<String, String> params;
        int requestCode;
        ProgressDialog dialog;

        LoadSearchResults(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(view.getContext());
            dialog.setMessage("Searching...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    if (!object.getString("message").equals(""))
                        Toasty.info(view.getContext(), object.getString("message"), Toast.LENGTH_SHORT).show();

                    refreshQuestionList(object.getJSONArray("results"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == Constants.CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);

            if (requestCode == Constants.CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }

    public class LoadUserCourseIds extends AsyncTask<Void, Void, String> {

        String url;
        HashMap<String, String> params;
        int requestCode;
        int position;

        LoadUserCourseIds(String url, HashMap<String, String> params, int requestCode, int position) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
            this.position = position;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    if (!object.getString("message").equals(""))
                        Toasty.info(view.getContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    refreshCourseIdList(object.getJSONArray("course_ids"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            switchActivity(position);
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == Constants.CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);

            if (requestCode == Constants.CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }
}

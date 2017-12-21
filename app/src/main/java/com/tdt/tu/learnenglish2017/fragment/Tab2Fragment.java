package com.tdt.tu.learnenglish2017.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
                Intent intent = new Intent(view.getContext(), CourseInfoActivity.class);
                intent.putExtra("course_id", courseList.get(i).getCourseId());
                intent.putExtra("course_name", courseList.get(i).getCourseName());
                intent.putExtra("price", courseList.get(i).getPrice());
                intent.putExtra("description", courseList.get(i).getDescription());
                intent.putExtra("link", courseList.get(i).getLink());

                startActivity(intent);
            }
        });
    }

    private void loadSearchResults(String searchQuery) {
        HashMap<String, String> params = new HashMap<>();
        params.put("search_query", searchQuery);

        PerformNetworkRequest request = new PerformNetworkRequest(Constants.URL_GET_SEARCH_RESULTS, params, Constants.CODE_POST_REQUEST);
        request.execute();
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

    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {

        String url;
        HashMap<String, String> params;
        int requestCode;
        ProgressDialog dialog;

        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
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
                        Toast.makeText(view.getContext(), object.getString("message"), Toast.LENGTH_SHORT).show();

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
}

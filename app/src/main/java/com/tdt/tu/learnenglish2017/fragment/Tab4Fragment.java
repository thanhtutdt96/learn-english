package com.tdt.tu.learnenglish2017.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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

public class Tab4Fragment extends Fragment {
    @BindView(R.id.listFavorite)
    ListView listView;
    private List<Course> listFavorite = new ArrayList<>();
    private List<String> listCourseId = new ArrayList<>();

    private CourseAdapter adapter;
    private View view;

    private BroadcastReceiver receiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment4, container, false);

        init();
        loadUserFavorites();
        listViewHandler();

        return view;
    }

    private void initReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadUserFavorites();
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("refresh_user_favorites");
        view.getContext().registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onStart() {
        super.onStart();
        initReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        view.getContext().unregisterReceiver(receiver);
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

    private void switchActivity(int position) {
        if (checkEnrolledCourse(position)) {
            SharedPreferences.Editor editor = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).edit();
            editor.putString("course_id", listFavorite.get(position).getCourseId());
            editor.putString("description", listFavorite.get(position).getDescription());
            editor.commit();

            Intent intent = new Intent(view.getContext(), LessonActivity.class);
            intent.putExtra("course_name", listFavorite.get(position).getCourseName());
            startActivity(intent);
        } else {
            Intent intent = new Intent(view.getContext(), CourseInfoActivity.class);
            intent.putExtra("course_id", listFavorite.get(position).getCourseId());
            intent.putExtra("course_name", listFavorite.get(position).getCourseName());
            intent.putExtra("price", listFavorite.get(position).getPrice());
            intent.putExtra("description", listFavorite.get(position).getDescription());
            intent.putExtra("link", listFavorite.get(position).getLink());

            startActivity(intent);
        }
    }

    private boolean checkEnrolledCourse(int position) {
        for (String courseId : listCourseId) {
            if (courseId.equals(listFavorite.get(position).getCourseId())) {
                return true;
            }
        }
        return false;
    }

    private void loadUserFavorites() {
        String email = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).getString("email", "");

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);

        LoadFavoriteCourses loadFavoriteCourses = new LoadFavoriteCourses(Constants.URL_GET_FAVORITES_BY_EMAIL, params, Constants.CODE_POST_REQUEST);
        loadFavoriteCourses.execute();
    }

    private void refreshFavoriteList(JSONArray courses) throws JSONException {
        listFavorite.clear();
        for (int i = 0; i < courses.length(); i++) {
            JSONObject obj = courses.getJSONObject(i);

            listFavorite.add(new Course(
                    obj.getString("icon"),
                    obj.getString("course_id"),
                    obj.getString("course_name"),
                    obj.getInt("price"),
                    obj.getString("description"),
                    obj.getString("link"),
                    Float.parseFloat(obj.getString("rating"))
            ));
        }
        adapter.notifyDataSetChanged();
    }

    private void init() {
        ButterKnife.bind(this, view);
        adapter = new CourseAdapter(view.getContext(), R.layout.course_row, listFavorite);
        listView.setAdapter(adapter);
    }

    private void refreshCourseIdList(JSONArray questions) throws JSONException {
        listCourseId.clear();
        for (int i = 0; i < questions.length(); i++) {
            JSONObject obj = questions.getJSONObject(i);
            listCourseId.add(obj.getString("course_id"));
        }
    }

    private class LoadFavoriteCourses extends AsyncTask<Void, Void, String> {

        String url;
        HashMap<String, String> params;
        int requestCode;

        LoadFavoriteCourses(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    if (!object.getString("message").equals(""))
                        Toasty.info(view.getContext(), object.getString("message"), Toast.LENGTH_SHORT).show();

                    refreshFavoriteList(object.getJSONArray("courses"));
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

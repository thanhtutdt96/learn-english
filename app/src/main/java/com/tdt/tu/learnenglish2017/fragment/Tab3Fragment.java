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

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

import static android.content.Context.MODE_PRIVATE;

public class Tab3Fragment extends Fragment {

    @BindView(R.id.listUserCourse)
    ListView listView;
    private View view;
    private ArrayList<Course> listUserCourse = new ArrayList<>();
    private CourseAdapter adapter;

    private BroadcastReceiver receiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment3, container, false);

        init();
        loadUserCourses();
        listViewHandler();
        return view;
    }

    private void initReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadUserCourses();
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("refresh_qa_list");
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
                SharedPreferences.Editor editor = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).edit();
                editor.putString("course_id", listUserCourse.get(i).getCourseId());
                editor.putString("description", listUserCourse.get(i).getDescription());
                editor.commit();

                Intent intent = new Intent(view.getContext(), LessonActivity.class);
                intent.putExtra("course_name", listUserCourse.get(i).getCourseName());
                intent.putExtra("link", listUserCourse.get(i).getLink());
                intent.putExtra("course_id", listUserCourse.get(i).getCourseId());
                startActivity(intent);
            }
        });
    }

    private void loadUserCourses() {
        SharedPreferences prefs = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE);
        String email = prefs.getString("email", "");

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);

        LoadUserCourses loadUserCourses = new LoadUserCourses(Constants.URL_GET_COURSES_BY_EMAIL, params, Constants.CODE_POST_REQUEST);
        loadUserCourses.execute();
    }

    private void refreshCourseList(JSONArray courses) throws JSONException {
        listUserCourse.clear();
        for (int i = 0; i < courses.length(); i++) {
            JSONObject obj = courses.getJSONObject(i);

            listUserCourse.add(new Course(
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
        adapter = new CourseAdapter(view.getContext(), R.layout.course_row, listUserCourse);
        listView.setAdapter(adapter);
    }

    private class LoadUserCourses extends AsyncTask<Void, Void, String> {

        String url;
        HashMap<String, String> params;
        int requestCode;

        LoadUserCourses(String url, HashMap<String, String> params, int requestCode) {
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

                    refreshCourseList(object.getJSONArray("courses"));
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
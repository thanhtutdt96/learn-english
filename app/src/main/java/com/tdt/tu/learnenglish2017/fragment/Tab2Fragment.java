package com.tdt.tu.learnenglish2017.fragment;

import android.content.Intent;
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

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Pham Thanh Tu on 26-Sep-17.
 */

public class Tab2Fragment extends Fragment {
    ArrayList<Course> listUserCourse = new ArrayList<>();
    CourseAdapter adapter;
    @BindView(R.id.listUserCourse)
    ListView listView;

    ArrayList<String> listCourseId = new ArrayList<>();
    View view;

    Integer[] imageId = {
            R.drawable.google_drive,
            R.drawable.firefox,
            R.drawable.adobe_acrobat,
            R.drawable.spotify,
            R.drawable.photoshop,
            R.drawable.dropbox,
            R.drawable.chrome,
            R.drawable.spotify,
            R.drawable.google_drive

    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment2_layout, container, false);

        init();
        loadUserCourses();
        listViewHandler();
        getAllCourseId();
        return view;
    }

    private void getAllCourseId() {
        for (int i = 0; i < listUserCourse.size(); i++) {
            listCourseId.add(listUserCourse.get(i).getCourseId());
        }

    }

    private void listViewHandler() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), LessonActivity.class);
                intent.putExtra("course_name", listUserCourse.get(i).getCourseName());
                startActivity(intent);
            }
        });
    }

    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {

        String url;
        HashMap<String, String> params;
        int requestCode;

        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
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
                        Toast.makeText(view.getContext(), object.getString("message"), Toast.LENGTH_SHORT).show();

                    refreshQuestionList(object.getJSONArray("courses"));
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

    private void loadUserCourses() {
        SharedPreferences prefs = view.getContext().getSharedPreferences("my_prefs", MODE_PRIVATE);
        String email = prefs.getString("email", "");

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);

        PerformNetworkRequest request = new PerformNetworkRequest(Constants.URL_GET_COURSES_BY_EMAIL, params, Constants.CODE_POST_REQUEST);
        request.execute();
    }

    private void refreshQuestionList(JSONArray questions) throws JSONException {
        listUserCourse.clear();

        for (int i = 0; i < questions.length(); i++) {
            JSONObject obj = questions.getJSONObject(i);

            listUserCourse.add(new Course(
                    imageId[i],
                    obj.getString("course_id"),
                    obj.getString("course_name"),
                    obj.getInt("price"),
                    obj.getString("description")
            ));
        }
        adapter = new CourseAdapter(view.getContext(), R.layout.course_row_layout, listUserCourse);
        listView.setAdapter(adapter);
    }

    private void init() {
        ButterKnife.bind(this, view);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserCourses();
    }
}

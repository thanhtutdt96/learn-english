package com.tdt.tu.learnenglish2017.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 1stks on 09-Nov-17.
 */

public class CourseFragment extends Fragment {
    @BindView(R.id.listCourse)
    ListView listView;
    ArrayList<Course> courseList = new ArrayList<>();
    CourseAdapter adapter;

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

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_course, container, false);
        init();
        loadCourses();
        listViewHandler();
        return view;
    }

    private void listViewHandler() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                SharedPreferences prefs = view.getContext().getSharedPreferences("my_prefs",MODE_PRIVATE);
//                SharedPreferences.Editor editor = prefs.edit();
//                editor.putString("course_id", courseList.get(position).getCourseId());
//                editor.commit();
//
//                Intent i = new Intent(view.getContext(), LessonActivity.class);
//                i.putExtra("courseName", courseList.get(position).getCourseName());
//                startActivity(i);
                Intent i = new Intent(view.getContext(), CourseInfoActivity.class);
                i.putExtra("course_id", courseList.get(position).getCourseId());
                i.putExtra("course_name", courseList.get(position).getCourseName());
                i.putExtra("price", courseList.get(position).getPrice());
                i.putExtra("description", courseList.get(position).getDescription());

                startActivity(i);

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

    private void loadCourses() {
        String categoryId = getArguments().getString("category_id", "");
        HashMap<String, String> params = new HashMap<>();
        params.put("category_id", categoryId);

        PerformNetworkRequest request = new PerformNetworkRequest(Constants.URL_GET_COURSES_BY_CATEGORY_ID, params, Constants.CODE_POST_REQUEST);
        request.execute();
    }

    private void refreshQuestionList(JSONArray questions) throws JSONException {
        courseList.clear();

        for (int i = 0; i < questions.length(); i++) {
            JSONObject obj = questions.getJSONObject(i);

            courseList.add(new Course(
                    imageId[i],
                    obj.getString("course_id"),
                    obj.getString("course_name"),
                    obj.getInt("price"),
                    obj.getString("description")
            ));
        }
        adapter = new CourseAdapter(view.getContext(), R.layout.course_row_layout, courseList);
        listView.setAdapter(adapter);

    }

    private void init() {
        ButterKnife.bind(this, view);
    }
}

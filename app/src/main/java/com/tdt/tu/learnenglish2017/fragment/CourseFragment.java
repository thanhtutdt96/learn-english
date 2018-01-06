package com.tdt.tu.learnenglish2017.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
import es.dmoral.toasty.Toasty;

/**
 * Created by 1stks on 09-Nov-17.
 */

public class CourseFragment extends Fragment {

    @BindView(R.id.listCourse)
    ListView listView;
    @BindView(R.id.backArrow)
    ImageView backArrow;
    @BindView(R.id.txtCategory)
    TextView txtCategory;
    private ArrayList<Course> courseList = new ArrayList<>();
    private CourseAdapter adapter;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_course, container, false);
        init();
        buttonHandler();
        loadCourses();
        listViewHandler();
        return view;
    }

    private void buttonHandler() {
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });
    }

    private void listViewHandler() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(view.getContext(), CourseInfoActivity.class);
                i.putExtra("course_id", courseList.get(position).getCourseId());
                i.putExtra("course_name", courseList.get(position).getCourseName());
                i.putExtra("price", courseList.get(position).getPrice());
                i.putExtra("description", courseList.get(position).getDescription());
                i.putExtra("link", courseList.get(position).getLink());

                startActivity(i);
            }
        });
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

        txtCategory.setText(getArguments().getString("category_name", ""));
        adapter = new CourseAdapter(view.getContext(), R.layout.course_row, courseList);
        listView.setAdapter(adapter);
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
                        Toasty.info(view.getContext(), object.getString("message"), Toast.LENGTH_SHORT).show();

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
}

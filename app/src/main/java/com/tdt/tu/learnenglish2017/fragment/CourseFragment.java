package com.tdt.tu.learnenglish2017.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    @BindView(R.id.recyclerCourse)
    RecyclerView recyclerCourse;
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

    private void recyclerViewHandler() {
        recyclerCourse.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    return true;
                }
            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View childView = recyclerCourse.findChildViewUnder(e.getX(), e.getY());
                int recyclerViewItemPosition;
                if (childView != null && gestureDetector.onTouchEvent(e)) {
                    recyclerViewItemPosition = recyclerCourse.getChildAdapterPosition(childView);

                    Intent i = new Intent(view.getContext(), CourseInfoActivity.class);
                    i.putExtra("course_id", courseList.get(recyclerViewItemPosition).getCourseId());
                    i.putExtra("course_name", courseList.get(recyclerViewItemPosition).getCourseName());
                    i.putExtra("price", courseList.get(recyclerViewItemPosition).getPrice());
                    i.putExtra("description", courseList.get(recyclerViewItemPosition).getDescription());
                    i.putExtra("link", courseList.get(recyclerViewItemPosition).getLink());
                    i.putExtra("rating", courseList.get(recyclerViewItemPosition).getRating());

                    startActivity(i);
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    private void loadCourses() {
        String categoryId = getArguments().getString("category_id", "");
        HashMap<String, String> params = new HashMap<>();
        params.put("category_id", categoryId);

        LoadCourses loadCourses = new LoadCourses(Constants.URL_GET_COURSES_BY_CATEGORY_ID, params, Constants.CODE_POST_REQUEST);
        loadCourses.execute();
    }

    private void refreshCourseList(JSONArray courses) throws JSONException {
        courseList.clear();

        for (int i = 0; i < courses.length(); i++) {
            JSONObject obj = courses.getJSONObject(i);

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

        adapter = new CourseAdapter(view.getContext(), courseList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerCourse.setLayoutManager(linearLayoutManager);
        recyclerCourse.setHasFixedSize(true);
        recyclerCourse.setItemAnimator(new DefaultItemAnimator());
        recyclerCourse.setAdapter(adapter);
    }

    private class LoadCourses extends AsyncTask<Void, Void, String> {

        String url;
        HashMap<String, String> params;
        int requestCode;

        LoadCourses(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
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
            recyclerViewHandler();
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

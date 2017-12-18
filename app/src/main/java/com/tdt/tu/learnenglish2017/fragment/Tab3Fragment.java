package com.tdt.tu.learnenglish2017.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.activity.CourseInfoActivity;
import com.tdt.tu.learnenglish2017.helper.CategoryAdapter;
import com.tdt.tu.learnenglish2017.helper.Constants;
import com.tdt.tu.learnenglish2017.helper.FeaturedCourseAdapter;
import com.tdt.tu.learnenglish2017.helper.RequestHandler;
import com.tdt.tu.learnenglish2017.item.Category;
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

public class Tab3Fragment extends Fragment {
    @BindView(R.id.recyclerCategories)
    RecyclerView recyclerCategory;
    @BindView(R.id.recyclerFeatured)
    RecyclerView recyclerFeatured;

    private List<Category> categoryList = new ArrayList<>();
    private List<Course> courseList = new ArrayList<>();
    private CategoryAdapter categoryAdapter;
    private FeaturedCourseAdapter courseAdapter;
    private View view;
    private int recyclerViewItemPosition;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment3_layout, container, false);

        init();
        loadData();

        return view;
    }

    public void init() {
        ButterKnife.bind(this, view);
        categoryAdapter = new CategoryAdapter(view.getContext(), categoryList);
        courseAdapter = new FeaturedCourseAdapter(view.getContext(), courseList);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerCategory.setLayoutManager(linearLayoutManager1);
        recyclerFeatured.setHasFixedSize(true);
        recyclerCategory.setItemAnimator(new DefaultItemAnimator());
        recyclerCategory.setAdapter(categoryAdapter);

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerFeatured.setLayoutManager(linearLayoutManager2);
        recyclerFeatured.setHasFixedSize(true);
        recyclerFeatured.setItemAnimator(new DefaultItemAnimator());
        recyclerFeatured.setAdapter(courseAdapter);
    }

    private void loadData() {
        LoadFeaturedCourse loadFeaturedCourse = new LoadFeaturedCourse(Constants.URL_GET_FEATURED_COURSES, null, Constants.CODE_GET_REQUEST);
        loadFeaturedCourse.execute();

        LoadCategory loadCategory = new LoadCategory(Constants.URL_GET_CATEGORIES, null, Constants.CODE_GET_REQUEST);
        loadCategory.execute();
    }

    private void refreshCategoryList(JSONArray categories) throws JSONException {
        categoryList.clear();

        for (int i = 0; i < categories.length(); i++) {
            JSONObject obj = categories.getJSONObject(i);

            categoryList.add(new Category(
                    obj.getString("icon"),
                    obj.getString("category_id"),
                    obj.getString("category_name")
            ));
        }
        categoryAdapter.notifyDataSetChanged();

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
                    obj.getString("description")
            ));
        }
        courseAdapter.notifyDataSetChanged();
    }

    private void recyclerCategoryHandler() {
        recyclerCategory.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    return true;
                }
            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View childView = recyclerCategory.findChildViewUnder(e.getX(), e.getY());
                int recyclerViewItemPosition;
                if (childView != null && gestureDetector.onTouchEvent(e)) {
                    recyclerViewItemPosition = recyclerCategory.getChildAdapterPosition(childView);
                    Fragment courseFragment = new CourseFragment();
                    replaceFragment(courseFragment);

                    Bundle bundle = new Bundle();
                    bundle.putString("category_id", categoryList.get(recyclerViewItemPosition).getId());
                    courseFragment.setArguments(bundle);
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

    private void recyclerFaeturedCourseHandler() {
        recyclerFeatured.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    return true;
                }
            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View childView = recyclerCategory.findChildViewUnder(e.getX(), e.getY());
                int recyclerViewItemPosition;
                if (childView != null && gestureDetector.onTouchEvent(e)) {
                    recyclerViewItemPosition = recyclerCategory.getChildAdapterPosition(childView);
                    Intent i = new Intent(view.getContext(), CourseInfoActivity.class);
                    i.putExtra("course_id", courseList.get(recyclerViewItemPosition).getCourseId());
                    i.putExtra("course_name", courseList.get(recyclerViewItemPosition).getCourseName());
                    i.putExtra("price", courseList.get(recyclerViewItemPosition).getPrice());
                    i.putExtra("description", courseList.get(recyclerViewItemPosition).getDescription());

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

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment3_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private class LoadCategory extends AsyncTask<Void, Void, String> {

        String url;
        HashMap<String, String> params;
        int requestCode;

        LoadCategory(String url, HashMap<String, String> params, int requestCode) {
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

                    refreshCategoryList(object.getJSONArray("categories"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            recyclerCategoryHandler();

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

    private class LoadFeaturedCourse extends AsyncTask<Void, Void, String> {

        String url;
        HashMap<String, String> params;
        int requestCode;

        LoadFeaturedCourse(String url, HashMap<String, String> params, int requestCode) {
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

                    refreshCourseList(object.getJSONArray("courses"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            recyclerFaeturedCourseHandler();
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



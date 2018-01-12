package com.tdt.tu.learnenglish2017.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.activity.CourseInfoActivity;
import com.tdt.tu.learnenglish2017.activity.LessonActivity;
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
import es.dmoral.toasty.Toasty;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Pham Thanh Tu on 26-Sep-17.
 */

public class Tab1Fragment extends Fragment {
    @BindView(R.id.recyclerCategories)
    RecyclerView recyclerCategory;
    @BindView(R.id.recyclerFeatured)
    RecyclerView recyclerFeatured;
    @BindView(R.id.recyclerTopCommunicationCourse)
    RecyclerView recyclerTopCommunicationCourse;
    @BindView(R.id.recyclerTopProficiencyCourse)
    RecyclerView recyclerTopProficiencyCourse;
    @BindView(R.id.progressLoading)
    ProgressBar progressLoading;


    private List<Category> categoryList = new ArrayList<>();
    private List<Course> featuredList = new ArrayList<>();
    private List<Course> topCommunicationList = new ArrayList<>();
    private List<Course> topProficiencyList = new ArrayList<>();
    private List<String> listCourseId = new ArrayList<>();

    private CategoryAdapter categoryAdapter;
    private FeaturedCourseAdapter featuredAdapter;
    private FeaturedCourseAdapter topCommunicationAdapter;
    private FeaturedCourseAdapter topProficiencyAdapter;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment1, container, false);

        init();
        loadData();

        return view;
    }

    public void init() {
        ButterKnife.bind(this, view);
        categoryAdapter = new CategoryAdapter(view.getContext(), categoryList);
        featuredAdapter = new FeaturedCourseAdapter(view.getContext(), featuredList);
        topCommunicationAdapter = new FeaturedCourseAdapter(view.getContext(), topCommunicationList);
        topProficiencyAdapter = new FeaturedCourseAdapter(view.getContext(), topProficiencyList);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerFeatured.setLayoutManager(linearLayoutManager1);
        recyclerFeatured.setHasFixedSize(true);
        recyclerFeatured.setItemAnimator(new DefaultItemAnimator());
        recyclerFeatured.setAdapter(featuredAdapter);

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerTopCommunicationCourse.setLayoutManager(linearLayoutManager2);
        recyclerTopCommunicationCourse.setHasFixedSize(true);
        recyclerTopCommunicationCourse.setItemAnimator(new DefaultItemAnimator());
        recyclerTopCommunicationCourse.setAdapter(topCommunicationAdapter);

        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerTopProficiencyCourse.setLayoutManager(linearLayoutManager3);
        recyclerTopProficiencyCourse.setHasFixedSize(true);
        recyclerTopProficiencyCourse.setItemAnimator(new DefaultItemAnimator());
        recyclerTopProficiencyCourse.setAdapter(topProficiencyAdapter);

        LinearLayoutManager linearLayoutManager4 = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerCategory.setLayoutManager(linearLayoutManager4);
        recyclerCategory.setHasFixedSize(true);
        recyclerCategory.setItemAnimator(new DefaultItemAnimator());
        recyclerCategory.setAdapter(categoryAdapter);

    }

    private void loadData() {
        LoadFeaturedCourse loadFeaturedCourse = new LoadFeaturedCourse(Constants.URL_GET_FEATURED_COURSES, null, Constants.CODE_GET_REQUEST);
        loadFeaturedCourse.execute();

        LoadTopCommunicationCourse loadTopCommunicationCourse = new LoadTopCommunicationCourse(Constants.URL_GET_TOP_COMMUNICATION_COURSES, null, Constants.CODE_GET_REQUEST);
        loadTopCommunicationCourse.execute();

        LoadTopProficiencyCourse loadTopProficiencyCourse = new LoadTopProficiencyCourse(Constants.URL_GET_TOP_PROFICIENCY_COURSES, null, Constants.CODE_GET_REQUEST);
        loadTopProficiencyCourse.execute();

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

    private void refreshFeaturedList(JSONArray courses) throws JSONException {
        featuredList.clear();
        for (int i = 0; i < courses.length(); i++) {
            JSONObject obj = courses.getJSONObject(i);

            featuredList.add(new Course(
                    obj.getString("icon"),
                    obj.getString("course_id"),
                    obj.getString("course_name"),
                    obj.getInt("price"),
                    obj.getString("description"),
                    obj.getString("link"),
                    Float.parseFloat(obj.getString("rating"))
            ));
        }
        featuredAdapter.notifyDataSetChanged();
    }

    private void refreshTopCommunicationList(JSONArray courses) throws JSONException {
        topCommunicationList.clear();

        for (int i = 0; i < courses.length(); i++) {
            JSONObject obj = courses.getJSONObject(i);

            topCommunicationList.add(new Course(
                    obj.getString("icon"),
                    obj.getString("course_id"),
                    obj.getString("course_name"),
                    obj.getInt("price"),
                    obj.getString("description"),
                    obj.getString("link"),
                    Float.parseFloat(obj.getString("rating"))
            ));
        }
        topCommunicationAdapter.notifyDataSetChanged();
    }

    private void refreshTopProficiencyList(JSONArray courses) throws JSONException {
        topProficiencyList.clear();

        for (int i = 0; i < courses.length(); i++) {
            JSONObject obj = courses.getJSONObject(i);

            topProficiencyList.add(new Course(
                    obj.getString("icon"),
                    obj.getString("course_id"),
                    obj.getString("course_name"),
                    obj.getInt("price"),
                    obj.getString("description"),
                    obj.getString("link"),
                    Float.parseFloat(obj.getString("rating"))
            ));
        }
        topProficiencyAdapter.notifyDataSetChanged();
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
                    bundle.putString("category_name", categoryList.get(recyclerViewItemPosition).getTitle());
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

    private void recyclerFeaturedCourseHandler() {
        recyclerFeatured.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    return true;
                }
            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View childView = recyclerFeatured.findChildViewUnder(e.getX(), e.getY());
                int recyclerViewItemPosition;
                if (childView != null && gestureDetector.onTouchEvent(e)) {
                    recyclerViewItemPosition = recyclerFeatured.getChildAdapterPosition(childView);

                    String email = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).getString("email", "");
                    HashMap<String, String> params = new HashMap<>();
                    params.put("email", email);

                    LoadUserCourseIds loadUserCourseIds = new LoadUserCourseIds(Constants.URL_GET_USER_COURSE_IDS_BY_EMAIL, params, Constants.CODE_POST_REQUEST, featuredList, recyclerViewItemPosition);
                    loadUserCourseIds.execute();
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

    private void recyclerTopCommunicationHandler() {
        recyclerTopCommunicationCourse.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    return true;
                }
            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View childView = recyclerTopCommunicationCourse.findChildViewUnder(e.getX(), e.getY());
                int recyclerViewItemPosition;
                if (childView != null && gestureDetector.onTouchEvent(e)) {
                    recyclerViewItemPosition = recyclerTopCommunicationCourse.getChildAdapterPosition(childView);

                    String email = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).getString("email", "");
                    HashMap<String, String> params = new HashMap<>();
                    params.put("email", email);

                    LoadUserCourseIds loadUserCourseIds = new LoadUserCourseIds(Constants.URL_GET_USER_COURSE_IDS_BY_EMAIL, params, Constants.CODE_POST_REQUEST, topCommunicationList, recyclerViewItemPosition);
                    loadUserCourseIds.execute();
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

    private void recyclerTopProficiencyHandler() {
        recyclerTopProficiencyCourse.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    return true;
                }
            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View childView = recyclerTopProficiencyCourse.findChildViewUnder(e.getX(), e.getY());
                int recyclerViewItemPosition;
                if (childView != null && gestureDetector.onTouchEvent(e)) {
                    recyclerViewItemPosition = recyclerTopProficiencyCourse.getChildAdapterPosition(childView);

                    String email = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).getString("email", "");
                    HashMap<String, String> params = new HashMap<>();
                    params.put("email", email);

                    LoadUserCourseIds loadUserCourseIds = new LoadUserCourseIds(Constants.URL_GET_USER_COURSE_IDS_BY_EMAIL, params, Constants.CODE_POST_REQUEST, topProficiencyList, recyclerViewItemPosition);
                    loadUserCourseIds.execute();
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

    private void refreshCourseIdList(JSONArray questions) throws JSONException {
        listCourseId.clear();
        for (int i = 0; i < questions.length(); i++) {
            JSONObject obj = questions.getJSONObject(i);
            listCourseId.add(obj.getString("course_id"));
        }
    }

    private void switchActivity(List<Course> list, int position) {
        if (checkEnrolledCourse(list, position)) {
            SharedPreferences.Editor editor = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).edit();
            editor.putString("course_id", list.get(position).getCourseId());
            editor.putString("description", list.get(position).getDescription());
            editor.commit();

            Intent intent = new Intent(view.getContext(), LessonActivity.class);
            intent.putExtra("course_name", list.get(position).getCourseName());
            intent.putExtra("link", list.get(position).getLink());
            startActivity(intent);
        } else {
            Intent intent = new Intent(view.getContext(), CourseInfoActivity.class);
            intent.putExtra("course_id", list.get(position).getCourseId());
            intent.putExtra("course_name", list.get(position).getCourseName());
            intent.putExtra("price", list.get(position).getPrice());
            intent.putExtra("description", list.get(position).getDescription());
            intent.putExtra("link", list.get(position).getLink());
            startActivity(intent);
        }
    }

    private boolean checkEnrolledCourse(List<Course> list, int position) {
        for (String courseId : listCourseId) {
            if (courseId.equals(list.get(position).getCourseId())) {
                return true;
            }
        }
        return false;
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    if (!object.getString("message").equals(""))
                        Toasty.info(view.getContext(), object.getString("message"), Toast.LENGTH_SHORT).show();

                    refreshCategoryList(object.getJSONArray("categories"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressLoading.setVisibility(View.GONE);
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
            progressLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    if (!object.getString("message").equals(""))
                        Toasty.info(view.getContext(), object.getString("message"), Toast.LENGTH_SHORT).show();

                    refreshFeaturedList(object.getJSONArray("courses"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            recyclerFeaturedCourseHandler();
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

    private class LoadTopCommunicationCourse extends AsyncTask<Void, Void, String> {

        String url;
        HashMap<String, String> params;
        int requestCode;

        LoadTopCommunicationCourse(String url, HashMap<String, String> params, int requestCode) {
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

                    refreshTopCommunicationList(object.getJSONArray("courses"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            recyclerTopCommunicationHandler();
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

    private class LoadTopProficiencyCourse extends AsyncTask<Void, Void, String> {

        String url;
        HashMap<String, String> params;
        int requestCode;

        LoadTopProficiencyCourse(String url, HashMap<String, String> params, int requestCode) {
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

                    refreshTopProficiencyList(object.getJSONArray("courses"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            recyclerTopProficiencyHandler();
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
        List<Course> list;
        int position;

        LoadUserCourseIds(String url, HashMap<String, String> params, int requestCode, List<Course> list, int position) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
            this.list = list;
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
            switchActivity(list, position);
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



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
    @BindView(R.id.recyclerFavorite)
    RecyclerView recyclerFavorite;
    private List<Course> favoriteList = new ArrayList<>();
    private List<String> courseIdList = new ArrayList<>();

    private CourseAdapter adapter;
    private View view;

    private BroadcastReceiver receiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment4, container, false);
        init();
        loadUserFavorites();
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

    private void recyclerViewHandler() {
        recyclerFavorite.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    return true;
                }
            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View childView = recyclerFavorite.findChildViewUnder(e.getX(), e.getY());
                int recyclerViewItemPosition;
                if (childView != null && gestureDetector.onTouchEvent(e)) {
                    recyclerViewItemPosition = recyclerFavorite.getChildAdapterPosition(childView);

                    String email = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).getString("email", "");
                    HashMap<String, String> params = new HashMap<>();
                    params.put("email", email);

                    LoadUserCourseIds loadUserCourseIds = new LoadUserCourseIds(Constants.URL_GET_USER_COURSE_IDS_BY_EMAIL, params, Constants.CODE_POST_REQUEST, recyclerViewItemPosition);
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

    private void switchActivity(int position) {
        if (checkEnrolledCourse(position)) {
            SharedPreferences.Editor editor = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).edit();
            editor.putString("course_id", favoriteList.get(position).getCourseId());
            editor.putString("description", favoriteList.get(position).getDescription());
            editor.commit();

            Intent intent = new Intent(view.getContext(), LessonActivity.class);
            intent.putExtra("course_name", favoriteList.get(position).getCourseName());
            intent.putExtra("link", favoriteList.get(position).getLink());

            startActivity(intent);
        } else {
            Intent intent = new Intent(view.getContext(), CourseInfoActivity.class);
            intent.putExtra("course_id", favoriteList.get(position).getCourseId());
            intent.putExtra("course_name", favoriteList.get(position).getCourseName());
            intent.putExtra("price", favoriteList.get(position).getPrice());
            intent.putExtra("description", favoriteList.get(position).getDescription());
            intent.putExtra("link", favoriteList.get(position).getLink());
            intent.putExtra("rating", favoriteList.get(position).getRating());

            startActivity(intent);
        }
    }

    private boolean checkEnrolledCourse(int position) {
        for (String courseId : courseIdList) {
            if (courseId.equals(favoriteList.get(position).getCourseId())) {
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
        favoriteList.clear();
        for (int i = 0; i < courses.length(); i++) {
            JSONObject obj = courses.getJSONObject(i);

            favoriteList.add(new Course(
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
        adapter = new CourseAdapter(view.getContext(), favoriteList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerFavorite.setLayoutManager(linearLayoutManager);
        recyclerFavorite.setHasFixedSize(true);
        recyclerFavorite.setItemAnimator(new DefaultItemAnimator());
        recyclerFavorite.setAdapter(adapter);
    }

    private void refreshCourseIdList(JSONArray courses) throws JSONException {
        courseIdList.clear();
        for (int i = 0; i < courses.length(); i++) {
            JSONObject obj = courses.getJSONObject(i);
            courseIdList.add(obj.getString("course_id"));
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

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
import com.tdt.tu.learnenglish2017.activity.LessonActivity;
import com.tdt.tu.learnenglish2017.helper.Constants;
import com.tdt.tu.learnenglish2017.helper.RequestHandler;
import com.tdt.tu.learnenglish2017.helper.UserCourseAdapter;
import com.tdt.tu.learnenglish2017.item.UserCourse;

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

    @BindView(R.id.recyclerUserCourse)
    RecyclerView recyclerUserCourse;
    private View view;
    private ArrayList<UserCourse> userCourseList = new ArrayList<>();
    private UserCourseAdapter adapter;

    private BroadcastReceiver receiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment3, container, false);

        init();
        loadUserCourses();
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
        intentFilter.addAction("refresh_user_courses");
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
        recyclerUserCourse.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    return true;
                }
            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View childView = recyclerUserCourse.findChildViewUnder(e.getX(), e.getY());
                int recyclerViewItemPosition;
                if (childView != null && gestureDetector.onTouchEvent(e)) {
                    recyclerViewItemPosition = recyclerUserCourse.getChildAdapterPosition(childView);

                    SharedPreferences.Editor editor = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).edit();
                    editor.putString("course_id", userCourseList.get(recyclerViewItemPosition).getCourseId());
                    editor.putString("description", userCourseList.get(recyclerViewItemPosition).getDescription());
                    editor.commit();

                    Intent intent = new Intent(view.getContext(), LessonActivity.class);
                    intent.putExtra("course_name", userCourseList.get(recyclerViewItemPosition).getCourseName());
                    intent.putExtra("link", userCourseList.get(recyclerViewItemPosition).getLink());
                    intent.putExtra("course_id", userCourseList.get(recyclerViewItemPosition).getCourseId());
                    startActivity(intent);
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

    private void loadUserCourses() {
        SharedPreferences prefs = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE);
        String email = prefs.getString("email", "");

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);

        LoadUserCourses loadUserCourses = new LoadUserCourses(Constants.URL_GET_COURSES_BY_EMAIL, params, Constants.CODE_POST_REQUEST);
        loadUserCourses.execute();
    }

    private void refreshCourseList(JSONArray courses) throws JSONException {
        userCourseList.clear();
        for (int i = 0; i < courses.length(); i++) {
            JSONObject obj = courses.getJSONObject(i);

            userCourseList.add(new UserCourse(
                    obj.getString("icon"),
                    obj.getString("course_id"),
                    obj.getString("course_name"),
                    obj.getString("description"),
                    obj.getString("link"),
                    Float.parseFloat(obj.getString("rating")),
                    obj.getInt("progress")
            ));
        }

        adapter.notifyDataSetChanged();
    }

    private void init() {
        ButterKnife.bind(this, view);

        adapter = new UserCourseAdapter(view.getContext(), userCourseList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerUserCourse.setLayoutManager(linearLayoutManager);
        recyclerUserCourse.setHasFixedSize(true);
        recyclerUserCourse.setItemAnimator(new DefaultItemAnimator());
        recyclerUserCourse.setAdapter(adapter);
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
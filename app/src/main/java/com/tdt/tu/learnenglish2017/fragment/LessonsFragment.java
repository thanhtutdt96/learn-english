package com.tdt.tu.learnenglish2017.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.activity.LessonActivity;
import com.tdt.tu.learnenglish2017.helper.Constants;
import com.tdt.tu.learnenglish2017.helper.DividerDecoration;
import com.tdt.tu.learnenglish2017.helper.LessonAdapter;
import com.tdt.tu.learnenglish2017.helper.RequestHandler;
import com.tdt.tu.learnenglish2017.item.Lesson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Pham Thanh Tu on 30-Oct-17.
 */

public class LessonsFragment extends Fragment {
    RecyclerView.LayoutManager layoutManager;
    private View view;
    private String courseId;
    private LessonAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<Lesson> lessonList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_lesson, container, false);

        init();
        isPermissionGranted();
        loadLessons();

        return view;
    }

    private void init() {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerLesson);
        adapter = new LessonAdapter(view.getContext(), lessonList);
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerDecoration(view.getContext(), LinearLayoutManager.VERTICAL, 5));
        recyclerView.setAdapter(adapter);
    }


    private void loadLessons() {

        SharedPreferences preferences = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE);
        courseId = preferences.getString("course_id", "");

        HashMap<String, String> params = new HashMap<>();
        params.put("course_id", courseId);

        PerformNetworkRequest request = new PerformNetworkRequest(Constants.URL_GET_LESSONS_BY_COURSE_ID, params, Constants.CODE_POST_REQUEST);
        request.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        }
    }

    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Permission required")
                        .setMessage("You must allow this app to access files on your device to download lessons!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return false;
            }
        } else {
            return true;
        }
    }

    private void refreshLessonList(JSONArray lessons) throws JSONException {
        for (int i = 0; i < lessons.length(); i++) {
            JSONObject obj = lessons.getJSONObject(i);

            lessonList.add(new Lesson(
                    "http://img.youtube.com/vi/" + obj.getString("link") + "/default.jpg",
                    obj.getString("lesson_name"),
                    obj.getString("duration"),
                    obj.getString("link")
            ));

            SharedPreferences.Editor editor = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).edit();
            editor.putString("link", lessonList.get(0).getLink());
            editor.commit();

            adapter.notifyDataSetChanged();
        }
    }


    class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
        int i = 0;
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

                    refreshLessonList(object.getJSONArray("lessons"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            LessonActivity.buttonDownloadAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    final int lastElement = recyclerView.getAdapter().getItemCount() - 1;
                    final int lastVisible = layoutManager.findLastVisibleItemPosition();
                    for (int i = 0; i <= lastElement; i++) {
                        if (i <= lastVisible) {
                            recyclerView.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.lessonDownload).performClick();
                        } else {
                            WaitForScroll waitForScroll = new WaitForScroll(i, lastElement);
                            waitForScroll.execute();
                        }
                    }

                }
            });
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

    class WaitForScroll extends AsyncTask<Void, Void, Void> {

        int position, lastElement;

        public WaitForScroll(int position, int lastElement) {
            this.position = position;
            this.lastElement = lastElement;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            recyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.lessonDownload).performClick();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            layoutManager.scrollToPosition(lastElement);
            return null;
        }
    }
}


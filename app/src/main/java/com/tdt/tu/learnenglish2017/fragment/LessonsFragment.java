package com.tdt.tu.learnenglish2017.fragment;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tdt.tu.learnenglish2017.R;
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
import static com.tdt.tu.learnenglish2017.activity.LessonActivity.buttonDownloadAll;

/**
 * Created by Pham Thanh Tu on 30-Oct-17.
 */

public class LessonsFragment extends Fragment {
    RecyclerView.LayoutManager layoutManager;
    private View view;
    private LessonAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<Lesson> lessonList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_lesson, container, false);

        init();
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
        String courseId = preferences.getString("course_id", "");

        HashMap<String, String> params = new HashMap<>();
        params.put("course_id", courseId);

        PerformNetworkRequest request = new PerformNetworkRequest(Constants.URL_GET_LESSONS_BY_COURSE_ID, params, Constants.CODE_POST_REQUEST);
        request.execute();
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

            buttonDownloadAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    buttonDownloadAll.setEnabled(false);
                    layoutManager.scrollToPosition(0);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                            final int lastElement = recyclerView.getAdapter().getItemCount() - 1;
                            final int lastVisible = layoutManager.findLastVisibleItemPosition();
                            int number = 2;

                            try {
                                for (i = 0; i <= lastElement; i++) {
                                    if (i <= lastVisible) {
                                        recyclerView.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.lessonDownload).performClick();
                                    } else {
                                        if (lastVisible * number >= lastElement) {
                                            WaitForScroll waitForScroll = new WaitForScroll(i, lastElement);
                                            waitForScroll.execute();
                                        } else {
                                            WaitForScroll waitForScroll = new WaitForScroll(i, lastVisible * number);
                                            waitForScroll.execute();
                                            if (i == lastVisible * number)
                                                number++;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Log.d("DownloadError", e.getMessage());
                                e.printStackTrace();
                            }

                        }
                    }, 50);
                }
            });
            buttonDownloadAll.setEnabled(false);
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
        protected void onPreExecute() {
            layoutManager.scrollToPosition(lastElement);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            recyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.lessonDownload).performClick();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}


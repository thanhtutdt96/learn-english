package com.tdt.tu.learnenglish2017.fragment;

import android.content.Context;
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

import com.google.android.youtube.player.YouTubePlayer;
import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.activity.LessonActivity;
import com.tdt.tu.learnenglish2017.helper.Constants;
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
    View view;
    LessonAdapter adapter;
    ListView listView;
    ArrayList<Lesson> lessonList;

    OnHeadlineSelectedListener mCallback;
    String courseId;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnHeadlineSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_lesson, container, false);

        init();
        loadLessons();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedLessonLink = lessonList.get(position).getLink();
                if (LessonActivity.mYoutubePlayer != null) {
                    LessonActivity.mYoutubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                    LessonActivity.mYoutubePlayer.loadVideo(selectedLessonLink);
                    LessonActivity.mYoutubePlayer.play();
                }
            }
        });

        return view;
    }

    private void init() {
        listView = (ListView) view.findViewById(R.id.lessons_ListView);
        lessonList = new ArrayList<>();
    }

    private void loadLessons() {
        SharedPreferences preferences = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE);
        courseId = preferences.getString("course_id", "");

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

            adapter = new LessonAdapter(view.getContext(), R.layout.lesson_row_layout, lessonList);

            listView.setAdapter(adapter);
        }
    }

    public interface OnHeadlineSelectedListener {
        void onArticleSelected();
    }

    class PerformNetworkRequest extends AsyncTask<Void, Void, String> {

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
            mCallback.onArticleSelected();
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


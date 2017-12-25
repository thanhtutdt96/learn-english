package com.tdt.tu.learnenglish2017.fragment;

import android.content.Intent;
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
import com.tdt.tu.learnenglish2017.activity.LessonQuizActivity;
import com.tdt.tu.learnenglish2017.helper.Constants;
import com.tdt.tu.learnenglish2017.helper.QuizAdapter;
import com.tdt.tu.learnenglish2017.helper.RequestHandler;
import com.tdt.tu.learnenglish2017.item.Quiz;

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
 * Created by 1stks on 21-Dec-17.
 */

public class QuizFragment extends Fragment {
    @BindView(R.id.recyclerQuiz)
    RecyclerView recyclerQuiz;

    private List<Quiz> quizList = new ArrayList<>();
    private QuizAdapter quizAdapter;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_quiz, container, false);

        init();
        loadData();

        return view;
    }

    public void init() {
        ButterKnife.bind(this, view);
        quizAdapter = new QuizAdapter(view.getContext(), quizList);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(view.getContext());
        recyclerQuiz.setLayoutManager(linearLayoutManager1);
        recyclerQuiz.setHasFixedSize(true);
        recyclerQuiz.setItemAnimator(new DefaultItemAnimator());
        recyclerQuiz.setAdapter(quizAdapter);
    }

    private void loadData() {
        String courseId = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).getString("course_id", "");

        HashMap<String, String> params = new HashMap<>();
        params.put("course_id", courseId);

        LoadLessonNumber loadLessonNumber = new LoadLessonNumber(Constants.URL_GET_LESSON_NUMBERS_BY_COURSE_ID, params, Constants.CODE_POST_REQUEST);
        loadLessonNumber.execute();
    }

    private void refreshQuizList(JSONArray quizzes) throws JSONException {
        quizList.clear();

        for (int i = 0; i < quizzes.length(); i++) {
            JSONObject obj = quizzes.getJSONObject(i);

            quizList.add(new Quiz(
                    obj.getString("lesson_id"),
                    obj.getString("lesson_number")
            ));
        }
        quizAdapter.notifyDataSetChanged();

    }

    private void recyclerQuizHandler() {
        recyclerQuiz.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    return true;
                }
            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View childView = recyclerQuiz.findChildViewUnder(e.getX(), e.getY());
                int recyclerViewItemPosition;
                if (childView != null && gestureDetector.onTouchEvent(e)) {
                    recyclerViewItemPosition = recyclerQuiz.getChildAdapterPosition(childView);

                    Intent intent = new Intent(view.getContext(), LessonQuizActivity.class);
                    intent.putExtra("lesson_id", quizList.get(recyclerViewItemPosition).getLessonId());
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

    private class LoadLessonNumber extends AsyncTask<Void, Void, String> {

        String url;
        HashMap<String, String> params;
        int requestCode;

        LoadLessonNumber(String url, HashMap<String, String> params, int requestCode) {
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

                    refreshQuizList(object.getJSONArray("quizzes"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            recyclerQuizHandler();

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

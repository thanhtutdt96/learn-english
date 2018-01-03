package com.tdt.tu.learnenglish2017.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.helper.Constants;
import com.tdt.tu.learnenglish2017.helper.FeaturedCourseAdapter;
import com.tdt.tu.learnenglish2017.helper.QuizResultAdapter;
import com.tdt.tu.learnenglish2017.helper.RequestHandler;
import com.tdt.tu.learnenglish2017.item.Course;
import com.tdt.tu.learnenglish2017.item.QuizResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class FirstQuizResultActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.ivHome)
    ImageView ivHome;
    @BindView(R.id.ivHistory)
    ImageView ivHistory;
    @BindView(R.id.txtResult)
    TextView txtResult;
    @BindView(R.id.circleResult)
    DonutProgress circleResult;
    @BindView(R.id.txtRank)
    TextView txtRank;
    @BindView(R.id.recyclerSuggestion)
    RecyclerView recyclerSuggestion;

    private List<QuizResult> quizResultList = new ArrayList<>();
    private List<Course> suggestionList = new ArrayList<>();
    private List<String> listCourseId = new ArrayList<>();

    private FeaturedCourseAdapter suggestionAdapter;
    private QuizResultAdapter quizResultAdapter;

    private String rank;
    private int score;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_quiz_result);
        init();
        saveQuizResult();
        loadSuggestion();
    }

    private void init() {
        ButterKnife.bind(this);

        score = getIntent().getIntExtra("first_quiz_score", 0);
        email = getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).getString("email", "");
        setContent();

        suggestionAdapter = new FeaturedCourseAdapter(this, suggestionList);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerSuggestion.setLayoutManager(linearLayoutManager1);
        recyclerSuggestion.setHasFixedSize(true);
        recyclerSuggestion.setItemAnimator(new DefaultItemAnimator());
        recyclerSuggestion.setAdapter(suggestionAdapter);

        ivHome.setOnClickListener(this);
        ivHistory.setOnClickListener(this);
    }

    private void loadSuggestion() {
        HashMap<String, String> params = new HashMap<>();
        params.put("rank", rank);

        LoadSuggestion loadSuggestion = new LoadSuggestion(Constants.URL_GET_COURSE_SUGGESTIONS, params, Constants.CODE_POST_REQUEST);
        loadSuggestion.execute();
    }

    private void setContent() {
        txtResult.setText(score + "/10");
        circleResult.setProgress(score);
        setRank(score);
    }

    private void setRank(int score) {
        if (score == 10) {
            rank = "EXCELLENT";
            txtRank.setTextColor(getResources().getColor(R.color.colorBlue));
            txtResult.setTextColor(getResources().getColor(R.color.colorBlue));
            circleResult.setFinishedStrokeColor(getResources().getColor(R.color.colorBlue));
        } else if (score < 10 && score >= 8) {
            rank = "GOOD";
            txtRank.setTextColor(getResources().getColor(R.color.colorGreen));
            txtResult.setTextColor(getResources().getColor(R.color.colorGreen));
            circleResult.setFinishedStrokeColor(getResources().getColor(R.color.colorGreen));
        } else if (score < 8 && score >= 5) {
            rank = "AVERAGE";
            txtRank.setTextColor(getResources().getColor(R.color.colorOrange));
            txtResult.setTextColor(getResources().getColor(R.color.colorOrange));
            circleResult.setFinishedStrokeColor(getResources().getColor(R.color.colorOrange));
        } else if (score < 5 && score >= 2) {
            rank = "POOR";
            txtRank.setTextColor(getResources().getColor(R.color.colorRed));
            txtResult.setTextColor(getResources().getColor(R.color.colorRed));
            circleResult.setFinishedStrokeColor(getResources().getColor(R.color.colorRed));
        } else if (score < 2 && score >= 0) {
            rank = "BAD";
            txtRank.setTextColor(getResources().getColor(R.color.colorRed));
            txtResult.setTextColor(getResources().getColor(R.color.colorRed));
            circleResult.setFinishedStrokeColor(getResources().getColor(R.color.colorRed));
        }
        txtRank.setText(rank);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivHome:
                finish();
                break;
            case R.id.ivHistory:
                initHistoryDialog();
                break;
        }
    }

    private void initHistoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_AppCompat_Light_Dialog));
        View dialogView = getLayoutInflater().inflate(R.layout.quiz_result, null);
        builder.setView(dialogView);
        View customTile = getLayoutInflater().inflate(R.layout.custom_dialog_title, null);
        builder.setCustomTitle(customTile);
        final AlertDialog alertDialog = builder.create();

        ImageView ivClose = (ImageView) customTile.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        ListView listView = (ListView) dialogView.findViewById(R.id.listHistory);
        quizResultAdapter = new QuizResultAdapter(dialogView.getContext(), R.layout.quiz_result_row, quizResultList);
        listView.setAdapter(quizResultAdapter);

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        LoadQuizResult loadQuizResult = new LoadQuizResult(Constants.URL_GET_FIRST_QUIZ_RESULTS, params, Constants.CODE_POST_REQUEST);
        loadQuizResult.execute();

        alertDialog.show();
    }

    private void saveQuizResult() {
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("score", String.valueOf(score));
        params.put("rank", rank);

        SaveQuizResult saveQuizResult = new SaveQuizResult(Constants.URL_ADD_FIRST_QUIZ_RESULT, params, Constants.CODE_POST_REQUEST);
        saveQuizResult.execute();
    }

    private void refreshResultList(JSONArray answers) throws JSONException {
        quizResultList.clear();

        for (int i = 0; i < answers.length(); i++) {
            JSONObject obj = answers.getJSONObject(i);

            quizResultList.add(new QuizResult(
                    obj.getString("date"),
                    Integer.valueOf(obj.getString("score")),
                    obj.getString("rank")
            ));
        }
        quizResultAdapter.notifyDataSetChanged();
    }

    private void refreshSuggestionList(JSONArray courses) throws JSONException {
        suggestionList.clear();

        for (int i = 0; i < courses.length(); i++) {
            JSONObject obj = courses.getJSONObject(i);

            suggestionList.add(new Course(
                    obj.getString("icon"),
                    obj.getString("course_id"),
                    obj.getString("course_name"),
                    obj.getInt("price"),
                    obj.getString("description"),
                    obj.getString("link")
            ));
        }
        suggestionAdapter.notifyDataSetChanged();
    }

    private void recyclerSuggestionHandler() {
        recyclerSuggestion.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(FirstQuizResultActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    return true;
                }
            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View childView = recyclerSuggestion.findChildViewUnder(e.getX(), e.getY());
                int recyclerViewItemPosition;
                if (childView != null && gestureDetector.onTouchEvent(e)) {
                    recyclerViewItemPosition = recyclerSuggestion.getChildAdapterPosition(childView);

                    String email = getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).getString("email", "");
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

    private void refreshCourseIdList(JSONArray questions) throws JSONException {
        listCourseId.clear();
        for (int i = 0; i < questions.length(); i++) {
            JSONObject obj = questions.getJSONObject(i);
            listCourseId.add(obj.getString("course_id"));
        }
    }

    private void switchActivity(int position) {
        if (checkEnrolledCourse(position)) {
            SharedPreferences.Editor editor = getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).edit();
            editor.putString("course_id", suggestionList.get(position).getCourseId());
            editor.putString("description", suggestionList.get(position).getDescription());
            editor.commit();

            Intent intent = new Intent(FirstQuizResultActivity.this, LessonActivity.class);
            intent.putExtra("course_name", suggestionList.get(position).getCourseName());
            startActivity(intent);
        } else {
            Intent intent = new Intent(FirstQuizResultActivity.this, CourseInfoActivity.class);
            intent.putExtra("course_id", suggestionList.get(position).getCourseId());
            intent.putExtra("course_name", suggestionList.get(position).getCourseName());
            intent.putExtra("price", suggestionList.get(position).getPrice());
            intent.putExtra("description", suggestionList.get(position).getDescription());
            intent.putExtra("link", suggestionList.get(position).getLink());

            startActivity(intent);
        }
    }

    private boolean checkEnrolledCourse(int position) {
        for (String courseId : listCourseId) {
            if (courseId.equals(suggestionList.get(position).getCourseId())) {
                return true;
            }
        }
        return false;
    }

    class SaveQuizResult extends AsyncTask<Void, Void, String> {

        String url;
        HashMap<String, String> params;
        int requestCode;
        ProgressDialog dialog;

        SaveQuizResult(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(FirstQuizResultActivity.this);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    if (!object.getString("message").equals(""))
                        Toasty.success(FirstQuizResultActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
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

    class LoadQuizResult extends AsyncTask<Void, Void, String> {

        String url;
        HashMap<String, String> params;
        int requestCode;
        ProgressDialog dialog;

        LoadQuizResult(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(FirstQuizResultActivity.this);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    if (!object.getString("message").equals(""))
                        Toasty.info(FirstQuizResultActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();

                    refreshResultList(object.getJSONArray("results"));
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

    private class LoadSuggestion extends AsyncTask<Void, Void, String> {

        String url;
        HashMap<String, String> params;
        int requestCode;

        LoadSuggestion(String url, HashMap<String, String> params, int requestCode) {
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
                        Toasty.info(FirstQuizResultActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();

                    refreshSuggestionList(object.getJSONArray("suggestions"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            recyclerSuggestionHandler();
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
                        Toasty.info(FirstQuizResultActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
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

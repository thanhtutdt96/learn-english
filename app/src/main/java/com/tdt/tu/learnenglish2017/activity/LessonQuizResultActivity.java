package com.tdt.tu.learnenglish2017.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.helper.Constants;
import com.tdt.tu.learnenglish2017.helper.QuizResultAdapter;
import com.tdt.tu.learnenglish2017.helper.RequestHandler;
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

public class LessonQuizResultActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.btnHome)
    Button btnHome;
    @BindView(R.id.btnRedo)
    Button btnRedo;
    @BindView(R.id.btnDone)
    Button btnDone;
    @BindView(R.id.btnHistory)
    Button btnHistory;
    @BindView(R.id.txtResult)
    TextView txtResult;
    @BindView(R.id.circleResult)
    DonutProgress circleResult;
    @BindView(R.id.txtRank)
    TextView txtRank;
    List<QuizResult> quizResultList = new ArrayList<>();
    QuizResultAdapter adapter;
    private String rank;
    private int score;
    private String email;
    private String lessonId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_quiz_result);

        init();
        saveQuizResult();

    }

    private void init() {
        ButterKnife.bind(this);

        score = getIntent().getIntExtra("lesson_quiz_score", 0);
        email = getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).getString("email", "");
        lessonId = getIntent().getStringExtra("lesson_id");
        setContent();

        btnHome.setOnClickListener(this);
        btnDone.setOnClickListener(this);
        btnRedo.setOnClickListener(this);
        btnHistory.setOnClickListener(this);
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
            case R.id.btnHome:
                btnDone.performClick();
                break;
            case R.id.btnRedo:
                Intent intent = new Intent(this, LessonQuizActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btnDone:
                finish();
                break;
            case R.id.btnHistory:
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
        adapter = new QuizResultAdapter(dialogView.getContext(), R.layout.quiz_result_row, quizResultList);
        listView.setAdapter(adapter);

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("lesson_id", lessonId);
        LoadQuizResult loadQuizResult = new LoadQuizResult(Constants.URL_GET_LESSON_QUIZ_RESULTS, params, Constants.CODE_POST_REQUEST);
        loadQuizResult.execute();

        alertDialog.show();
    }

    private void saveQuizResult() {
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("lesson_id", lessonId);
        params.put("score", String.valueOf(score));
        params.put("rank", rank);

        SaveQuizResult saveQuizResult = new SaveQuizResult(Constants.URL_ADD_LESSON_QUIZ_RESULT, params, Constants.CODE_POST_REQUEST);
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
        adapter.notifyDataSetChanged();
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
            dialog = new ProgressDialog(LessonQuizResultActivity.this);
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
                        Toasty.info(LessonQuizResultActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
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
            dialog = new ProgressDialog(LessonQuizResultActivity.this);
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
                        Toasty.info(LessonQuizResultActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();

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

}

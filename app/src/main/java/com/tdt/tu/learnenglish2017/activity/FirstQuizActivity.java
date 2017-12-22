package com.tdt.tu.learnenglish2017.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.helper.Constants;
import com.tdt.tu.learnenglish2017.helper.MediaManager;
import com.tdt.tu.learnenglish2017.helper.RequestHandler;
import com.tdt.tu.learnenglish2017.item.QuizQuestion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FirstQuizActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.ivHome)
    ImageView txtQuit;
    @BindView(R.id.txtQuestionNumber)
    TextView txtQuestionNumber;
    @BindView(R.id.timeCircle)
    DonutProgress timeCircle;
    @BindView(R.id.txtQuestion)
    TextView txtQuestion;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.rbOptionA)
    RadioButton rbOptionA;
    @BindView(R.id.rbOptionB)
    RadioButton rbOptionB;
    @BindView(R.id.rbOptionC)
    RadioButton rbOptionC;
    @BindView(R.id.ivTrueA)
    ImageView ivTrueA;
    @BindView(R.id.ivTrueB)
    ImageView ivTrueB;
    @BindView(R.id.ivTrueC)
    ImageView ivTrueC;
    @BindView(R.id.ivFalseA)
    ImageView ivFalseA;
    @BindView(R.id.ivFalseB)
    ImageView ivFalseB;
    @BindView(R.id.ivFalseC)
    ImageView ivFalseC;
    @BindView(R.id.btnCheckAnswer)
    Button btnCheckAnswer;
    @BindView(R.id.btnNext)
    Button btnNext;
    String lessonId;
    private List<QuizQuestion> questionList = new ArrayList<>();
    private QuizQuestion currentQuestion;
    private int questionNumber = 0;
    private int score = 0;
    private CountDownTimer timerQuestion;
    private long currentTime;
    private MediaManager mediaManager;
    private long QUESTION_TIME = 31000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_quiz);
        init();
        loadData();
    }

    private void init() {
        ButterKnife.bind(this);
        mediaManager = new MediaManager(this);
        mediaManager.addSound("true", R.raw.true_sound);
        mediaManager.addSound("false", R.raw.false_sound);

        txtQuit.setOnClickListener(this);
        btnCheckAnswer.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivHome:
                finish();
                break;

            case R.id.btnCheckAnswer:
                checkAnswerButtonHandler();
                break;

            case R.id.btnNext:
                nextButtonHandler();
                break;
        }
    }

    private void displayQuestion() {
        timeHandler(QUESTION_TIME);
        currentQuestion = questionList.get(questionNumber);
        txtQuestionNumber.setText("" + (questionNumber + 1) + "/10");
        txtQuestion.setText(currentQuestion.getQuestion());
        rbOptionA.setText(currentQuestion.getOptionA());
        rbOptionB.setText(currentQuestion.getOptionB());
        rbOptionC.setText(currentQuestion.getOptionC());
        questionNumber++;
    }

    private void nextButtonHandler() {
        btnNext.setVisibility(View.INVISIBLE);
        btnCheckAnswer.setVisibility(View.VISIBLE);
        radioGroup.clearCheck();
        enableAllOptions();
        hideAllImage();
        if (questionNumber < 10)
            displayQuestion();
        else {
            Intent intent = new Intent(this, FirstQuizResultActivity.class);
            intent.putExtra("first_quiz_score", score);

            startActivity(intent);
            finish();
        }
    }

    private void checkAnswerButtonHandler() {
        btnNext.setVisibility(View.VISIBLE);
        btnCheckAnswer.setVisibility(View.INVISIBLE);
        disableAllOptions();
        pauseTimer();
        checkAnswer();
    }

    private void disableAllOptions() {
        rbOptionA.setEnabled(false);
        rbOptionB.setEnabled(false);
        rbOptionC.setEnabled(false);
    }

    private void enableAllOptions() {
        rbOptionA.setEnabled(true);
        rbOptionA.setClickable(true);
        rbOptionA.setTypeface(null, Typeface.NORMAL);
        rbOptionB.setEnabled(true);
        rbOptionB.setClickable(true);
        rbOptionB.setTypeface(null, Typeface.NORMAL);
        rbOptionC.setEnabled(true);
        rbOptionC.setClickable(true);
        rbOptionC.setTypeface(null, Typeface.NORMAL);
    }

    private void hideAllImage() {
        ivTrueA.setVisibility(View.INVISIBLE);
        ivTrueB.setVisibility(View.INVISIBLE);
        ivTrueC.setVisibility(View.INVISIBLE);
        ivFalseA.setVisibility(View.INVISIBLE);
        ivFalseB.setVisibility(View.INVISIBLE);
        ivFalseC.setVisibility(View.INVISIBLE);
    }

    private void checkAnswer() {
        RadioButton rbAnswer = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            mediaManager.playSound("false");

            if (rbOptionA.getText().toString().equals(currentQuestion.getAnswer())) {
                rbOptionA.setEnabled(true);
                rbOptionA.setClickable(false);
                rbOptionA.setTypeface(null, Typeface.BOLD);

            } else if (rbOptionB.getText().toString().equals(currentQuestion.getAnswer())) {
                rbOptionB.setEnabled(true);
                rbOptionB.setClickable(false);
                rbOptionB.setTypeface(null, Typeface.BOLD);
            } else if (rbOptionC.getText().toString().equals(currentQuestion.getAnswer())) {
                rbOptionC.setEnabled(true);
                rbOptionC.setClickable(false);
                rbOptionC.setTypeface(null, Typeface.BOLD);
            }
            return;
        }
        if (rbAnswer.getText().equals(currentQuestion.getAnswer())) {
            mediaManager.playSound("true");
            score++;
            if (rbAnswer.getId() == rbOptionA.getId()) {
                ivTrueA.setVisibility(View.VISIBLE);
                rbOptionA.setEnabled(true);
                rbOptionA.setClickable(false);
                rbOptionA.setTypeface(null, Typeface.BOLD);
            } else if (rbAnswer.getId() == rbOptionB.getId()) {
                ivTrueB.setVisibility(View.VISIBLE);
                rbOptionB.setEnabled(true);
                rbOptionB.setClickable(false);
                rbOptionB.setTypeface(null, Typeface.BOLD);
            } else if (rbAnswer.getId() == rbOptionC.getId()) {
                ivTrueC.setVisibility(View.VISIBLE);
                rbOptionC.setEnabled(true);
                rbOptionC.setClickable(false);
                rbOptionC.setTypeface(null, Typeface.BOLD);
            }
        } else {
            mediaManager.playSound("false");
            if (rbAnswer.getId() == rbOptionA.getId()) {
                ivFalseA.setVisibility(View.VISIBLE);
            } else if (rbAnswer.getId() == rbOptionB.getId()) {
                ivFalseB.setVisibility(View.VISIBLE);
            } else if (rbAnswer.getId() == rbOptionC.getId()) {
                ivFalseC.setVisibility(View.VISIBLE);
            }

            if (rbOptionA.getText().toString().equals(currentQuestion.getAnswer())) {
                rbOptionA.setEnabled(true);
                rbOptionA.setClickable(false);
                rbOptionA.setTypeface(null, Typeface.BOLD);

            } else if (rbOptionB.getText().toString().equals(currentQuestion.getAnswer())) {
                rbOptionB.setEnabled(true);
                rbOptionB.setClickable(false);
                rbOptionB.setTypeface(null, Typeface.BOLD);
            } else if (rbOptionC.getText().toString().equals(currentQuestion.getAnswer())) {
                rbOptionC.setEnabled(true);
                rbOptionC.setClickable(false);
                rbOptionC.setTypeface(null, Typeface.BOLD);
            }
        }

    }

    private void loadData() {
        LoadFirstQuizQuestions loadFirstQuizQuestions = new LoadFirstQuizQuestions(Constants.URL_GET_FIRST_QUIZ_QUESTIONS, null, Constants.CODE_POST_REQUEST);
        loadFirstQuizQuestions.execute();
    }

    private void refreshFirstQuizQuestionsList(JSONArray questions) throws JSONException {
        questionList.clear();
        for (int i = 0; i < questions.length(); i++) {
            JSONObject obj = questions.getJSONObject(i);

            questionList.add(new QuizQuestion(
                    obj.getInt("quiz_id"),
                    obj.getString("content"),
                    obj.getString("optionA"),
                    obj.getString("optionB"),
                    obj.getString("optionC"),
                    obj.getString("answer")
            ));
        }

    }

    private void timeHandler(long timePlay) {
        timerQuestion = new CountDownTimer(timePlay, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeCircle.setProgress(millisUntilFinished / 1000);
                currentTime = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                checkAnswerButtonHandler();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nextButtonHandler();
                    }
                }, 2500);

            }
        };
        timerQuestion.start();
    }

    private void pauseTimer() {
        if (currentTime > 0)
            timerQuestion.cancel();
    }

    private void resumeTimer() {
        if (currentTime > 0)
            timeHandler(currentTime);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeTimer();
    }

    private class LoadFirstQuizQuestions extends AsyncTask<Void, Void, String> {

        String url;
        HashMap<String, String> params;
        int requestCode;

        LoadFirstQuizQuestions(String url, HashMap<String, String> params, int requestCode) {
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
                        Toast.makeText(FirstQuizActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();

                    refreshFirstQuizQuestionsList(object.getJSONArray("questions"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            displayQuestion();
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

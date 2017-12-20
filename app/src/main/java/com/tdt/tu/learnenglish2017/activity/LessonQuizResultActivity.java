package com.tdt.tu.learnenglish2017.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.helper.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LessonQuizResultActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.ivHome)
    ImageView ivHome;
    @BindView(R.id.btnRedo)
    Button btnRedo;
    @BindView(R.id.btnDone)
    Button btnDone;
    @BindView(R.id.txtResult)
    TextView txtResult;
    @BindView(R.id.circleResult)
    DonutProgress circleResult;
    @BindView(R.id.txtRank)
    TextView txtRank;

    private String rank;
    private int score;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_quiz_result);

        init();

    }

    private void init() {
        ButterKnife.bind(this);

        score = getIntent().getIntExtra("lesson_quiz_score", 0);
        email = getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).getString("email", "");
        setContent();
        ivHome.setOnClickListener(this);
        btnDone.setOnClickListener(this);
        btnRedo.setOnClickListener(this);
    }

    private void setContent() {
        txtResult.setText(score + "/10");
        circleResult.setProgress(score);
        setRank(score);
    }

    private void setRank(int score) {
        if (score == 10) {
            rank = "EXCELLENT";
            txtRank.setTextColor(getResources().getColor(R.color.colorDarkGreen));
            txtResult.setTextColor(getResources().getColor(R.color.colorDarkGreen));
            circleResult.setUnfinishedStrokeColor(getResources().getColor(R.color.colorDarkGreen));
        } else if (score < 10 && score >= 8) {
            rank = "GOOD";
            txtRank.setTextColor(getResources().getColor(R.color.colorGreen));
            txtResult.setTextColor(getResources().getColor(R.color.colorGreen));
            circleResult.setUnfinishedStrokeColor(getResources().getColor(R.color.colorGreen));
        } else if (score < 8 && score >= 5) {
            rank = "AVERAGE";
            txtRank.setTextColor(getResources().getColor(R.color.colorYellow));
            txtResult.setTextColor(getResources().getColor(R.color.colorYellow));
            circleResult.setUnfinishedStrokeColor(getResources().getColor(R.color.colorYellow));
        } else if (score < 5 && score >= 2) {
            rank = "POOR";
            txtRank.setTextColor(getResources().getColor(R.color.colorRed));
            txtResult.setTextColor(getResources().getColor(R.color.colorRed));
            circleResult.setUnfinishedStrokeColor(getResources().getColor(R.color.colorRed));
        } else if (score < 2 && score >= 0) {
            rank = "BAD";
            txtRank.setTextColor(getResources().getColor(R.color.colorRed));
            txtResult.setTextColor(getResources().getColor(R.color.colorRed));
            circleResult.setUnfinishedStrokeColor(getResources().getColor(R.color.colorRed));
        }
        txtRank.setText(rank);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivHome:
                btnDone.performClick();
                break;
            case R.id.btnRedo:
                Intent intent = new Intent(this, LessonQuizActivity.class);
                startActivity(intent);
                break;
            case R.id.btnDone:
                buttonDoneHandler();
                break;
        }
    }

    private void buttonDoneHandler() {


        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

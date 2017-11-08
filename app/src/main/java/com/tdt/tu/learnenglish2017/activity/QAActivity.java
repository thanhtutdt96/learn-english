package com.tdt.tu.learnenglish2017.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.fragment.Tab1Fragment;
import com.tdt.tu.learnenglish2017.helper.Constants;
import com.tdt.tu.learnenglish2017.helper.CourseAdapter;
import com.tdt.tu.learnenglish2017.helper.QuestionAdapter;
import com.tdt.tu.learnenglish2017.helper.RequestHandler;
import com.tdt.tu.learnenglish2017.item.Course;
import com.tdt.tu.learnenglish2017.item.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class QAActivity extends AppCompatActivity {
    @BindView(R.id.listViewQA)
    ListView listView;
    @BindView(R.id.spinLesson)
    Spinner spinner;
    @BindView(R.id.imgbackArrow)
    ImageView backArrow;
    @BindView(R.id.imgClose)
    ImageView closeForm;
    @BindView(R.id.askForm)
    LinearLayout askForm;
    @BindView(R.id.btnAsk)
    Button buttonAsk;
    @BindView(R.id.btnPost)
    Button buttonPost;
    @BindView(R.id.editTitle)
    EditText editTitle;
    @BindView(R.id.editContent)
    EditText editContent;

    QuestionAdapter adapter;
    List<Question> questionList = new ArrayList<>();
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qa);

        init();

        buttonHandler();
        spinnerHandler();
        loadQuestions();
    }

    private void buttonHandler() {
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askForm.setVisibility(View.VISIBLE);
                buttonAsk.setVisibility(View.GONE);
            }
        });

        closeForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askForm.setVisibility(View.GONE);
                buttonAsk.setVisibility(View.VISIBLE);
                clearAllValues();
            }
        });

        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createQuestion();
                askForm.setVisibility(View.GONE);
                buttonAsk.setVisibility(View.VISIBLE);
                clearAllValues();
            }
        });
    }

    private void clearAllValues() {
        editContent.getText().clear();
        editTitle.getText().clear();
        spinner.setSelection(0);
    }

    private void init() {
        ButterKnife.bind(this);
    }

    private void spinnerHandler() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lessons_array));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {

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
                        Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();

                    refreshQuestionList(object.getJSONArray("questions"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);


            if (requestCode == CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }

    private void createQuestion() {
        SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
        String courseId = prefs.getString("courseId", "");
        String name = prefs.getString("username", "");
        String title = editTitle.getText().toString().trim();
        String content = editContent.getText().toString().trim();
        String lesson_number = spinner.getSelectedItem().toString();

        if (lesson_number.equals("General question"))
            lesson_number = "";

        if (TextUtils.isEmpty(content)) {
            editContent.setError("Please enter content");
            editContent.requestFocus();
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("course_id", courseId);
        params.put("lesson_number", lesson_number);
        params.put("name", name);
        params.put("title", title);
        params.put("content", content);
        Log.d("map", params.toString());

        PerformNetworkRequest request = new PerformNetworkRequest(Constants.URL_ADD_QUESTION, params, CODE_POST_REQUEST);
        request.execute();
    }

    private void loadQuestions() {
        PerformNetworkRequest request = new PerformNetworkRequest(Constants.URL_GET_QUESTIONS, null, CODE_GET_REQUEST);
        request.execute();
    }

    private void refreshQuestionList(JSONArray questions) throws JSONException {
        questionList.clear();

        for (int i = 0; i < questions.length(); i++) {
            JSONObject obj = questions.getJSONObject(i);

            questionList.add(new Question(
                    obj.getString("name"),
                    obj.getString("date"),
                    obj.getString("lesson_number"),
                    obj.getString("title"),
                    obj.getString("content")
            ));
        }
        QuestionAdapter adapter = new QuestionAdapter(this, R.layout.question_row_layout, questionList);
        listView.setAdapter(adapter);

    }
}

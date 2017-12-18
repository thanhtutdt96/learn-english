package com.tdt.tu.learnenglish2017.fragment;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.helper.AnswerAdapter;
import com.tdt.tu.learnenglish2017.helper.Constants;
import com.tdt.tu.learnenglish2017.helper.RequestHandler;
import com.tdt.tu.learnenglish2017.item.Answer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;


public class AnswerFragment extends Fragment {
    @BindView(R.id.txtContent_Answer)
    TextView content;
    @BindView(R.id.txtTitle_Answer)
    TextView title;
    @BindView(R.id.txtDate_Answer)
    TextView date;
    @BindView(R.id.txtlessonNumber_Answer)
    TextView lessonNumber;
    @BindView(R.id.txtName_Answer)
    TextView name;
    @BindView(R.id.imgbackArrow_Answer)
    ImageView backArrow;
    @BindView(R.id.listAnswer)
    ListView listView;
    @BindView(R.id.editAnswer)
    EditText editAnswer;
    @BindView(R.id.btnPost_Answer)
    Button btnPost;

    View view;
    String questionId;
    ArrayList<Answer> answerList = new ArrayList<>();
    AnswerAdapter adapter;

    public AnswerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_answer, container, false);

        init();
        backArrowHandler();
        setQuestionSection();
        loadAnswers();
        buttonHandler();

        return view;
    }

    private void buttonHandler() {
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAnswer();
                editAnswer.getText().clear();
                loadAnswers();
            }
        });
    }

    private void backArrowHandler() {
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });
    }

    private void setQuestionSection() {
        questionId = getArguments().getString("question_id", "");
        name.setText(getArguments().getString("name", ""));
        date.setText(getArguments().getString("date", ""));
        lessonNumber.setText(getArguments().getString("lesson_number", ""));
        title.setText(getArguments().getString("title", ""));
        content.setText(getArguments().getString("content", ""));

    }

    private void loadAnswers() {
        HashMap<String, String> params = new HashMap<>();
        params.put("question_id", questionId);

        PerformNetworkRequest request = new PerformNetworkRequest(Constants.URL_GET_ANSWERS, params, Constants.CODE_POST_REQUEST);
        request.execute();
    }

    private void createAnswer() {
        SharedPreferences prefs = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE);
        String name = prefs.getString("username", "");
        String answer = editAnswer.getText().toString().trim();


        if (TextUtils.isEmpty(answer)) {
            editAnswer.setError("Please enter content");
            editAnswer.requestFocus();
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("question_id", questionId);
        params.put("name", name);
        params.put("content", answer);

        PerformNetworkRequest request = new PerformNetworkRequest(Constants.URL_ADD_ANSWER, params, Constants.CODE_POST_REQUEST);
        request.execute();
    }

    private void refreshAnswerList(JSONArray answers) throws JSONException {
        answerList.clear();

        for (int i = 0; i < answers.length(); i++) {
            JSONObject obj = answers.getJSONObject(i);

            answerList.add(new Answer(
                    obj.getString("name"),
                    obj.getString("date"),
                    obj.getString("content")
            ));
        }
        adapter.notifyDataSetChanged();

    }

    private void init() {
        ButterKnife.bind(this, view);
        adapter = new AnswerAdapter(view.getContext(), R.layout.answer_row_layout, answerList);
        listView.setAdapter(adapter);
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
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    if (!object.getString("message").equals(""))
                        Toast.makeText(view.getContext(), object.getString("message"), Toast.LENGTH_SHORT).show();

                    refreshAnswerList(object.getJSONArray("answers"));
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

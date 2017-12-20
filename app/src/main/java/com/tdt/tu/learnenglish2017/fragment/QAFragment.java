package com.tdt.tu.learnenglish2017.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.helper.Constants;
import com.tdt.tu.learnenglish2017.helper.QuestionAdapter;
import com.tdt.tu.learnenglish2017.helper.RequestHandler;
import com.tdt.tu.learnenglish2017.item.Question;

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

public class QAFragment extends Fragment {
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.spinLesson)
    Spinner spinner;
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

    List<Question> questionList = new ArrayList<>();
    QuestionAdapter adapter;
    View view;

    String courseId;
    String name;
    int numberOfQuestionRow;

    BroadcastReceiver receiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_qa, container, false);

        init();
        buttonHandler();
        spinnerHandler();
        loadQuestions();
        listViewHandler();

        return view;
    }

    private String prepareQuestionId() {
        int i = numberOfQuestionRow;
        i++;
        String newId = "Q" + String.valueOf(i);
        return newId;
    }


    private void callFragment(Fragment fragment) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.qAContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void listViewHandler() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Fragment answerFragment = new AnswerFragment();
                callFragment(answerFragment);

                Bundle bundle = new Bundle();
                bundle.putString("course_id", courseId);
                bundle.putString("question_id", questionList.get(i).getQuestionId());
                bundle.putString("name", questionList.get(i).getName());
                bundle.putString("date", questionList.get(i).getDate());
                bundle.putString("lesson_number", questionList.get(i).getLesson());
                bundle.putString("title", questionList.get(i).getTitle());
                bundle.putString("content", questionList.get(i).getContent());

                answerFragment.setArguments(bundle);
            }
        });
    }

    private void buttonHandler() {

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
            }
        });
    }

    private void clearAllValues() {
        editContent.getText().clear();
        editTitle.getText().clear();
        spinner.setSelection(0);
    }

    private void init() {
        ButterKnife.bind(this, view);
        adapter = new QuestionAdapter(view.getContext(), R.layout.question_row_layout, questionList);
        listView.setAdapter(adapter);

        SharedPreferences prefs = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE);
        courseId = prefs.getString("course_id", "");
        name = prefs.getString("username", "");
    }

    private void spinnerHandler() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lessons_array));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void createQuestion() {

        String title = editTitle.getText().toString().trim();
        String content = editContent.getText().toString().trim();
        String lesson_number = spinner.getSelectedItem().toString();

        if (content.length() == 0) {
            editContent.setError("You cannot leave your question details blank!");
            editContent.requestFocus();
            return;
        } else {
            if (lesson_number.equals("General"))
                lesson_number = "";
            HashMap<String, String> params = new HashMap<>();
            params.put("question_id", prepareQuestionId());
            params.put("course_id", courseId);
            params.put("lesson_number", lesson_number);
            params.put("name", name);
            params.put("title", title);
            params.put("content", content);

            PerformNetworkRequest request = new PerformNetworkRequest(Constants.URL_ADD_QUESTION, params, Constants.CODE_POST_REQUEST);
            request.execute();

            askForm.setVisibility(View.GONE);
            buttonAsk.setVisibility(View.VISIBLE);
            clearAllValues();
        }
    }

    private void loadQuestions() {
        PerformNetworkRequest request = new PerformNetworkRequest(Constants.URL_GET_QUESTIONS, null, Constants.CODE_GET_REQUEST);
        request.execute();
    }

    private void refreshQuestionList(JSONArray questions) throws JSONException {
        questionList.clear();
        numberOfQuestionRow = questions.length();
        for (int i = 0; i < numberOfQuestionRow; i++) {
            JSONObject obj = questions.getJSONObject(i);
            if (obj.getString("course_id").equals(courseId)) {
                questionList.add(new Question(
                        obj.getString("question_id"),
                        obj.getString("name"),
                        obj.getString("date"),
                        obj.getString("lesson_number"),
                        obj.getString("title"),
                        obj.getString("content"),
                        obj.getInt("comment_count")
                ));
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadQuestions();
    }

    private void initReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadQuestions();
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("refreshList");
        view.getContext().registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onStart() {
        super.onStart();
        initReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        view.getContext().unregisterReceiver(receiver);
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
                        Toasty.success(view.getContext(), object.getString("message"), Toast.LENGTH_SHORT).show();

                    refreshQuestionList(object.getJSONArray("questions"));
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

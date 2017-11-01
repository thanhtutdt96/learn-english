package com.tdt.tu.learnenglish2017.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.helper.Constants;
import com.tdt.tu.learnenglish2017.helper.CourseAdapter;
import com.tdt.tu.learnenglish2017.helper.QuestionAdapter;
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
import java.util.List;

import es.dmoral.toasty.Toasty;

public class QAActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView backArrow;
    QuestionAdapter adapter;
    List<Question> questionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qa);

        backArrow = (ImageView) findViewById(R.id.imgbackArrow);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        adapter = new QuestionAdapter(questionList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        getJSON(Constants.URL_ALL_QUESTIONS);
        adapter.notifyDataSetChanged();

    }

    private void getJSON(final String urlWebService) {

        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    prepareQuestionData(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {

                try {
                    //creating a URL
                    URL url = new URL(urlWebService);

                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line
                    String json;

                    //reading until we don't find null
                    while ((json = bufferedReader.readLine()) != null) {

                        //appending it to string builder
                        sb.append(json + "\n");
                    }

                    //finally returning the read string
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }

        //creating asynctask object and executing it
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    private void prepareQuestionData(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);

        if (jsonObject.getInt("success") == 1) {
            JSONArray jsonArray = jsonObject.getJSONArray("questions");

            String[] usernames = new String[jsonArray.length()];
            String[] dates = new String[jsonArray.length()];
            String[] lessons = new String[jsonArray.length()];
            String[] titles = new String[jsonArray.length()];
            String[] contents = new String[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                usernames[i] = object.getString("name");
                dates[i] = object.getString("date");
                lessons[i] = object.getString("lesson_id");
                titles[i] = object.getString("title");
                contents[i] = object.getString("content");

                questionList.add(new Question(usernames[i], dates[i], lessons[i], titles[i], contents[i]));
            }
        } else {
            Toasty.warning(this, "No courses found", Toast.LENGTH_SHORT);
        }
    }
}

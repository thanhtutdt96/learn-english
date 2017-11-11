package com.tdt.tu.learnenglish2017.activity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bluejamesbond.text.DocumentView;
import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.helper.Constants;
import com.tdt.tu.learnenglish2017.helper.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CourseInfoActivity extends AppCompatActivity {
    @BindView(R.id.txtCourseName_Info)
    TextView courseName;
    @BindView(R.id.price_Info)
    TextView coursePrice;
    @BindView(R.id.txtDescription_Info)
    DocumentView courseDescription;
    @BindView(R.id.btnBuy_Info)
    Button btnBuy;
    @BindView(R.id.btnFavorite_Info)
    TextView btnFavorite;

    String courseId;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_info);

        init();
        setContent();
        buttonHandler();
    }

    private void buttonHandler() {
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyCourse();
                btnBuy.setText("Bought");
                btnBuy.setEnabled(false);
                btnBuy.setBackgroundColor(getResources().getColor(R.color.colorGrey));
            }
        });

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCourseToFavorite();
            }
        });
    }

    private void setContent() {
        SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
        email = prefs.getString("email", "");
        courseId = getIntent().getExtras().getString("course_id", "");
        String name = getIntent().getExtras().getString("course_name", "");
        int price = getIntent().getExtras().getInt("price", 0);
        String description = getIntent().getExtras().getString("description", "");
        courseName.setText(name);

        SpannableString priceUnit = new SpannableString("đ" + String.valueOf(getIntent().getExtras().getInt("price", 0)));
        priceUnit.setSpan(new UnderlineSpan(), 0, 1, 0);
        if (price == 0) {
            coursePrice.setText("Free");
            btnBuy.setText("Get");
        } else
            coursePrice.setText(priceUnit);
        courseDescription.setText(description);

    }

    private void init() {
        ButterKnife.bind(this);
    }

    private void buyCourse() {
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("course_id", courseId);

        PerformNetworkRequest request = new PerformNetworkRequest(Constants.URL_ADD_USER_COURSE, params, Constants.CODE_POST_REQUEST);
        request.execute();
    }

    private void addCourseToFavorite() {
        SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
        String email = prefs.getString("email", "");

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("course_id", courseId);

        PerformNetworkRequest request = new PerformNetworkRequest(Constants.URL_ADD_USER_FAVORITE, params, Constants.CODE_POST_REQUEST);
        request.execute();
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
                        Toast.makeText(CourseInfoActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();

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

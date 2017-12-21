package com.tdt.tu.learnenglish2017.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.helper.Constants;
import com.tdt.tu.learnenglish2017.helper.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class CourseInfoActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener {

    @BindView(R.id.txtCourseName_Info)
    TextView courseName;
    @BindView(R.id.price_Info)
    TextView coursePrice;
    @BindView(R.id.txtDescription_Info)
    TextView courseDescription;
    @BindView(R.id.btnBuy_Info)
    Button btnBuy;
    @BindView(R.id.btnFavorite_Info)
    TextView btnFavorite;
    @BindView(R.id.ivBack)
    ImageView ivBack;

    private YouTubePlayerFragment youTubePlayerFragment;
    private int REQUEST_VIDEO = 1;

    private String courseId;
    private String description;
    private String name;
    private String email;
    private int price;
    private String link;
    private ArrayList<String> listCourseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_info);

        init();
        setContent();
        initYoutubePlayer();
        loadCourseIdList();
    }

    private void buttonHandler() {
        for (String tmp : listCourseId) {
            if (tmp.equals(courseId)) {
                disableBuy();
                disableFavorite();
            }
        }
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyCourse();
                disableBuy();
                final AlertDialog.Builder builder = new AlertDialog.Builder(CourseInfoActivity.this);
                builder.setMessage("Do you want to open this course and learn now?");
                builder.setCancelable(false);
                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences.Editor editor = getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).edit();
                        editor.putString("course_id", courseId);
                        editor.putString("description", description);
                        editor.commit();

                        Intent intent = new Intent(CourseInfoActivity.this, LessonActivity.class);
                        intent.putExtra("course_name", name);
                        startActivity(intent);

                    }
                });
                builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCourseToFavorite();
                disableFavorite();
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void disableBuy() {
        btnBuy.setText("Bought");
        btnBuy.setEnabled(false);
        btnBuy.setCompoundDrawablesWithIntrinsicBounds(this.getResources().getDrawable(R.drawable.ic_check_orange_24dp), null, null, null);
        btnBuy.setBackgroundColor(getResources().getColor(R.color.colorGrey));
    }

    private void disableFavorite() {
        btnFavorite.setText("Added to your favorite");
        btnFavorite.setEnabled(false);
        btnFavorite.setTextColor(getResources().getColor(R.color.colorWhite));
        btnFavorite.setCompoundDrawablesWithIntrinsicBounds(this.getResources().getDrawable(R.drawable.ic_check_orange_24dp), null, null, null);
        btnFavorite.setBackgroundColor(getResources().getColor(R.color.colorGrey));
    }

    private void setContent() {
        email = getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).getString("email", "");
        courseId = getIntent().getStringExtra("course_id");
        name = getIntent().getStringExtra("course_name");
        price = getIntent().getIntExtra("price", 0);
        description = getIntent().getStringExtra("description");
        link = getIntent().getStringExtra("link");
        courseName.setText(name);

        SpannableString priceUnit = new SpannableString("đ" + String.valueOf(price));
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
        SharedPreferences prefs = getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE);
        String email = prefs.getString("email", "");

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("course_id", courseId);

        PerformNetworkRequest request = new PerformNetworkRequest(Constants.URL_ADD_USER_FAVORITE, params, Constants.CODE_POST_REQUEST);
        request.execute();
    }

    private void loadCourseIdList() {
        SharedPreferences prefs = getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE);
        String email = prefs.getString("email", "");

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);

        PerformNetworkRequest request = new PerformNetworkRequest(Constants.URL_GET_COURSE_ID_BY_EMAIL, params, Constants.CODE_POST_REQUEST);
        request.execute();
    }

    private void refreshCourseIdList(JSONArray questions) throws JSONException {
        listCourseId = new ArrayList<>();
        for (int i = 0; i < questions.length(); i++) {
            JSONObject obj = questions.getJSONObject(i);

            listCourseId.add(obj.getString("course_id"));
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            youTubePlayer.cueVideo(link);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError())
            youTubeInitializationResult.getErrorDialog(this, REQUEST_VIDEO);
        else
            Toasty.error(this, "Error loading video!", Toast.LENGTH_SHORT).show();
    }

    private void initYoutubePlayer() {
        youTubePlayerFragment = (YouTubePlayerFragment) this.getFragmentManager().findFragmentById(R.id.youtubePlayerFragment);
        youTubePlayerFragment.initialize(Constants.API_KEY, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VIDEO)
            getYouTubePlayerProvider().initialize(Constants.API_KEY, this);
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtubePlayerFragment);
    }

    public class PerformNetworkRequest extends AsyncTask<Void, Void, String> {

        String url;
        HashMap<String, String> params;
        int requestCode;
        ProgressDialog dialog;

        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(CourseInfoActivity.this);
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
                        Toast.makeText(CourseInfoActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                    refreshCourseIdList(object.getJSONArray("course_ids"));
                    buttonHandler();
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

package com.tdt.tu.learnenglish2017.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.fragment.AboutCourseFragment;
import com.tdt.tu.learnenglish2017.fragment.LessonsFragment;
import com.tdt.tu.learnenglish2017.fragment.QAFragment;
import com.tdt.tu.learnenglish2017.fragment.QuizFragment;
import com.tdt.tu.learnenglish2017.helper.Constants;
import com.tdt.tu.learnenglish2017.helper.RequestHandler;
import com.tdt.tu.learnenglish2017.helper.SectionsPagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class LessonActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener {

    public static YouTubePlayer mYoutubePlayer;
    public static Button buttonDownloadAll;
    public static TextView lessonTitle;
    public static TextView courseTitle;
    private SimpleRatingBar ratingBar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private YouTubePlayerFragment youTubePlayerFragment;

    private int REQUEST_VIDEO = 1;
    private String link;
    private String courseId;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        init();
        showLoading();
        createTabs();
        loadCourseRating();
        ratingBarHandler();
        initYoutubePlayer();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void ratingBarHandler() {
        ratingBar.setOnRatingBarChangeListener(new SimpleRatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(SimpleRatingBar simpleRatingBar, float rating, boolean fromUser) {
                saveCourseRating(rating);

                Intent intent = new Intent();
                intent.setAction("refresh_user_courses");
                sendBroadcast(intent);
            }
        });
    }

    private void init() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        courseTitle = (TextView) findViewById(R.id.txtCourseName);
        buttonDownloadAll = (Button) findViewById(R.id.btnDownloadAll);
        lessonTitle = (TextView) findViewById(R.id.txtLessonName);
        ratingBar = (SimpleRatingBar) findViewById(R.id.ratingBar);

        courseTitle.setText(getIntent().getStringExtra("course_name"));
        link = getIntent().getStringExtra("link");
        courseId = getIntent().getStringExtra("course_id");
        email = getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).getString("email", "");
    }

    private void initYoutubePlayer() {
        youTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtubePlayerFragment);
        youTubePlayerFragment.initialize(Constants.API_KEY, this);
    }

    private void createTabs() {
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("Lesson");
        tabLayout.getTabAt(1).setText("Q&A");
        tabLayout.getTabAt(2).setText("Quiz");
        tabLayout.getTabAt(3).setText("About");
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LessonsFragment());
        adapter.addFragment(new QAFragment());
        adapter.addFragment(new QuizFragment());
        adapter.addFragment(new AboutCourseFragment());
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            mYoutubePlayer = youTubePlayer;
            youTubePlayer.loadVideo(link);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError())
            youTubeInitializationResult.getErrorDialog(LessonActivity.this, REQUEST_VIDEO);
        else
            Toasty.error(this, "Error loading video!", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VIDEO)
            getYouTubePlayerProvider().initialize(Constants.API_KEY, this);
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtubePlayerFragment);
    }

    private void showLoading() {
        final ProgressDialog dialog = new ProgressDialog(LessonActivity.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 2000);
    }

    private void loadCourseRating() {
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("course_id", courseId);

        LoadCourseRating loadCourseRating = new LoadCourseRating(Constants.URL_LOAD_COURSE_RATING, params, Constants.CODE_POST_REQUEST);
        loadCourseRating.execute();
    }

    private void saveCourseRating(float rating) {
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("course_id", courseId);
        params.put("rating", String.valueOf(rating));

        SaveCourseRating saveCourseRating = new SaveCourseRating(Constants.URL_SAVE_COURSE_RATING, params, Constants.CODE_POST_REQUEST);
        saveCourseRating.execute();
    }

    private void refreshCourseRating(JSONArray rating) throws JSONException {
        ratingBar.setRating(Float.parseFloat(rating.getJSONObject(0).getString("rating")));
    }

    class LoadCourseRating extends AsyncTask<Void, Void, String> {

        String url;
        HashMap<String, String> params;
        int requestCode;

        LoadCourseRating(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    if (!object.getString("message").equals(""))
                        Toast.makeText(LessonActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();

                    refreshCourseRating(object.getJSONArray("rating"));
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

    class SaveCourseRating extends AsyncTask<Void, Void, String> {

        String url;
        HashMap<String, String> params;
        int requestCode;

        SaveCourseRating(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
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

package com.tdt.tu.learnenglish2017.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.fragment.LessonsFragment;
import com.tdt.tu.learnenglish2017.fragment.MoreFragment;
import com.tdt.tu.learnenglish2017.fragment.QAFragment;
import com.tdt.tu.learnenglish2017.helper.Constants;
import com.tdt.tu.learnenglish2017.helper.SectionsPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LessonActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener {

    public static YouTubePlayer mYoutubePlayer;
    Handler handler;
    String[] links;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tabs_Lesson)
    TabLayout tabLayout;
    @BindView(R.id.txtCourseName_Lesson)
    TextView courseTitle;
    int REQUEST_VIDEO = 1;
    String courseName;
    String courseId;
    String link;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private YouTubePlayerFragment youTubePlayerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        init();

        //createTabs();
        new setupTabsandFragments().execute();
        initYoutubePlayer();

    }

    private void init() {
        ButterKnife.bind(this);

        courseName = this.getIntent().getStringExtra("course_name");
        courseId = this.getIntent().getStringExtra("course_id");
        courseTitle.setText(courseName);

        SharedPreferences preferences = getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE);
        link = preferences.getString("link", "");
    }

    private void initYoutubePlayer() {
        youTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtubePlayerFragment);
        youTubePlayerFragment.initialize(Constants.API_KEY, this);
    }

    private void createTabs() {
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("Lessons");
        tabLayout.getTabAt(1).setText("Q&A");
        tabLayout.getTabAt(2).setText("More");
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LessonsFragment());
        adapter.addFragment(new QAFragment());
        adapter.addFragment(new MoreFragment());
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
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(LessonActivity.this, REQUEST_VIDEO);

        } else {
            Toast.makeText(this, "Error loading video!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VIDEO)
            getYouTubePlayerProvider().initialize(Constants.API_KEY, this);
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtubePlayerFragment);
    }

    class setupTabsandFragments extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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

        @Override
        protected Void doInBackground(Void... voids) {
            createTabs();

            return null;
        }
    }
}

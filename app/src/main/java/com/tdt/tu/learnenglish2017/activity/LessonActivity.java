package com.tdt.tu.learnenglish2017.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.fragment.LessonsFragment;
import com.tdt.tu.learnenglish2017.fragment.MoreFragment;
import com.tdt.tu.learnenglish2017.fragment.QAFragment;
import com.tdt.tu.learnenglish2017.helper.Constants;
import com.tdt.tu.learnenglish2017.helper.SectionsPagerAdapter;

public class LessonActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener {

    public static YouTubePlayer mYoutubePlayer;
    public static FrameLayout youtubeLayout;
    public static VideoView videoView;
    public static Activity activity;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView courseTitle;
    private MediaController mediaController;
    private int REQUEST_VIDEO = 1;
    private String courseName;
    private String courseId;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private YouTubePlayerFragment youTubePlayerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        init();
        showLoading();
        createTabs();
        initYoutubePlayer();
        initVideoPlayer();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void init() {
        activity = this;
        youtubeLayout = (FrameLayout) findViewById(R.id.youtubeLayout);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        courseTitle = (TextView) findViewById(R.id.txtCourseName_Lesson);
        //videoView = (VideoView) findViewById(R.id.videoView);

        courseName = this.getIntent().getStringExtra("course_name");
        courseId = this.getIntent().getStringExtra("course_id");
        courseTitle.setText("ABC");
    }

    private void initVideoPlayer() {

//
//        videoView.requestFocus();
//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                videoView.start();
//                mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
//                    @Override
//                    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
//                        mediaController.setAnchorView(videoView);
//                    }
//                });
//            }
//        });
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
            youTubePlayer.loadVideo(getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).getString("link", ""));
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

}

package com.tdt.tu.learnenglish2017.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.Toast;

import com.tdt.tu.learnenglish2017.R;

import java.io.File;

import bg.devlabs.fullscreenvideoview.FullscreenVideoView;
import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;


/**
 * Created by 1stks on 13-Dec-17.
 */

public class DialogVideoActivity extends AppCompatActivity {

    @BindView(R.id.videoView)
    FullscreenVideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_video_player);
        ButterKnife.bind(this);
        try {
            File videoFile = new File(getIntent().getStringExtra("videoPath"));
            videoView.videoFile(videoFile).enableAutoStart();
        } catch (Exception e) {
            Toasty.error(this, "Cannot play video", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}

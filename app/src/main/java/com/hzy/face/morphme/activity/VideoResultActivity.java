package com.hzy.face.morphme.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.google.android.exoplayer2.Player;
import com.hzy.face.morphme.R;
import com.hzy.face.morphme.consts.RouterHub;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = RouterHub.VIDEO_RESULT_ACTIVITY)
public class VideoResultActivity extends AppCompatActivity {

    public static final String EXTRA_FILE_PATH = "EXTRA_FILE_PATH";

    @BindView(R.id.exo_video_view)
    VideoView mExoVideoView;

    private String mFilePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_result);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mExoVideoView.setRepeatMode(Player.REPEAT_MODE_ONE);
        mExoVideoView.setOnPreparedListener(() -> {
            if (!mExoVideoView.isPlaying()) {
                mExoVideoView.start();
            }
        });
        try {
            mFilePath = getIntent().getStringExtra(EXTRA_FILE_PATH);
            mExoVideoView.setVideoPath(mFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

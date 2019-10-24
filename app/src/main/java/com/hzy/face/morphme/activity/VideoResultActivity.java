package com.hzy.face.morphme.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.UriUtils;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.google.android.exoplayer2.Player;
import com.hzy.face.morphme.R;
import com.hzy.face.morphme.consts.RouterHub;
import com.hzy.face.morphme.utils.SaveMediaCallback;
import com.hzy.face.morphme.utils.SpaceUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = RouterHub.VIDEO_RESULT_ACTIVITY)
public class VideoResultActivity extends AppCompatActivity {

    public static final String EXTRA_FILE_PATH = "EXTRA_FILE_PATH";

    @BindView(R.id.exo_video_view)
    VideoView mExoVideoView;

    private String mFilePath;
    private boolean mSavePublic;

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

    @OnClick({R.id.save_button, R.id.share_button})
    public void onButtonsClicked(View view) {
        switch (view.getId()) {
            case R.id.save_button:
                saveVideoPublic();
                break;
            case R.id.share_button:
                if (!StringUtils.isTrimEmpty(mFilePath)) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM, UriUtils.file2Uri(new File(mFilePath)));
                    intent.setType("video/*");
                    startActivity(Intent.createChooser(intent, getString(R.string.share_to)));
                }
                break;
        }
    }

    private void saveVideoPublic() {
        if (!mSavePublic) {
            SpaceUtils.savePublicMediaCopy(mFilePath, SpaceUtils.SAVE_TYPE_MP4,
                    new SaveMediaCallback() {
                        @Override
                        public void onSuccess(File dst) {
                            mSavePublic = true;
                            snakeBarShow(dst.getPath());
                        }

                        @Override
                        public void onFail() {

                        }
                    });
        } else {
            snakeBarShow(getString(R.string.file_saved_already));
        }
    }

    private void snakeBarShow(String msg) {
        SnackbarUtils.with(mExoVideoView).setMessage(msg).show();
    }
}

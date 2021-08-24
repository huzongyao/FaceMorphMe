package com.hzy.face.morphme.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.IntentUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.hzy.face.morphme.R;
import com.hzy.face.morphme.consts.RouterHub;
import com.hzy.face.morphme.utils.SaveMediaCallback;
import com.hzy.face.morphme.utils.SpaceUtils;
import com.hzy.face.morphme.widget.Ratio34ImageView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = RouterHub.GIF_RESULT_ACTIVITY)
public class GifResultActivity extends AppCompatActivity {
    public static final String EXTRA_FILE_PATH = "EXTRA_FILE_PATH";

    @BindView(R.id.gif_image_view)
    Ratio34ImageView mGifImageView;

    private String mFilePath;
    private boolean mSavePublic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif_result);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        try {
            mFilePath = getIntent().getStringExtra(EXTRA_FILE_PATH);
            Glide.with(this).load(mFilePath).into(mGifImageView);
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
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.save_button:
                saveImagePublic();
                break;
            case R.id.share_button:
                if (!StringUtils.isTrimEmpty(mFilePath)) {
                    Intent intent = IntentUtils.getShareImageIntent(mFilePath);
                    startActivity(Intent.createChooser(intent, getString(R.string.share_to)));
                }
                break;
        }
    }

    private void saveImagePublic() {
        if (!mSavePublic) {
            SpaceUtils.savePublicMediaCopy(mFilePath, SpaceUtils.SAVE_TYPE_GIF,
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
        SnackbarUtils.with(mGifImageView).setMessage(msg).show();
    }
}

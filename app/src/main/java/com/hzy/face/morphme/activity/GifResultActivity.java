package com.hzy.face.morphme.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.IntentUtils;
import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.hzy.face.morphme.R;
import com.hzy.face.morphme.consts.RouterHub;
import com.hzy.face.morphme.widget.Ratio34ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = RouterHub.GIF_RESULT_ACTIVITY)
public class GifResultActivity extends AppCompatActivity {
    public static final String EXTRA_FILE_PATH = "EXTRA_FILE_PATH";

    @BindView(R.id.gif_image_view)
    Ratio34ImageView mGifImageView;

    private String mGifFilePath;

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
            mGifFilePath = getIntent().getStringExtra(EXTRA_FILE_PATH);
            Glide.with(this).load(mGifFilePath).into(mGifImageView);
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

    @OnClick(R.id.share_button)
    public void onShareClicked() {
        if (!StringUtils.isTrimEmpty(mGifFilePath)) {
            Intent intent = IntentUtils.getShareImageIntent("", mGifFilePath);
            startActivity(Intent.createChooser(intent, getString(R.string.share_to)));
        }
    }
}

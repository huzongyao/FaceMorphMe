package com.hzy.face.morphme.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hzy.face.morpher.MorpherApi;
import com.hzy.face.morphme.R;
import com.hzy.face.morphme.consts.RouterHub;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = RouterHub.VERSION_INFO_ACTIVITY)
public class VersionInfoActivity extends AppCompatActivity {

    @BindView(R.id.opencv_version)
    TextView mOpencvVersion;
    @BindView(R.id.stasm_version)
    TextView mStasmVersion;
    @BindView(R.id.libyuv_version)
    TextView mLibyuvVersion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_info);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        showLibsVersionInfo();
    }

    @SuppressLint("SetTextI18n")
    private void showLibsVersionInfo() {
        mOpencvVersion.setText("OpenCV Version: " + MorpherApi.getOpenCvVersionString());
        mStasmVersion.setText("Stasm Version: " + MorpherApi.getStasmVersionString());
        mLibyuvVersion.setText("libyuv Version: " + MorpherApi.getLibYUVVersionString());
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

    @OnClick({R.id.opencv_version, R.id.stasm_version})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.opencv_version:
                WebViewActivity.startUrl("https://opencv.org/");
                break;
            case R.id.stasm_version:
                WebViewActivity.startUrl("http://www.milbo.users.sonic.net/stasm/");
                break;
        }
    }
}

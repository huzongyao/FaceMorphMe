package com.hzy.face.morphme.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.AppUtils;
import com.hzy.face.morphme.R;
import com.hzy.face.morphme.consts.RouterHub;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = RouterHub.ABOUT_ACTIVITY)
public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.version_name)
    TextView mVersionName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setupVersionInfo();
    }

    private void setupVersionInfo() {
        String versionName = AppUtils.getAppVersionName();
        mVersionName.setText(versionName);
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

    @OnClick({R.id.version_info_item,
            R.id.btn_open_3rd,
            R.id.about_me_item})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.version_info_item:
                WebViewActivity.startUrl(getString(R.string.github_project_release));
                break;
            case R.id.btn_open_3rd:
                ARouter.getInstance().build(RouterHub.VERSION_INFO_ACTIVITY).navigation();
                break;
            case R.id.about_me_item:
                WebViewActivity.startUrl(getString(R.string.github_user_page));
                break;
        }
    }
}

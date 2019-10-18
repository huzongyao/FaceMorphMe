package com.hzy.face.morphme.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.hzy.face.morphme.R;
import com.hzy.face.morphme.consts.RouterHub;

import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = RouterHub.MAIN_ACTIVITY)
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_about:
                ARouter.getInstance().build(RouterHub.ABOUT_ACTIVITY).navigation();
                break;
            case R.id.main_menu_settings:
                ARouter.getInstance().build(RouterHub.SETTINGS_ACTIVITY).navigation();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.btn_two_images,
            R.id.btn_detect_face,
            R.id.btn_video_export})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_two_images:
                ARouter.getInstance().build(RouterHub.TWO_MORPH_ACTIVITY).navigation();
                break;
            case R.id.btn_detect_face:
                ARouter.getInstance().build(RouterHub.FACE_DETECT_ACTIVITY).navigation();
                break;
            case R.id.btn_video_export:
                ARouter.getInstance().build(RouterHub.VIDEO_MORPH_ACTIVITY).navigation();
                break;
        }
    }
}

package com.hzy.face.morphme.activity;

import android.app.Activity;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.StringUtils;
import com.hzy.face.morphme.R;
import com.hzy.face.morphme.consts.RouterHub;
import com.hzy.face.morphme.utils.CascadeUtils;

@Route(path = RouterHub.SPLASH_ACTIVITY)
public class SplashActivity extends Activity {

    private static boolean isAssetsLoad = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isAssetsLoad) {
            startMainApp();
        } else {
            setContentView(R.layout.activity_splash);
            ensureCascadeAndStart();
        }
    }

    private void ensureCascadeAndStart() {
        new Thread() {
            @Override
            public void run() {
                try {
                    String path = CascadeUtils.ensureCascadePath();
                    Thread.sleep(1000);
                    if (!StringUtils.isTrimEmpty(path)) {
                        isAssetsLoad = true;
                        startMainApp();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void startMainApp() {
        ARouter.getInstance().build(RouterHub.MAIN_ACTIVITY).navigation();
        finish();
    }
}

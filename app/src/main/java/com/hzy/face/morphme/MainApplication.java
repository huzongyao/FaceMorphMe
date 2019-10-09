package com.hzy.face.morphme;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.getConfig().setBorderSwitch(false);
        Utils.init(this);
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
    }
}

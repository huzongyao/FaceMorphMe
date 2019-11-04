package com.hzy.face.morphme;

import android.app.Application;
import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;

import xcrash.XCrash;

public class MainApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        XCrash.init(this);
    }

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

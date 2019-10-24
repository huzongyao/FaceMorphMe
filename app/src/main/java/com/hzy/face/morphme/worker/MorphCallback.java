package com.hzy.face.morphme.worker;

import android.graphics.Bitmap;

public abstract class MorphCallback {

    protected void onStart() {
    }

    protected abstract void onOneFrame(Bitmap bitmap, int index, float alpha);

    protected void onAbort() {
    }

    protected void onFinish() {
    }
}

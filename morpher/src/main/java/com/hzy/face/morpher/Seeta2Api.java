package com.hzy.face.morpher;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;

public enum Seeta2Api {
    INSTANCE;

    private volatile long mInstanceId;

    Seeta2Api() {
    }

    public synchronized boolean isInited() {
        return mInstanceId != 0;
    }

    public synchronized void init(String fdPath, String pdPath) {
        if (mInstanceId == 0) {
            mInstanceId = nInitSeeta(fdPath, pdPath);
        }
    }

    public synchronized void destory() {
        if (mInstanceId != 0) {
            nDestory(mInstanceId);
            mInstanceId = 0;
        }
    }

    public Rect[] detectFaceRect(Bitmap bitmap) {
        if (mInstanceId != 0) {
            int[] ret = nSeetaDetect(mInstanceId, bitmap);
            return MorphUtils.intArray2RectArray(ret);
        }
        return new Rect[0];
    }

    public PointF[] detectLandmarks(Bitmap bitmap) {
        return detectLandmarks(bitmap, false);
    }

    public PointF[] detectLandmarks(Bitmap bitmap, boolean withCorners) {
        if (mInstanceId != 0) {
            float[] floats = nSeetaLandmarks(mInstanceId, bitmap, withCorners);
            return MorphUtils.floatArray2PointFArray(floats);
        }
        return new PointF[0];
    }

    private static native float[] nSeetaLandmarks(long instance, Bitmap bitmap, boolean withCorners);

    private static native int[] nSeetaDetect(long instance, Bitmap bitmap);

    private static native long nInitSeeta(String fdPath, String pdPath);

    private static native void nDestory(long instance);

    static {
        System.loadLibrary("morpher");
    }
}

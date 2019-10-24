package com.hzy.face.morphme.worker;

import android.graphics.Bitmap;

import com.blankj.utilcode.util.ImageUtils;
import com.hzy.face.morpher.MorpherApi;
import com.hzy.face.morphme.bean.FaceImage;

import java.util.List;

public class MultiMorphWorker implements Runnable {

    private volatile boolean mMorphRunning;
    private List<FaceImage> mFaceImages;
    private Bitmap mOutputBitmap;
    private MorphCallback mCallback;
    private int mTransFrames = 5;

    public void setOutBitmap(Bitmap bitmap) {
        mOutputBitmap = bitmap;
    }

    public void setFaceImages(List<FaceImage> faceImages) {
        this.mFaceImages = faceImages;
    }

    public void setCallback(MorphCallback callback) {
        mCallback = callback;
    }

    public void setTransFrames(int count) {
        mTransFrames = count;
    }

    @Override
    public void run() {
        // at least 2 images
        if (mFaceImages != null && mFaceImages.size() > 1) {
            if (mCallback != null) {
                mCallback.onStart();
            }
            mMorphRunning = true;
            int listSize = mFaceImages.size();
            for (int i = 0; i < listSize; i++) {
                if (mMorphRunning) {
                    FaceImage img1 = mFaceImages.get(i);
                    // next image could be circular
                    FaceImage img2 = mFaceImages.get((i + 1) % listSize);
                    if (!morphOneTransform(img1, img2, i)) {
                        break;
                    }
                }
            }
            if (mMorphRunning) {
                if (mCallback != null) {
                    mCallback.onAbort();
                }
            }
        }
        if (mCallback != null) {
            mCallback.onFinish();
        }
    }

    private boolean morphOneTransform(FaceImage img1, FaceImage img2, int index) {
        // load bitmap in every round to avoid too much memory usage
        try {
            Bitmap bmp1 = ImageUtils.getBitmap(img1.path);
            Bitmap bmp2 = ImageUtils.getBitmap(img2.path);
            if (bmp1 != null && bmp2 != null && mOutputBitmap != null) {
                float alphaStep = 1.0f / mTransFrames;
                float alpha = 0f;
                while (alpha <= 1.0f && mMorphRunning) {
                    MorpherApi.morphToBitmap(bmp1, bmp2, mOutputBitmap, img1.points,
                            img2.points, img1.indices, alpha);
                    if (mCallback != null) {
                        mCallback.onOneFrame(mOutputBitmap, index, alpha);
                    }
                    alpha += alphaStep;
                }
                bmp1.recycle();
                bmp2.recycle();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void abort() {
        mMorphRunning = false;
    }
}

package com.hzy.face.morphme.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bilibili.burstlinker.BurstLinker;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.hzy.face.morpher.MorpherApi;
import com.hzy.face.morphme.R;
import com.hzy.face.morphme.bean.FaceImage;
import com.hzy.face.morphme.consts.RequestCode;
import com.hzy.face.morphme.consts.RouterHub;
import com.hzy.face.morphme.utils.ActionUtils;
import com.hzy.face.morphme.utils.CascadeUtils;
import com.hzy.face.morphme.utils.ConfigUtils;
import com.hzy.face.morphme.utils.FaceUtils;
import com.hzy.face.morphme.utils.SpaceUtils;
import com.hzy.face.morphme.widget.Ratio34ImageView;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = RouterHub.TWO_MORPH_ACTIVITY)
public class TwoMorphActivity extends AppCompatActivity {

    @BindView(R.id.imageview_first)
    Ratio34ImageView mImageviewFirst;
    @BindView(R.id.imageview_second)
    Ratio34ImageView mImageviewSecond;
    @BindView(R.id.imageview_output)
    Ratio34ImageView mImageviewOut;
    @BindView(R.id.alpha_text)
    TextView mAlphaText;
    @BindView(R.id.alpha_progress)
    ProgressBar mAlphaProgress;

    private BurstLinker mBurstLinker;
    private String mGifFilePath;
    private Point mImageSize;
    private float mFrameSpace;
    private int mFrameDelayMs;
    private int mGifQuantizer;
    private int mGifDitherer;
    private ExecutorService mFaceExecutor;
    private ProgressDialog mProgressDialog;
    private float mMorphAlpha = -1f;
    private volatile boolean mMorphRunning = false;
    private List<FaceImage> mFaceImages;
    private List<ImageView> mImageViews;
    private Bitmap mOutputBitmap;
    private String mSelectPath;
    private int mCurrentIndex;
    private int mDetectorIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initConfigurations();
        setContentView(R.layout.activity_two_morph);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mImageViews = new LinkedList<>();
        mImageViews.add(mImageviewFirst);
        mImageViews.add(mImageviewSecond);
        mOutputBitmap = Bitmap.createBitmap(mImageSize.x, mImageSize.y, Bitmap.Config.ARGB_8888);
        mFaceExecutor = Executors.newSingleThreadExecutor();
        SpaceUtils.clearUsableSpace();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.loading_wait_tips));
        mProgressDialog.setCancelable(false);
        mFaceExecutor.submit(CascadeUtils::initSeetaApi);
    }

    private void initConfigurations() {
        mFaceImages = new LinkedList<>();
        mFaceImages.add(null);
        mFaceImages.add(null);
        mImageSize = ConfigUtils.getConfigResolution();
        int frames = ConfigUtils.getConfigFrameCount();
        mFrameSpace = (frames > 0) ? (1f / frames) : 0.1f;
        int duration = ConfigUtils.getConfigTransDuration();
        mFrameDelayMs = duration / frames;
        mGifQuantizer = ConfigUtils.getConfigGifQuantizer();
        mGifDitherer = ConfigUtils.getConfigGifDitherer();
        mDetectorIndex = ConfigUtils.getConfigDetector();
    }

    @Override
    protected void onPause() {
        mMorphRunning = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMorphRunning = false;
        mFaceExecutor.shutdownNow();
        mOutputBitmap.recycle();
        super.onDestroy();
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

    @OnClick({R.id.imageview_first,
            R.id.imageview_second,
            R.id.btn_start_morph,
            R.id.btn_save_gif})
    public void onViewClicked(View view) {
        mMorphRunning = false;
        switch (view.getId()) {
            case R.id.imageview_first:
                mCurrentIndex = 0;
                ActionUtils.startImageContentAction(this, RequestCode.CHOOSE_IMAGE);
                break;
            case R.id.imageview_second:
                mCurrentIndex = 1;
                ActionUtils.startImageContentAction(this, RequestCode.CHOOSE_IMAGE);
                break;
            case R.id.btn_start_morph:
                startMorphProcess(false);
                break;
            case R.id.btn_save_gif:
                startMorphProcess(true);
                break;
        }
    }

    private void snakeBarShow(String msg) {
        SnackbarUtils.with(mImageviewFirst).setMessage(msg).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // choose a image
        if (requestCode == RequestCode.CHOOSE_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri dataUri = data.getData();
                    if (dataUri != null) {
                        File imgFile = SpaceUtils.newUsableFile();
                        mSelectPath = imgFile.getPath();
                        UCrop.Options options = new UCrop.Options();
                        options.setCompressionQuality(100);
                        UCrop.of(dataUri, Uri.fromFile(imgFile))
                                .withOptions(options)
                                .withMaxResultSize(mImageSize.x, mImageSize.y)
                                .withAspectRatio(3, 4)
                                .start(this, RequestCode.CROP_IMAGE);
                    }
                }
            }
        } else if (requestCode == RequestCode.CROP_IMAGE) {
            // crop a image
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Glide.with(this).load(mSelectPath).into(mImageViews.get(mCurrentIndex));
                    startDetectFaceInfo();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startDetectFaceInfo() {
        mProgressDialog.show();
        mFaceExecutor.submit(() -> {
            FaceImage faceImage = FaceUtils.getFaceFromPath(
                    mSelectPath, mImageSize.x, mImageSize.y, mDetectorIndex);
            mFaceImages.set(mCurrentIndex, faceImage);
            runOnUiThread(() -> {
                mProgressDialog.dismiss();
                if (faceImage == null) {
                    mImageViews.get(mCurrentIndex).setImageResource(R.drawable.ic_head);
                    snakeBarShow(getString(R.string.no_face_detected));
                }
            });
        });
    }

    /**
     * Start To processing the images
     *
     * @param isSave if you want to save gif
     */
    private void startMorphProcess(boolean isSave) {
        if (mFaceImages.get(0) != null && mFaceImages.get(1) != null) {
            if (isSave) {
                snakeBarShow(getString(R.string.morphing_please_wait));
            }
            mAlphaProgress.setProgress(0);
            mFaceExecutor.submit(() -> morphToBitmapAsync(isSave));
        } else {
            snakeBarShow(getString(R.string.choose_images_first));
        }
    }

    private void morphToBitmapAsync(boolean needSave) {
        mMorphRunning = true;
        try {
            if (needSave) {
                mBurstLinker = new BurstLinker();
                mGifFilePath = SpaceUtils.newUsableFile().getPath();
                mBurstLinker.init(mOutputBitmap.getWidth(), mOutputBitmap.getHeight(),
                        mGifFilePath, BurstLinker.CPU_COUNT);
            }
            FaceImage face1 = mFaceImages.get(0);
            FaceImage face2 = mFaceImages.get(1);
            Bitmap bmp1 = FaceUtils.getBmpWithSize(face1.path, mImageSize.x, mImageSize.y);
            Bitmap bmp2 = FaceUtils.getBmpWithSize(face2.path, mImageSize.x, mImageSize.y);
            while (mMorphRunning) {
                float alpha = 1 - Math.abs(mMorphAlpha);
                MorpherApi.morphToBitmap(bmp1, bmp2, mOutputBitmap, face1.points,
                        face2.points, face1.indices, alpha);
                runOnUiThread(() -> {
                    mImageviewOut.setImageBitmap(mOutputBitmap);
                    mAlphaProgress.setProgress((int) (alpha * 100));
                    mAlphaText.setText(getString(R.string.alpha_format_text, alpha));
                });
                if (needSave) {
                    mBurstLinker.connect(mOutputBitmap, mGifQuantizer,
                            mGifDitherer, 0, 0, mFrameDelayMs);
                }
                mMorphAlpha += mFrameSpace;
                if (mMorphAlpha > 1) {
                    mMorphAlpha = -1;
                    if (needSave) {
                        mBurstLinker.release();
                        runOnUiThread(this::routerShareGifImage);
                        needSave = false;
                    }
                }
            }
            if (bmp1 != null) {
                bmp1.recycle();
            }
            if (bmp2 != null) {
                bmp2.recycle();
            }
            mMorphAlpha = -1f;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void routerShareGifImage() {
        if (!StringUtils.isTrimEmpty(mGifFilePath)) {
            ARouter.getInstance().build(RouterHub.GIF_RESULT_ACTIVITY)
                    .withString(GifResultActivity.EXTRA_FILE_PATH, mGifFilePath)
                    .navigation(this);
        }
    }
}

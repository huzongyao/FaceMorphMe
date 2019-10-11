package com.hzy.face.morphme.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
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
import com.bilibili.burstlinker.BurstLinker;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.IntentUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hzy.face.morpher.MorpherApi;
import com.hzy.face.morphme.R;
import com.hzy.face.morphme.consts.RequestCode;
import com.hzy.face.morphme.consts.RouterHub;
import com.hzy.face.morphme.utils.ActionUtils;
import com.hzy.face.morphme.utils.CascadeUtils;
import com.hzy.face.morphme.utils.ConfigUtils;
import com.hzy.face.morphme.utils.SpaceUtils;
import com.hzy.face.morphme.widget.Ratio34ImageView;
import com.yalantis.ucrop.UCrop;

import java.io.File;
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
    @BindView(R.id.image_progress_first)
    ProgressBar mProgressFirst;
    @BindView(R.id.image_progress_second)
    ProgressBar mProgressSecond;
    @BindView(R.id.imageview_output)
    Ratio34ImageView mImageviewOut;
    @BindView(R.id.alpha_text)
    TextView mAlphaText;

    private FaceImage[] mFaceImages;
    private FaceImage mSelectedImage;
    private ExecutorService mFaceExecutor;
    private ProgressDialog mProgressDialog;
    private Bitmap mOutputBitmap;
    private float mMorphAlpha = -1f;
    private volatile boolean mMorphRunning = false;
    private BurstLinker mBurstLinker;
    private String mGifFilePath;
    private Point mImageSize;
    private float mFrameSpace;
    private int mFrameDuration;

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
        mFaceExecutor = Executors.newSingleThreadExecutor();
        SpaceUtils.clearUsableSpace();
        mFaceImages = new FaceImage[]{
                new FaceImage(mImageviewFirst, mProgressFirst),
                new FaceImage(mImageviewSecond, mProgressSecond),
        };
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.loading_wait_tips));
        mProgressDialog.setCancelable(false);
        mOutputBitmap = Bitmap.createBitmap(mImageSize.x, mImageSize.y, Bitmap.Config.ARGB_8888);
    }

    private void initConfigurations() {
        mImageSize = ConfigUtils.getConfigResolution();
        int frames = ConfigUtils.getConfigFrameCount();
        mFrameSpace = (frames > 0) ? (1f / frames) : 0.1f;
        int duration = ConfigUtils.getConfigDuration();
        mFrameDuration = duration / frames;
    }

    @Override
    protected void onDestroy() {
        mMorphRunning = false;
        mFaceExecutor.shutdownNow();
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
                mSelectedImage = mFaceImages[0];
                ActionUtils.startImageContentAction(this, RequestCode.CHOOSE_IMAGE);
                break;
            case R.id.imageview_second:
                mSelectedImage = mFaceImages[1];
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
                        mSelectedImage.imageFile = imgFile;
                        UCrop.Options options = new UCrop.Options();
                        options.setCompressionQuality(100);
                        options.setMaxBitmapSize(16000);
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
                    Bitmap bitmap = ImageUtils.getBitmap(mSelectedImage.imageFile);
                    if (mSelectedImage.bitmap != null && !mSelectedImage.bitmap.isRecycled()) {
                        mSelectedImage.bitmap.recycle();
                    }
                    mSelectedImage.bitmap = bitmap;
                    mSelectedImage.imageView.setImageBitmap(bitmap);
                    startDetectFaceInfo();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startDetectFaceInfo() {
        mProgressDialog.show();
        mSelectedImage.prepared = false;
        mFaceExecutor.submit(() -> {
            Bitmap bitmap = mSelectedImage.bitmap;
            String cascadePath = CascadeUtils.ensureCascadePath();
            PointF[] points = MorpherApi.detectFaceLandmarks(bitmap, cascadePath, true);
            if (points != null && points.length > 0) {
                mSelectedImage.points = points;
                int[] indices = MorpherApi.getSubDivPointIndex(bitmap.getWidth(), bitmap.getHeight(), points);
                if (indices != null && indices.length > 0) {
                    mSelectedImage.indices = indices;
                    mSelectedImage.prepared = true;
                }
            }
            runOnUiThread(() -> {
                mProgressDialog.dismiss();
                if (!mSelectedImage.prepared) {
                    mSelectedImage.imageView.setImageResource(R.drawable.ic_head);
                    snakeBarShow(getString(R.string.no_face_detected));
                }
            });
        });
    }

    private void startMorphProcess(boolean isSave) {
        if (!mFaceImages[0].prepared || !mFaceImages[1].prepared) {
            ToastUtils.showShort(R.string.choose_images_first);
            return;
        }
        mFaceExecutor.submit(() -> morphToBitmapAsync(isSave));
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
            while (mMorphRunning) {
                float alpha = 1 - Math.abs(mMorphAlpha);
                MorpherApi.morphToBitmap(mFaceImages[0].bitmap,
                        mFaceImages[1].bitmap, mOutputBitmap,
                        mFaceImages[0].points, mFaceImages[1].points,
                        mFaceImages[0].indices, alpha);
                runOnUiThread(() -> {
                    mImageviewOut.setImageBitmap(mOutputBitmap);
                    mAlphaText.setText(getString(R.string.alpha_format_text, alpha));
                });
                if (needSave) {
                    mBurstLinker.connect(mOutputBitmap, BurstLinker.KMEANS_QUANTIZER,
                            BurstLinker.M2_DITHER, 0, 0, mFrameDuration);
                }
                mMorphAlpha += mFrameSpace;
                if (mMorphAlpha > 1) {
                    mMorphAlpha = -1;
                    if (needSave) {
                        mBurstLinker.release();
                        runOnUiThread(this::shareGifImage);
                        needSave = false;
                    }
                }
            }
            mMorphAlpha = -1f;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shareGifImage() {
        if (!StringUtils.isTrimEmpty(mGifFilePath)) {
            Intent intent = IntentUtils.getShareImageIntent("", mGifFilePath);
            startActivity(Intent.createChooser(intent, getString(R.string.share_to)));
        }
    }

    class FaceImage {
        File imageFile;
        Bitmap bitmap;
        ImageView imageView;
        ProgressBar progress;
        PointF[] points;
        int[] indices;
        boolean prepared;

        FaceImage(ImageView imageView, ProgressBar progress) {
            this.imageView = imageView;
            this.progress = progress;
        }
    }
}

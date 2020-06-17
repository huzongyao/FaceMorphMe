package com.hzy.face.morphme.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.bumptech.glide.Glide;
import com.hzy.face.morpher.MorpherApi;
import com.hzy.face.morpher.Seeta2Api;
import com.hzy.face.morphme.R;
import com.hzy.face.morphme.consts.RequestCode;
import com.hzy.face.morphme.consts.RouterHub;
import com.hzy.face.morphme.utils.ActionUtils;
import com.hzy.face.morphme.utils.BitmapDrawUtils;
import com.hzy.face.morphme.utils.ModelFileUtils;
import com.hzy.face.morphme.utils.SpaceUtils;
import com.hzy.face.morphme.widget.Ratio34ImageView;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = RouterHub.FACE_DETECT_ACTIVITY)
public class FaceDetectActivity extends AppCompatActivity {

    @BindView(R.id.demo_image)
    Ratio34ImageView mDemoImage;
    private Bitmap mDemoBitmap;
    private ProgressDialog mProgressDialog;
    private String mSelectPath;
    private ExecutorService mExecutor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detect);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.loading_wait_tips));
        mProgressDialog.setCancelable(false);
        mExecutor = Executors.newSingleThreadExecutor();
        mExecutor.submit(ModelFileUtils::initSeetaApi);
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

    @OnClick({R.id.demo_image, R.id.btn_detect_face,
            R.id.btn_seeta_detect, R.id.btn_seeta_landmarks})
    public void onButtonsClicked(View view) {
        switch (view.getId()) {
            case R.id.demo_image:
                ActionUtils.startImageContentAction(this, RequestCode.CHOOSE_IMAGE);
                break;
            case R.id.btn_detect_face:
                detectFromBitmap(2);
                break;
            case R.id.btn_seeta_detect:
                detectFromBitmap(3);
                break;
            case R.id.btn_seeta_landmarks:
                detectFromBitmap(4);
                break;
        }
    }


    private void detectFromBitmap(int type) {
        mDemoBitmap = ImageUtils.getBitmap(mSelectPath);
        if (mDemoBitmap != null && !mDemoBitmap.isRecycled()) {
            mProgressDialog.show();
            mDemoImage.setImageBitmap(mDemoBitmap);
            mExecutor.submit(() -> doDetectAsync(type));
        } else {
            snakeBarShow(getString(R.string.choose_images_first));
        }
    }

    private void doDetectAsync(int type) {
        final Bitmap bitmap = mDemoBitmap.copy(Bitmap.Config.ARGB_8888, true);
        switch (type) {
            case 2:
                Rect[] faces = MorpherApi.detectFaceRect(bitmap, ModelFileUtils.getCascadeFacePath());
                BitmapDrawUtils.drawRectsOnBitmap(bitmap, faces);
                break;
            case 3:
                faces = Seeta2Api.INSTANCE.detectFaceRect(bitmap);
                BitmapDrawUtils.drawRectsOnBitmap(bitmap, faces);
                break;
            case 4:
                PointF[] points = Seeta2Api.INSTANCE.detectLandmarks(bitmap);
                BitmapDrawUtils.drawPointsOnBitmap(bitmap, points);
                break;
        }
        runOnUiThread(() -> {
            mDemoImage.setImageBitmap(bitmap);
            mProgressDialog.dismiss();
            snakeBarShow(getString(R.string.operation_finished));
        });
    }

    private void snakeBarShow(String msg) {
        SnackbarUtils.with(mDemoImage).setMessage(msg).show();
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
                                .withMaxResultSize(360, 480)
                                .withAspectRatio(3, 4)
                                .start(this, RequestCode.CROP_IMAGE);
                    }
                }
            }
        } else if (requestCode == RequestCode.CROP_IMAGE) {
            // crop a image
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Glide.with(this).load(mSelectPath).into(mDemoImage);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

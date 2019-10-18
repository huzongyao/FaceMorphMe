package com.hzy.face.morphme.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.bumptech.glide.Glide;
import com.hzy.face.morpher.MorpherApi;
import com.hzy.face.morphme.R;
import com.hzy.face.morphme.consts.RequestCode;
import com.hzy.face.morphme.consts.RouterHub;
import com.hzy.face.morphme.utils.ActionUtils;
import com.hzy.face.morphme.utils.BitmapDrawUtils;
import com.hzy.face.morphme.utils.CascadeUtils;
import com.hzy.face.morphme.utils.SpaceUtils;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = RouterHub.FACE_DETECT_ACTIVITY)
public class FaceDetectActivity extends AppCompatActivity {

    @BindView(R.id.demo_image)
    ImageView mImageView;
    @BindView(R.id.type_select_spinner)
    Spinner mTypeSelectSpinner;

    private Bitmap mDemoBitmap;
    private int mSelectTypeIndex;
    private ProgressDialog mProgressDialog;
    private String mSelectPath;

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
        mTypeSelectSpinner.setOnItemSelectedListener(getTypeSelectListener());
    }

    private AdapterView.OnItemSelectedListener getTypeSelectListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectTypeIndex = i;
                detectFromBitmap();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
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

    @OnClick({R.id.btn_load_img,
            R.id.btn_detect_face})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_load_img:
                ActionUtils.startImageContentAction(this, RequestCode.CHOOSE_IMAGE);
                break;
            case R.id.btn_detect_face:
                detectFromBitmap();
                break;
        }
    }

    private void detectFromBitmap() {
        mDemoBitmap = ImageUtils.getBitmap(mSelectPath);
        if (mDemoBitmap != null && !mDemoBitmap.isRecycled()) {
            mProgressDialog.show();
            mImageView.setImageBitmap(mDemoBitmap);
            new Thread() {
                @Override
                public void run() {
                    doDetectAsync();
                }
            }.start();
        }
    }

    private void doDetectAsync() {
        String cascadePath = CascadeUtils.ensureCascadePath();
        final Bitmap bitmap = mDemoBitmap.copy(Bitmap.Config.ARGB_8888, true);
        switch (mSelectTypeIndex) {
            case 0:
                PointF[] points = MorpherApi.detectFaceLandmarks(bitmap, cascadePath);
                BitmapDrawUtils.drawPointsOnBitmap(bitmap, points);
                break;
            case 1:
                points = MorpherApi.getFaceSubDiv(bitmap, cascadePath);
                BitmapDrawUtils.drawTrianglesOnBitmap(bitmap, points);
                BitmapDrawUtils.drawPointsOnBitmap(bitmap, points);
                break;
            case 2:
                RectF[] faces = MorpherApi.detectFaceRect(bitmap, CascadeUtils.getCascadeFacePath());
                BitmapDrawUtils.drawRectsOnBitmap(bitmap, faces);
                break;
        }
        mImageView.post(() -> {
            mImageView.setImageBitmap(bitmap);
            mProgressDialog.dismiss();
            snakeBarShow("Face Detect Finished!!");
        });
    }

    private void snakeBarShow(String msg) {
        SnackbarUtils.with(mImageView).setMessage(msg).show();
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
                    Glide.with(this).load(mSelectPath).into(mImageView);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

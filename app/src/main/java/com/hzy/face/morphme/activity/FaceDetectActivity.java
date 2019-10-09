package com.hzy.face.morphme.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
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
import com.hzy.face.morpher.MorpherApi;
import com.hzy.face.morphme.R;
import com.hzy.face.morphme.consts.AppConfigs;
import com.hzy.face.morphme.consts.RequestCode;
import com.hzy.face.morphme.consts.RouterHub;
import com.hzy.face.morphme.utils.ActionUtils;
import com.hzy.face.morphme.utils.BitmapDrawUtils;
import com.hzy.face.morphme.utils.CascadeUtils;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detect);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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

    @OnClick({R.id.btn_load_img, R.id.btn_detect_face})
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
        if (mDemoBitmap != null && !mDemoBitmap.isRecycled()) {
            mImageView.setImageBitmap(mDemoBitmap);
            snakeBarShow("Detecting... Please Wait!!");
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
            snakeBarShow("Face Detect Finished!!");
        });
    }

    private void snakeBarShow(String msg) {
        SnackbarUtils.with(mImageView).setMessage(msg).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RequestCode.CHOOSE_IMAGE) {
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = ActionUtils.getBitmapFromPickerIntent(data, getContentResolver());
                if (bitmap != null) {
                    if (mDemoBitmap != null) {
                        mDemoBitmap.recycle();
                    }
                    mDemoBitmap = ImageUtils.compressBySampleSize(bitmap,
                            AppConfigs.MAX_BITMAP_SIZE, AppConfigs.MAX_BITMAP_SIZE, true);
                    mImageView.setImageBitmap(mDemoBitmap);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

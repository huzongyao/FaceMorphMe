package com.hzy.face.morphme.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.hzy.face.morphme.R;
import com.hzy.face.morphme.adapter.ImageGridAdapter;
import com.hzy.face.morphme.bean.FaceImage;
import com.hzy.face.morphme.consts.RequestCode;
import com.hzy.face.morphme.consts.RouterHub;
import com.hzy.face.morphme.event.ItemDragEvent;
import com.hzy.face.morphme.utils.ActionUtils;
import com.hzy.face.morphme.utils.ConfigUtils;
import com.hzy.face.morphme.utils.FaceUtils;
import com.hzy.face.morphme.utils.SpaceUtils;
import com.hzy.face.morphme.widget.Ratio34ImageView;
import com.hzy.face.morphme.widget.recycler.ItemClickListener;
import com.hzy.face.morphme.widget.recycler.ItemTouchListener;
import com.hzy.face.morphme.worker.MP4OutputWorker;
import com.hzy.face.morphme.worker.MorphCallback;
import com.hzy.face.morphme.worker.MultiMorphWorker;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = RouterHub.VIDEO_MORPH_ACTIVITY)
public class MorphVideoActivity extends AppCompatActivity {

    @BindView(R.id.src_image_list)
    RecyclerView mSourceRecyclerList;
    @BindView(R.id.delete_area)
    ImageView mDeleteArea;
    Ratio34ImageView mDialogImageView;

    private ImageGridAdapter mSourceAdapter;
    private ItemTouchListener mSourceListener;
    private ImageGridAdapter.ViewHolder mSelectedHolder;
    private Point mImageSize;
    private File mSelectImageFile;
    private ProgressDialog mProgressDialog;
    private List<FaceImage> mDataList;
    private ExecutorService mFaceExecutor;
    private Dialog mImageDialog;
    private Bitmap mOutputBitmap;
    private MultiMorphWorker mMorphWorker;
    private int mTransFrameCount;
    private boolean mMorphSave;
    private String mOutputPath;
    private MP4OutputWorker mVideoWorker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_morph_video);
        ButterKnife.bind(this);
        initConfigurations();
        setupUILayout();
        EventBus.getDefault().register(this);
        SpaceUtils.clearUsableSpace();
        prepareMorphData();
    }

    private void initConfigurations() {
        mDataList = new ArrayList<>();
        mFaceExecutor = Executors.newSingleThreadExecutor();
        mImageSize = ConfigUtils.getConfigResolution();
        mTransFrameCount = ConfigUtils.getConfigFrameCount();
    }

    private void prepareMorphData() {
        mOutputBitmap = Bitmap.createBitmap(mImageSize.x, mImageSize.y, Bitmap.Config.ARGB_8888);
        mMorphWorker = new MultiMorphWorker();
        mMorphWorker.setOutBitmap(mOutputBitmap);
        mMorphWorker.setTransFrames(mTransFrameCount);
        mMorphWorker.setCallback(new MorphCallback() {
            @Override
            protected void onStart() {
                if (mMorphSave) {
                    mOutputPath = SpaceUtils.newUsableFile().getPath();
                    mVideoWorker = new MP4OutputWorker(mOutputPath, mImageSize.x, mImageSize.y);
                    mVideoWorker.start();
                }
            }

            @Override
            protected void onOneFrame(Bitmap bitmap) {
                if (mMorphSave) {
                    mVideoWorker.queenFrame(bitmap);
                }
                runOnUiThread(() -> mDialogImageView.setImageBitmap(bitmap));
            }

            @Override
            protected void onAbort() {
                if (mMorphSave) {
                    mVideoWorker.release();
                }
            }

            @Override
            protected void onFinish() {
                if (mMorphSave) {
                    mVideoWorker.release();
                    mDeleteArea.postDelayed(() -> {
                        mImageDialog.dismiss();
                        routerShareVideoPage();
                    }, 2000);
                }
            }
        });
    }

    private void setupUILayout() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mSourceRecyclerList.setLayoutManager(new GridLayoutManager(this, 3));
        mSourceAdapter = new ImageGridAdapter(mDataList);
        mSourceListener = new ItemTouchListener(mSourceRecyclerList, mSourceAdapter);
        mSourceListener.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder) {
                mSelectedHolder = (ImageGridAdapter.ViewHolder) holder;
                ActionUtils.startImageContentAction(MorphVideoActivity.this, RequestCode.CHOOSE_IMAGE);
            }

            @Override
            public void onLongClick(RecyclerView.ViewHolder holder) {
            }
        });
        mSourceRecyclerList.addOnItemTouchListener(mSourceListener);
        mSourceRecyclerList.setAdapter(mSourceAdapter);
        initPageDialogs();
        snakeBarShow(getString(R.string.image_long_touch_tips));
    }

    private void initPageDialogs() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.loading_wait_tips));
        mProgressDialog.setCancelable(false);
        mImageDialog = new Dialog(this);
        mImageDialog.setCancelable(false);
        mImageDialog.setContentView(R.layout.dialog_image_preview);
        mDialogImageView = mImageDialog.findViewById(R.id.dialog_image_view);
        mImageDialog.findViewById(R.id.dialog_btn_cancel)
                .setOnClickListener(view -> {
                    mMorphWorker.abort();
                    mImageDialog.dismiss();
                });
    }

    private void routerShareVideoPage() {
        if (!StringUtils.isTrimEmpty(mOutputPath)) {
            ARouter.getInstance().build(RouterHub.VIDEO_RESULT_ACTIVITY)
                    .withString(VideoResultActivity.EXTRA_FILE_PATH, mOutputPath)
                    .navigation(this);
        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onImageItemDrag(ItemDragEvent e) {
        mDeleteArea.setVisibility(e.dragging ? View.VISIBLE : View.GONE);
    }

    @OnClick({R.id.btn_start_morph,
            R.id.btn_save_video})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start_morph:
                morphFaceImages(false);
                break;
            case R.id.btn_save_video:
                morphFaceImages(true);
                break;
        }
    }

    private void morphFaceImages(boolean isSave) {
        if (mDataList.size() >= 2) {
            Glide.with(this).load(mDataList.get(0).path).into(mDialogImageView);
            mImageDialog.show();
            mMorphWorker.setFaceImages(mDataList);
            mMorphSave = isSave;
            mFaceExecutor.submit(mMorphWorker);
        } else {
            snakeBarShow(getString(R.string.select_2images_tips));
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        mFaceExecutor.shutdownNow();
        mOutputBitmap.recycle();
        super.onDestroy();
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
                        mSelectImageFile = imgFile;
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
                    detectAndAddFace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void detectAndAddFace() {
        mProgressDialog.show();
        mFaceExecutor.submit(() -> {
            FaceImage faceImage = FaceUtils.getFaceFromPath(mSelectImageFile.getPath());
            runOnUiThread(() -> {
                mProgressDialog.dismiss();
                if (faceImage == null) {
                    snakeBarShow(getString(R.string.no_face_detected));
                } else {
                    int position = mSelectedHolder.getAdapterPosition();
                    if (position < mDataList.size()) {
                        mDataList.set(position, faceImage);
                        mSourceAdapter.notifyItemChanged(position);
                    } else {
                        mDataList.add(faceImage);
                        mSourceAdapter.notifyItemInserted(mDataList.size() - 1);
                    }
                }
            });
        });
    }

    private void snakeBarShow(String msg) {
        SnackbarUtils.with(mDeleteArea).setMessage(msg).show();
    }
}

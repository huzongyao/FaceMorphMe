package com.hzy.face.morphme.utils;

import android.annotation.SuppressLint;
import android.os.Environment;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.Utils;
import com.hzy.face.morphme.consts.AppConst;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class SpaceUtils {

    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

    public static final int SAVE_TYPE_GIF = 0x11;
    public static final int SAVE_TYPE_MP4 = 0x12;

    public static void savePublicMediaCopy(String src, int type, SaveMediaCallback cb) {
        PermissionUtils.permission(PermissionConstants.STORAGE)
                .callback(new PermissionUtils.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        try {
                            File dstFile = null;
                            if (type == SAVE_TYPE_GIF) {
                                dstFile = newPublicGifFile();
                            } else if (type == SAVE_TYPE_MP4) {
                                dstFile = newPublicMp4File();
                            }
                            FileUtils.copy(src, dstFile.getPath());
                            if (cb != null) {
                                cb.onSuccess(dstFile);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onDenied() {
                        if (cb != null) {
                            cb.onFail();
                        }
                    }
                }).request();
    }

    public static File newPublicGifFile() {
        return newPublicFile(Environment.DIRECTORY_DCIM, ".gif");
    }

    public static File newPublicMp4File() {
        return newPublicFile(Environment.DIRECTORY_DCIM, ".mp4");
    }

    private static File newPublicFile(String type, String ext) {
        try {
            File pictureDir = Environment.getExternalStoragePublicDirectory(type);
            String fileName = "morph-" + df.format(new Date()) + ext;
            return new File(pictureDir, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * new some file with random file name
     *
     * @return file object
     */
    public static File newUsableFile() {
        File fileDir = getUsableFilePath();
        if (fileDir.exists()) {
            String randomName = UUID.randomUUID().toString();
            return new File(fileDir, randomName);
        }
        return null;
    }

    public static void clearUsableSpace() {
        File spacePath = getUsableFilePath();
        if (spacePath.exists()) {
            FileUtils.deleteAllInDir(spacePath);
        }
    }

    /**
     * get some available path to store files
     *
     * @return some path
     */
    public static File getUsableFilePath() {
        File fileDir = null;
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                fileDir = Utils.getApp().getExternalFilesDir(AppConst.WORKSPACE_DIR);
            }
            if (fileDir == null) {
                fileDir = new File(Utils.getApp().getFilesDir(), AppConst.WORKSPACE_DIR);
            }
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileDir;
    }
}

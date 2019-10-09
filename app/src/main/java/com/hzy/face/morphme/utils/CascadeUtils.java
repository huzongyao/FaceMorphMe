package com.hzy.face.morphme.utils;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.Utils;
import com.hzy.face.morphme.consts.AppConfigs;

import java.io.File;
import java.io.InputStream;

public class CascadeUtils {

    public static String getCascadePath() {
        return new File(Utils.getApp().getFilesDir(), AppConfigs.CASCADE_DIR).getPath();
    }

    public static String getCascadeFacePath() {
        String dirPath = getCascadePath();
        return new File(dirPath, AppConfigs.CASCADE_FACE_FILE).getPath();
    }

    /**
     * Prepare cascade files and return the cascade path
     *
     * @return the cascade path for face recogn
     */
    public static String ensureCascadePath() {
        try {
            String[] fileNames = Utils.getApp().getAssets().list(AppConfigs.CASCADE_DIR);
            File cascadeDir = new File(Utils.getApp().getFilesDir(), AppConfigs.CASCADE_DIR);
            if (!cascadeDir.exists()) {
                cascadeDir.mkdirs();
            }
            if (fileNames != null) {
                for (String fileName : fileNames) {
                    File outFile = new File(cascadeDir, fileName);
                    if (!outFile.exists()) {
                        InputStream is = Utils.getApp().getAssets()
                                .open(AppConfigs.CASCADE_DIR + File.separator + fileName);
                        FileIOUtils.writeFileFromIS(outFile, is);
                    }
                }
            }
            return cascadeDir.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

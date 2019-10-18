package com.hzy.face.morphme.utils;

import android.graphics.Bitmap;
import android.graphics.PointF;

import com.blankj.utilcode.util.ImageUtils;
import com.hzy.face.morpher.MorpherApi;
import com.hzy.face.morphme.bean.FaceImage;

public class FaceUtils {

    public static FaceImage getFaceFromPath(String path) {
        try {
            Bitmap bitmap = ImageUtils.getBitmap(path);
            String cascadePath = CascadeUtils.ensureCascadePath();
            PointF[] points = MorpherApi.detectFaceLandmarks(bitmap, cascadePath, true);
            if (points != null && points.length > 0) {
                int[] indices = MorpherApi.getSubDivPointIndex(bitmap.getWidth(),
                        bitmap.getHeight(), points);
                if (indices != null && indices.length > 0) {
                    return new FaceImage(path, points, indices);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

package com.hzy.face.morphme.bean;

import android.graphics.PointF;

public class FaceImage {
    public String path;
    public PointF[] points;
    public int[] indices;

    public FaceImage() {
    }

    public FaceImage(String path, PointF[] points, int[] indices) {
        this.path = path;
        this.points = points;
        this.indices = indices;
    }
}

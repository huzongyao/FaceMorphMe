package com.hzy.face.morpher;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;

public class MorpherApi {

    public static final boolean NATIVE_POINTF = false;

    public static RectF[] detectFaceRect(Bitmap bitmap, String classifierPath) {
        float[] floats = nDetectFaceRect(bitmap, classifierPath);
        return MorphUtils.floatArray2RectFArray(floats);
    }

    public static PointF[] detectFaceLandmarks(Bitmap bitmap, String classifierPath) {
        return detectFaceLandmarks(bitmap, classifierPath, false);
    }

    public static PointF[] detectFaceLandmarks(Bitmap bitmap, String classifierPath, boolean withCorners) {
        PointF[] points;
        if (NATIVE_POINTF) {
            points = nDetectFace(bitmap, "stub", classifierPath);
        } else {
            float[] numbers = nDetectFaceFArray(bitmap, "stub", classifierPath, withCorners);
            points = MorphUtils.floatArray2PointFArray(numbers);
        }
        return points;
    }

    public static PointF[] getFaceSubDiv(Bitmap bitmap, String classifierPath) {
        PointF[] points;
        if (NATIVE_POINTF) {
            points = nGetFaceSubDiv(bitmap, "stub", classifierPath);
        } else {
            float[] numbers = nGetFaceSubDivFArray(bitmap, "stub", classifierPath);
            points = MorphUtils.floatArray2PointFArray(numbers);
        }
        return points;
    }

    public static int[] getSubDivPointIndex(int width, int height, PointF[] points) {
        float[] inArray = MorphUtils.pointFArray2FloatArray(points);
        return nGetSubDivPointIndex(width, height, inArray);
    }

    public static int morphToBitmap(Bitmap src, Bitmap dst, Bitmap morph, PointF[] pSrc,
                                    PointF[] pDst, int[] indices, float alpha) {
        return nMorphToBitmap(src, dst, morph, MorphUtils.pointFArray2FloatArray(pSrc),
                MorphUtils.pointFArray2FloatArray(pDst), indices, alpha);
    }

    public static native String getOpenCvVersionString();

    public static native String getStasmVersionString();

    private static native int nMorphToBitmap(Bitmap src, Bitmap dst, Bitmap morph, float[] pSrc, float[] pDst, int[] indices, float alpha);

    /**
     * detect faces and get face rect
     *
     * @param bitmap         bitmap
     * @param classifierPath classifier Path
     * @return faces rect [r1.left, r1.top, r1.right, r1.bottom, ...]
     */
    private static native float[] nDetectFaceRect(Bitmap bitmap, String classifierPath);

    /**
     * detect face key points
     *
     * @param bitmap         src bitmap
     * @param imgPath        some tag for log, not important
     * @param classifierPath classifier Path
     * @return key points
     */
    private static native PointF[] nDetectFace(Bitmap bitmap, String imgPath, String classifierPath);

    /**
     * @param bitmap         src bitmap
     * @param imgPath        some tag for log, not important
     * @param classifierPath classifier Path
     * @param withCorners    if you want photo corner points
     * @return [p1.x, p1.y, p2.x, p2.y, ...]
     */
    private static native float[] nDetectFaceFArray(Bitmap bitmap, String imgPath, String classifierPath, boolean withCorners);

    /**
     * Detect one face and sub div with the key points
     *
     * @param bitmap         src bitmap
     * @param imgPath        some tag for log, not important
     * @param classifierPath classifier Path
     * @return div points [tr1.p1.x, tr1.p1.y, tr1.p2.x, tr1.p2.y, tr1.p3.x, tr1.p3.y, ...]
     */
    private static native PointF[] nGetFaceSubDiv(Bitmap bitmap, String imgPath, String classifierPath);

    /**
     * Detect one face and sub div with the key points
     *
     * @param bitmap         src bitmap
     * @param imgPath        some tag for log, not important
     * @param classifierPath classifier Path
     * @return [p1.x, p1.y, p2.x, p2.y, ...]
     */
    private static native float[] nGetFaceSubDivFArray(Bitmap bitmap, String imgPath, String classifierPath);

    /**
     * * Return 8 key points of a rect
     * * * 0---1---2
     * * * |       |
     * * * 7       3
     * * * |       |
     * * * 6---5---4
     *
     * @param width  rect width
     * @param height rect height
     * @return [p1.x, p1.y, p2.x, p2.y, ...]
     */
    private static native float[] nGetRectCornersFArray(int width, int height);

    /**
     * get triangles points index list,
     * with the index, we can index the points[], and get the real coordinates,
     * and construct all the triangles
     *
     * @param width  image width
     * @param height image height
     * @param points points
     * @return triangles point index in points [tr1.p1.index, tr1.p2.index, tr1.p3.index, ...]
     */
    private static native int[] nGetSubDivPointIndex(int width, int height, float[] points);

    static {
        System.loadLibrary("morpher");
    }
}

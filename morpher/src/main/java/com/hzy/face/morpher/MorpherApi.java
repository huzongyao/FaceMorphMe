package com.hzy.face.morpher;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;

public class MorpherApi {

    public static final boolean NATIVE_POINTF = false;

    /**
     * detect faces from a bitmap
     *
     * @param bitmap         bitmap
     * @param classifierPath model file path
     * @return face rects, could be more than one
     */
    public static RectF[] detectFaceRect(Bitmap bitmap, String classifierPath) {
        float[] floats = nDetectFaceRect(bitmap, classifierPath);
        return MorphUtils.floatArray2RectFArray(floats);
    }

    /**
     * detect the face landmark points in a bitmap
     *
     * @param bitmap         bitmap
     * @param classifierPath classifier directory, ensure model files is there
     * @return points
     */
    public static PointF[] detectFaceLandmarks(Bitmap bitmap, String classifierPath) {
        return detectFaceLandmarks(bitmap, classifierPath, false);
    }

    /**
     * detect the face landmark points in a bitmap
     *
     * @param bitmap         bitmap
     * @param classifierPath classifier directory
     * @param withCorners    if you need bitmap border key points(for triangulation the whole image)
     * @return points
     */
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

    /**
     * detect the face landmark points in a bitmap like detectFaceLandmarks,
     * but the points are ordered with triangles that could divide the whole image
     *
     * @param bitmap         bitmap
     * @param classifierPath classifier directory
     * @return triangle vertexes [tr1.a, tr1.b, tr1.c, tr2.a, ...]
     */
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

    /**
     * Delaunay Triangulation with the size of the image, and the key points
     * with the indices and the points, we can reconstruct all the triangles
     *
     * @param width  width
     * @param height height
     * @param points key points
     * @return the indices of points
     */
    public static int[] getSubDivPointIndex(int width, int height, PointF[] points) {
        float[] inArray = MorphUtils.pointFArray2FloatArray(points);
        return nGetSubDivPointIndex(width, height, inArray);
    }

    /**
     * Morph with two bitmaps
     *
     * @param src     bitmap1
     * @param dst     bitmap2
     * @param morph   alpha bitmap
     * @param pSrc    key points 1
     * @param pDst    key points 2 with the same size of points 1
     * @param indices points indies, with the right index, we can construct the triangles
     * @param alpha   current alpha [0, 1]
     * @return status
     */
    public static int morphToBitmap(Bitmap src, Bitmap dst, Bitmap morph, PointF[] pSrc,
                                    PointF[] pDst, int[] indices, float alpha) {
        return nMorphToBitmap(src, dst, morph, MorphUtils.pointFArray2FloatArray(pSrc),
                MorphUtils.pointFArray2FloatArray(pDst), indices, alpha);
    }

    public static native String getOpenCvVersionString();

    public static native String getStasmVersionString();

    public static native String getLibYUVVersionString();

    /**
     * bitmap to yuv
     *
     * @param bitmap bitmap
     * @param yuv    out yuv
     * @param format android color format
     */
    public static native void bitmap2YUV(Bitmap bitmap, byte[] yuv, int format);

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

package com.hzy.face.morpher;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;

public class MorpherApi {

    /**
     * detect faces from a bitmap
     *
     * @param bitmap         bitmap
     * @param classifierPath model file path
     * @return face rects, could be more than one
     */
    public static Rect[] detectFaceRect(Bitmap bitmap, String classifierPath) {
        int[] floats = nDetectFaceRect(bitmap, classifierPath);
        return MorphUtils.intArray2RectArray(floats);
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
    private static native int[] nDetectFaceRect(Bitmap bitmap, String classifierPath);

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

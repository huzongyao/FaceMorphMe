package com.hzy.face.morpher;

import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

public class MorphUtils {

    public static float[] pointFArray2FloatArray(PointF[] points) {
        float[] inArray = new float[points.length * 2];
        for (int i = 0; i < points.length; i++) {
            inArray[i * 2] = points[i].x;
            inArray[i * 2 + 1] = points[i].y;
        }
        return inArray;
    }

    /**
     * construct PointF array by floats
     *
     * @param floats [p1.x, p1.y, p2.x, p2.y, ...]
     * @return PointF [p1, p2, p3, ...]
     */
    public static PointF[] floatArray2PointFArray(float[] floats) {
        int pointLength = floats.length / 2;
        PointF[] points = new PointF[pointLength];
        for (int i = 0; i < pointLength; i++) {
            PointF p = new PointF(floats[i * 2], floats[i * 2 + 1]);
            points[i] = p;
        }
        return points;
    }

    /**
     * construct RectFs by float array
     *
     * @param floats [r1.left, r1.top, r1.right, r1.bottom, ...]
     * @return RectFs [r1, r2, r3...]
     */
    public static RectF[] floatArray2RectFArray(float[] floats) {
        int rectCount = floats.length / 4;
        RectF[] ret = new RectF[rectCount];
        for (int i = 0; i < rectCount; i++) {
            RectF rect = new RectF(floats[i * 4], floats[i * 4 + 1],
                    floats[i * 4 + 2], floats[i * 4 + 3]);
            ret[i] = rect;
        }
        return ret;
    }

    /**
     * construct RectFs by float array
     *
     * @param ints [r1.left, r1.top, r1.right, r1.bottom, ...]
     * @return RectFs [r1, r2, r3...]
     */
    public static Rect[] intArray2RectArray(int[] ints) {
        int rectCount = ints.length / 4;
        Rect[] ret = new Rect[rectCount];
        for (int i = 0; i < rectCount; i++) {
            Rect rect = new Rect(ints[i * 4], ints[i * 4 + 1],
                    ints[i * 4 + 2], ints[i * 4 + 3]);
            ret[i] = rect;
        }
        return ret;
    }
}

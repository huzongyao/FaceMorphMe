package com.hzy.face.morphme.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

public class BitmapDrawUtils {

    public static void drawPointsOnBitmap(Bitmap bitmap, PointF[] points) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.GREEN);
        float pointSize = bitmap.getWidth() / 150.0f;
        for (PointF p : points) {
            canvas.drawCircle(p.x, p.y, pointSize, paint);
        }
    }

    public static void drawTrianglesWithIndices(Bitmap bitmap, PointF[] points, int[] indices) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.MAGENTA);
        int triangleLength = indices.length / 3;
        for (int i = 0; i < triangleLength; i++) {
            int p0 = i * 3;
            drawLineWithPoints(canvas, paint, points[indices[p0]], points[indices[p0 + 1]]);
            drawLineWithPoints(canvas, paint, points[indices[p0 + 1]], points[indices[p0 + 2]]);
            drawLineWithPoints(canvas, paint, points[indices[p0 + 2]], points[indices[p0]]);
        }
    }

    public static void drawTrianglesOnBitmap(Bitmap bitmap, PointF[] points) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.MAGENTA);
        int triangleLength = points.length / 3;
        for (int i = 0; i < triangleLength; i++) {
            int p0 = i * 3;
            drawLineWithPoints(canvas, paint, points[p0], points[p0 + 1]);
            drawLineWithPoints(canvas, paint, points[p0 + 1], points[p0 + 2]);
            drawLineWithPoints(canvas, paint, points[p0 + 2], points[p0]);
        }
    }

    static void drawLineWithPoints(Canvas canvas, Paint paint, PointF p1, PointF p2) {
        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
    }

    public static void drawRectsOnBitmap(Bitmap bitmap, Rect[] rects) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setAntiAlias(true);
        paint.setColor(Color.MAGENTA);
        for (Rect rect : rects) {
            canvas.drawRect(rect, paint);
        }
    }
}

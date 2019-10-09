//
// Created by huzongyao on 2019/9/17.
//

#include "NdkUtils.h"


void *lockAndroidBitmap(JNIEnv *env, jobject bitmap, AndroidBitmapInfo &info) {
    if (AndroidBitmap_getInfo(env, bitmap, &info) < 0) {
        LOGE("AndroidBitmap_getInfo Error!");
        return nullptr;
    }
    void *pixels = nullptr;
    if (AndroidBitmap_lockPixels(env, bitmap, &pixels) < 0) {
        LOGE("AndroidBitmap_lockPixels Error!");
        return nullptr;
    }
    return pixels;
}

Mat lockAndroidBitmapMat(JNIEnv *env, jobject bitmap) {
    AndroidBitmapInfo info;
    void *data = lockAndroidBitmap(env, bitmap, info);
    assert(data != nullptr);
    assert(info.format == ANDROID_BITMAP_FORMAT_RGBA_8888);
    return Mat(info.height, info.width, CV_8UC4, data);
}

jobjectArray point2fVector2APointFArray(JNIEnv *env, const std::vector<Point2f> &points) {
    jclass class_PointF = env->FindClass("android/graphics/PointF");
    jmethodID method_PointF = env->GetMethodID(class_PointF, "<init>", "(FF)V");
    assert(class_PointF != nullptr && method_PointF != nullptr);
    const size_t count = points.size();
    jobjectArray objArray = env->NewObjectArray(count, class_PointF, nullptr);
    for (size_t i = 0; i < count; ++i) {
        const Point2f &point = points[i];
        jobject object_point = env->NewObject(class_PointF, method_PointF, point.x, point.y);
        env->SetObjectArrayElement(objArray, i, object_point);
    }
    return objArray;
}

jfloatArray point2fVector2AFloatArray(JNIEnv *env, const std::vector<Point2f> &points) {
    const size_t count = points.size();
    jfloatArray ret = env->NewFloatArray(count * 2);
    auto buffer = new float[count * 2];
    for (size_t i = 0; i < count; ++i) {
        const Point2f &point = points[i];
        buffer[2 * i] = point.x;
        buffer[2 * i + 1] = point.y;
    }
    env->SetFloatArrayRegion(ret, 0, count * 2, buffer);
    delete buffer;
    return ret;
}

jfloatArray rectVector2AFloatArray(JNIEnv *env, const std::vector<Rect> &rects) {
    const size_t count = rects.size();
    jfloatArray ret = env->NewFloatArray(count * 4);
    auto buffer = new float[count * 4];
    for (size_t i = 0; i < count; ++i) {
        const Rect &rect = rects[i];
        buffer[2 * i] = rect.x;
        buffer[2 * i + 1] = rect.y;
        buffer[2 * i + 2] = rect.x + rect.width;
        buffer[2 * i + 3] = rect.y + rect.height;
    }
    env->SetFloatArrayRegion(ret, 0, count * 4, buffer);
    delete buffer;
    return ret;
}

jsize jFloatArray2point2fVector(JNIEnv *env, jfloatArray _floats, std::vector<Point2f> &points) {
    points.clear();
    jfloat *floats = env->GetFloatArrayElements(_floats, nullptr);
    jsize count = env->GetArrayLength(_floats) / 2;
    for (int i = 0; i < count; i++) {
        points.emplace_back(floats[2 * i], floats[2 * i + 1]);
    }
    env->ReleaseFloatArrayElements(_floats, floats, 0);
    return count;
}
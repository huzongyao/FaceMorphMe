//
// Created by huzongyao on 2019/9/17.
//

#ifndef FACEMORPHME_NDKUTILS_H
#define FACEMORPHME_NDKUTILS_H

#include <cv.h>

#ifdef __cplusplus
extern "C" {
#endif

#include <jni.h>
#include <android/bitmap.h>

#ifdef NDEBUG
#define LOGD(...) do{}while(0)
#define LOGI(...) do{}while(0)
#define LOGW(...) do{}while(0)
#define LOGE(...) do{}while(0)
#define LOGF(...) do{}while(0)
#else
#define LOG_TAG "NATIVE.LOG"

#include <android/log.h>

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,LOG_TAG,__VA_ARGS__)
#endif

using namespace cv;

/**
 * lock android bitmap and return data and write info
 * @param env
 * @param bitmap
 * @param info
 * @return data
 */
void *lockAndroidBitmap(JNIEnv *env, jobject bitmap, AndroidBitmapInfo &info);

/**
 * lock android bitmap and convert to RGBA8888 Mat, input must be RGBA8888 bitmap
 * @param env
 * @param bitmap
 * @return mat
 */
Mat lockAndroidBitmapMat(JNIEnv *env, jobject bitmap);

/**
 * convert points to android PointF array
 * @param env
 * @param points
 * @return
 */
jobjectArray point2fVector2APointFArray(JNIEnv *env, const std::vector<Point2f> &points);

/**
 * convert points to java float array
 * @param env
 * @param points
 * @return [x1, y1, x2, y2, ...]
 */
jfloatArray point2fVector2AFloatArray(JNIEnv *env, const std::vector<Point2f> &points);

/**
 * convert rects to java float array
 * @param env
 * @param rects
 * @return
 */
jfloatArray rectVector2AFloatArray(JNIEnv *env, const std::vector<Rect> &rects);

/**
 * convert jfloatArray to point2fVector
 * @param env
 * @param _floats
 * @param points
 * @return point count
 */
jsize jFloatArray2point2fVector(JNIEnv *env, jfloatArray _floats, std::vector<Point2f> &points);

#ifdef __cplusplus
}
#endif

#endif //FACEMORPHME_NDKUTILS_H

//
// Created by Administrator on 2019/9/13.
//

#include "MorpherApi.h"
#include <opencv2/opencv.hpp>
#include "MorphUtils.h"
#include "Subdiv2DIndex.h"
#include "ColorUtils.h"

using namespace cv;

JNIEXPORT jstring JNICALL
JNI_FUNC(getOpenCvVersionString)(JNIEnv *env, jclass type) {
    return env->NewStringUTF(CV_VERSION);
}

JNIEXPORT jstring JNICALL
JNI_FUNC(getLibYUVVersionString)(JNIEnv *env, jclass type) {
    return env->NewStringUTF(CVAUX_STR(LIBYUV_VERSION));
}

JNIEXPORT jintArray JNICALL
JNI_FUNC(nDetectFaceRect)(JNIEnv *env, jclass type, jobject bitmap, jstring classifierPath_) {
    const char *classifierPath = env->GetStringUTFChars(classifierPath_, nullptr);
    CascadeClassifier cascade(classifierPath);
    AndroidBitmapInfo info;
    Mat gray;
    void *pixels = lockAndroidBitmap(env, bitmap, info);
    assert(pixels != nullptr);
    Mat image(info.height, info.width, CV_8UC4, pixels);
    // convert to gray map
    cvtColor(image, gray, COLOR_RGBA2GRAY);
    AndroidBitmap_unlockPixels(env, bitmap);
    std::vector<Rect> faces;
    // detect
    cascade.detectMultiScale(gray, faces);
    env->ReleaseStringUTFChars(classifierPath_, classifierPath);
    auto intArray = rectVector2AIntArray(env, faces);
    return intArray;
}

JNIEXPORT jfloatArray JNICALL
JNI_FUNC(nGetRectCornersFArray)(JNIEnv *env, jclass type, jint width, jint height) {
    std::vector<Point2f> cornerPts;
    MorphUtils::getImageCornerPoints(width, height, cornerPts);
    jfloatArray floatArray = point2fVector2AFloatArray(env, cornerPts);
    return floatArray;
}

JNIEXPORT jintArray JNICALL
JNI_FUNC(nGetSubDivPointIndex)(JNIEnv *env, jclass type, jint width,
                               jint height, jfloatArray points_) {
    std::vector<Point2f> inPoints;
    jFloatArray2point2fVector(env, points_, inPoints);
    Rect rect(0, 0, width, height);
    Subdiv2DIndex subDiv(rect);
    for (const auto &point : inPoints) {
        if (point.inside(rect)) {
            subDiv.insert(point);
        }
    }
    std::vector<int> triangleIndices;
    subDiv.getTrianglesIndices(triangleIndices);
    int count = triangleIndices.size();
    jintArray ret = env->NewIntArray(count);
    env->SetIntArrayRegion(ret, 0, count, &triangleIndices[0]);
    return ret;
}

JNIEXPORT jint JNICALL
JNI_FUNC(nMorphToBitmap)(JNIEnv *env, jclass type, jobject src, jobject dst, jobject morph,
                         jfloatArray pSrc_, jfloatArray pDst_, jintArray indices_, jfloat alpha) {
    std::vector<Point2f> pSrc, pDst, pOut;
    jFloatArray2point2fVector(env, pSrc_, pSrc);
    jFloatArray2point2fVector(env, pDst_, pDst);
    MorphUtils::getPointsWithAlpha(pSrc, pDst, pOut, alpha);

    Mat srcMat = lockAndroidBitmapMat(env, src);
    Mat dstMat = lockAndroidBitmapMat(env, dst);
    Mat outMat = lockAndroidBitmapMat(env, morph);
    Mat outMat32;

    // operate with float32 for better color accuracy
    srcMat.convertTo(srcMat, CV_32F);
    dstMat.convertTo(dstMat, CV_32F);
    outMat.convertTo(outMat32, CV_32F);

    jint *indices = env->GetIntArrayElements(indices_, nullptr);
    jsize triangleCount = env->GetArrayLength(indices_) / 3;
    for (int i = 0; i < triangleCount; i++) {
        int x = indices[i * 3];
        int y = indices[i * 3 + 1];
        int z = indices[i * 3 + 2];

        std::vector<Point2f> t1, t2, t;
        // Triangle corners for image 1.
        t1.push_back(pSrc[x]);
        t1.push_back(pSrc[y]);
        t1.push_back(pSrc[z]);

        // Triangle corners for image 2.
        t2.push_back(pDst[x]);
        t2.push_back(pDst[y]);
        t2.push_back(pDst[z]);

        // Triangle corners for morphed image.
        t.push_back(pOut[x]);
        t.push_back(pOut[y]);
        t.push_back(pOut[z]);

        // Morph with every single triangle
        MorphUtils::morphTriangle(srcMat, dstMat, outMat32, t1, t2, t, alpha);
    }

    // write color map back to bitmap
    outMat32.convertTo(outMat, CV_8U);

    AndroidBitmap_unlockPixels(env, morph);
    AndroidBitmap_unlockPixels(env, dst);
    AndroidBitmap_unlockPixels(env, src);
    env->ReleaseIntArrayElements(indices_, indices, 0);
    return 0;
}

JNIEXPORT void JNICALL
JNI_FUNC(bitmap2YUV)(JNIEnv *env, jclass type, jobject bitmap, jbyteArray yuv_, jint format) {
    jbyte *yuv = env->GetByteArrayElements(yuv_, nullptr);
    AndroidBitmapInfo info;
    void *data = lockAndroidBitmap(env, bitmap, info);
    if (data == nullptr) {
        return;
    }
    if (info.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
        ColorUtils::rgba2YUV420((uint8_t *) data, (uint8_t *) yuv, info.width, info.height, format);
    } else if (info.format == ANDROID_BITMAP_FORMAT_RGB_565) {

    }
    env->ReleaseByteArrayElements(yuv_, yuv, 0);
    AndroidBitmap_unlockPixels(env, bitmap);
}
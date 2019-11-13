//
// Created by huzongyao on 2019/11/12.
//
#include "Seeta2Api.h"

JNIEXPORT jlong JNICALL
SEETA_JNI(nInitSeeta)(JNIEnv *env, jclass type, jstring fdPath_, jstring pdPath_) {
    const char *fdPath = env->GetStringUTFChars(fdPath_, nullptr);
    const char *pdPath = env->GetStringUTFChars(pdPath_, nullptr);
    LOGD("Create Detector:[%s, %s]", fdPath, pdPath);
    auto *detector = new SeetaDetector(fdPath, pdPath);
    env->ReleaseStringUTFChars(fdPath_, fdPath);
    env->ReleaseStringUTFChars(pdPath_, pdPath);
    return (jlong) detector;
}

JNIEXPORT void JNICALL
SEETA_JNI(nDestory)(JNIEnv *env, jclass type, jlong instance) {
    if (instance) {
        auto *detector = (SeetaDetector *) instance;
        delete detector;
    }
}

JNIEXPORT jintArray JNICALL
SEETA_JNI(nSeetaDetect)(JNIEnv *env, jclass type, jlong instance, jobject bitmap) {
    if (instance) {
        auto *detector = (SeetaDetector *) instance;
        AndroidBitmapInfo info;
        Mat rgb;
        void *pixels = lockAndroidBitmap(env, bitmap, info);
        assert(pixels != nullptr);
        Mat image(info.height, info.width, CV_8UC4, pixels);
        cvtColor(image, rgb, COLOR_RGBA2RGB);
        AndroidBitmap_unlockPixels(env, bitmap);
        SeetaImageData input = SeetaImageData();
        input.width = info.width;
        input.height = info.height;
        input.channels = rgb.channels();
        input.data = rgb.data;
        auto result = detector->detect(input);
        LOGI("Seeta Detected: [%d]", result.size);
        return seetaFaces2AIntArray(env, result);
    }
    return env->NewIntArray(0);
}

JNIEXPORT jfloatArray JNICALL
SEETA_JNI(nSeetaLandmarks)(JNIEnv *env, jclass type, jlong instance, jobject bitmap,
                           jboolean isCorner) {
    std::vector<SeetaPointF> result;
    if (instance) {
        auto *detector = (SeetaDetector *) instance;
        AndroidBitmapInfo info;
        Mat rgb;
        void *pixels = lockAndroidBitmap(env, bitmap, info);
        assert(pixels != nullptr);
        Mat image(info.height, info.width, CV_8UC4, pixels);
        cvtColor(image, rgb, COLOR_RGBA2RGB);
        AndroidBitmap_unlockPixels(env, bitmap);
        SeetaImageData input = SeetaImageData();
        input.width = info.width;
        input.height = info.height;
        input.channels = rgb.channels();
        input.data = rgb.data;
        result = detector->mark81(input);
        if (isCorner == JNI_TRUE && !result.empty()) {
            detector->pushCorners(input, result);
        }
    }
    jfloatArray pointFArray = seetaPoints2AFloats(env, result);
    return pointFArray;
}

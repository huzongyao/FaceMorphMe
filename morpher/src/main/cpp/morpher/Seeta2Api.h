//
// Created by huzongyao on 2019/11/12.
//

#ifndef FACEMORPHME_SEETA2API_H
#define FACEMORPHME_SEETA2API_H

#include "NdkUtils.h"
#include "SeetaDetector.h"
#include <opencv2/opencv.hpp>

#define SEETA_JNI(x) Java_com_hzy_face_morpher_Seeta2Api_##x

#ifdef __cplusplus
extern "C" {
#endif


JNIEXPORT jlong JNICALL
SEETA_JNI(nInitSeeta)(JNIEnv *env, jclass type, jstring fdPath_, jstring pdPath_);

JNIEXPORT void JNICALL
SEETA_JNI(nDestory)(JNIEnv *env, jclass type, jlong instance);

JNIEXPORT jintArray JNICALL
SEETA_JNI(nSeetaDetect)(JNIEnv *env, jclass type, jlong instance, jobject bitmap);

JNIEXPORT jfloatArray JNICALL
SEETA_JNI(nSeetaLandmarks)(JNIEnv *env, jclass type, jlong instance, jobject bitmap,
                           jboolean withCorners);

#ifdef __cplusplus
}
#endif

#endif //FACEMORPHME_SEETA2API_H

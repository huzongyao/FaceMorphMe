//
// Created by Administrator on 2019/9/13.
//

#ifndef FACEMORPHME_MORPHERAPI_H
#define FACEMORPHME_MORPHERAPI_H

#include "NdkUtils.h"

#define JNI_FUNC(x) Java_com_hzy_face_morpher_MorpherApi_##x

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jstring JNICALL
JNI_FUNC(getOpenCvVersionString)(JNIEnv *env, jclass type);

JNIEXPORT jstring JNICALL
JNI_FUNC(getStasmVersionString)(JNIEnv *env, jclass type);

JNIEXPORT jstring JNICALL
JNI_FUNC(getLibYUVVersionString)(JNIEnv *env, jclass type);

JNIEXPORT jintArray JNICALL
JNI_FUNC(nDetectFaceRect)(JNIEnv *env, jclass type, jobject bitmap, jstring classifierPath_);

JNIEXPORT jobjectArray JNICALL
JNI_FUNC(nDetectFace)(JNIEnv *env, jclass type, jobject bitmap,
                      jstring imgPath_, jstring classifierPath_);

JNIEXPORT jfloatArray JNICALL
JNI_FUNC(nDetectFaceFArray)(JNIEnv *env, jclass type, jobject bitmap, jstring imgPath_,
                            jstring classifierPath_, jboolean isCorner);

JNIEXPORT jobjectArray JNICALL
JNI_FUNC(nGetFaceSubDiv)(JNIEnv *env, jclass type, jobject bitmap,
                         jstring imgPath_, jstring classifierPath_);

JNIEXPORT jfloatArray JNICALL
JNI_FUNC(nGetFaceSubDivFArray)(JNIEnv *env, jclass type, jobject bitmap,
                               jstring imgPath_, jstring classifierPath_);

JNIEXPORT jfloatArray JNICALL
JNI_FUNC(nGetRectCornersFArray)(JNIEnv *env, jclass type, jint width, jint height);

JNIEXPORT jintArray JNICALL
JNI_FUNC(nGetSubDivPointIndex)(JNIEnv *env, jclass type, jint width,
                               jint height, jfloatArray points_);

JNIEXPORT jint JNICALL
JNI_FUNC(nMorphToBitmap)(JNIEnv *env, jclass type, jobject src, jobject dst, jobject morph,
                         jfloatArray pSrc_, jfloatArray pDst_, jintArray indices_, jfloat alpha);

JNIEXPORT void JNICALL
JNI_FUNC(bitmap2YUV)(JNIEnv *env, jclass type, jobject bitmap, jbyteArray yuv_, jint format);

#ifdef __cplusplus
}
#endif

#endif //FACEMORPHME_MORPHERAPI_H

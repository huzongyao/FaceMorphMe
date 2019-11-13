LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
OPENCV_LIB_TYPE := STATIC

# You Should Copy OpenCV STATIC libs to ./opencv First!
# Or Errors: [opencv/jni/OpenCV.mk: No such file or directory]
# download libOpenCV and unzip to ./opencv
# https://github.com/huzongyao/FaceMorphMe/releases/download/v1.0.0/libopencv-3.4.7.7z
include $(LOCAL_PATH)/opencv/jni/OpenCV.mk


LOCAL_MODULE := morpher

MORPHER_SOURCE:= \
    $(wildcard $(LOCAL_PATH)/morpher/*.cpp) \

STASM_SOURCE := \
	$(LOCAL_PATH)/stasm/asm.cpp             \
	$(LOCAL_PATH)/stasm/classicdesc.cpp     \
	$(LOCAL_PATH)/stasm/convshape.cpp       \
	$(LOCAL_PATH)/stasm/err.cpp             \
	$(LOCAL_PATH)/stasm/eyedet.cpp          \
	$(LOCAL_PATH)/stasm/eyedist.cpp         \
	$(LOCAL_PATH)/stasm/faceroi.cpp         \
	$(LOCAL_PATH)/stasm/hat.cpp             \
	$(LOCAL_PATH)/stasm/hatdesc.cpp         \
	$(LOCAL_PATH)/stasm/landmarks.cpp       \
	$(LOCAL_PATH)/stasm/misc.cpp            \
	$(LOCAL_PATH)/stasm/pinstart.cpp        \
	$(LOCAL_PATH)/stasm/print.cpp           \
	$(LOCAL_PATH)/stasm/shape17.cpp         \
	$(LOCAL_PATH)/stasm/shapehacks.cpp      \
	$(LOCAL_PATH)/stasm/shapemod.cpp        \
	$(LOCAL_PATH)/stasm/startshape.cpp      \
	$(LOCAL_PATH)/stasm/stasm.cpp           \
	$(LOCAL_PATH)/stasm/stasm_lib.cpp       \
	$(LOCAL_PATH)/stasm/MOD_1/facedet.cpp   \
	$(LOCAL_PATH)/stasm/MOD_1/initasm.cpp   \

VENUS_SOURCE := \
	$(LOCAL_PATH)/venus/Beauty.cpp          \
	$(LOCAL_PATH)/venus/blend.cpp           \
	$(LOCAL_PATH)/venus/blur.cpp            \
	$(LOCAL_PATH)/venus/colorspace.cpp      \
	$(LOCAL_PATH)/venus/Effect.cpp          \
	$(LOCAL_PATH)/venus/Feature.cpp         \
	$(LOCAL_PATH)/venus/ImageWarp.cpp       \
	$(LOCAL_PATH)/venus/inpaint.cpp         \
	$(LOCAL_PATH)/venus/Makeup.cpp          \
	$(LOCAL_PATH)/venus/opencv_utility.cpp  \
	$(LOCAL_PATH)/venus/Region.cpp          \

LIBYUV_SOURCE := \
    $(wildcard $(LOCAL_PATH)/libyuv/source/*.cc) \

SEETA2_SOURCE := \
    $(wildcard $(LOCAL_PATH)/seeta2/SeetaNet/src/*.cpp) \
    $(wildcard $(LOCAL_PATH)/seeta2/SeetaNet/src/orz/mem/*.cpp) \
    $(wildcard $(LOCAL_PATH)/seeta2/SeetaNet/src/orz/sync/*.cpp) \
    $(wildcard $(LOCAL_PATH)/seeta2/SeetaNet/src/orz/tools/*.cpp) \
    $(wildcard $(LOCAL_PATH)/seeta2/FaceDetector/src/*.cpp) \

LOCAL_SRC_FILES += $(MORPHER_SOURCE)
LOCAL_SRC_FILES += $(STASM_SOURCE)
LOCAL_SRC_FILES += $(VENUS_SOURCE)
LOCAL_SRC_FILES += $(LIBYUV_SOURCE)
LOCAL_SRC_FILES += $(SEETA2_SOURCE)

LOCAL_C_INCLUDES += $(LOCAL_PATH)/libyuv/include

LOCAL_C_INCLUDES += $(LOCAL_PATH)/seeta2/SeetaNet/include
LOCAL_C_INCLUDES += $(LOCAL_PATH)/seeta2/SeetaNet/src
LOCAL_C_INCLUDES += $(LOCAL_PATH)/seeta2/SeetaNet/src/include_inner
LOCAL_C_INCLUDES += $(LOCAL_PATH)/seeta2/SeetaNet/src/include_inner/layers
LOCAL_C_INCLUDES += $(LOCAL_PATH)/seeta2/FaceDetector/include

LOCAL_CXXFLAGS += -Wall -std=c++11 -frtti -fexceptions -ffunction-sections -fdata-sections
LOCAL_CXXFLAGS += -DLIBYUV_NEON
LOCAL_LDFLAGS += -Wl,--gc-sections -llog -ljnigraphics
LOCAL_ARM_NEON  := true

include $(BUILD_SHARED_LIBRARY)
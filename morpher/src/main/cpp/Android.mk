LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
OPENCV_LIB_TYPE := STATIC

# You Should Copy OpenCV STATIC libs to ./opencv First!
# Or Errors: [opencv/jni/OpenCV.mk: No such file or directory]
# Download OpenCV SDK: https://opencv.org/releases/
include $(LOCAL_PATH)/opencv/jni/OpenCV.mk


LOCAL_MODULE := morpher

MORPHER_SOURCE:= \
    $(wildcard $(LOCAL_PATH)/morpher/*.cpp) \

LIBYUV_SOURCE := \
    $(wildcard $(LOCAL_PATH)/libyuv/source/*.cc) \

SEETA2_SOURCE := \
    $(wildcard $(LOCAL_PATH)/seeta2/SeetaNet/src/*.cpp) \
    $(wildcard $(LOCAL_PATH)/seeta2/SeetaNet/src/orz/mem/*.cpp) \
    $(wildcard $(LOCAL_PATH)/seeta2/SeetaNet/src/orz/sync/*.cpp) \
    $(wildcard $(LOCAL_PATH)/seeta2/SeetaNet/src/orz/tools/*.cpp) \
    $(wildcard $(LOCAL_PATH)/seeta2/FaceDetector/src/*.cpp) \

LOCAL_SRC_FILES += $(MORPHER_SOURCE)
LOCAL_SRC_FILES += $(LIBYUV_SOURCE)
LOCAL_SRC_FILES += $(SEETA2_SOURCE)

LOCAL_C_INCLUDES += $(LOCAL_PATH)/libyuv/include
LOCAL_C_INCLUDES += $(LOCAL_PATH)/seeta2/SeetaNet/include
LOCAL_C_INCLUDES += $(LOCAL_PATH)/seeta2/SeetaNet/src
LOCAL_C_INCLUDES += $(LOCAL_PATH)/seeta2/SeetaNet/src/include_inner
LOCAL_C_INCLUDES += $(LOCAL_PATH)/seeta2/SeetaNet/src/include_inner/layers
LOCAL_C_INCLUDES += $(LOCAL_PATH)/seeta2/FaceDetector/include

LOCAL_CFLAGS += -Wall -ffunction-sections -fdata-sections
LOCAL_CXXFLAGS += -Wall -std=c++11 -frtti -fexceptions -ffunction-sections -fdata-sections
LOCAL_LDFLAGS += -Wl,--gc-sections

LOCAL_LDLIBS := -llog -lz -landroid -ljnigraphics -lm

LOCAL_CXXFLAGS += -DLIBYUV_NEON
LOCAL_ARM_NEON  := true

include $(BUILD_SHARED_LIBRARY)
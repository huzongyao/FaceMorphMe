//
// Created by huzongyao on 2019/10/21.
//

#ifndef FACEMORPHME_COLORUTILS_H
#define FACEMORPHME_COLORUTILS_H

#include <libyuv.h>

class ColorUtils {
public:
    // define types according to android MediaCodecInfo.CodecCapabilities
    static const int TYPE_I420 = 19;
    static const int TYPE_NV12 = 21;
    static const int TYPE_YV12 = 20;
    static const int TYPE_NV21 = 39;

    static void rgba2YUV420(uint8_t *rgba, uint8_t *yuv, int width, int height, int type);
};


#endif //FACEMORPHME_COLORUTILS_H

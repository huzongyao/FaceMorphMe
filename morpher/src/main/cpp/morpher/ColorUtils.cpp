//
// Created by huzongyao on 2019/10/21.
//

#include "ColorUtils.h"
#include "NdkUtils.h"

using namespace libyuv;

/**
 * I420: YYYYYYYY UUVV    =>YUV420P
 * YV12: YYYYYYYY VVUU    =>YUV420P
 * NV12: YYYYYYYY UVUV     =>YUV420SP
 * NV21: YYYYYYYY VUVU     =>YUV420SP
 */
void ColorUtils::rgba2YUV(uint8_t *rgba, uint8_t *yuv, int width, int height, int type) {
    int wh = width * height; // width * height
    int hW = (width + 1) / 2; // half width
    int w4 = width * 4; // width * 4 (rgba line buffer size)
    switch (type) {
        case TYPE_I420:
            ARGBToI420(rgba, w4, yuv, width, yuv + wh, hW, yuv + (wh * 5 / 4), hW, width, height);
            break;
        case TYPE_YV12:
            ARGBToI420(rgba, w4, yuv, width, yuv + (wh * 5 / 4), hW, yuv + wh, hW, width, height);
            break;
        case TYPE_NV12:
            ARGBToNV12(rgba, w4, yuv, width, yuv + wh, width, width, height);
            break;
        case TYPE_NV21:
            break;
        default:
            LOGE("Color Format [%d] Not Support!!", type);
            break;
    }
}
package com.hzy.face.morphme.consts;

import android.annotation.TargetApi;
import android.media.MediaCodecInfo;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.M)
public class VideoConst {
    public static final String MIMETYPE_VIDEO_HEVC = "video/hevc";
    public static final String MIMETYPE_VIDEO_AVC = "video/avc";
    public static final String MIMETYPE_VIDEO_MPEG4 = "video/mp4v-es";
    public static final String MIMETYPE_VIDEO_H263 = "video/3gpp";

    public static final String[] MIMETYPE_PRIORITY = {
            "NONE",
            MIMETYPE_VIDEO_H263,
            MIMETYPE_VIDEO_MPEG4,
            MIMETYPE_VIDEO_AVC,
            MIMETYPE_VIDEO_HEVC,
    };

    /**
     * I420: YYYYYYYY UUVV    =>YUV420P
     * YV12: YYYYYYYY VVUU    =>YUV420P
     * NV12: YYYYYYYY UVUV     =>YUV420SP
     * NV21: YYYYYYYY VUVU     =>YUV420SP
     * <p>
     * Support 4 kind of YUV420 Color, May Cover most devices
     */
    public static final int[] COLOR_FORMAT_ORDER = {
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar,      //YV21(I420)  19
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar,  //NV12        21
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar,  //YV12      20
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar,  //NV21  39
    };
}

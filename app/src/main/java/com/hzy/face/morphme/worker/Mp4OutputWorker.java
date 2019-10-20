package com.hzy.face.morphme.worker;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Build;
import android.util.Log;

import com.hzy.face.morpher.MorpherApi;

import java.nio.ByteBuffer;

public class Mp4OutputWorker {

    public static final String MIMETYPE_VIDEO_AVC = "video/avc";
    private static final int FRAME_RATE = 15;
    private static final int COMPRESS_RATIO = 64;
    private static final int I_FRAME_INTERVAL = 5;

    private String mOutputVideoPath;
    private MediaMuxer mMediaMuxer;
    private MediaCodec mMediaCodec;
    private MediaFormat mMediaFormat;
    private int mVideoTrack;
    private MediaCodec.BufferInfo mBufferInfo;
    private long mEncoderTimeUs;
    private byte[] mInputYUVData;
    private long mDequeueTimeoutUS = 10_000L;

    public Mp4OutputWorker(String filePath) {
        mOutputVideoPath = filePath;
        mBufferInfo = new MediaCodec.BufferInfo();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void start(int width, int height) {
        try {
            mMediaFormat = MediaFormat.createVideoFormat(MIMETYPE_VIDEO_AVC, width, height);
            int bitRate = height * width * 64 * FRAME_RATE / COMPRESS_RATIO;
            mMediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
            mMediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
            // YUV 编码比较常见
            int colorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar;
            mMediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mMediaFormat.setInteger(MediaFormat.KEY_BITRATE_MODE,
                        MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CQ);
                mMediaFormat.setInteger(MediaFormat.KEY_COMPLEXITY,
                        MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CQ);
            }
            mMediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, I_FRAME_INTERVAL);
            // init codec
            mMediaCodec = MediaCodec.createEncoderByType(MIMETYPE_VIDEO_AVC);
            mMediaCodec.configure(mMediaFormat, null, null,
                    MediaCodec.CONFIGURE_FLAG_ENCODE);
            // init muxer
            mMediaMuxer = new MediaMuxer(mOutputVideoPath,
                    MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            mInputYUVData = new byte[(int) (width * height * 1.5)];
            mEncoderTimeUs = 0;
            mMediaCodec.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void release() {
        try {
            mMediaCodec.stop();
            mMediaCodec.release();
            mMediaMuxer.stop();
            mMediaMuxer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void queenFrame(Bitmap bitmap) {
        int inputBufferIndex = mMediaCodec.dequeueInputBuffer(mDequeueTimeoutUS);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                inputBuffer = mMediaCodec.getInputBuffer(inputBufferIndex);
            } else {
                inputBuffer = mMediaCodec.getInputBuffers()[inputBufferIndex];
            }
            if (inputBuffer != null) {
                inputBuffer.clear();
                MorpherApi.bitmap2YUVI420Bytes(bitmap, mInputYUVData);
                inputBuffer.put(mInputYUVData);
                mMediaCodec.queueInputBuffer(inputBufferIndex,
                        0, mInputYUVData.length, mEncoderTimeUs, 0);
                mEncoderTimeUs += 50_000;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void checkOutputBuffer() {
        int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(mBufferInfo, mDequeueTimeoutUS);
        do {
            if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                Log.e("TAG", "INFO_OUTPUT_FORMAT_CHANGED!!");
                MediaFormat newFormat = mMediaCodec.getOutputFormat();
                mVideoTrack = mMediaMuxer.addTrack(newFormat);
                mMediaMuxer.start();
            } else if (outputBufferIndex >= 0) {
                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    Log.e("TAG", "BUFFER_FLAG_CODEC_CONFIG IGNORED!!");
                } else {
                    ByteBuffer outputBuffer = mMediaCodec.getOutputBuffer(outputBufferIndex);
                    if (outputBuffer != null) {
                        Log.e("TAG", "DATA Frame: " + mBufferInfo.size);
                        outputBuffer.position(mBufferInfo.offset);
                        outputBuffer.limit(mBufferInfo.offset + mBufferInfo.size);
                        mMediaMuxer.writeSampleData(mVideoTrack, outputBuffer, mBufferInfo);
                        mMediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                    }
                }
            }
            outputBufferIndex = mMediaCodec.dequeueOutputBuffer(mBufferInfo, mDequeueTimeoutUS);
        } while (outputBufferIndex > 0);
    }
}

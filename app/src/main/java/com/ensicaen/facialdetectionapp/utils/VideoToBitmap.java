package com.ensicaen.facialdetectionapp.utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class VideoToBitmap {
    private FFmpegMediaMetadataRetriever _input;
    private FFmpegMediaMetadataRetriever.Metadata _metadata;
    private double _duration; // in ms
    private double _fpms; // frame per ms
    private int _frameCount;
    private double _frameGap; // in ms
    private int _index;

    public VideoToBitmap(String videoInputPath) {
        _input = new FFmpegMediaMetadataRetriever();
        _input.setDataSource(videoInputPath);
        _metadata = _input.getMetadata();
        _duration = _metadata.getDouble(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);
        _fpms = _metadata.getDouble(FFmpegMediaMetadataRetriever.METADATA_KEY_FRAMERATE) / 1000;
        _frameCount = (int)(_duration * _fpms);
        _frameGap = _duration / _frameCount;
        _index = 0;
        Log.i("FaceDetectionApp", _duration + "_" + _frameGap + "_" + _frameCount);
    }

    public Bitmap getFrame(int frameIndex) {
        return _input.getFrameAtTime((long) (frameIndex * _frameGap * 1000L), FFmpegMediaMetadataRetriever.OPTION_CLOSEST_SYNC);
    }

    public Bitmap getNextFrame() {
        Bitmap nextFrame = getFrame(getIndex());
        _index += 1;
        return nextFrame;
    }

    public boolean hasNextFrame() {
        return _index <= _frameCount;
    }

    public int getIndex() {
        return _index;
    }

    public void close() {
        _input.release();
    }
}

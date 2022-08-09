package com.ensicaen.facialdetectionapp.controller;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;

import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FrameAnalyzer implements ImageAnalysis.Analyzer {
    private FaceDetector _detector;
    private FaceListener _faceListener;
    private double _processingTime;
    private int _frameProcessed;

    public FrameAnalyzer() {
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                //.setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .setMinFaceSize(0.3f)
                .build();

        _detector = FaceDetection.getClient(options);
        _processingTime = 0.0f;
        _frameProcessed = 0;
    }

    @Override
    public void analyze(ImageProxy frameProxy) {
        long start = System.currentTimeMillis();
        @SuppressLint("UnsafeOptInUsageError") Image frame = frameProxy.getImage();
        if (frame != null) {
            /* Get InputImage for ML KIT */
            InputImage image = InputImage.fromMediaImage(frame, frameProxy.getImageInfo().getRotationDegrees());

            _faceListener.setInputImage(image);
            _faceListener.setImageProxy(frameProxy);
            @SuppressLint("UnsafeOptInUsageError") Task<List<Face>> result =
                    _detector.process(image).addOnSuccessListener(_faceListener)
                            .addOnCompleteListener(task -> {
                                _frameProcessed += 1;
                                long end = System.currentTimeMillis();
                                _processingTime = _processingTime + (((end - start) - _processingTime) / _frameProcessed);
                                Log.i("FaceDetectionApp", String.valueOf(_processingTime) + "(" + (end - start) + ")");
                                frameProxy.close();
                            });
        }
    }

    /* Used to analyze synchronously bitmap coming from image directory */
    public void analyze(Bitmap input) {
        InputImage image = InputImage.fromBitmap(input, 0);
        _faceListener.setInputImage(image);
        @SuppressLint("UnsafeOptInUsageError") Task<List<Face>> result =
                _detector.process(image).addOnSuccessListener(_faceListener);

        while(!result.isComplete()) {
            try {
                Thread.sleep(5);
            } catch(InterruptedException e) {

            }
        }
    }

    /* Save frame in internal storage */
    public static void saveFrame(String path, Bitmap frame) {
        try {
            @SuppressLint("UnsafeOptInUsageError") FileOutputStream out = new FileOutputStream("/data/user/0/com.ensicaen.facialdetectionapp/"+path+".png");
            frame.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addFaceListener(FaceListener faceListener) {
        _faceListener = faceListener;
    }

    public FaceListener getFaceListener() {
        return _faceListener;
    }
}

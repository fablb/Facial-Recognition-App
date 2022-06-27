package com.ensicaen.facialdetectionapp.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;

import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class FrameAnalyzer implements ImageAnalysis.Analyzer {
    private FaceDetector _detector;
    private FaceListener _faceListener;

    public FrameAnalyzer() {
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .setMinFaceSize(0.2f)
                .enableTracking()
                .build();

        _detector = FaceDetection.getClient(options);
    }

    @Override
    public void analyze(ImageProxy frameProxy) {
        @SuppressLint("UnsafeOptInUsageError") Image frame = frameProxy.getImage();
        if (frame != null) {
            /* Get InputImage for ML KIT */
            InputImage image = InputImage.fromMediaImage(frame, frameProxy.getImageInfo().getRotationDegrees());

            _faceListener.setInputImage(image);
            _faceListener.setImageProxy(frameProxy);
            @SuppressLint("UnsafeOptInUsageError") Task<List<Face>> result =
                    _detector.process(image).addOnSuccessListener(_faceListener)
                            .addOnCompleteListener(task -> {
                                frameProxy.close();
                            });
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
}

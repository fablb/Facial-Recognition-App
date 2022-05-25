package com.ensicaen.facialdetectionapp.controler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.ensicaen.facialdetectionapp.utils.BitmapUtils;
import com.ensicaen.facialdetectionapp.utils.YuvToRgbConverter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import Catalano.Imaging.FastBitmap;

public class FrameAnalyzer implements ImageAnalysis.Analyzer {

    @Override
    public void analyze(ImageProxy frameProxy) {
        @SuppressLint("UnsafeOptInUsageError") Image frame = frameProxy.getImage();
        if (frame != null) {
            /* Get InputImage for ML KIT */
            InputImage image = InputImage.fromMediaImage(frame, frameProxy.getImageInfo().getRotationDegrees());

            /* Convert ImageProxy to Bitmap for future usage */
            @SuppressLint("UnsafeOptInUsageError") Bitmap bitmapImage = BitmapUtils.getBitmap(frameProxy);
            //FastBitmap fb = new FastBitmap(bitmapImage);
            //fb.toGrayscale();

            detectFaces(image, bitmapImage, frameProxy);
        }
    }

    private void detectFaces(InputImage image, Bitmap bitmapImage, ImageProxy frameProxy) {
        // [START set_detector_options]
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                .setMinFaceSize(0.2f)
                .build();
        // [END set_detector_options]

        // [START get_detector]
        FaceDetector detector = FaceDetection.getClient(options);
        // Or use the default options:
        // FaceDetector detector = FaceDetection.getClient();
        // [END get_detector]

        // [START run_detector]
        Task<List<Face>> result =
            detector.process(image)
                .addOnSuccessListener(
                    faces -> {
                        for (Face face : faces) {
                            Rect bounds = face.getBoundingBox();
                            float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                            float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees

                            /* Stops if the face is not centered */
                            if (!faceCenteringDetection(face, image.getWidth(), image.getHeight())) {
                                return;
                            }

                            /* Save frame in internal storage for debugging purpose */
                            /*
                            try {
                                @SuppressLint("UnsafeOptInUsageError") FileOutputStream out = new FileOutputStream("/data/user/0/com.ensicaen.facialdetectionapp/files/"+frameProxy.getImage().getTimestamp()+"_test.png");
                                Bitmap croppedFace = Bitmap.createBitmap(bitmapImage, bounds.left, bounds.top, bounds.width(), bounds.height());
                                croppedFace.compress(Bitmap.CompressFormat.PNG, 50, out);
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }*/
                        }
                    })
                .addOnFailureListener(
                    e -> Log.i("FaceDetectionApp", e.toString()))
                .addOnCompleteListener(task -> {
                    frameProxy.close();
                });
        // [END run_detector]
    }

    /* Check if face is centered in image */
    private boolean faceCenteringDetection(Face face, int width, int height) {
        /* In portrait mode width and height are swapped compare to landscape */
        int middleX = height / 2;
        int middleY = width / 2;
        int offsetX = height / 5;
        int offsetY = width / 5;
        Rect center = new Rect(middleX - offsetX,middleY - offsetY,middleX + offsetX, middleY + offsetY);
        PointF noise = face.getLandmark(FaceLandmark.NOSE_BASE).getPosition();
        if (center.contains((int)noise.x, (int)noise.y)) {
            return true;
        } else {
            return false;
        }
    }
}

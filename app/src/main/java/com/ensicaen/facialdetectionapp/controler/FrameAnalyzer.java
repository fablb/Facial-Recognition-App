package com.ensicaen.facialdetectionapp.controler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.Image;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.preference.PreferenceManager;

import com.ensicaen.facialdetectionapp.utils.BitmapUtils;
import com.ensicaen.facialdetectionapp.utils.YuvToRgbConverter;
import com.ensicaen.facialdetectionapp.view.MainActivity;
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
    private Context context;
    private FrameListener frameListener;
    private int previewWidth;
    private int previewHeight;

    public FrameAnalyzer(Context c, int pWidth, int pHeight) {
        context = c;
        previewWidth = pWidth;
        previewHeight = pHeight;
    }

    @Override
    public void analyze(ImageProxy frameProxy) {
        @SuppressLint("UnsafeOptInUsageError") Image frame = frameProxy.getImage();
        if (frame != null) {
            /* Get InputImage for ML KIT */
            InputImage image = InputImage.fromMediaImage(frame, frameProxy.getImageInfo().getRotationDegrees());

            //FastBitmap fb = new FastBitmap(bitmapImage);
            //fb.toGrayscale();

            detectFaces(image, frameProxy);
        }
    }

    private void detectFaces(InputImage image, ImageProxy frameProxy) {
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .setMinFaceSize(0.2f)
                .build();

        FaceDetector detector = FaceDetection.getClient(options);

        @SuppressLint("UnsafeOptInUsageError") Task<List<Face>> result =
            detector.process(image)
                .addOnSuccessListener(
                    faces -> {
                        if (faces.isEmpty()) {
                            frameListener.drawFaceBounds(null);
                        }
                        for (Face face : faces) {
                            Rect bounds = face.getBoundingBox();
                            float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                            float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees

                            /* Stops if the eyes are close */
                            if ((face.getLeftEyeOpenProbability() != null) || (face.getRightEyeOpenProbability() != null)) {
                                if ((face.getLeftEyeOpenProbability() < 0.75f) || (face.getRightEyeOpenProbability() < 0.75f)) {
                                    Toast.makeText(context, "Please open your eyes", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                            /* Stops if the face is not centered */
                            if (!faceCenteringDetection(face, image.getWidth(), image.getHeight())) {
                                return;
                            }



                            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("switch_save_face", false)) {
                                saveFrame(String.valueOf(frameProxy.getImage().getTimestamp()), getFaceBitmap(frameProxy, bounds));
                            }

                            /* Bounds needs to be transformed as previewView and ImageProxy dimensions are different */
                            frameListener.drawFaceBounds(transformBounds(bounds, image.getHeight(), image.getWidth())); // Swap width and height as mobile phone is in portrait mode
                        }
                    })
                .addOnFailureListener(
                    e -> Log.i("FaceDetectionApp", e.toString()))
                .addOnCompleteListener(task -> {
                    frameProxy.close();
                });
    }

    /* Convert ImageProxy to Bitmap for future usage */
    @SuppressLint("UnsafeOptInUsageError")
    public Bitmap getBitmap(ImageProxy image) {
        return BitmapUtils.getBitmap(image);
    }

    public Bitmap getFaceBitmap(ImageProxy image, Rect faceBounds) {
        return Bitmap.createBitmap(getBitmap(image), faceBounds.left, faceBounds.top, faceBounds.width(), faceBounds.height());
    }

    /* Save frame in internal storage */
    public void saveFrame(String name, Bitmap frame) {
        try {
            @SuppressLint("UnsafeOptInUsageError") FileOutputStream out = new FileOutputStream("/data/user/0/com.ensicaen.facialdetectionapp/files/"+name+".png");
            frame.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void addFrameListener(FrameListener fl) {
        frameListener = fl;
    }

    public Rect transformBounds(Rect bounds, int imageWidth, int imageHeight) {
        float scaleX = previewWidth / (float)imageWidth;
        float scaleY = previewHeight / (float)imageHeight;

        int flippedLeft = imageWidth - bounds.right;
        int flippedRight = imageWidth - bounds.left;

        // Scale all coordinates to match preview
        int scaledLeft = (int) (scaleX * flippedLeft);
        int scaledTop = (int) (scaleY * bounds.top);
        int scaledRight = (int) (scaleX * flippedRight);
        int scaledBottom = (int) (scaleY * bounds.bottom);

        return new Rect(scaledLeft, scaledTop, scaledRight, scaledBottom);
    }
}

package com.ensicaen.facialdetectionapp.controler;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;

import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;

import java.util.ArrayList;
import java.util.List;

public class FaceDetectorListener implements OnSuccessListener, OnFailureListener {
    private InputImage _image;
    private ImageProxy _frameProxy;
    private FrameListener _frameListener;
    private SharedPreferences _preferences;

    public FaceDetectorListener(FrameListener frameListener, SharedPreferences preferences) {
        _image = null;
        _frameProxy = null;
        _frameListener = frameListener;
        _preferences = preferences;
    }

    public void setInputImage(InputImage image) {
        _image = image;
    }

    public void setImageProxy(ImageProxy frameProxy) {
        _frameProxy = frameProxy;
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public void onSuccess(Object o) {
        List<Face> faces = (List<Face>) o;

        if (faces.isEmpty()) {
            _frameListener.drawFaceBounds(null);
            _frameListener.drawFacePoints(null);
        }
        for (Face face : faces) {
            Rect bounds = face.getBoundingBox();
            List<PointF> contours = getAll(face);

            /* Stops if the face is not centered */
            //
            // Methods faceCenteringDetection and getCropBitmap must be reworked
            //
            //if (!FaceUtils.faceCenteringDetection(face, _image.getWidth(), _image.getHeight())) {
            //    return;
            //}
            //
            //if (_preferences.getBoolean("switch_save_face", false)) {
            //    Bitmap faceBitmap = BitmapUtils.getCropBitmap(_frameProxy, bounds);
            //    FrameAnalyzer.saveFrame("files/"+String.valueOf(_frameProxy.getImage().getTimestamp()), faceBitmap);
            //}

            /* Swap width and height for drawing depending on device orientation */
            int rotationDegrees = _frameProxy.getImageInfo().getRotationDegrees();
            if (rotationDegrees == 0 || rotationDegrees == 180) {
                _frameListener.setImageSourceInfo(_image.getWidth(), _image.getHeight(), true);
            } else {
                _frameListener.setImageSourceInfo(_image.getHeight(), _image.getWidth(), true);
            }

            _frameListener.drawFacePoints(contours);
            _frameListener.drawFaceBounds(bounds);
        }
    }

    private List<PointF> getAll(Face face) {
        List<PointF> contours = new ArrayList<>();
        for (FaceContour faceContour: face.getAllContours()) {
            for (PointF point : faceContour.getPoints()) {
                contours.add(point);
            }
        }
        return contours;
    }

    @Override
    public void onFailure(Exception e) {
        Log.e("FaceDetectionApp", e.toString());
    }
}

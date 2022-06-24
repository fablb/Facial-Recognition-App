package com.ensicaen.facialdetectionapp.controller;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;
import android.widget.Toast;

import com.ensicaen.facialdetectionapp.utils.BitmapUtils;
import com.ensicaen.facialdetectionapp.utils.FaceUtils;
import com.ensicaen.facialdetectionapp.utils.Point2D;
import com.ensicaen.facialdetectionapp.utils.SingleToast;
import com.ensicaen.facialdetectionapp.view.CameraView;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;

import java.util.ArrayList;
import java.util.List;

public class LivenessDetectorListener extends FaceListener {
    private FrameListener _drawListener;
    private CameraView _cameraView;

    public LivenessDetectorListener(FrameListener drawListener, CameraView cameraView) {
        _drawListener = drawListener;
        _cameraView = cameraView;
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public void onSuccess(Object o) {
        List<Face> faces = (List<Face>) o;

        Rect centerBounds = FaceUtils.getCenter(_image.getWidth(), _image.getHeight(), true);
        int rotationDegrees = _frameProxy.getImageInfo().getRotationDegrees();
        _drawListener.setImageSourceInfo(_image.getWidth(), _image.getHeight(), rotationDegrees, true);

        if (faces.isEmpty()) {
            SingleToast.show(_cameraView, "No face on screen", Toast.LENGTH_SHORT);
            _drawListener.drawCenterBounds(centerBounds, Color.RED);
            return;
        }
        for (Face face : faces) {
            Rect bounds = face.getBoundingBox();
            float eulerX = face.getHeadEulerAngleX();
            float eulerY = face.getHeadEulerAngleY();
            float eulerZ = face.getHeadEulerAngleZ();

            if (!centerBounds.contains(bounds)) {
                SingleToast.show(_cameraView, "Face is not centered", Toast.LENGTH_SHORT);
                _drawListener.drawCenterBounds(centerBounds, Color.RED);
                return;
            }

            PointF leftFace = face.getContour(FaceContour.UPPER_LIP_TOP).getPoints().get(5);
            PointF rightFace = face.getContour(FaceContour.LOWER_LIP_BOTTOM).getPoints().get(4);
            Log.i("FaceDetectionApp", String.valueOf(leftFace.y - rightFace.y));
            List<PointF> pointsDraw = new ArrayList<>();
            pointsDraw.add(leftFace);
            pointsDraw.add(rightFace);
            _drawListener.drawPoints(pointsDraw);
        }
    }

    @Override
    public void onFailure(Exception e) {
        Log.e("FaceDetectionApp", e.toString());
    }
}

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
    private float _lastEulerX;
    private float _lastEulerY;
    private float _lastEulerZ;
    private float _rightEyeLengthMax;
    private float _leftEyeLengthMax;


    public LivenessDetectorListener(FrameListener drawListener, CameraView cameraView) {
        _drawListener = drawListener;
        _cameraView = cameraView;
        _lastEulerX = 0.0f;
        _lastEulerY = 0.0f;
        _lastEulerZ = 0.0f;
        _rightEyeLengthMax = 0.0f;
        _leftEyeLengthMax = 0.0f;
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
            float eulerXDifference = _lastEulerX - eulerX;
            float eulerYDifference = _lastEulerY - eulerY;
            float eulerZDifference = _lastEulerZ - eulerZ;
            Log.i("FaceDetectionApp",  eulerXDifference + "_" + eulerYDifference + "_" + eulerZDifference);
            _lastEulerX = eulerX;
            _lastEulerY = eulerY;
            _lastEulerZ = eulerZ;

            List<PointF> rightEye = face.getContour(FaceContour.RIGHT_EYE).getPoints();
            List<PointF> leftEye = face.getContour(FaceContour.LEFT_EYE).getPoints();

            if (((rightEye.get(12).y-rightEye.get(4).y)/bounds.height() < _rightEyeLengthMax*0.60f) ||((leftEye.get(12).y-leftEye.get(4).y)/bounds.height() < _leftEyeLengthMax*0.60f)) {
                SingleToast.show(_cameraView, "Blink detected", Toast.LENGTH_SHORT);
            }
            if ((rightEye.get(12).y-rightEye.get(4).y)/bounds.height() > _rightEyeLengthMax) {
                _rightEyeLengthMax = (rightEye.get(12).y-rightEye.get(4).y)/bounds.height();
            }
            if ((leftEye.get(12).y-leftEye.get(4).y)/bounds.height() > _leftEyeLengthMax) {
                _leftEyeLengthMax = (leftEye.get(12).y-leftEye.get(4).y)/bounds.height();
            }


            PointF leftFace = face.getContour(FaceContour.LEFT_EYE).getPoints().get(8);
            PointF rightFace = face.getContour(FaceContour.RIGHT_EYE).getPoints().get(0);
            Float distance = leftFace.x - rightFace.x;
            Log.i("FaceDetectionApp", distance + "_" + bounds.width());
            Log.i("FaceDetectionApp", String.valueOf(distance/ bounds.width()));
            List<PointF> pointsDraw = new ArrayList<>();
            pointsDraw.add(leftFace);
            pointsDraw.add(rightFace);
            _drawListener.drawPoints(pointsDraw);
            _drawListener.drawFacePoints(face.getContour(FaceContour.FACE).getPoints());
            _drawListener.drawFaceBounds(bounds);
        }
    }

    @Override
    public void onFailure(Exception e) {
        Log.e("FaceDetectionApp", e.toString());
    }
}

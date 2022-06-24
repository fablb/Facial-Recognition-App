package com.ensicaen.facialdetectionapp.controller;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;
import android.widget.Toast;

import com.ensicaen.facialdetectionapp.model.Profile;
import com.ensicaen.facialdetectionapp.utils.BitmapUtils;
import com.ensicaen.facialdetectionapp.utils.FaceUtils;
import com.ensicaen.facialdetectionapp.utils.LBP;
import com.ensicaen.facialdetectionapp.utils.Point2D;
import com.ensicaen.facialdetectionapp.utils.SingleToast;
import com.ensicaen.facialdetectionapp.utils.SizedArrayList;
import com.ensicaen.facialdetectionapp.view.CameraView;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;

import java.util.List;

public class FaceAuthenticationListener extends FaceListener {
    private FrameListener _drawListener;
    private CameraView _cameraView;
    private SizedArrayList<Point2D> _faceBoundsCenter;
    private LBP _lbp;
    private Profile _user;
    private int authenticationTry;
    private static final double AUTH_THRESHOLD = 0.06;

    public FaceAuthenticationListener(FrameListener drawListener, CameraView cameraView, Profile user) {
        _drawListener = drawListener;
        _cameraView = cameraView;
        _faceBoundsCenter = new SizedArrayList<>(STABLE_SCREEN_FRAME_COUNT);
        _lbp = new LBP();
        _user = user;
        authenticationTry = 0;
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

            if (!FaceUtils.isStraight(eulerX, eulerY, eulerZ)) {
                SingleToast.show(_cameraView, "Face is not straight", Toast.LENGTH_SHORT);
                _drawListener.drawCenterBounds(centerBounds, Color.RED);
                return;
            }

            Point2D boundsCenter = new Point2D(bounds.centerX(), bounds.centerY());

            /* Fill before computing mean movement */
            if (_faceBoundsCenter.size() < _faceBoundsCenter.capacity()) {
                _faceBoundsCenter.add(boundsCenter);
                _drawListener.drawCenterBounds(centerBounds, Color.RED);
                return;
            }

            int meanDistance = boundsCenter.meanDistance(_faceBoundsCenter);
            _faceBoundsCenter.add(boundsCenter);

            /* Avoid blur image */
            if (meanDistance > STABLE_SCREEN_THRESHOLD) {
                SingleToast.show(_cameraView, "Face or mobile phone is moving", Toast.LENGTH_SHORT);
                _drawListener.drawCenterBounds(centerBounds, Color.RED);
                return;
            }
            List<PointF> rightEye = face.getContour(FaceContour.RIGHT_EYE).getPoints();
            List<PointF> leftEye = face.getContour(FaceContour.LEFT_EYE).getPoints();

            if(!eyesOpen(rightEye, leftEye)) {
                SingleToast.show(_cameraView, "Eyes closed", Toast.LENGTH_SHORT);
                _drawListener.drawCenterBounds(centerBounds, Color.RED);
                return;
            }
            if(!lookAtTheCamera(rightEye, leftEye)) {
                SingleToast.show(_cameraView, "Look at the screen please", Toast.LENGTH_SHORT);
                _drawListener.drawCenterBounds(centerBounds, Color.RED);
                return;
            }

            _drawListener.drawCenterBounds(centerBounds, Color.GREEN);

            Bitmap cropBitmap = BitmapUtils.getCropBitmap(_frameProxy, bounds);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(cropBitmap, 140, 140, true);

            double similarityScore = _lbp.compare(scaledBitmap, _user.getFeatures());
            Log.i("FaceDetectionApp", "similarity: " + similarityScore);
            authenticationTry += 1;
            if (similarityScore <= AUTH_THRESHOLD) {
                SingleToast.clear();
                _cameraView.close(true);
            } else {
                if (authenticationTry % 20 == 0) {
                    SingleToast.show(_cameraView, "Authentication failed, retrying...", Toast.LENGTH_SHORT);
                    if (authenticationTry == 100) {
                        SingleToast.clear();
                        _cameraView.close(false);
                    }
                }
            }
        }
    }

    @Override
    public void onFailure(Exception e) {
        Log.e("FaceDetectionApp", e.toString());
    }
}

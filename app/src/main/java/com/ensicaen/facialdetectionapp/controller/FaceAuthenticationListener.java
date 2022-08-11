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
    private AttentionDetector _attentionDetector;
    private LBP _lbp;
    private Profile _user;
    private int authenticationTry;
    private static final double AUTH_THRESHOLD = 0.06;

    public FaceAuthenticationListener(FrameListener drawListener, CameraView cameraView, Profile user) {
        _drawListener = drawListener;
        _cameraView = cameraView;
        _attentionDetector = new AttentionDetector();
        _lbp = new LBP();
        _user = user;
        authenticationTry = 0;
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public void onSuccess(Object o) {
        List<Face> faces = (List<Face>) o;
        int rotationDegrees = _frameProxy.getImageInfo().getRotationDegrees();
        _drawListener.setImageSourceInfo(_image.getWidth(), _image.getHeight(), rotationDegrees, true);
        _attentionDetector.setImageSize(_image.getWidth(), _image.getHeight(), true);
        Rect _insideRect = _attentionDetector.getInsideRect();

        if (faces.isEmpty()) {
            SingleToast.show(_cameraView, "No face on screen", Toast.LENGTH_SHORT);
            _drawListener.drawCenterBounds(_insideRect, Color.RED);
            return;
        }
        for (Face face : faces) {
            Rect bounds = face.getBoundingBox();
            float eulerX = face.getHeadEulerAngleX();
            float eulerY = face.getHeadEulerAngleY();
            float eulerZ = face.getHeadEulerAngleZ();

            if (!_attentionDetector.isInside(bounds)) {
                SingleToast.show(_cameraView, "Whole face is not inside screen", Toast.LENGTH_SHORT);
                _drawListener.drawCenterBounds(_insideRect, Color.RED);
                return;
            }

            if (_attentionDetector.isStraight(eulerX, eulerY, eulerZ)) {
                SingleToast.show(_cameraView, "Face is not straight", Toast.LENGTH_SHORT);
                _drawListener.drawCenterBounds(_insideRect, Color.RED);
                return;
            }

            /* Avoid blur image */
            if (_attentionDetector.isMoving(bounds.centerX(), bounds.centerY())) {
                SingleToast.show(_cameraView, "Face or mobile phone is moving", Toast.LENGTH_SHORT);
                _drawListener.drawCenterBounds(_insideRect, Color.RED);
                return;
            }

            List<PointF> leftEye = face.getContour(FaceContour.LEFT_EYE).getPoints();
            List<PointF> rightEye = face.getContour(FaceContour.RIGHT_EYE).getPoints();

            if(!_attentionDetector.isLooking(leftEye, rightEye, BitmapUtils.getBitmap(_frameProxy))) {
                SingleToast.show(_cameraView, "Look at the screen please", Toast.LENGTH_SHORT);
                _drawListener.drawCenterBounds(_insideRect, Color.RED);
                return;
            }

            _drawListener.drawCenterBounds(_insideRect, Color.GREEN);

            Bitmap cropBitmap = BitmapUtils.getCropBitmap(_frameProxy, bounds);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(cropBitmap, 320, 320, true);

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

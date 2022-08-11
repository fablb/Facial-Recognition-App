package com.ensicaen.facialdetectionapp.controller;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;
import android.widget.Toast;

import com.ensicaen.facialdetectionapp.utils.BackgroundSubtraction;
import com.ensicaen.facialdetectionapp.utils.BitmapUtils;
import com.ensicaen.facialdetectionapp.utils.SingleToast;
import com.ensicaen.facialdetectionapp.view.CameraView;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;

import java.util.List;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Tools.ObjectiveFidelity;

public class LivenessDetectorListener extends FaceListener {
    private FrameListener _drawListener;
    private CameraView _cameraView;
    private int _closed_eye_frame;
    private float _rightEyeLengthMax;
    private float _leftEyeLengthMax;
    private int _frameProcessed;
    private BackgroundSubtraction _bs;
    private double _maxMSE;
    private int _timeBeforeDecision = 5; // number of frame
    private AttentionDetector _attentionDetector;

    public LivenessDetectorListener(FrameListener drawListener, CameraView cameraView) {
        _drawListener = drawListener;
        _cameraView = cameraView;
        _rightEyeLengthMax = 0.0f;
        _leftEyeLengthMax = 0.0f;
        _frameProcessed = 0;
        _maxMSE = 0.0;
        _closed_eye_frame = 0;
        _attentionDetector = new AttentionDetector();
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

            if (!_attentionDetector.isInside(bounds)) {
                SingleToast.show(_cameraView, "Whole face is not inside screen", Toast.LENGTH_SHORT);
                _drawListener.drawCenterBounds(_insideRect, Color.RED);
                return;
            }

            List<PointF> leftEye = face.getContour(FaceContour.LEFT_EYE).getPoints();
            List<PointF> rightEye = face.getContour(FaceContour.RIGHT_EYE).getPoints();

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(BitmapUtils.getBitmap(_frameProxy), _frameProxy.getWidth()/4, _frameProxy.getWidth()/4, true);
            //BitmapUtils.saveFrame(scaledBitmap, "/data/user/0/com.ensicaen.facialdetectionapp/output/ld/scaledBitmap_"+_frameProcessed+".png");
            FastBitmap custom = new FastBitmap(scaledBitmap);

            _frameProcessed += 1;
            if (_bs == null) {
                _bs = new BackgroundSubtraction(custom, 15);
            } else {
                Bitmap lastFg = _bs.getForegroundBitmap();
                _bs.update(custom);
                if (_frameProcessed > 2) {
                    ObjectiveFidelity of = new ObjectiveFidelity(new FastBitmap(lastFg), _bs.getForeground());
                    double mse = of.getMSE();
                    if (mse > _maxMSE) {
                        _maxMSE = mse;
                    }
                    if ((_frameProcessed - 2) % _timeBeforeDecision == 0) { // Minus two because we do not process first two frames to initialize background subtraction image
                        if (_maxMSE > 28) {
                            SingleToast.show(_cameraView, "Alive!", Toast.LENGTH_SHORT);
                            Log.i("FaceDetectionApp", "Alive!");
                        } else {
                            SingleToast.show(_cameraView, "Not alive!", Toast.LENGTH_SHORT);
                            Log.i("FaceDetectionApp", "Not alive!");
                        }
                        _maxMSE = 0.0;
                    }
                    Log.i("FaceDetectionApp", "mse value background subtraction: " + mse);

                }
            }

            if (((rightEye.get(12).y-rightEye.get(4).y)/bounds.height() < _rightEyeLengthMax*0.65f) ||((leftEye.get(12).y-leftEye.get(4).y)/bounds.height() < _leftEyeLengthMax*0.65f)) {
                _closed_eye_frame++;
                return;
            } else {
                if (_closed_eye_frame != 0) {
                    if (_closed_eye_frame <= 5) {
                        SingleToast.show(_cameraView, "Blinking detected", Toast.LENGTH_SHORT);
                        Log.d("FaceDetectionApp", "Blinking detected");
                    } else {
                        SingleToast.show(_cameraView, "Eye(s) closed", Toast.LENGTH_SHORT);
                        Log.d("FaceDetectionApp", "Closing detected");
                    }
                    _closed_eye_frame = 0;
                }
            }
            if ((rightEye.get(12).y-rightEye.get(4).y)/bounds.height() > _rightEyeLengthMax) {
                _rightEyeLengthMax = (rightEye.get(12).y-rightEye.get(4).y)/bounds.height();
            }
            if ((leftEye.get(12).y-leftEye.get(4).y)/bounds.height() > _leftEyeLengthMax) {
                _leftEyeLengthMax = (leftEye.get(12).y-leftEye.get(4).y)/bounds.height();
            }

            _drawListener.drawFacePoints(face.getContour(FaceContour.FACE).getPoints());
            _drawListener.drawFaceBounds(bounds);
            _drawListener.drawEyesPoints(rightEye, leftEye);
        }
    }

    @Override
    public void onFailure(Exception e) {
        Log.e("FaceDetectionApp", e.toString());
    }
}

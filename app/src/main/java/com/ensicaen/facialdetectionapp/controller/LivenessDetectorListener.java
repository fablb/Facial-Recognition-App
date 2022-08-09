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
import com.ensicaen.facialdetectionapp.utils.FaceUtils;
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
    private float _rightEyeLengthMax;
    private float _leftEyeLengthMax;
    private int _frameProcessed;
    private BackgroundSubtraction _bs;

    public LivenessDetectorListener(FrameListener drawListener, CameraView cameraView) {
        _drawListener = drawListener;
        _cameraView = cameraView;
        _rightEyeLengthMax = 0.0f;
        _leftEyeLengthMax = 0.0f;
        _frameProcessed = 0;
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public void onSuccess(Object o) {
        List<Face> faces = (List<Face>) o;

        Rect centerBounds = FaceUtils.getCenter(_image.getWidth(), _image.getHeight(), true);
        int rotationDegrees = _frameProxy.getImageInfo().getRotationDegrees();
        _drawListener.setImageSourceInfo(_image.getWidth(), _image.getHeight(), rotationDegrees, true);

        if (faces.isEmpty()) {
            if (_cameraView != null) {
                SingleToast.show(_cameraView, "No face on screen", Toast.LENGTH_SHORT);
                _drawListener.drawCenterBounds(centerBounds, Color.RED);
            } else {
                Log.i("FaceDetectionApp", "No face on screen");
            }
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
            } else {
                _drawListener.drawCenterBounds(centerBounds, Color.GREEN);
            }
            //Log.i("FaceDetectionApp", bounds.toString());

            //Bitmap scaledBitmap = Bitmap.createScaledBitmap(BitmapUtils.getCropBitmap(_frameProxy, bounds), 260, 320, true);
            Bitmap scaledBitmap = BitmapUtils.getBitmap(_frameProxy);
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
                    Log.i("FaceDetectionApp", String.valueOf(mse));
                    //BitmapUtils.saveFrame(_bs.getForegroundBitmap(), "/data/user/0/com.ensicaen.facialdetectionapp/output/ld/bsBitmap_"+_frameProcessed+".png");
                    //((ImageView)_datasetView.findViewById(R.id.dataset_frame)).setImageBitmap(_bs.getForegroundBitmap());
                }
            }

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

            _drawListener.drawFacePoints(face.getContour(FaceContour.FACE).getPoints());
            _drawListener.drawFaceBounds(bounds);
        }
    }

    @Override
    public void onFailure(Exception e) {
        Log.e("FaceDetectionApp", e.toString());
    }
}

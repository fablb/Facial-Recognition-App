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
import com.ensicaen.facialdetectionapp.utils.LBP;
import com.ensicaen.facialdetectionapp.utils.Point2D;
import com.ensicaen.facialdetectionapp.utils.SingleToast;
import com.ensicaen.facialdetectionapp.utils.SizedArrayList;
import com.ensicaen.facialdetectionapp.view.CameraView;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;

import java.util.List;

public class FaceAcquisitionListener extends FaceListener {
    private FrameListener _drawListener;
    private CameraView _cameraView;
    private SizedArrayList<Point2D> _faceBoundsCenter;
    private LBP _lbp;

    public FaceAcquisitionListener(FrameListener drawListener, CameraView cameraView) {
        _drawListener = drawListener;
        _cameraView = cameraView;
        _faceBoundsCenter = new SizedArrayList<>(STABLE_SCREEN_FRAME_COUNT);
        _lbp = new LBP();
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
            //SingleToast.clear();
            Bitmap cropBitmap = BitmapUtils.getCropBitmap(_frameProxy, bounds);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(cropBitmap, 140, 140, true);
            _cameraView.close(_lbp.compute(scaledBitmap));

            //Log.i("FaceDetectionApp", p.get_name() + " " + p.get_date() + " " + Arrays.toString(p.get_features()));
            //Profile b = db.searchByName("Fabien")[0];
            //Log.i("FaceDetectionApp", "Search: " + b.get_name() + " " + b.get_date() + " " + Arrays.toString(b.get_features()));
            //((ImageView)_cameraView.findViewById(R.id.face_acquisition)).setImageBitmap(cropBitmap);
        }
    }



    @Override
    public void onFailure(Exception e) {
        Log.e("FaceDetectionApp", e.toString());
    }
}

package com.ensicaen.facialdetectionapp.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.ensicaen.facialdetectionapp.R;
import com.ensicaen.facialdetectionapp.utils.BitmapUtils;
import com.ensicaen.facialdetectionapp.utils.FaceUtils;
import com.ensicaen.facialdetectionapp.utils.Point2D;
import com.ensicaen.facialdetectionapp.utils.SingleToast;
import com.ensicaen.facialdetectionapp.utils.SizedArrayList;
import com.ensicaen.facialdetectionapp.view.CameraView;
import com.ensicaen.facialdetectionapp.view.MainActivity;
import com.google.mlkit.vision.face.Face;

import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class FaceAcquisitionListener extends FaceListener {
    private FrameListener _drawListener;
    private CameraView _cameraView;
    private SizedArrayList<Point2D> _faceBoundsCenter;

    public FaceAcquisitionListener(FrameListener drawListener, CameraView cameraView) {
        _drawListener = drawListener;
        _cameraView = cameraView;
        _faceBoundsCenter = new SizedArrayList<>(6);
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
            if (meanDistance > 10) {
                SingleToast.show(_cameraView, "Face or mobile phone is moving", Toast.LENGTH_SHORT);
                _drawListener.drawCenterBounds(centerBounds, Color.RED);
                return;
            }
            _drawListener.drawCenterBounds(centerBounds, Color.GREEN);
            SingleToast.clear();
            ((ImageView)_cameraView.findViewById(R.id.face_acquisition)).setImageBitmap(BitmapUtils.getCropBitmap(_frameProxy, bounds));
        }
    }

    @Override
    public void onFailure(Exception e) {
        Log.e("FaceDetectionApp", e.toString());
    }
}

package com.ensicaen.facialdetectionapp.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.util.Log;
import android.widget.Toast;

import com.ensicaen.facialdetectionapp.utils.SingleToast;
import com.ensicaen.facialdetectionapp.view.CameraView;
import com.ensicaen.facialdetectionapp.view.MainActivity;
import com.google.mlkit.vision.face.Face;

import java.util.List;

public class FaceAcquisitionListener extends FaceListener {
    private CameraView _cameraView;

    public FaceAcquisitionListener(CameraView cameraView) {
        _cameraView = cameraView;
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public void onSuccess(Object o) {
        List<Face> faces = (List<Face>) o;

        if (faces.isEmpty()) {
            SingleToast.show(_cameraView, "No face on screen!", Toast.LENGTH_SHORT);
        }
        for (Face face : faces) {
            Rect bounds = face.getBoundingBox();

            _cameraView.close();

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
            /*int rotationDegrees = _frameProxy.getImageInfo().getRotationDegrees();
            if (rotationDegrees == 0 || rotationDegrees == 180) {
                _frameListener.setImageSourceInfo(_image.getWidth(), _image.getHeight(), true);
            } else {
                _frameListener.setImageSourceInfo(_image.getHeight(), _image.getWidth(), true);
            }*/
        }
    }

    @Override
    public void onFailure(Exception e) {
        Log.e("FaceDetectionApp", e.toString());
    }
}

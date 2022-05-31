package com.ensicaen.facialdetectionapp.controler;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import android.util.Size;

import androidx.camera.core.ImageProxy;

import com.ensicaen.facialdetectionapp.utils.BitmapUtils;
import com.ensicaen.facialdetectionapp.utils.FaceUtils;
import com.ensicaen.facialdetectionapp.utils.RectUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;

import java.util.List;

public class FaceDetectorListener implements OnSuccessListener, OnFailureListener {
    private InputImage _image;
    private ImageProxy _frameProxy;
    private FrameListener _frameListener;
    private SharedPreferences _preferences;
    private Size _previewViewSize;

    public FaceDetectorListener(FrameListener frameListener, SharedPreferences preferences, Size previewViewSize) {
        _image = null;
        _frameProxy = null;
        _frameListener = frameListener;
        _preferences = preferences;
        _previewViewSize = previewViewSize;
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
        }
        for (Face face : faces) {
            Rect bounds = face.getBoundingBox();

            /* Stops if the face is not centered */
            if (!FaceUtils.faceCenteringDetection(face, _image.getWidth(), _image.getHeight())) {
                return;
            }

            Bitmap faceBitmap = BitmapUtils.getCropBitmap(_frameProxy, bounds);

            if (_preferences.getBoolean("switch_save_face", false)) {
                FrameAnalyzer.saveFrame("files/"+String.valueOf(_frameProxy.getImage().getTimestamp()), faceBitmap);
            }

            /* Bounds needs to be transformed as previewView and ImageProxy dimensions are different */
            _frameListener.drawFaceBounds(RectUtils.transformBounds(bounds, _previewViewSize.getWidth(), _previewViewSize.getHeight(),_image.getHeight(), _image.getWidth())); // Swap width and height as mobile phone is in portrait mode
        }
    }

    @Override
    public void onFailure(Exception e) {
        Log.e("FaceDetectionApp", e.toString());
    }
}

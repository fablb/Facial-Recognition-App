package com.ensicaen.facialdetectionapp.controller;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;

import androidx.camera.core.ImageProxy;

import com.ensicaen.facialdetectionapp.utils.BitmapUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

public abstract class FaceListener implements OnSuccessListener, OnFailureListener {
    protected InputImage _image = null;
    protected ImageProxy _frameProxy = null;
    private static final float CLOSED_EYE_THRESHOLD = 0.65f;

    abstract public void onSuccess(Object o);

    abstract public void onFailure(Exception e);

    public void setInputImage(InputImage image) {
        _image = image;
    }

    public void setImageProxy(ImageProxy frameProxy) {
        _frameProxy = frameProxy;
    }
}

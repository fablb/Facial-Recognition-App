package com.ensicaen.facialdetectionapp.controller;

import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;

public abstract class FaceListener implements OnSuccessListener, OnFailureListener {
    protected InputImage _image;
    protected ImageProxy _frameProxy;

    abstract public void onSuccess(Object o);

    abstract public void onFailure(Exception e);

    public void setInputImage(InputImage image) {
        _image = image;
    }

    public void setImageProxy(ImageProxy frameProxy) {
        _frameProxy = frameProxy;
    }
}

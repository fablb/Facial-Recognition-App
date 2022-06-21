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
    protected InputImage _image;
    protected ImageProxy _frameProxy;
    protected static final int STABLE_SCREEN_FRAME_COUNT = 9;
    protected static final int STABLE_SCREEN_THRESHOLD = 10;
    private final float THRESHOLD = 0.70f;

    abstract public void onSuccess(Object o);

    abstract public void onFailure(Exception e);

    public void setInputImage(InputImage image) {
        _image = image;
    }

    public void setImageProxy(ImageProxy frameProxy) {
        _frameProxy = frameProxy;
    }

    public boolean lookAtTheCamera(List<PointF> rightEye, List<PointF> leftEye) {
        return leftEyeLooking(leftEye) && rightEyeLooking(rightEye);
    }

    public boolean leftEyeLooking(List<PointF> leftEye) {
        int iris = 0;
        int average = eyeAverageColor(leftEye);

        Rect eye = new Rect((int)leftEye.get(3).x, (int)leftEye.get(3).y, (int)leftEye.get(11).x, (int)leftEye.get(11).y);
        Bitmap bmp = BitmapUtils.getCropBitmap(_frameProxy, eye);

        for (int row = 0; row < bmp.getHeight(); row++) {
            for (int column = 0;column < bmp.getWidth(); column++) {
                if ( BitmapUtils.getPixelGray(bmp, column, row) < average ) {
                    iris++;
                }
            }
        }
        return iris/(float)(bmp.getHeight()*bmp.getWidth()) > THRESHOLD;
    }

    public boolean rightEyeLooking(List<PointF> rightEye) {
        int iris = 0;
        int average = eyeAverageColor(rightEye);

        Rect eye = new Rect((int)rightEye.get(3).x, (int)rightEye.get(3).y, (int)rightEye.get(11).x, (int)rightEye.get(11).y);
        Bitmap bmp = BitmapUtils.getCropBitmap(_frameProxy, eye);

        for (int row = 0; row < bmp.getHeight(); row++) {
            for (int column = 0;column < bmp.getWidth(); column++) {
                if (BitmapUtils.getPixelGray(bmp, column, row) < average ) {
                    iris++;
                }
            }
        }
        return iris/(float)(bmp.getHeight()*bmp.getWidth()) > THRESHOLD;
    }

    public int eyeAverageColor(List<PointF> eye) {
        int average = 0, pixelCount = 0;
        Rect eyeRect = new Rect((int)eye.get(2).x, (int)eye.get(2).y, (int)eye.get(10).x, (int)eye.get(10).y);
        Bitmap bmp = BitmapUtils.getCropBitmap(_frameProxy, eyeRect);
        for (int row = 0; row < bmp.getHeight(); row++) {
            for (int column = 0;column < bmp.getWidth(); column++) {
                average += BitmapUtils.getPixelGray(bmp, column, row);
                pixelCount++;
            }
        }
        return average/(pixelCount);
    }

}

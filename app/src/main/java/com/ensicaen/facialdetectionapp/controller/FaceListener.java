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
    private final float CENTER_THRESHOLD = 0.70f;
    private final float OPEN_TRHESHOLD = 500f;

    abstract public void onSuccess(Object o);

    abstract public void onFailure(Exception e);

    public void setInputImage(InputImage image) {
        _image = image;
    }

    public void setImageProxy(ImageProxy frameProxy) {
        _frameProxy = frameProxy;
    }

    public boolean eyesOpen(List<PointF> rightEye, List<PointF> leftEye) {
        return eyeOpen(leftEye) && eyeOpen(rightEye);
    }

    public boolean eyeOpen(List<PointF> eye) {
        double standard_deviation = 0.0;
        float average = eyeAverageColor(eye);

        Rect eyeRect = new Rect((int)eye.get(0).x, (int)eye.get(4).y, (int)eye.get(8).x, (int)eye.get(12).y);
        Bitmap bmp = BitmapUtils.getCropBitmap(_frameProxy, eyeRect);

        int row = bmp.getHeight()/2;

        for (int column = 1; column < bmp.getWidth(); column++) {
            standard_deviation += (float)Math.pow(average - BitmapUtils.getPixelGray(bmp, column, row), 2);
        }
        return (standard_deviation/(float)bmp.getWidth()) > OPEN_TRHESHOLD;
    }

    public boolean lookAtTheCamera(List<PointF> rightEye, List<PointF> leftEye) {
        return eyeLooking(leftEye) && eyeLooking(rightEye);
    }

    public boolean eyeLooking(List<PointF> eye) {
        int iris = 0;
        float average = eyeAverageColor(eye);

        Rect eyeRect = new Rect((int)eye.get(3).x, (int)eye.get(3).y, (int)eye.get(11).x, (int)eye.get(11).y);
        Bitmap bmp = BitmapUtils.getCropBitmap(_frameProxy, eyeRect);

        for (int row = 0; row < bmp.getHeight(); row++) {
            for (int column = 0;column < bmp.getWidth(); column++) {
                if ( BitmapUtils.getPixelGray(bmp, column, row) < average ) {
                    iris++;
                }
            }
        }
        return iris/(float)(bmp.getHeight()*bmp.getWidth()) > CENTER_THRESHOLD;
    }

    public float eyeAverageColor(List<PointF> eye) {
        float average = 0;
        int pixelCount = 0;
        Rect eyeRect = new Rect((int)eye.get(2).x, (int)eye.get(2).y, (int)eye.get(10).x, (int)eye.get(10).y);
        Bitmap bmp = BitmapUtils.getCropBitmap(_frameProxy, eyeRect);
        for (int row = 0; row < bmp.getHeight(); row++) {
            for (int column = 0;column < bmp.getWidth(); column++) {
                average += BitmapUtils.getPixelGray(bmp, column, row);
                pixelCount++;
            }
        }
        return average/(float)(pixelCount);
    }

}

package com.ensicaen.facialdetectionapp.controller;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.widget.Toast;

import com.ensicaen.facialdetectionapp.utils.BitmapUtils;
import com.ensicaen.facialdetectionapp.utils.FaceUtils;
import com.ensicaen.facialdetectionapp.utils.Point2D;
import com.ensicaen.facialdetectionapp.utils.SingleToast;
import com.ensicaen.facialdetectionapp.view.CameraView;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;

import java.util.ArrayList;
import java.util.List;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Tools.ImageStatistics;

public class LivenessDetectorListener extends FaceListener {
    private FrameListener _drawListener;
    private CameraView _cameraView;
    private float _lastEulerX;
    private float _lastEulerY;
    private float _lastEulerZ;
    private FastBitmap _lastBounds;
    private int _frameProcessed;


    public LivenessDetectorListener(FrameListener drawListener, CameraView cameraView) {
        _drawListener = drawListener;
        _cameraView = cameraView;
        _lastEulerX = 0.0f;
        _lastEulerY = 0.0f;
        _lastEulerZ = 0.0f;
        _lastBounds = null;
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
            float eulerXDifference = _lastEulerX - eulerX;
            float eulerYDifference = _lastEulerY - eulerY;
            float eulerZDifference = _lastEulerZ - eulerZ;
            //Log.i("FaceDetectionApp",  eulerX + "_" + eulerY + "_" + eulerZ);
            _lastEulerX = eulerX;
            _lastEulerY = eulerY;
            _lastEulerZ = eulerZ;

            //Log.i("FaceDetectionApp", String.valueOf(ImageStatistics.Mean(crop)));

            PointF leftEar = face.getContour(FaceContour.FACE).getPoints().get(28);
            PointF rightEar = face.getContour(FaceContour.FACE).getPoints().get(8);
            PointF forehead = face.getContour(FaceContour.FACE).getPoints().get(0);
            PointF chin = face.getContour(FaceContour.FACE).getPoints().get(18);
            float faceWidth = rightEar.x - leftEar.x;
            float faceHeight = forehead.y - chin.y;
            float offsetX = 0.16f * faceWidth;
            float offsetY = 0.16f * faceHeight;
            Rect faceBounds = new Rect((int)(rightEar.x + offsetX), (int)(forehead.y + offsetY), (int)(leftEar.x - offsetX), (int)(chin.y - offsetY));
            Rect _customBounds = new Rect((int)(leftEar.x - offsetX), (int)(forehead.y + offsetY), (int)(rightEar.x + offsetX), (int)(chin.y - offsetY));
            //Log.i("FaceDetectionApp", bounds.exactCenterX() + "_" + bounds.exactCenterY() + "_" + _customBounds.exactCenterX() + "_" + _customBounds.exactCenterY());

            //Log.i("FaceDetectionApp", distance + "_" + bounds.width());
            //Log.i("FaceDetectionApp", distance / _image.getWidth() + "_" + (float)bounds.width() / (float)_image.getWidth() + "_" + (float)bounds.height() / (float)_image.getHeight());

            //Rect customBounds = new Rect()
            //Log.i("FaceDetectionApp", String.valueOf(_customBounds.height()));
            FastBitmap custom = new FastBitmap(BitmapUtils.getCropBitmap(_frameProxy, _customBounds));
            //custom.toGrayscale();

            float sumDifferenceRGB = 0.0f;
            float meanDifferenceRGB = 0.0f;
            float sumDifferenceGray = 0.0f;
            float meanDifferenceGray = 0.0f;
            float i = 0.0f;
            _frameProcessed += 1;
            if (_lastBounds != null) {
                for (int x = 0; x < _lastBounds.getWidth() && x < custom.getWidth(); x++) {
                    for (int y = 0; y < _lastBounds.getHeight() && y < custom.getHeight(); y++) {
                        //if (!faceBounds.contains(x, y)) {
                        sumDifferenceRGB += (Math.abs(_lastBounds.getRed(x, y) - custom.getRed(x, y)) + Math.abs(_lastBounds.getGreen(x, y) - custom.getGreen(x, y)) + Math.abs(_lastBounds.getBlue(x, y) - custom.getBlue(x, y))) / 3.0f;
                        sumDifferenceGray += Math.abs(_lastBounds.getGray(x, y) - custom.getGray(x, y));
                        i++;
                        //}
                    }
                }
                meanDifferenceRGB = sumDifferenceRGB / i;
                meanDifferenceGray = sumDifferenceGray / i;
                Log.i("FaceDetectionApp", meanDifferenceRGB + "_" + meanDifferenceGray);
            }
            _lastBounds = custom;

            List<PointF> pointsDraw = new ArrayList<>();
            pointsDraw.add(leftEar);
            pointsDraw.add(rightEar);
            pointsDraw.add(forehead);
            pointsDraw.add(chin);
            pointsDraw.add(new PointF(_customBounds.centerX(), _customBounds.centerY()));
            //_drawListener.drawPoints(pointsDraw);
            //_drawListener.drawFacePoints(face.getContour(FaceContour.FACE).getPoints());
            _drawListener.drawFaceBounds(_customBounds);
        }
    }

    @Override
    public void onFailure(Exception e) {
        Log.e("FaceDetectionApp", e.toString());
    }
}

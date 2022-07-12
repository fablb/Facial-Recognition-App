package com.ensicaen.facialdetectionapp.controller;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.ensicaen.facialdetectionapp.R;
import com.ensicaen.facialdetectionapp.utils.BackgroundSubtraction;
import com.ensicaen.facialdetectionapp.utils.BitmapUtils;
import com.ensicaen.facialdetectionapp.utils.FaceUtils;
import com.ensicaen.facialdetectionapp.utils.Point2D;
import com.ensicaen.facialdetectionapp.utils.SingleToast;
import com.ensicaen.facialdetectionapp.view.CameraView;
import com.ensicaen.facialdetectionapp.view.DatasetView;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Tools.ImageStatistics;
import Catalano.Imaging.Tools.ObjectiveFidelity;

public class LivenessDetectorListener extends FaceListener {
    private FrameListener _drawListener;
    private DatasetView _datasetView;
    private CameraView _cameraView;
    private float _lastEulerX;
    private float _lastEulerY;
    private float _lastEulerZ;
    private Rect _lastBounds;
    private FastBitmap _lastBoundsBitmap;
    private int _frameProcessed;
    private Point[] _lastFacePoints;
    private BackgroundSubtraction _bs;
    private double _processingTime;


    public LivenessDetectorListener(FrameListener drawListener, CameraView cameraView) {
        _drawListener = drawListener;
        _cameraView = cameraView;
        _lastEulerX = 0.0f;
        _lastEulerY = 0.0f;
        _lastEulerZ = 0.0f;
        _lastBounds = null;
        _lastBoundsBitmap = null;
        _frameProcessed = 0;
        _lastFacePoints = new Point[5];
        _bs = null;
        _processingTime = 0.0f;
    }

    public LivenessDetectorListener(DatasetView datasetView) {
        _drawListener = null;
        _cameraView = null;
        _datasetView = datasetView;
        _lastEulerX = 0.0f;
        _lastEulerY = 0.0f;
        _lastEulerZ = 0.0f;
        _lastBounds = null;
        _lastBoundsBitmap = null;
        _frameProcessed = 0;
        _lastFacePoints = new Point[5];
        _bs = null;
        _processingTime = 0.0f;
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public void onSuccess(Object o) {
        long start = System.currentTimeMillis();
        List<Face> faces = (List<Face>) o;

        Rect centerBounds = FaceUtils.getCenter(_image.getWidth(), _image.getHeight(), true);
        int rotationDegrees = 0;
        if (_frameProxy != null) {
            rotationDegrees = _frameProxy.getImageInfo().getRotationDegrees();
        }
        if (_drawListener != null) {
            _drawListener.setImageSourceInfo(_image.getWidth(), _image.getHeight(), rotationDegrees, true);
        }

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
            /*
            if (!centerBounds.contains(bounds)) {
                if (_cameraView != null) {
                    SingleToast.show(_cameraView, "Face is not centered", Toast.LENGTH_SHORT);
                    _drawListener.drawCenterBounds(centerBounds, Color.RED);
                } else {
                    Log.i("FaceDetectionApp", "Face is not centered");
                }
                return;
            } else {
                if (_drawListener != null) {
                    _drawListener.drawCenterBounds(centerBounds, Color.GREEN);
                }
            }*/
            //float eulerXDifference = _lastEulerX - eulerX;
            //float eulerYDifference = _lastEulerY - eulerY;
            //float eulerZDifference = _lastEulerZ - eulerZ;
            //Log.i("FaceDetectionApp",  eulerX + "_" + eulerY + "_" + eulerZ);

            //Log.i("FaceDetectionApp", String.valueOf(ImageStatistics.Mean(crop)));
/*
            PointF leftEar, rightEar, forehead, nose, chin;
            try {
                leftEar = Objects.requireNonNull(face.getContour(FaceContour.FACE)).getPoints().get(28);
                rightEar = Objects.requireNonNull(face.getContour(FaceContour.FACE)).getPoints().get(8);
                forehead = Objects.requireNonNull(face.getContour(FaceContour.FACE)).getPoints().get(0);
                nose = Objects.requireNonNull(face.getContour(FaceContour.NOSE_BRIDGE)).getPoints().get(1);
                chin = Objects.requireNonNull(face.getContour(FaceContour.FACE)).getPoints().get(18);
            } catch (NullPointerException e) {
                //_drawListener.drawFacePoints(null);
                return;
            }*/
            //float faceWidth = rightEar.x - leftEar.x;
            //float faceHeight = forehead.y - chin.y;
            //float offsetX = 0.16f * faceWidth;
            //float offsetY = 0.16f * faceHeight;
            //Rect faceBounds = new Rect((int)(rightEar.x + offsetX), (int)(forehead.y + offsetY), (int)(leftEar.x - offsetX), (int)(chin.y - offsetY));
            //Rect _customBounds = new Rect((int)(leftEar.x - offsetX), (int)(forehead.y + offsetY), (int)(rightEar.x + offsetX), (int)(chin.y - offsetY));
            //Log.i("FaceDetectionApp", bounds.exactCenterX() + "_" + bounds.exactCenterY() + "_" + _customBounds.exactCenterX() + "_" + _customBounds.exactCenterY());

            //Log.i("FaceDetectionApp", distance + "_" + bounds.width());
            //Log.i("FaceDetectionApp", distance / _image.getWidth() + "_" + (float)bounds.width() / (float)_image.getWidth() + "_" + (float)bounds.height() / (float)_image.getHeight());

            //Rect customBounds = new Rect()
            //Log.i("FaceDetectionApp", String.valueOf(_customBounds.height()));

            if (_frameProcessed % 5 == 0) {
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(BitmapUtils.getCropBitmap(_image, bounds), 140, 140, true);

                FastBitmap custom = new FastBitmap(scaledBitmap);

                if (_bs == null) {
                    _bs = new BackgroundSubtraction(custom, 30);
                } else {
                    Bitmap lastFg = _bs.getForegroundBitmap();
                    _bs.update(custom);
                    if (_frameProcessed > 10) {
                        ObjectiveFidelity of = new ObjectiveFidelity(new FastBitmap(lastFg), _bs.getForeground());

                        int error = of.getTotalError();
                        double mse = of.getMSE();
                        double snr = of.getSNR();
                        double dSnr = of.getDerivativeSNR();
                        double psnr = of.getPSNR();
                        if (mse > 50) {
                            Log.i("FaceDetectionApp", "Alive! " + mse);
                        }
                        //((ImageView)_datasetView.findViewById(R.id.dataset_frame)).setImageBitmap(_bs.getForegroundBitmap());
                    }
                }
            }

            _frameProcessed += 1;
            _processingTime = _processingTime + (((System.currentTimeMillis() - start) - _processingTime) / _frameProcessed);

            //Log.i("FaceDetectionApp", String.valueOf(_processingTime));

            /*
            if (_lastBoundsBitmap != null) {
                ObjectiveFidelity of = new ObjectiveFidelity(_lastBoundsBitmap, custom);

                int error = of.getTotalError();
                double mse = of.getMSE();
                double snr = of.getSNR();
                double dSnr = of.getDerivativeSNR();
                double psnr = of.getPSNR();
                Log.i("FaceDetectionApp", error + "_" + mse + "_" + snr + "_" + dSnr + "_" + psnr);
            }
            _lastBoundsBitmap = custom;

            custom.toGrayscale();

            int sumPixelDiff = 0;
            int meanPixelDiff;
            int[] pixelDiffCount = {0,0,0,0,0,0,0};
            if (_lastBoundsBitmap != null) {
                for (int x = 0; x < custom.getWidth(); x++) {
                    for (int y = 0; y < custom.getHeight(); y++) {
                        int currentGray = custom.getGray(x, y);
                        int lastGray = _lastBoundsBitmap.getGray(x, y);
                        int diff = Math.abs(currentGray - lastGray);
                        sumPixelDiff += diff;
                        if (diff > 0) {
                            pixelDiffCount[0] += 1;
                            if (diff > 8) {
                                pixelDiffCount[1] += 1;
                                if (diff > 16) {
                                    pixelDiffCount[2] += 1;
                                    if (diff > 24) {
                                        pixelDiffCount[3] += 1;
                                        if (diff > 32) {
                                            pixelDiffCount[4] += 1;
                                            if (diff > 40) {
                                                pixelDiffCount[5] += 1;
                                                if (diff > 48) {
                                                    pixelDiffCount[6] += 1;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                meanPixelDiff = sumPixelDiff / (custom.getWidth() * custom.getHeight());
                Log.i("FaceDetectionApp",
                        bounds.width() + "_" +
                        bounds.height() + "_" +
                        bounds.centerX() + "_" +
                        bounds.centerY() + "_" +
                        eulerX + "_" +
                        eulerY + "_" +
                        eulerZ + "_" +
                        (int)leftEar.x + "_" +
                        (int)leftEar.y + "_" +
                        (int)rightEar.x + "_" +
                        (int)rightEar.y + "_" +
                        (int)forehead.x + "_" +
                        (int)forehead.y + "_" +
                        (int)nose.x + "_" +
                        (int)nose.y + "_" +
                        (int)chin.x + "_" +
                        (int)chin.y + "_" +
                        _lastBounds.width() + "_" +
                        _lastBounds.height() + "_" +
                        _lastBounds.centerX() + "_" +
                        _lastBounds.centerY() + "_" +
                        _lastEulerX + "_" +
                        _lastEulerY + "_" +
                        _lastEulerZ + "_" +
                        _lastFacePoints[0].x + "_" +
                        _lastFacePoints[0].y + "_" +
                        _lastFacePoints[1].x + "_" +
                        _lastFacePoints[1].y + "_" +
                        _lastFacePoints[2].x + "_" +
                        _lastFacePoints[2].y + "_" +
                        _lastFacePoints[3].x + "_" +
                        _lastFacePoints[3].y + "_" +
                        _lastFacePoints[4].x + "_" +
                        _lastFacePoints[4].y + "_" +
                        meanPixelDiff + "_" +
                        pixelDiffCount[0] + "_" +
                        pixelDiffCount[1] + "_" +
                        pixelDiffCount[2] + "_" +
                        pixelDiffCount[3] + "_" +
                        pixelDiffCount[4] + "_" +
                        pixelDiffCount[5] + "_" +
                        pixelDiffCount[6]);
            }
            _lastBounds = bounds;
            _lastBoundsBitmap = custom;
            _lastEulerX = eulerX;
            _lastEulerY = eulerY;
            _lastEulerZ = eulerZ;

            _lastFacePoints[0] = new Point((int)leftEar.x, (int)leftEar.y);
            _lastFacePoints[1] = new Point((int)rightEar.x, (int)rightEar.y);
            _lastFacePoints[2] = new Point((int)forehead.x, (int)forehead.y);
            _lastFacePoints[3] = new Point((int)nose.x, (int)nose.y);
            _lastFacePoints[4] = new Point((int)chin.x, (int)chin.y);
            */
            //_drawListener.drawFacePoints(face.getContour(FaceContour.FACE).getPoints());
        }
    }

    @Override
    public void onFailure(Exception e) {
        Log.e("FaceDetectionApp", e.toString());
    }
}

package com.ensicaen.facialdetectionapp.controller;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.widget.Toast;

import com.ensicaen.facialdetectionapp.utils.BitmapUtils;
import com.ensicaen.facialdetectionapp.utils.Point2D;
import com.ensicaen.facialdetectionapp.utils.SingleToast;
import com.ensicaen.facialdetectionapp.utils.SizedArrayList;

import java.util.List;

public class AttentionDetector {
    private int _imageWidth;
    private int _imageHeight;
    private boolean _imageFlipped;
    private Rect _insideRect; /* Rect describing a part of the image. This is used as ML Kit returns face's bounds even if the whole face is not inside the image */
    private SizedArrayList<Point2D> _keyPoints; /* Key points considered in the moving method */
    private final float INSIDE_THRESHOLD = 0.80f; /* Define inside rectangle size scaled on image size */
    private final float STRAIGHT_FACE_THRESHOLD_X = 7.0f; /* Pitch threshold (in degree) */
    private final float STRAIGHT_FACE_THRESHOLD_Y = 7.0f; /* Yaw threshold (in degree) */
    private final float STRAIGHT_FACE_THRESHOLD_Z = 7.0f; /* Roll threshold (in degree) */
    private final int MOVING_POINTS_CAPACITY = 6; /* Number of points considered in the moving method */
    protected final int STABLE_THRESHOLD = 10; /* Mean distance value considered as stable (face/mobile phone) */
    private final float EYE_CENTER_THRESHOLD = 0.70f; /* Define if an eye is centered */

    public AttentionDetector() {
        _keyPoints = new SizedArrayList<>(MOVING_POINTS_CAPACITY);
    }

    public AttentionDetector(int imageWidth, int imageHeight) {
        _imageWidth = imageWidth;
        _imageHeight = imageHeight;
        _imageFlipped = false;
        _insideRect = getInsideRect();
        _keyPoints = new SizedArrayList<>(MOVING_POINTS_CAPACITY);
    }

    public AttentionDetector(int imageWidth, int imageHeight, boolean imageFlipped) {
        _imageWidth = imageWidth;
        _imageHeight = imageHeight;
        _imageFlipped = imageFlipped;
        _insideRect = getInsideRect();
        _keyPoints = new SizedArrayList<>(MOVING_POINTS_CAPACITY);
    }

    public void setImageSize(int width, int height, boolean flipped) {
        _imageWidth = width;
        _imageHeight = height;
        _imageFlipped = flipped;
        _insideRect = getInsideRect();
    }

    /* Check if a face is straight, relatively to a defined threshold, based on euler angles */
    public boolean isStraight(float eulerX, float eulerY, float eulerZ) {
        if (eulerX < -STRAIGHT_FACE_THRESHOLD_X || eulerX > STRAIGHT_FACE_THRESHOLD_X) {
            return false;
        }
        if (eulerY < -STRAIGHT_FACE_THRESHOLD_Y || eulerY > STRAIGHT_FACE_THRESHOLD_Y) {
            return false;
        }
        if (eulerZ < -STRAIGHT_FACE_THRESHOLD_Z || eulerZ > STRAIGHT_FACE_THRESHOLD_Z) {
            return false;
        }
        return true;
    }

    /* Check if a given rect bounds are in the inside rect, if not we consider that the bounds are not in the image */
    public boolean isInside(Rect bounds) {
        return _insideRect.contains(bounds);
    }

    /* Check if a face is moving (or mobile phone) based on the mean distance between the point in args and the last points added.
    This is mainly used to avoid blur image.
    In our implementation the face's bounds center for each frame is taken and the mean distance is computed based
    on the last MOVING_POINTS_CAPACITY points.
     */
    public boolean isMoving(int pointX, int pointY) {
        Point2D point = new Point2D(pointX, pointY);

        /* Fill before computing mean movement */
        if (_keyPoints.size() < _keyPoints.capacity()) {
            _keyPoints.add(point);
            return true;
        }

        int meanDistance = point.meanDistance(_keyPoints);
        _keyPoints.add(point);

        return meanDistance > STABLE_THRESHOLD;
    }

    /* Check if the eyes are looking at the camera */
    public boolean isLooking(List<PointF> leftEye, List<PointF> rightEye, Bitmap image) {
        Rect leftEyeRect = new Rect((int)leftEye.get(3).x, (int)leftEye.get(3).y, (int)leftEye.get(11).x, (int)leftEye.get(11).y);
        Rect rightEyeRect = new Rect((int)rightEye.get(3).x, (int)rightEye.get(3).y, (int)rightEye.get(11).x, (int)rightEye.get(11).y);
        Bitmap leftEyeBitmap = BitmapUtils.getCropBitmap(image, leftEyeRect);
        Bitmap rightEyeBitmap = BitmapUtils.getCropBitmap(image, rightEyeRect);
        return eyeLooking(leftEyeBitmap) || eyeLooking(rightEyeBitmap);
    }

    /* Check if an eye is looking at the screen */
    public boolean eyeLooking(Bitmap eye) {
        int iris = 0;
        float average = eyeAverageColor(eye);

        for (int row = 0; row < eye.getHeight(); row++) {
            for (int column = 0;column < eye.getWidth(); column++) {
                if ( BitmapUtils.getPixelGray(eye, column, row) < average ) {
                    iris++;
                }
            }
        }
        return iris/(float)(eye.getHeight()*eye.getWidth()) > EYE_CENTER_THRESHOLD;
    }

    public float eyeAverageColor(Bitmap eye) {
        float average = 0;
        int pixelCount = 0;
        for (int row = 0; row < eye.getHeight(); row++) {
            for (int column = 0;column < eye.getWidth(); column++) {
                average += BitmapUtils.getPixelGray(eye, column, row);
                pixelCount++;
            }
        }
        return average/(float)(pixelCount);
    }

    /* Return inside rect which is scaled from the image size relatively to a defined threshold */
    public Rect getInsideRect() {
        int offsetX;
        int offsetY;
        int width = _imageWidth;
        int height = _imageHeight;
        Rect center;

        /* In portrait mode width and height are swapped compare to landscape */
        if (_imageFlipped) {
            width = _imageHeight;
            height = _imageWidth;
        }
        offsetX = (int) (width * (1.0f - INSIDE_THRESHOLD));
        offsetY = (int) (height * (1.0f - INSIDE_THRESHOLD));

        /* In portrait mode rect bounds are swapped compare to landscape */
        if (_imageFlipped) {
            center = new Rect(offsetX, offsetY, width - offsetX, height - offsetY);
        } else {
            center = new Rect(width - offsetX,height - offsetY, offsetX, offsetY);
        }

        return center;
    }
}

package com.ensicaen.facialdetectionapp.utils;

import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;

import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;
import com.google.mlkit.vision.face.FaceLandmark;

public class FaceUtils {

    /* Check if face is centered in image */
    public static boolean isCentered(Rect faceBounds, int w, int h, boolean imageFlipped) {
        int offsetX;
        int offsetY;
        int width = w;
        int height = h;
        Rect center;

        /* In portrait mode width and height are swapped compare to landscape */
        if (imageFlipped) {
            width = h;
            height = w;
        }
        offsetX = width / 10;
        offsetY = height / 10;

        /* In portrait mode rect bounds are swapped compare to landscape */
        if (imageFlipped) {
            center = new Rect(offsetX, offsetY, width - offsetX, height - offsetY);
        } else {
            center = new Rect(width - offsetX,height - offsetY, offsetX, offsetY);
        }

        return center.contains(faceBounds);
    }

    public static boolean isStraight(float eulerX, float eulerY, float eulerZ) {
        float xThreshold = 7.0f;
        float yThreshold = 7.0f;
        float zThreshold = 7.0f;

        if (eulerX < -xThreshold || eulerX > xThreshold) {
            return false;
        }
        if (eulerY < -yThreshold || eulerY > yThreshold) {
            return false;
        }
        if (eulerZ < -zThreshold || eulerZ > zThreshold) {
            return false;
        }
        return true;
    }


}

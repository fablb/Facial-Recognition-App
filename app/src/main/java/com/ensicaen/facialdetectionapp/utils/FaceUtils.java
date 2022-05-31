package com.ensicaen.facialdetectionapp.utils;

import android.graphics.PointF;
import android.graphics.Rect;

import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;
import com.google.mlkit.vision.face.FaceLandmark;

public class FaceUtils {

    /* Check if face is centered in image */
    public static boolean faceCenteringDetection(Face face, int width, int height) {
        /* In portrait mode width and height are swapped compare to landscape */
        int middleX = height / 2;
        int middleY = width / 2;
        int offsetX = height / 5;
        int offsetY = width / 5;
        Rect center = new Rect(middleX - offsetX,middleY - offsetY,middleX + offsetX, middleY + offsetY);
        PointF noise = face.getContour(FaceContour.NOSE_BRIDGE).getPoints().get(1);
        if (center.contains((int)noise.x, (int)noise.y)) {
            return true;
        } else {
            return false;
        }
    }
}

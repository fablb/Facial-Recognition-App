package com.ensicaen.facialdetectionapp.utils;

import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class RectUtils {
    public static Rect transformBounds(Rect bounds, int previewWidth, int previewHeight, int imageWidth, int imageHeight) {
        float scaleX = previewWidth / (float) imageWidth;
        float scaleY = previewHeight / (float) imageHeight;

        int flippedLeft = imageWidth - bounds.right;
        int flippedRight = imageWidth - bounds.left;

        // Scale all coordinates to match preview
        int scaledLeft = (int) (scaleX * flippedLeft);
        int scaledTop = (int) (scaleY * bounds.top);
        int scaledRight = (int) (scaleX * flippedRight);
        int scaledBottom = (int) (scaleY * bounds.bottom);

        return new Rect(scaledLeft, scaledTop, scaledRight, scaledBottom);
    }

    public static List<PointF> transformListPoint(List<PointF> contours, int previewWidth, int previewHeight, int imageWidth, int imageHeight) {
        List<PointF> scaledContours = new ArrayList<>();
        float scaleY = previewWidth / (float) imageWidth;
        float scaleX = previewHeight / (float) imageHeight;

        for (PointF point : contours) {
            scaledContours.add(new PointF(previewWidth - (point.x * scaleY), (point.y * scaleX)));
        }

        return scaledContours;
    }
}

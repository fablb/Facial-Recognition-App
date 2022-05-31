package com.ensicaen.facialdetectionapp.utils;

import android.graphics.Rect;

public class RectUtils {
    public static Rect transformBounds(Rect bounds, int previewWidth, int previewHeight, int imageWidth, int imageHeight) {
        float scaleX = previewWidth / (float)imageWidth;
        float scaleY = previewHeight / (float)imageHeight;

        int flippedLeft = imageWidth - bounds.right;
        int flippedRight = imageWidth - bounds.left;

        // Scale all coordinates to match preview
        int scaledLeft = (int) (scaleX * flippedLeft);
        int scaledTop = (int) (scaleY * bounds.top);
        int scaledRight = (int) (scaleX * flippedRight);
        int scaledBottom = (int) (scaleY * bounds.bottom);

        return new Rect(scaledLeft, scaledTop, scaledRight, scaledBottom);
    }
}

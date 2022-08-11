package com.ensicaen.facialdetectionapp.controller;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;

import java.util.List;

public interface FrameListener {
    public void setImageSourceInfo(int width, int height, int rotationDegrees, boolean isFlipped);
    public void drawFaceBounds(Rect face);
    public void drawCenterBounds(Rect face, int color);
    public void drawFacePoints(List<PointF> Line);
    public void drawPoints(List<PointF> points);

    public void drawEyesPoints(List<PointF> rightEye,List<PointF> leftEye);
}

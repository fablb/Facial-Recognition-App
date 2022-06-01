package com.ensicaen.facialdetectionapp.controler;

import android.graphics.PointF;
import android.graphics.Rect;

import java.util.List;

public interface FrameListener {
    public void setImageSourceInfo(int width, int height, boolean isFlipped);
    public void drawFaceBounds(Rect face);
    public void drawFaceLine(List<PointF> Line);
}

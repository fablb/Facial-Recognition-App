package com.ensicaen.facialdetectionapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ensicaen.facialdetectionapp.controler.FrameListener;

import java.util.ArrayList;

public class CameraOverlay extends View implements FrameListener {
    private ArrayList<Rect> faceBounds;
    private Paint paint;

    public CameraOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        faceBounds = new ArrayList<>();
        paint = new Paint();
        paint.setColor(Color.rgb(0, 255, 0));
        paint.setStrokeWidth(6);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!faceBounds.isEmpty()) {
            for (Rect face: faceBounds) {
                canvas.drawRect(face, paint);
            }
        }
    }

    @Override
    public void drawFaceBounds(Rect face) {
        faceBounds.clear();
        faceBounds.add(face);
        invalidate();
    }
}

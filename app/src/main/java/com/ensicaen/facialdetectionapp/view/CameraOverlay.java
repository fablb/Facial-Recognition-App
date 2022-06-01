package com.ensicaen.facialdetectionapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.preference.PreferenceManager;

import com.ensicaen.facialdetectionapp.controler.FrameListener;

import java.util.ArrayList;
import java.util.List;

public class CameraOverlay extends View implements FrameListener {
    private ArrayList<Rect> faceBounds;
    private List<PointF> faceContours;
    private Paint paint;
    private boolean clear;

    public CameraOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        faceContours = new ArrayList<>();
        faceBounds = new ArrayList<>();
        clear = false;
        paint = new Paint();
        paint.setColor(Color.rgb(0, 255, 0));
        paint.setStrokeWidth(6);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (clear) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY);
            clear = false;
            return;
        }

        if (!faceBounds.isEmpty()) {
            for (Rect face: faceBounds) {
                canvas.drawRect(face, paint);
            }
        }

        if(!faceContours.isEmpty()) {
            for (int i = 0; i < faceContours.size() - 1; i++) {
                canvas.drawLine(faceContours.get(i).x, faceContours.get(i).y, faceContours.get(i + 1).x, faceContours.get(i + 1).y, paint);
            }
        }
    }

    @Override
    public void drawFaceBounds(Rect face) {
        if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("switch_draw_face_bounds", false)) {
            faceBounds.clear();
            if (face != null) {
                faceBounds.add(face);
            }
            invalidate();
        }
    }

    @Override
    public void drawFaceLine(List<PointF> Line) {
        if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("switch_draw_face_bounds", false)) {
            faceContours.clear();
            if (Line != null) {
                for(PointF point: Line) {
                    faceContours.add(point);
                }
            }
            invalidate();
        }
    }

    public void clear() {
        clear = true;
        invalidate();
    }
}

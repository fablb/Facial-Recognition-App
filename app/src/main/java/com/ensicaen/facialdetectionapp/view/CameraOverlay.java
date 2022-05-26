package com.ensicaen.facialdetectionapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.preference.PreferenceManager;

import com.ensicaen.facialdetectionapp.controler.FrameListener;

import java.util.ArrayList;

public class CameraOverlay extends View implements FrameListener {
    private ArrayList<Rect> faceBounds;
    private Paint paint;
    private boolean clear;

    public CameraOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
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

    public void clear() {
        clear = true;
        invalidate();
    }
}

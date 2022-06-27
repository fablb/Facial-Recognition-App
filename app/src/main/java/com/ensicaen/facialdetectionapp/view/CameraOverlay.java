package com.ensicaen.facialdetectionapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.preference.PreferenceManager;

import com.ensicaen.facialdetectionapp.controller.FrameListener;

import java.util.ArrayList;
import java.util.List;

public class CameraOverlay extends View implements FrameListener {
    private ArrayList<Rect> faceBounds;
    private List<PointF> faceContours;
    private List<PointF> _points;
    private Rect centerBounds;
    private Paint paint;
    private int imageWidth;
    private int imageHeight;
    private float postScaleWidthOffset;
    private float postScaleHeightOffset;
    private float scaleFactor;
    private boolean isImageFlipped;

    public CameraOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        scaleFactor = 1.0f;
        faceContours = new ArrayList<>();
        faceBounds = new ArrayList<>();
        _points = new ArrayList<>();
        paint = new Paint();
        paint.setColor(Color.rgb(0, 255, 0));
        paint.setStrokeWidth(6);
        paint.setStyle(Paint.Style.STROKE);
        centerBounds = null;
    }

    @Override
    public void setImageSourceInfo(int width, int height, int rotationDegrees, boolean isFlipped) {
        /* Swap width and height for drawing depending on device orientation */
        if (rotationDegrees == 0 || rotationDegrees == 180) {
            imageWidth = width;
            imageHeight = height;
        } else {
            imageWidth = height;
            imageHeight = width;
        }
        isImageFlipped = isFlipped;
        updateTransformation();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!faceBounds.isEmpty()) {
            for (Rect face: faceBounds) {
                canvas.drawRect(face, paint);
            }
        }

        if(!faceContours.isEmpty()) {
            for (PointF point : faceContours) {
                canvas.drawCircle(point.x, point.y, 1, paint);
            }
        }

        if(centerBounds != null) {
            canvas.drawRect(centerBounds, paint);
        }

        if (!_points.isEmpty()) {
            for (PointF point : _points) {
                canvas.drawPoint(point.x, point.y, paint);
            }
        }
    }

    @Override
    public void drawPoints(List<PointF> points) {
        _points.clear();
        if (!points.isEmpty()) {
            for(PointF point: points) {
                _points.add(translatePoint(point));
            }
        }
        invalidate();
    }

    @Override
    public void drawFaceBounds(Rect face) {
        if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("switch_draw_face_bounds", false)) {
            faceBounds.clear();
            if (face != null) {
                faceBounds.add(translateRect(face));
            }
            invalidate();
        }
    }

    @Override
    public void drawCenterBounds(Rect center, int color) {
        paint.setColor(color);
        centerBounds = translateRect(center);
        invalidate();
    }

    @Override
    public void drawFacePoints(List<PointF> Line) {
        if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("switch_draw_face_bounds", false)) {
            faceContours.clear();
            if (Line != null) {
                for(PointF point: Line) {
                    faceContours.add(translatePoint(point));
                }
            }
            invalidate();
        }
    }

    public Rect translateRect(Rect rect) {
        float x = translateX(rect.centerX());
        float y = translateY(rect.centerY());

        int left = (int)(x - scale(rect.width() / 2.0f));
        int top = (int)(y - scale(rect.height() / 2.0f));
        int right = (int)(x + scale(rect.width() / 2.0f));
        int bottom = (int)(y + scale(rect.height() / 2.0f));

        return new Rect(left, top, right, bottom);
    }

    public PointF translatePoint(PointF point) {
        return new PointF(translateX(point.x), translateY(point.y));
    }

    /**
     * Adjusts the x coordinate from the image's coordinate system to the view coordinate system.
     */
    public float translateX(float x) {
        if (isImageFlipped) {
            return getWidth() - (scale(x) - postScaleWidthOffset);
        } else {
            return scale(x) - postScaleWidthOffset;
        }
    }

    /** Adjusts the supplied value from the image scale to the view scale. */
    public float scale(float imagePixel) {
        return imagePixel * scaleFactor;
    }

    /**
     * Adjusts the y coordinate from the image's coordinate system to the view coordinate system.
     */
    public float translateY(float y) {
        return scale(y) - postScaleHeightOffset;
    }

    private void updateTransformation() {
        float viewAspectRatio = (float) getWidth() / getHeight();
        float imageAspectRatio = (float) imageWidth / imageHeight;

        postScaleWidthOffset = 0;
        postScaleHeightOffset = 0;
        if (viewAspectRatio > imageAspectRatio) {
            // The image needs to be vertically cropped to be displayed in this view.
            scaleFactor = (float) getWidth() / imageWidth;
            postScaleHeightOffset = ((float) getWidth() / imageAspectRatio - getHeight()) / 2;
        } else {
            // The image needs to be horizontally cropped to be displayed in this view.
            scaleFactor = (float) getHeight() / imageHeight;
            postScaleWidthOffset = ((float) getHeight() * imageAspectRatio - getWidth()) / 2;
        }
    }
}

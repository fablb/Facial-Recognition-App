package com.ensicaen.facialdetectionapp.utils;

import android.graphics.Bitmap;

import Catalano.Imaging.FastBitmap;

public class BackgroundSubtraction {
    private FastBitmap _ini; /* Initial image */
    private FastBitmap _foreground; /* Foreground */
    private int _threshold;

    public BackgroundSubtraction(Bitmap ini, int threshold) {
        if (threshold < 0 || threshold > 255) {
            throw new IllegalArgumentException("Threshold must be between 0 and 255");
        }
        _ini = new FastBitmap(ini);
        _ini.toGrayscale();
        _threshold = threshold;
        init();
    }

    public BackgroundSubtraction(FastBitmap ini, int threshold) {
        if (threshold < 0 || threshold > 255) {
            throw new IllegalArgumentException("BackgroundSubtraction: threshold must be between 0 and 255");
        }
        if (ini.getColorSpace() != FastBitmap.ColorSpace.Grayscale) {
            ini.toGrayscale();
        }
        _ini = ini;
        _threshold = threshold;
        init();
    }

    private void init() {
        _foreground = new FastBitmap(_ini.getWidth(), _ini.getHeight(), FastBitmap.ColorSpace.Grayscale);
    }

    public void update(FastBitmap current) {
        if ((current.getWidth() != _ini.getWidth()) || (current.getHeight() != _ini.getHeight())) {
            throw new IllegalArgumentException("BackgroundSubtraction: all frames must have the same size");
        }
        for (int x = 0; x < current.getWidth(); x++) {
            for (int y = 0; y < current.getHeight(); y++) {
                int currentGray = current.getGray(x,y);
                if (Math.abs(currentGray - _ini.getGray(x,y)) > _threshold) {
                    _foreground.setGray(x,y,255);
                } else {
                    _foreground.setGray(x,y,0);
                }
            }
        }
        _ini = current;
    }

    public FastBitmap getForeground() {
        return _foreground;
    }

    public Bitmap getForegroundBitmap() {
        return _foreground.toBitmap();
    }
}
package com.ensicaen.facialdetectionapp.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/* Custom Toast class to avoid Toast spam and overlapping. If the Toast text does
   not change, wait a specified time (i.e SAME_TOAST_DURATION_BEFORE_OVERLAP) before
   overlapping it to avoid visual spamming. Otherwise, overlap directly the
   current Toast.
 */
public class SingleToast {
    private static Toast _toast;
    private static String _text;
    private static long _lastToast;
    private static final int SAME_TOAST_DURATION_BEFORE_OVERLAP = 2000; // in ms

    public static void show(Context context, String text, int duration) {
        if (_toast == null) {
            _toast = Toast.makeText(context.getApplicationContext(), text, duration);
            _text = text;
        } else {
            if (_text.equals(text)) {
                if (System.currentTimeMillis() - _lastToast > SAME_TOAST_DURATION_BEFORE_OVERLAP) {
                    _toast.cancel();
                } else {
                    return;
                }
            } else {
                _text = text;
                _toast.cancel();
                _toast.setText(_text);
            }
        }
        _lastToast = System.currentTimeMillis();
        _toast.show();
    }

    public static void clear() {
        if (_toast != null) {
            _toast.cancel();
        }
    }
}

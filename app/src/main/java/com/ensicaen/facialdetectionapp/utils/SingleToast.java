package com.ensicaen.facialdetectionapp.utils;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

public class SingleToast {
    private static Toast _toast;

    public static void show(Context context, String text, int duration) {
        if (_toast == null) {
            _toast = Toast.makeText(context.getApplicationContext(), text, duration);
            _toast.show();
        } else {
            if (_toast.getView().getWindowVisibility() != View.VISIBLE) { // Avoid Toast overlap
                _toast.setText(text);
                _toast.show();
            }
        }
    }
}

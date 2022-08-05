package com.ensicaen.facialdetectionapp;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;

enum SubjectType {
    NONE,
    NORMAL,
    GLASSES,
    HAPPY,
    LEFTLIGHT,
    RIGHTLIGHT,
    NOGLASSES,
    SAD,
    SLEEPY,
    SURPRISED,
    WINK
}

public class Subject {
    private EnumMap<SubjectType, Bitmap> _images;
    private String _name;
    private int[] resLog = new int[]{0, 31, 50, 63, 74, 82, 89, 95, 101, 106, 110, 114, 118, 121, 124, 127, 130, 133, 135, 137, 140, 142, 144, 146, 148, 149, 151, 153, 155, 156, 158, 159, 160, 162, 163, 164, 166, 167, 168, 169, 170, 172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 181, 182, 183, 184, 185, 186, 186, 187, 188, 189, 189, 190, 191, 192, 192, 193, 194, 194, 195, 196, 196, 197, 198, 198, 199, 199, 200, 201, 201, 202, 202, 203, 203, 204, 205, 205, 206, 206, 207, 207, 208, 208, 209, 209, 210, 210, 211, 211, 212, 212, 212, 213, 213, 214, 214, 215, 215, 215, 216, 216, 217, 217, 218, 218, 218, 219, 219, 220, 220, 220, 221, 221, 221, 222, 222, 223, 223, 223, 224, 224, 224, 225, 225, 225, 226, 226, 226, 227, 227, 227, 228, 228, 228, 229, 229, 229, 230, 230, 230, 230, 231, 231, 231, 232, 232, 232, 233, 233, 233, 233, 234, 234, 234, 235, 235, 235, 235, 236, 236, 236, 236, 237, 237, 237, 238, 238, 238, 238, 239, 239, 239, 239, 240, 240, 240, 240, 241, 241, 241, 241, 242, 242, 242, 242, 242, 243, 243, 243, 243, 244, 244, 244, 244, 245, 245, 245, 245, 245, 246, 246, 246, 246, 247, 247, 247, 247, 247, 248, 248, 248, 248, 248, 249, 249, 249, 249, 249, 250, 250, 250, 250, 250, 251, 251, 251, 251, 251, 252, 252, 252, 252, 252, 253, 253, 253, 253, 253, 253, 254, 254, 254, 254, 254, 255, 255};

    public Subject(String name) {
        _name = name;
        _images = new EnumMap<>(SubjectType.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void addImage(SubjectType type, Bitmap image) {

        int newGray;
        Bitmap imgMutable = image.copy(Bitmap.Config.ARGB_8888,true);
        for (int x = 0; x < imgMutable.getWidth(); x++) {
            for (int y = 0; y < imgMutable.getHeight(); y++) {
                newGray = resLog[(int)(imgMutable.getColor(x,y).red() * 255)];
                imgMutable.setPixel(x, y, Color.rgb(newGray, newGray, newGray));
            }
        }
        //Log.i("FaceDetectionApp", _name + type + String.valueOf(image.getColor(75,75).red() * 255));
        saveImage("illuOutput/" + _name + "_" + type, imgMutable);
        _images.put(type, imgMutable);
    }

    public double sRGB_to_linear(double x) {
        if (x < 0.04045) return x/12.92;
        return Math.pow((x+0.055)/1.055, 2.4);
    }

    public Bitmap getImage(SubjectType type) {
        return _images.get(type);
    }

    public static SubjectType parseType(String type) {
        switch (type) {
            case "normal":
                return SubjectType.NORMAL;
            case "glasses":
                return SubjectType.GLASSES;
            case "happy":
                return SubjectType.HAPPY;
            case "leftlight":
                return SubjectType.LEFTLIGHT;
            case "rightlight":
                return SubjectType.RIGHTLIGHT;
            case "noglasses":
                return SubjectType.NOGLASSES;
            case "sad":
                return SubjectType.SAD;
            case "sleepy":
                return SubjectType.SLEEPY;
            case "surprised":
                return SubjectType.SURPRISED;
            case "wink":
                return SubjectType.WINK;
            default:
                return SubjectType.NONE;
        }
    }

    public static void saveImage(String path, Bitmap frame) {
        try {
            @SuppressLint("UnsafeOptInUsageError") FileOutputStream out = new FileOutputStream("/data/user/0/com.ensicaen.facialdetectionapp/"+path+".png");
            frame.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

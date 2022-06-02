package com.ensicaen.facialdetectionapp;

import android.graphics.Bitmap;

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

    public Subject(String name) {
        _name = name;
        _images = new EnumMap<>(SubjectType.class);
    }

    public void addImage(SubjectType type, Bitmap image) {
        _images.put(type, image);
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
}

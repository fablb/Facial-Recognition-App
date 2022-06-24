package com.ensicaen.facialdetectionapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class LBPRecognitionPerformance {
    private File _datasetPath;
    private HashMap<String, Subject> _dataset;
    private LBPRecognition _recognition;

    public LBPRecognitionPerformance(String datasetPath) {
        _datasetPath = new File(datasetPath);
        _dataset = new HashMap<>();
        _recognition = new LBPRecognition(7);
    }

    public void run() {
        int length = (int)(Math.pow(_dataset.keySet().size(), 2) * Math.pow(SubjectType.values().length, 2) - _dataset.keySet().size() * SubjectType.values().length);
        double[][] features = new double[length][2];
        int total = 0;
        FileWriter genuineScores = null;
        FileWriter imposterScores = null;

        try {
            genuineScores = new FileWriter("/data/data/com.ensicaen.facialdetectionapp/results/genuineScores.txt");
            imposterScores = new FileWriter("/data/data/com.ensicaen.facialdetectionapp/results/imposterScores.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String subject1Name : _dataset.keySet()) {
            Subject subject1 = _dataset.get(subject1Name);
            for (SubjectType subject1Type : SubjectType.values()) {
                Bitmap subject1Image = subject1.getImage(subject1Type);
                for (String subject2Name : _dataset.keySet()) {
                    Subject subject2 = _dataset.get(subject2Name);
                    for (SubjectType subject2Type : SubjectType.values()) {
                        Bitmap subject2Image = subject2.getImage(subject2Type);
                        if (subject1Name.equals(subject2Name) && subject1Type == subject2Type) { // Exclude same pictures
                            continue;
                        }
                        double featureDistance = _recognition.compare(subject1Image, subject2Image);
                        try {
                            if (!subject1Name.equals(subject2Name)) {
                                imposterScores.write(featureDistance+System.lineSeparator());
                                //features[total][0] = featureDistance;
                                //features[total][1] = 0.0;
                                //Log.i("FaceDetectionApp", "falsePositive! (" + subject1Name + "_" + subject1Type + "|" + subject2Name + "_" + subject2Type + ") " + featureDistance);
                            } else {
                                genuineScores.write(featureDistance+System.lineSeparator());
                                //features[total][0] = featureDistance;
                                //features[total][1] = 1.0;
                                //Log.i("FaceDetectionApp", "falseNegative! (" + subject1Name + "_" + subject1Type + "|" + subject2Name + "_" + subject2Type + ") " + featureDistance);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        total++;
                        if (total % 1000 == 0) {
                            Log.i("FaceDetectionApp", String.valueOf(total));
                        }
                    }
                }
            }
        }
        Log.i("FaceDetectionApp", "Total comparison: " + total);

        try {
            genuineScores.close();
            imposterScores.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        File[] files = _datasetPath.listFiles();

        for (int i = 0; i < files.length; i++) {
            Bitmap subjectBitmap = BitmapFactory.decodeFile(files[i].getAbsolutePath());
            String[] nameSplit = files[i].getName().split("\\.", 0);
            String subjectName = nameSplit[0];
            String subjectType;
            if (nameSplit.length > 2) {
                subjectType = nameSplit[1].split("_", 0)[0];
            } else {
                subjectName = nameSplit[0].split("_", 0)[0];
                subjectType = "";
            }
            Subject subject = _dataset.get(subjectName);
            if (subject == null) {
                subject = new Subject(subjectName);
            }
            subject.addImage(Subject.parseType(subjectType), subjectBitmap);
            _dataset.put(subjectName, subject);
        }
    }

    public void cropAndSave(String savePath) {
        File[] files = _datasetPath.listFiles();
        FaceDetector detector = setupFaceDetector();

        for (int i = 0; i < files.length; i++) {
            Bitmap input = BitmapFactory.decodeFile(files[i].getAbsolutePath());
            InputImage image = InputImage.fromBitmap(input, 0);
            String outputName = files[i].getName().substring(0, files[i].getName().lastIndexOf('.'));
            @SuppressLint("UnsafeOptInUsageError") Task<List<Face>> result =
                detector.process(image)
                    .addOnSuccessListener(
                            faces -> {
                                for (Face face : faces) {
                                    Rect faceBounds = face.getBoundingBox();
                                    Bitmap outputCrop = Bitmap.createBitmap(input, faceBounds.left, faceBounds.top, faceBounds.width(), faceBounds.height());
                                    saveFrame(savePath + outputName + "_crop", outputCrop);
                                }
                            })
                    .addOnFailureListener(
                            e -> Log.i("FaceDetectionApp", e.toString()));
        }
    }

    public FaceDetector setupFaceDetector() {
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .build();

        return FaceDetection.getClient(options);
    }

    public static void saveFrame(String path, Bitmap output) {
        try {
            @SuppressLint("UnsafeOptInUsageError") FileOutputStream out = new FileOutputStream(path+".png");
            output.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

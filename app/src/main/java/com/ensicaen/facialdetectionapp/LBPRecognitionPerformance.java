package com.ensicaen.facialdetectionapp;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.File;
import java.io.FileOutputStream;
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
        int falsePositive = 0;
        int falseNegative = 0;
        int total = 0;

        for (double threshold = 0.005; threshold < 0.2; threshold += 0.005) {
            for (String subject1Name : _dataset.keySet()) {
                Subject subject1 = _dataset.get(subject1Name);
                for (SubjectType subject1Type : SubjectType.values()) {
                    Bitmap subject1Image = subject1.getImage(subject1Type);
                    for (String subject2Name : _dataset.keySet()) {
                        Subject subject2 = _dataset.get(subject2Name);
                        for (SubjectType subject2Type : SubjectType.values()) {
                            Bitmap subject2Image = subject2.getImage(subject2Type);
                            double featureDistance = _recognition.compare(subject1Image, subject2Image);
                            if (featureDistance < threshold) {
                                if (!subject1Name.equals(subject2Name)) {
                                    falsePositive++;
                                    //Log.i("FaceDetectionApp", "falsePositive! (" + subject1Name + "_" + subject1Type + "|" + subject2Name + "_" + subject2Type + ") " + featureDistance);
                                }
                            } else {
                                if (subject1Name.equals(subject2Name)) {
                                    falseNegative++;
                                    //Log.i("FaceDetectionApp", "falseNegative! (" + subject1Name + "_" + subject1Type + "|" + subject2Name + "_" + subject2Type + ") " + featureDistance);
                                }
                            }
                            total++;
                        }
                    }
                }
            }
            Log.i("FaceDetectionApp", "Threshold: " + threshold);
            Log.i("FaceDetectionApp", "Number of false positive: " + falsePositive);
            Log.i("FaceDetectionApp", "Number of false negative: " + falseNegative);
            int totalFalse = falsePositive+falseNegative;
            Log.i("FaceDetectionApp", "Total false: " + totalFalse);
            Log.i("FaceDetectionApp", "Recognition rate: " + (100.0 - ((float)totalFalse*100.0/(float)total)));
            Log.i("FaceDetectionApp", "Total comparison: " + total);
            falsePositive = 0;
            falseNegative = 0;
            total = 0;
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

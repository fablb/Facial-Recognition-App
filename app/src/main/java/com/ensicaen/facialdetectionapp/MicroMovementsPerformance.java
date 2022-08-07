package com.ensicaen.facialdetectionapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.ensicaen.facialdetectionapp.utils.BackgroundSubtraction;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Tools.ObjectiveFidelity;

public class MicroMovementsPerformance {
    private File _datasetPath;

    public MicroMovementsPerformance(String datasetPath) {
        _datasetPath = new File(datasetPath);
    }

    public void run() {
        ArrayList<Bitmap> frames;
        BackgroundSubtraction bs;
        FastBitmap fb;
        FastBitmap last;
        int threshold = 30;

        for (int deviceId = 2; deviceId < 3; deviceId++) {
            for (int sessionId = 1; sessionId < 4; sessionId++) {
                for (int subjectId = 36; subjectId < 56; subjectId++) {
                    for (int typeId = 1; typeId < 4; typeId++) {
                        if (typeId == 1) {
                            continue;
                        }
                        frames = load(deviceId,sessionId,subjectId,typeId);
                        fb = new FastBitmap(frames.get(0).copy(Bitmap.Config.ARGB_8888,true));
                        bs = new BackgroundSubtraction(fb, threshold);
                        for (int i = 1; i < frames.size(); i++) {
                            fb = new FastBitmap(frames.get(i).copy(Bitmap.Config.ARGB_8888,true));
                            last = new FastBitmap(bs.getForeground());
                            bs.update(fb);
                            if (i > 2) {
                                ObjectiveFidelity of = new ObjectiveFidelity(last, bs.getForeground());
                                Log.i("FaceDetectionApp", String.valueOf(of.getMSE()));
                            }
                            //saveFrame("/data/data/com.ensicaen.facialdetectionapp/output/bs/"+deviceId + "_" + sessionId + "_" + subjectId + "_" + typeId + "_" +i, bs.getForegroundBitmap());
                        }
                        Log.i("FaceDetectionApp", "-");
                    }
                }
            }
        }
    }    /*
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
    }*/

    public ArrayList<Bitmap> load(int deviceId, int sessionId, int subjectId, int typeId) {
        ArrayList<Bitmap> frames = new ArrayList<>();
        File[] files = new File(_datasetPath.getAbsolutePath() + "/" + deviceId + "_" + sessionId + "_" + subjectId + "_" + typeId).listFiles();
        for (int j = 0; j < files.length; j++) {
            Bitmap input = BitmapFactory.decodeFile(files[j].getAbsolutePath());
            //Bitmap scaledInput = Bitmap.createScaledBitmap(input, (int)(input.getWidth() * 0.5), (int)(input.getHeight() * 0.5), false);
            frames.add(input);
        }
        return frames;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void cropAndSave(String savePath) {
        File[] dirs = _datasetPath.listFiles();
        FaceDetector detector = setupFaceDetector();
        InputImage image;
        File[] files;

        for (int i = 130; i < 180; i++) {
            try {
                Files.createDirectories(Paths.get(savePath + dirs[i].getName()));
            } catch (IOException e) {

            }
            files = new File(dirs[i].getAbsolutePath()).listFiles();
            for (int j = 0; j < files.length; j++) {
                Log.i("FaceDetectionApp", files[j].getAbsolutePath());
                Bitmap input = BitmapFactory.decodeFile(files[j].getAbsolutePath());
                Bitmap scaledInput = Bitmap.createScaledBitmap(input, (int)(input.getWidth() * 0.5), (int)(input.getHeight() * 0.5), false);
                image = InputImage.fromBitmap(scaledInput, 0);
                String dirName = dirs[i].getName();
                String fileName = files[j].getName();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                @SuppressLint("UnsafeOptInUsageError") Task<List<Face>> result =
                        detector.process(image)
                                .addOnSuccessListener(
                                        faces -> {
                                            for (Face face : faces) {
                                                Rect faceBounds = face.getBoundingBox();
                                                int widthOffset = (int)(faceBounds.width() * 0.3);
                                                int heightOffset = (int)(faceBounds.height() * 0.3);
                                                Bitmap outputCrop = Bitmap.createBitmap(scaledInput, faceBounds.left - widthOffset / 2, faceBounds.top - heightOffset / 2, faceBounds.width() + widthOffset, faceBounds.height() + heightOffset);
                                                //Log.i("FaceDetectionApp", savePath + dirName + "/" + fileName);
                                                saveFrame(savePath + dirName + "/" + fileName, outputCrop);
                                            }
                                        })
                                .addOnFailureListener(
                                        e -> Log.i("FaceDetectionApp", e.toString()));
            }
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

package com.ensicaen.facialdetectionapp.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ensicaen.facialdetectionapp.R;
import com.ensicaen.facialdetectionapp.controller.FrameAnalyzer;
import com.ensicaen.facialdetectionapp.controller.LivenessDetectorListener;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public class DatasetView extends AppCompatActivity {
    private ImageView _frameView;
    private TextView _progressText;
    private Button _startButton;
    private FrameAnalyzer _frameAnalyzer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dataset_view);

        _frameView = findViewById(R.id.dataset_frame);
        _progressText = findViewById(R.id.dataset_progress);
        _startButton = findViewById(R.id.dataset_start);
        _frameAnalyzer = new FrameAnalyzer();
        _frameAnalyzer.addFaceListener(new LivenessDetectorListener(this));
        _startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int subjectId = 36; subjectId < 38; subjectId++) {
                    for (int type = 1; type < 4; type++) {
                        String name = "1_1_"+subjectId+"_"+type;
                        Log.i("FaceDetectionApp", name);
                        processDataset("/data/data/com.ensicaen.facialdetectionapp/dataset/internship/images/"+name);
                    }
                }
            }
        });
    }

    private void processDataset(String dir) {
        _frameAnalyzer.addFaceListener(new LivenessDetectorListener(this));
        File directory = new File(dir);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            Bitmap b = BitmapFactory.decodeFile(files[i].getAbsolutePath());
            _frameAnalyzer.analyze(b);
            try {
                Thread.sleep(15);
            } catch(InterruptedException e) {

            }
            //Log.i("FaceDetectionApp", "Progress: " + i + "/" + files.length);
        }
    }

    public void setImage(Bitmap b) {
        _frameView.setImageBitmap(b);
    }
}

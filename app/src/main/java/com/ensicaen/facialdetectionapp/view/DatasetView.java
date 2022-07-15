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
    private String _current;

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
                for (int subjectId = 36; subjectId < 52; subjectId++) {
                    for (int type = 1; type < 4; type++) {
                        _current = "1_2_"+subjectId+"_"+type;
                        Log.i("FaceDetectionApp", _current);
                        processDataset("/data/data/com.ensicaen.facialdetectionapp/dataset/internship/images/"+_current);
                    }
                }
            }
        });
    }

    private void processDataset(String dir) {
        LivenessDetectorListener liveness = new LivenessDetectorListener(this);
        liveness.setDataName(_current);
        _frameAnalyzer.addFaceListener(liveness);
        File directory = new File(dir);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (i % 5 == 0) {
                Bitmap b = BitmapFactory.decodeFile(files[i].getAbsolutePath());
                _frameAnalyzer.analyze(b);
            }
        }
    }
}

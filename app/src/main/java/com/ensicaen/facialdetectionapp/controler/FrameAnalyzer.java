package com.ensicaen.facialdetectionapp.controler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Log;

import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.ensicaen.facialdetectionapp.utils.BitmapUtils;
import com.ensicaen.facialdetectionapp.utils.YuvToRgbConverter;
import com.google.mlkit.vision.common.InputImage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class FrameAnalyzer implements ImageAnalysis.Analyzer {

    @Override
    public void analyze(ImageProxy frameProxy) {
        @SuppressLint("UnsafeOptInUsageError") Image frame = frameProxy.getImage();
        if (frame != null) {
            /* Get InputImage for ML KIT */
            //InputImage image = InputImage.fromMediaImage(frame, frameProxy.getImageInfo().getRotationDegrees());

            /* Convert ImageProxy to Bitmap for future usage */
            //@SuppressLint("UnsafeOptInUsageError") Bitmap bitmapImage = BitmapUtils.getBitmap(frameProxy);

            /* Save frame in internal storage for debugging purpose */
            /*
            try {
                FileOutputStream out = new FileOutputStream("/data/user/0/com.ensicaen.facialdetectionapp/files/"+frame.getTimestamp()+"_test.png");
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 50, out);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            */
        }
        frameProxy.close();
    }
}

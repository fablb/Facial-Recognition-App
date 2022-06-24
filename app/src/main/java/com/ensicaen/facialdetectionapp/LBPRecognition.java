package com.ensicaen.facialdetectionapp;

import android.graphics.Bitmap;

import com.ensicaen.facialdetectionapp.utils.MathUtils;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Texture.BinaryPattern.MultiblockLocalBinaryPattern;
import Catalano.Imaging.Tools.ImageHistogram;

public class LBPRecognition {
    private int _blockSize;
    private MultiblockLocalBinaryPattern _lbp;

    public LBPRecognition(int blockSize) {
        _blockSize = blockSize;
        _lbp = new MultiblockLocalBinaryPattern(_blockSize, _blockSize);
    }

    public double compare(Bitmap x, Bitmap y) {

        FastBitmap a = new FastBitmap(getMutableBitmap(x));
        FastBitmap b = new FastBitmap(getMutableBitmap(y));
        a.toGrayscale();
        b.toGrayscale();
        ImageHistogram aHistogram = _lbp.ComputeFeatures(a);
        ImageHistogram bHistogram = _lbp.ComputeFeatures(b);
        return MathUtils.euclideanDistance(aHistogram.getValues(), bHistogram.getValues());
    }

    public Bitmap getMutableBitmap(Bitmap a) {
        if (a.isMutable()) {
            return a;
        } else {
            return a.copy(Bitmap.Config.ARGB_8888,true);
        }
    }
}

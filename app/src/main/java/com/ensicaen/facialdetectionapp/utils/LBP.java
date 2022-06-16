package com.ensicaen.facialdetectionapp.utils;

import android.graphics.Bitmap;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Texture.BinaryPattern.MultiblockLocalBinaryPattern;
import Catalano.Imaging.Tools.ImageHistogram;

public class LBP {
    private MultiblockLocalBinaryPattern _lbp;

    public LBP() {
        _lbp = new MultiblockLocalBinaryPattern(7, 7);
    }

    public LBP(int blockSize) {
        _lbp = new MultiblockLocalBinaryPattern(blockSize, blockSize);
    }

    public int[] compute(Bitmap x) {
        FastBitmap a = new FastBitmap(getMutableBitmap(x));
        a.toGrayscale();
        return _lbp.ComputeFeatures(a).getValues();
    }

    public double compare(Bitmap x, Bitmap y) {
        FastBitmap a = new FastBitmap(getMutableBitmap(x));
        FastBitmap b = new FastBitmap(getMutableBitmap(y));
        a.toGrayscale();
        b.toGrayscale();
        ImageHistogram aHistogram = _lbp.ComputeFeatures(a);
        ImageHistogram bHistogram = _lbp.ComputeFeatures(b);
        return MathUtils.cosineDistance(aHistogram.getValues(), bHistogram.getValues());
    }

    public double compare(Bitmap x, int[] features) {
        FastBitmap a = new FastBitmap(getMutableBitmap(x));
        a.toGrayscale();
        ImageHistogram aHistogram = _lbp.ComputeFeatures(a);
        return MathUtils.cosineDistance(aHistogram.getValues(), features);
    }

    public Bitmap getMutableBitmap(Bitmap a) {
        if (a.isMutable()) {
            return a;
        } else {
            return a.copy(Bitmap.Config.ARGB_8888,true);
        }
    }
}

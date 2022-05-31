package com.ensicaen.facialdetectionapp.utils;

public class MathUtils {

    public static double cosineDistance(int[] p, int[] q) {
        int sumProduct = 0;
        double sumP = 0;
        double sumQ = 0;

        for (int i = 0; i < p.length; i++) {
            sumProduct += p[i] * q[i];
            sumP += Math.pow(Math.abs(p[i]), 2);
            sumQ += Math.pow(Math.abs(q[i]), 2);
        }

        sumP = Math.sqrt(sumP);
        sumQ = Math.sqrt(sumQ);

        return 1.0D - sumProduct / (sumP * sumQ);
    }
}

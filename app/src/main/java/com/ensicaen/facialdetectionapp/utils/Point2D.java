package com.ensicaen.facialdetectionapp.utils;

import android.graphics.Point;

import java.util.ArrayList;

public class Point2D extends Point {
    public Point2D(int x, int y) {
        super(x,y);
    }

    public int distance(Point2D b) {
        return (int)Math.hypot(Math.abs(b.y - y), Math.abs(b.x - x));
    }

    /* Check mean distance between this point and other one */
    public int meanDistance(ArrayList<Point2D> points) {
        int sum = 0;

        for (Point2D point : points) {
            sum += distance(point);
        }

        return (int)(sum / points.size());
    }
}

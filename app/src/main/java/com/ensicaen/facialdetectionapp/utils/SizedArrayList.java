package com.ensicaen.facialdetectionapp.utils;

import java.util.ArrayList;
import java.util.Stack;

public class SizedArrayList<T> extends ArrayList<T> {
    private int _capacity;

    public SizedArrayList(int size) {
        super(size);
        _capacity = size;
    }

    @Override
    public boolean add(T object) {
        //If the array is too big, remove elements until it's the right size.
        while (size() >= _capacity) {
            remove(0);
        }
        return super.add(object);
    }

    public int capacity() {
        return _capacity;
    }

    public T last() {
        return get(size() - 1);
    }
}

package com.ensicaen.facialdetectionapp.model;

import java.util.Arrays;
import java.util.Date;

public class Profile {
    private String _name;
    private int[] _features;
    private Date _date;

    public Profile(String name, Date date) {
        _name = name;
        _date = date;
        _features = new int[256];
    }

    public Profile(String name, int[] features, Date date) {
        _name = name;
        _date = date;
        _features = Arrays.copyOf(features, 256);
    }

    public Profile(String name, String features, Date date) {
        _name = name;
        _date = date;
        String[] featuresSplit = features.split(",");
        for (int i = 0; i < featuresSplit.length; i++) {
            _features[i] = Integer.parseInt(featuresSplit[i]);
        }
    }

    public String get_name() {
        return _name;
    }

    public void set_features(int[] features) {
        _features = features;
    }

    public int[] get_features() {
        return _features;
    }

    public String convertFeaturesToString() {
        return Arrays.toString(_features).replace("[","").replace("]","");
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public Date get_date() {
        return _date;
    }

    public void set_date(Date date) {
        this._date = date;
    }
}

package com.ensicaen.facialdetectionapp.model;

import java.util.Arrays;
import java.util.Date;

public class Profil {
    private String _name;
    private int[] _features;
    private Date _date;

    public Profil(String name, Date date) {
        _name = name;
        _date = date;
        _features = new int[156];
    }

    public Profil(String name, int[] features, Date date) {
        _name = name;
        _date = date;
        _features = Arrays.copyOf(features, 156);
    }

    public Profil(String name, String features, Date date) {
        _name = name;
        _date = date;
        int i = 0;
        String c = ""+features.charAt(i);
        while( c !="]" ) {
            if ((c != "[") && (c != ",") ) {
                _features[i] = Integer.parseInt(c);
            }
            i++;
            c = ""+features.charAt(i);
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
        return Arrays.toString(_features);
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

package com.ensicaen.facialdetectionapp.model;

import java.util.Date;

public class Profil {
    private String _name;
    private double _features;
    private Date _registedDate;

    public Profil(String name, double features, Date registedDate) {
        _name = name;
        _registedDate = registedDate;
        _features = features;
    }

    public String get_name() {
        return _name;
    }

    public void set_features(double features) {
        this._features = features;
    }

    public double get_features() {
        return _features;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public Date get_registedDate() {
        return _registedDate;
    }

    public void set_registedDate(Date registedDate) {
        this._registedDate = registedDate;
    }
}

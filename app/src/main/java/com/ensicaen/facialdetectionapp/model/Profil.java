package com.ensicaen.facialdetectionapp.model;

import java.util.Date;

public class Profil {
    private String _name;
    private double features;
    private Date _registedDate;

    public Profil(String name, Date registedDate) {
        _name = name;
        _registedDate = registedDate;
        features = 1.099;
    }

    public String get_name() {
        return _name;
    }

    public void set_features(double features) {
        this.features = features;
    }

    public double get_features() {
        return features;
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

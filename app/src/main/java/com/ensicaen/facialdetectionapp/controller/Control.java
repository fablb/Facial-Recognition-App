package com.ensicaen.facialdetectionapp.controller;

import android.content.Context;

import com.ensicaen.facialdetectionapp.model.LocalDB;
import com.ensicaen.facialdetectionapp.model.Profile;

import java.util.Date;

public final class Control {
    private static Control _instance = null;
    private static Profile _profile;
    private static LocalDB _localAccess;

    /**
     * Private constructor
     */
    public Control() {
        super();
    }

    /**
     * Instance's creation
     * @param context
     * @return
     */
    public static final Control get_instance(Context context) {
        if(_instance==null) {

            _instance = new Control();
            _localAccess = new LocalDB(context);
        }
        return _instance;
    }

    /**
     * Profil's creation
     * @param name
     * @param context
     */
    public void createProfil(String name, Context context) {
        _profile = new Profile(name, recupFeatures(), new Date());
        _localAccess.add(_profile);
    }

    /**
     * Scan the face and return the features
     * @return
     */
    public int[] recupFeatures() {
        int[] array = new int[156];
        return array;
    }
}

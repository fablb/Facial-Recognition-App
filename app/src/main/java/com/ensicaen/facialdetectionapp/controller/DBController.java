package com.ensicaen.facialdetectionapp.controller;

import android.content.Context;

import com.ensicaen.facialdetectionapp.model.LocalDB;
import com.ensicaen.facialdetectionapp.model.Profile;

import java.util.Date;

public final class DBController {
    private static DBController _instance = null;
    private static Profile _profile;
    private static LocalDB _localAccess;

    /**
     * Private constructor
     */
    private DBController() {
        super();
    }

    /**
     * Instance's creation
     * @param context
     * @return
     */
    public static DBController getInstance(Context context) {
        if(_instance==null) {
            _instance = new DBController();
            _localAccess = new LocalDB(context);
        }
        return _instance;
    }

    /**
     * Profil's creation
     * @param name
     */
    public void createProfile(String name, int[] features) {
        _profile = new Profile(name, features, new Date());
        _localAccess.add(_profile);
    }

    public Profile getProfile(String name) {
        return _localAccess.searchByName(name);
    }
}

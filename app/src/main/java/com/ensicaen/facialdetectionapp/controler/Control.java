package com.ensicaen.facialdetectionapp.controler;

import android.content.Context;

import com.ensicaen.facialdetectionapp.model.LocalAccessBDD;
import com.ensicaen.facialdetectionapp.model.Profil;

import java.util.Date;

public final class Control {
    private static Control _instance = null;
    private static Profil _profil;
    private static LocalAccessBDD _localAccess;

    public Control() {
        super();
    }

    public static final Control get_instance(Context context) {
        if(_instance==null) {

            _instance = new Control();
            _localAccess = new LocalAccessBDD(context);
            _profil = _localAccess.readLast();
        }
        return _instance;
    }

    public void createProfil(String name, Context context) {
        _profil = new Profil(name, new Date());
        _localAccess.add(_profil);
    }


}

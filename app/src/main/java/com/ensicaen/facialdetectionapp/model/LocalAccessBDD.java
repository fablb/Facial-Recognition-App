package com.ensicaen.facialdetectionapp.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ensicaen.facialdetectionapp.utils.MySQLiteOpenHelper;

import java.util.Date;

public class LocalAccessBDD {
    private String _bdName = "bdProfil.sqlite";
    private Integer _version = 1;
    private MySQLiteOpenHelper _bdAccess;
    private SQLiteDatabase _bd;

    public LocalAccessBDD(Context context) {
        _bdAccess = new MySQLiteOpenHelper(context, _bdName, null, _version);
    }

    public void add(Profil profil) {
        _bd = _bdAccess.getWritableDatabase();
        String req = "INSERT INTO profil (name, features, registredDate) VALUES ";
        req += "("+ profil.get_name() +",\""+ profil.get_features() +"\",\""+ profil.get_registedDate() +"\")";
        _bd.execSQL(req);
    }

    public Profil readLast() {
        _bd = _bdAccess.getReadableDatabase();
        Profil profil = null;
        String req = "SELECT * FROM profil";
        Cursor cursor = _bd.rawQuery(req, null);
        if(!cursor.isAfterLast()) {
            Date date = new Date();
            String name = cursor.getString(1);
            Double features = cursor.getDouble(2);
            profil = new Profil(name, date);
            profil.set_features(features);
        }
        cursor.close();
        return profil;
    }
}

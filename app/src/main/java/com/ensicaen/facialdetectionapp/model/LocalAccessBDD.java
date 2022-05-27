package com.ensicaen.facialdetectionapp.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ensicaen.facialdetectionapp.utils.MySQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

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
        String req = "INSERT INTO profil(name, features, registred_date) VALUES ";
        req += "(\'"+ profil.get_name() +"\',\'"+ profil.convertFeaturesToString() +"\',\'"+ profil.get_date() +"\')";
        _bd.execSQL(req);
    }

    public Profil[] searchByName(String name) {
        String p_name = "", p_features = "";
        _bd = _bdAccess.getReadableDatabase();
        String req = "SELECT * FROM profil WHERE name = \'" + name + "\'";
        Cursor cursor = _bd.rawQuery(req, null);
        Profil[] profils = new Profil[cursor.getCount()];
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            p_name = cursor.getString(1);
            p_features = cursor.getString(2);
            profils[i] = new Profil(p_name, p_features, new Date());
            cursor.moveToNext();
        }
        cursor.close();
        return profils;
    }
}

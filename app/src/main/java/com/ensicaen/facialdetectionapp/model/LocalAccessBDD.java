package com.ensicaen.facialdetectionapp.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ensicaen.facialdetectionapp.utils.MySQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        req += "(\'"+ profil.get_name() +"\',\'"+ profil.get_features() +"\',\'"+ profil.get_registedDate() +"\')";
        //Log.e(profil.get_name()+" "+profil.get_features()+" "+profil.get_registedDate());
        _bd.execSQL(req);
    }

}

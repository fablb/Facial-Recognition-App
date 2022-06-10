package com.ensicaen.facialdetectionapp.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ensicaen.facialdetectionapp.utils.MySQLiteOpenHelper;

import java.util.Date;

public class LocalDB {
    private String _bdName = "bdProfil.sqlite";
    private Integer _version = 1;
    private MySQLiteOpenHelper _bdAccess;
    private SQLiteDatabase _bd;

    public LocalDB(Context context) {
        _bdAccess = new MySQLiteOpenHelper(context, _bdName, null, _version);
    }

    public void add(Profile profile) {
        _bd = _bdAccess.getWritableDatabase();
        String req = "INSERT INTO profil(name, features, registred_date) VALUES ";
        req += "(\'"+ profile.get_name() +"\',\'"+ profile.convertFeaturesToString() +"\',\'"+ profile.get_date() +"\')";
        _bd.execSQL(req);
    }

    public Profile[] searchByName(String name) {
        String p_name = "", p_features = "";
        _bd = _bdAccess.getReadableDatabase();
        String req = "SELECT * FROM profil WHERE name = \'" + name + "\'";
        Cursor cursor = _bd.rawQuery(req, null);
        Profile[] profiles = new Profile[cursor.getCount()];
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            p_name = cursor.getString(1);
            p_features = cursor.getString(2);
            profiles[i] = new Profile(p_name, p_features, new Date());
            cursor.moveToNext();
        }
        cursor.close();
        return profiles;
    }
}

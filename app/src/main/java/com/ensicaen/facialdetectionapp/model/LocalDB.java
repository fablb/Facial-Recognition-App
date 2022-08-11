package com.ensicaen.facialdetectionapp.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ensicaen.facialdetectionapp.utils.MySQLiteOpenHelper;

import java.util.Date;

public class LocalDB {
    private String _bdName = "dbFaceRecognition.sqlite";
    private Integer _version = 1;
    private MySQLiteOpenHelper _bdAccess;
    private SQLiteDatabase _bd;

    public LocalDB(Context context) {
        _bdAccess = new MySQLiteOpenHelper(context, _bdName, null, _version);
    }

    public void add(Profile profile) {
        _bd = _bdAccess.getWritableDatabase();
        String req = "INSERT INTO profil(name, features, registered_date) VALUES ";
        req += "(\'"+ profile.get_name() +"\',\'"+ profile.convertFeaturesToString() +"\',\'"+ profile.get_date() +"\');";
        _bd.execSQL(req);
    }

    public Profile searchByName(String name) {
        _bd = _bdAccess.getReadableDatabase();
        String req = "SELECT * FROM profil WHERE name = \'" + name + "\'";
        Cursor cursor = _bd.rawQuery(req, null);

        if (cursor.getCount() == 0) {
            return null;
        }

        cursor.moveToFirst();
        Profile profile = new Profile(cursor.getString(1), cursor.getString(2), new Date());
        cursor.close();

        Log.i("FaceDetectionApp", profile.get_name() + profile.get_date());

        return profile;
    }
}

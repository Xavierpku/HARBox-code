package com.example.databox20;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MagData {
    public long timestamp;
    public double mx;
    public double my;
    public double mz;

    public static final String TABLE_NAME = "MagData";
    public static final String _ID = "_id";
    public static final String TIMESTAMP = "timestamp";
    public static final String MX = "mx";
    public static final String MY = "my";
    public static final String MZ = "mz";

    //生成表的sql语句
    public static String tableCreateSQL() {
        StringBuffer sql = new StringBuffer();
        sql.append("create table ");
        sql.append(TABLE_NAME);
        sql.append(" (");
        sql.append(_ID);
        sql.append(" integer primary key autoincrement,");
        sql.append(TIMESTAMP);
        sql.append(" long,");
        sql.append(MX);
        sql.append(" double,");
        sql.append(MY);
        sql.append(" double,");
        sql.append(MZ);
        sql.append(" double");
        sql.append(" )");
        return sql.toString();
    }

    public long insertDataBase(SQLiteDatabase sqLiteDatabase) {
        ContentValues values = new ContentValues();
        values.put(TIMESTAMP, timestamp);
        values.put(MX, mx);
        values.put(MY, my);
        values.put(MZ, mz);
        return(sqLiteDatabase.insert(TABLE_NAME, null, values));
    }

    public MagData clone() {
        MagData magData = new MagData();
        magData.timestamp = timestamp;
        magData.mx = mx;
        magData.my = my;
        magData.mz = mz;
        return magData;
    }
}


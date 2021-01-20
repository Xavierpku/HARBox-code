package com.example.databox20;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GyroData {
    public long timestamp;
    public double gx;
    public double gy;
    public double gz;

    public static final String TABLE_NAME = "GyroData";
    public static final String _ID = "_id";
    public static final String TIMESTAMP = "timestamp";
    public static final String GX = "gx";
    public static final String GY = "gy";
    public static final String GZ = "gz";

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
        sql.append(GX);
        sql.append(" double,");
        sql.append(GY);
        sql.append(" double,");
        sql.append(GZ);
        sql.append(" double");
        sql.append(" )");
        return sql.toString();
    }

    public long insertDataBase(SQLiteDatabase sqLiteDatabase) {
        ContentValues values = new ContentValues();
        values.put(TIMESTAMP, timestamp);
        values.put(GX, gx);
        values.put(GY, gy);
        values.put(GZ, gz);
        return(sqLiteDatabase.insert(TABLE_NAME, null, values));
    }

    public GyroData clone() {
        GyroData gyroData = new GyroData();
        gyroData.timestamp = timestamp;
        gyroData.gx = gx;
        gyroData.gy = gy;
        gyroData.gz = gz;
        return gyroData;
    }
}

package com.example.databox20;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AccData {
    public long timestamp;
    public double ax;
    public double ay;
    public double az;

    public static final String TABLE_NAME = "AccData";
    public static final String _ID = "_id";
    public static final String TIMESTAMP = "timestamp";
    public static final String AX = "ax";
    public static final String AY = "ay";
    public static final String AZ = "az";

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
        sql.append(AX);
        sql.append(" double,");
        sql.append(AY);
        sql.append(" double,");
        sql.append(AZ);
        sql.append(" double");
        sql.append(" )");
        return sql.toString();
    }

    public long insertDataBase(SQLiteDatabase sqLiteDatabase) {
        ContentValues values = new ContentValues();
        values.put(TIMESTAMP, timestamp);
        values.put(AX, ax);
        values.put(AY, ay);
        values.put(AZ, az);
        return(sqLiteDatabase.insert(TABLE_NAME, null, values));
    }

    public AccData clone() {
        AccData accData = new AccData();
        accData.timestamp = timestamp;
        accData.ax = ax;
        accData.ay = ay;
        accData.az = az;
        return accData;
    }
}

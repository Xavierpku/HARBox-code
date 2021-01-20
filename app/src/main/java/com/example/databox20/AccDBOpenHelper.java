package com.example.databox20;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AccDBOpenHelper extends SQLiteOpenHelper {
    private static final String DBNAME = "AccData.db3";
    private static final int VERSION = 1;

    public AccDBOpenHelper(SensorDataCollect.SensorBinder sensorBinder, String name, Context context, int version){
        super(context,DBNAME,null,VERSION);
    }

    public AccDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(AccData.tableCreateSQL());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        System.out.println( "----------AccDBOpenHelper onUpdata Called----------" + oldVersion + "-->" + newVersion);
    }
}

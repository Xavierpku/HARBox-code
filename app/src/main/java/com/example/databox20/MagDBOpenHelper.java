package com.example.databox20;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MagDBOpenHelper extends SQLiteOpenHelper {
    private static final String DBNAME = "MagData.db3";
    private static final int VERSION = 1;
    public MagDBOpenHelper(SensorDataCollect.SensorBinder sensorBinder, String name, Context context, int version){
        super(context,DBNAME,null,VERSION);
    }

    public MagDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(MagData.tableCreateSQL());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        System.out.println( "----------MagAndOriDBOpenHelper onUpdata Called----------" + oldVersion + "-->" + newVersion);
    }
}

package com.example.databox20;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class GyroDBOpenHelper extends SQLiteOpenHelper {
    private static final String DBNAME = "Gyro.db3";
    private static final int VERSION = 1;
    public GyroDBOpenHelper(SensorDataCollect.SensorBinder sensorBinder, String name, Context context, int version){
        super(context,DBNAME,null,VERSION);
    }

    public GyroDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(GyroData.tableCreateSQL());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        System.out.println( "----------GyrDBOpenHelper onUpdata Called----------" + oldVersion + "-->" + newVersion);
    }
}

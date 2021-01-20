package com.example.databox20;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class SensorDataCollect extends Service {
    private SensorBinder sensorBinder = new SensorBinder();
    private SensorManager sensorManager;
    private boolean isRecord;
    private Timer updateTimer;

    private AccDBOpenHelper accDBOpenHelper;
    private SQLiteDatabase accDB;
    private List<AccData> accDataList;
    private AccData accData;
    private float accValues[];

    private GyroDBOpenHelper gyroDBOpenHelper;
    private SQLiteDatabase gyroDB;
    private List<GyroData> gyroDataList;
    private GyroData gyroData;
    private float gyroValues[];

    private MagDBOpenHelper magDBOpenHelper;
    private SQLiteDatabase magDB;
    private List<MagData> magDataList;
    private MagData magData;
    private float magValues[];

    public String motionList[] = {
            "Walk", "Hop", "Call", "Wave", "typing"
    };


    class SensorBinder extends Binder {
        public void startRecord(){
            isRecord = true;
            Log.d("sensorService","start record");
        }

        public void stopRecord(){
            isRecord = false;
            Log.d("sensorService","stop record");
        }

        public void clearRecord() {
            accDataList.clear();
            gyroDataList.clear();
            magDataList.clear();
        }

        public void closeDB(){
            if (accDB != null && accDB.isOpen()) {
                accDB.close();
            }
            if (gyroDB != null && gyroDB.isOpen()) {
                gyroDB.close();
            }
            if (magDB != null && magDB.isOpen()) {
                magDB.close();
            }
        }

        public void reinit(int motionIndex){
            initializeVariables(motionIndex);
        }

        public void registerListener(){
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            Sensor accelerator = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(mySensorListener, accelerator, SensorManager.SENSOR_DELAY_GAME);

            Sensor gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            sensorManager.registerListener(mySensorListener, gyro, SensorManager.SENSOR_DELAY_GAME);

            Sensor mag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            sensorManager.registerListener(mySensorListener, mag, SensorManager.SENSOR_DELAY_GAME);

            Log.d("sensorService","All sensor have been successfully registered");
        }



        public boolean insertDB(){
            if (accDataList.isEmpty() && gyroDataList.isEmpty() &&  magDataList.isEmpty()) {
                return false;
            } else {
                accDB.beginTransaction();
                try {
                    for (AccData accData : accDataList) {
                        accData.insertDataBase(accDB);
                    }
                    accDB.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    accDB.endTransaction();
                }

                gyroDB.beginTransaction();
                try {
                    for (GyroData gyroData: gyroDataList) {
                        gyroData.insertDataBase(gyroDB);
                    }
                    gyroDB.setTransactionSuccessful();
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                finally {
                    gyroDB.endTransaction();
                }

                magDB.beginTransaction();
                try {
                    for (MagData magAndOriData: magDataList) {
                        magAndOriData.insertDataBase(magDB);
                    }
                    magDB.setTransactionSuccessful();
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                finally {
                    magDB.endTransaction();
                }

                return true;
            }
        }

        public void unregisterListener(){
            sensorManager.unregisterListener(mySensorListener);
            Log.d("sensorService","Listeners are unregistered ! ");
        }

    }
    @Override
    public IBinder onBind(Intent intent) {
        return sensorBinder;
    }
    public boolean onUnBind(Intent intent){
        Log.d("sensorService","DataAcquireService is onUnbinded ! ");
        return super.onUnbind(intent);
    }

    public void onRebind(Intent intent){
        Log.d("sensorService","DataAcquireService is onRebind 1 !");
        super.onRebind(intent);
    }

    public SensorDataCollect() {
    }

    private SensorEventListener mySensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()){
                case Sensor.TYPE_ACCELEROMETER:
                    accValues = event.values.clone();
                    synchronized (accData) {
                        accData.timestamp = System.currentTimeMillis();
                        accData.ax = accValues[0];
                        accData.ay = accValues[1];
                        accData.az = accValues[2];

                        if (isRecord) {
                            accDataList.add(accData.clone());
                        }
                    }
                    break;

                case Sensor.TYPE_GYROSCOPE:
                    gyroValues = event.values.clone();
                    synchronized (gyroData) {
                        gyroData.timestamp = System.currentTimeMillis();
                        gyroData.gx = gyroValues[0];
                        gyroData.gy = gyroValues[1];
                        gyroData.gz = gyroValues[2];

                        if (isRecord) {
                            gyroDataList.add(gyroData.clone());
                        }
                    }
                    break;

                case Sensor.TYPE_MAGNETIC_FIELD:
                    magValues = event.values.clone();
                    synchronized (magData){
                        magData.timestamp = System.currentTimeMillis();
                        magData.mx = magValues[0];
                        magData.my = magValues[1];
                        magData.mz = magValues[2];

                        if (isRecord){
                            magDataList.add(magData.clone());
                        }
                    }
                    break;
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d("sensorService","DataAcquireService is started ! ");
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy(){
        if (accDB != null && accDB.isOpen()) {
            accDB.close();
        }
        if (gyroDB != null && gyroDB.isOpen()) {
            gyroDB.close();
        }
        if (magDB != null && magDB.isOpen()) {
            magDB.close();
        }
        sensorManager.unregisterListener(mySensorListener);
        Log.d("sensorService","DataAcquireService is destroyed ! ");
        super.onDestroy();
    }

    private void initializeVariables(int motionIndex) {

        accDBOpenHelper = new AccDBOpenHelper(this, motionList[motionIndex]+"AccData.db3", null, 1);
        accDB = accDBOpenHelper.getReadableDatabase();
        accDataList = new ArrayList<AccData>();
        accData = new AccData();

        gyroDBOpenHelper = new GyroDBOpenHelper(this, motionList[motionIndex]+"GyroData.db3", null, 1);
        gyroDB = gyroDBOpenHelper.getReadableDatabase();
        gyroDataList = new ArrayList<GyroData>();
        gyroData = new GyroData();

        magDBOpenHelper = new MagDBOpenHelper(this, motionList[motionIndex]+"MagData.db3", null, 1);
        magDB = magDBOpenHelper.getReadableDatabase();
        magDataList = new ArrayList<MagData>();
        magData = new MagData();

        isRecord = false;
    }
}
package com.example.databox20;

public class FileName {
    public String accFileName;
    public String gyroFileName;
    public String magFileName;
    public long timestamp;

    public FileName(){
        this.timestamp = System.currentTimeMillis()%10000;
    }
    public String getAccFileName(){
        accFileName = "AccData"+String.valueOf(timestamp)+".db3";
        return accFileName;
    }
    public String getGyroFileName(){
        gyroFileName = "GyroData"+String.valueOf(timestamp)+".db3";
        return gyroFileName;
    }
    public String getMagFileName(){
        magFileName = "MagData"+String.valueOf(timestamp)+".db3";
        return magFileName;
    }
}

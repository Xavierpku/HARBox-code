package com.example.databox20;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.ArrayList;

//import it.sauronsoftware.ftp4j.FTPAbortedException;
//import it.sauronsoftware.ftp4j.FTPClient;
//import it.sauronsoftware.ftp4j.FTPDataTransferException;
//import it.sauronsoftware.ftp4j.FTPException;
//import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

//import it.sauronsoftware.ftp4j.FTPAbortedException;
//import it.sauronsoftware.ftp4j.FTPClient;
//import it.sauronsoftware.ftp4j.FTPDataTransferException;
//import it.sauronsoftware.ftp4j.FTPException;
//import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

public class SecondMainActivity extends AppCompatActivity implements View.OnClickListener {

    private Chronometer chronometer = null;    //Add an timer to show the time of collecting
    private SensorDataCollect.SensorBinder sensorBinder; // The binder is to monitor and control the function of service
    private ArrayList<Long> savedTime;
    private Button btnStart, btnEnd, btnSubmit, btnClear, btnNext, btnPrevious;
    private static String SerialNumber = android.os.Build.SERIAL;
    private ProgressBar progressBar;
    private EditText instrText;
    private TextView stateText;
    private GifImageView gifImageView;

    private String url = "xx.xx.xx.xx";
    private String port = "21";
    private String username = "xxxxxxxx";
    private String password = "xxxxxxxx";

    public static final String remotePath = SerialNumber + String.valueOf((int) SystemClock.uptimeMillis());

    private String filenamePath = "/data/data/com.example.databox20/databases/";
    private int numOfActivities = 5;
    private String accfileName;
    private String gyrofileName;
    private String magfileName;
    private int motionIndex = 0;
    private Vibrator vibrator = null;

    public String motionList[] = {
            "Walk", "Hop", "Call", "Wave", "typing"
    };
    public String gifList[] = {
        "walking.gif", "hop.gif", "phone_call.gif", "wave.gif", "typing.gif"
    };
//    public String instrList[] = {
//            "将手机拿在手上，正常走路45秒",
//            "将手机拿在手上，单脚跳45秒",
//            "保持打电话姿势45秒钟",
//            "将手机拿在手上，挥手45秒",
//            "模拟打字动作45秒（开始后可在本处输入文本）"
//    };
public String instrList[] = {
        "Hold the phone in your hand and walk for 45 seconds.",
        "Hold the phone in your hand and jump on one foot for 45 seconds",
        "Hold the calling motion for 45 seconds",
        "Hold the phone in your hand and wave your hand for 45 seconds",
        "Simulate typing for 45 seconds (you can type here)"
};
    public String motionMandarinList[] = {
            "行走","单脚跳","打电话","挥手","打字"
    };

    public static final int UPDATE_TEXT = 1;
    public static final int FAILED_UPDATE_TEXT = 0;
    public static final int FILE_NOT_EXIST = 2;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            sensorBinder = (SensorDataCollect.SensorBinder) service;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity_main);
        try {
            initialization();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void initialization() throws IOException {
        Vibrator vibrator = (Vibrator) this.getApplication().getSystemService(Service.VIBRATOR_SERVICE);
        gifImageView = (GifImageView)findViewById(R.id.gif);
        btnStart = (Button) findViewById(R.id.start_collect);
        btnEnd = (Button) findViewById(R.id.end_collect);
        btnSubmit = (Button)findViewById(R.id.submit);
        btnClear = (Button)findViewById(R.id.clear);
        btnNext = (Button)findViewById(R.id.next);
        btnPrevious = (Button) findViewById(R.id.previous);

        btnSubmit.setBackgroundColor(Color.parseColor("#CC6063"));

        btnStart.setOnClickListener(this);
        btnEnd.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);

        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        instrText = (EditText)findViewById(R.id.instruction);
        stateText = (TextView)findViewById(R.id.show_state);


        chronometer = (Chronometer)findViewById(R.id.chronometer_area);
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener()
        {
            @SuppressLint("MissingPermission")
            public void onChronometerTick(Chronometer arg0) {
                //获取到计时器的的内容，并将一些与数字无关的替换成空字符串，^(\\d(2)：\\dd(2))是一个正则表达式意思是不适宜分秒显示的格式的其他的东西，替换成空串
                 String time=arg0.getText().toString().replace("^(\\d(2)：\\dd(2))", "");
                 if("00:45".equals(time))
                 {//当时间到达三十秒是手机就开始震动，其中这个方法的第一个参数：是震动的频率，他是一个long型的数组，第二个参数是：是否重复震动，0：不重复；1：重复
                    vibrator.vibrate(new long[]{500,500,300,300},-1);
//                     vibrator.vibrate(1000);
                 }
            }
        });
        Intent bindIntent = new Intent(this,SensorDataCollect.class);
        bindService(bindIntent,connection,BIND_AUTO_CREATE);
        progressBar.setVisibility(View.INVISIBLE);

        GifDrawable gifFromAssets = new GifDrawable(getResources(), R.drawable.walking);
        gifImageView.setImageDrawable(gifFromAssets);
        instrText.setHint(instrList[motionIndex]);
        accfileName = motionList[motionIndex] + "AccData.db3";
        gyrofileName = motionList[motionIndex] + "GyroData.db3";
        magfileName = motionList[motionIndex] + "MagData.db3";
    }

    private void reinitialization() throws IOException {
        Log.d("MotionIndex", String.valueOf(motionIndex));
        GifDrawable gifFromAssets = null;
        if(motionIndex<numOfActivities) {
            switch (motionIndex) {
                case 0:
                    gifFromAssets = new GifDrawable(getResources(), R.drawable.walking);
                    break;

                case 1:
                    gifFromAssets = new GifDrawable(getResources(), R.drawable.hop);
                    break;

                case 2:
                    gifFromAssets = new GifDrawable(getResources(), R.drawable.phone_call);
                    break;

                case 3:
                    gifFromAssets = new GifDrawable(getResources(), R.drawable.wave);
                    break;

                case 4:
                    gifFromAssets = new GifDrawable(getResources(), R.drawable.typing);
                    break;

                default:
                    break;
            }
            gifImageView.setImageDrawable(gifFromAssets);
            instrText.setHint(instrList[motionIndex]);
            stateText.setText(" ");
            btnEnd.setClickable(false);
            accfileName = motionList[motionIndex] + "AccData.db3";
            gyrofileName = motionList[motionIndex] + "GyroData.db3";
            magfileName = motionList[motionIndex] + "MagData.db3";
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_collect:
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                sensorBinder.reinit(motionIndex);
                sensorBinder.registerListener();
                sensorBinder.startRecord();

                btnStart.setClickable(false);
                btnEnd.setClickable(true);
                btnSubmit.setClickable(false);

                break;

            case R.id.end_collect:
                chronometer.stop();

                sensorBinder.stopRecord();
                sensorBinder.insertDB();
                sensorBinder.clearRecord();
                sensorBinder.closeDB();
                sensorBinder.unregisterListener();
                btnStart.setClickable(true);
                btnEnd.setClickable(false);
                btnSubmit.setClickable(true);
                Toast.makeText(getBaseContext(), "请点击'提交数据'按钮以提交 '"+motionMandarinList[motionIndex]+"' 实验数据",Toast.LENGTH_SHORT).show();
                break;

            case R.id.submit:
                if(progressBar.getVisibility()==View.INVISIBLE){
                    progressBar.setVisibility(View.VISIBLE);
                }
                else
                    progressBar.setVisibility(View.VISIBLE);
                stateText.setText("Uploading Data, please wait，请耐心等待......");
                new Thread(new DataThread(url,port,username,password,remotePath,filenamePath,motionIndex)).start();
                break;

            case R.id.clear:
                deleteFile(filenamePath+motionList[motionIndex]+"AccData.db3");
                deleteFile(filenamePath+motionList[motionIndex]+"GyroData.db3");
                deleteFile(filenamePath+motionList[motionIndex]+"MagData.db3");
                deleteFile(filenamePath+motionList[motionIndex]+"AccData.db3-journal");
                deleteFile(filenamePath+motionList[motionIndex]+"GyroData.db3-journal");
                deleteFile(filenamePath+motionList[motionIndex]+"MagData.db3-journal");
//                Toast.makeText(getBaseContext(), "已删除本次实验数据！"+filenamePath+motionList[motionIndex]+"AccData.db3",Toast.LENGTH_SHORT).show();
                break;

            case R.id.next:
                chronometer.setBase(SystemClock.elapsedRealtime());
                motionIndex+=1;
                if(motionIndex<numOfActivities){
                    try {
                        reinitialization();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    motionIndex-=1;
                    Intent intent = new Intent(SecondMainActivity.this, ThirdMainActivity.class);
                    startActivity(intent);
                    Log.d("start third activity: ","True");
                }
                break;

            case R.id.previous:
                chronometer.setBase(SystemClock.elapsedRealtime());
                motionIndex-=1;
                if(motionIndex>=0){
                    try {
                        reinitialization();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    motionIndex = 0;
                }
                break;

            default:
                break;
        }
    }
    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }
//    public String ftpUpload(String url, String port, String username, String password, String remotePath, String filenamePath, String fileName) {
//        FTPClient ftpClient = new FTPClient();
//        FileInputStream fis = null;
//        InputStream in = null;
//        String returnMessage = "Failed to upload!";
//        try{
//            ftpClient.connect(url,Integer.parseInt(port));
//            boolean loginResult = ftpClient.login(username, password);
//            int returnCode = ftpClient.getReplyCode();
//            Log.d("returnCode", String.valueOf(returnCode));
//            if(loginResult && FTPReply.isPositiveCompletion(returnCode)){
//                ftpClient.enterLocalPassiveMode();
//                ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE, ftpClient.BINARY_FILE_TYPE);
//
//                boolean changeDir = ftpClient.changeWorkingDirectory(remotePath);
//                Log.d("change directory", String.valueOf(changeDir));
//                if(!changeDir) {
//                    boolean makeDir = ftpClient.makeDirectory(remotePath);
//                    Log.d("change directory", String.valueOf(changeDir));
//                    Log.d("makeDir", String.valueOf(makeDir));
//                    boolean directCode = ftpClient.changeWorkingDirectory(remotePath);
//                    Log.d("change directory", String.valueOf(directCode));
//                }
//
//                ftpClient.setBufferSize(1024);
//                ftpClient.setControlEncoding("UTF-8");
////                File file = new File(filenamePath+fileName);
////                in = new FileInputStream(file);
////                String tempName = remotePath+File.separator+file.getName();
////                boolean flag = ftpClient.storeFile(new String(tempName.getBytes("UTF-8"),"ISO-8859-1"),in);
//
//                fis = new FileInputStream(filenamePath+fileName);
//                Log.d("filesize", String.valueOf(fis.available()));
//                boolean flag = ftpClient.storeFile(fileName,fis);
//                String returnString = ftpClient.getReplyString();
//                Log.d("returnCode", returnString);
//                Log.d("Upload: ", String.valueOf(flag));
//
//                returnMessage = "Successfully uploaded!";
////                ftpClient.logout();
//            }
//            else{
//                returnMessage = "Failed to upload!";
//            }
//        } catch (SocketException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try{
//                ftpClient.disconnect();
//            }catch (IOException e){
//                e.printStackTrace();
//            }
//        }
//        return returnMessage;
//    }


    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case UPDATE_TEXT:
                    if(progressBar.getVisibility()==View.VISIBLE){
                        progressBar.setVisibility(View.INVISIBLE);
                    }else{
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    stateText.setText("Upload successfully, click 'Next");
                    break;
                case FAILED_UPDATE_TEXT:
                    if(progressBar.getVisibility()==View.VISIBLE){
                        progressBar.setVisibility(View.INVISIBLE);
                    }else{
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    stateText.setText("Fail to upload, please try again");
                case FILE_NOT_EXIST:
                    if(progressBar.getVisibility()==View.VISIBLE){
                        progressBar.setVisibility(View.INVISIBLE);
                    }else{
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    stateText.setText("Data not exist, please start first");
                default:
                    break;
            }
        }
    };

    class DataThread implements Runnable{
        private String url;
        private String port;
        private String username;
        private String password;
        private String remotePath;
        private String filenamePath;
        private int motion_Index;
        private String returnMessage;

        public DataThread(String url, String port, String username, String password, String remotePath, String filenamePath, int motion_Index){
            this.url = url;
            this.port = port;
            this.username = username;
            this.password = password;
            this.remotePath = remotePath;
            this.filenamePath = filenamePath;
            this.motion_Index = motion_Index;
        }

//        @Override
//        public void run(){
//            FTPClient ftpClient = new FTPClient();
//            FileInputStream fis = null;
//            InputStream in = null;
//            String returnMessage = "Failed to upload!";
//            try{
//                ftpClient.connect(url);
//                ftpClient.login(username, password);
//                ftpClient.setPassive(true);
//                ftpClient.setType(FTPClient.TYPE_BINARY);
//                ftpClient.changeDirectory(remotePath);
//
//                String fileName = motionList[motion_Index]+"AccData.db3";
//                File f = new File(filenamePath+fileName);
//                ftpClient.upload(f);
//
//                fileName = motionList[motion_Index]+"GyroData.db3";
//                f = new File(filenamePath+fileName);
//                ftpClient.upload(f);
//
//                fileName = motionList[motion_Index]+"MagData.db3";
//                f = new File(filenamePath+fileName);
//                ftpClient.upload(f);
//
//                ftpClient.disconnect(true);
//
//                Message message = new Message();
//                message.what = UPDATE_TEXT;
//                handler.sendMessage(message);
//
//            } catch (SocketException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (FTPException e) {
//                e.printStackTrace();
//            } catch (FTPIllegalReplyException e) {
//                e.printStackTrace();
//            } catch (FTPAbortedException e) {
//                e.printStackTrace();
//            } catch (FTPDataTransferException e) {
//                e.printStackTrace();
//            }
//        }
        @Override
        public void run(){
            FTPClient ftpClient = new FTPClient();
            FileInputStream fis = null;
            InputStream in = null;
            String returnMessage = "Failed to upload!";
            try{
                ftpClient.connect(url,Integer.parseInt(port));
                boolean loginResult = ftpClient.login(username, password);
                int returnCode = ftpClient.getReplyCode();
                Log.d("returnCode", String.valueOf(returnCode));
                if(loginResult && FTPReply.isPositiveCompletion(returnCode)){
                    ftpClient.enterLocalPassiveMode();
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                    boolean changeDir = ftpClient.changeWorkingDirectory(remotePath);
                    Log.d("change directory", String.valueOf(changeDir));
                    if(!changeDir) {
                        boolean makeDir = ftpClient.makeDirectory(remotePath);
                        Log.d("change directory", String.valueOf(changeDir));
                        Log.d("makeDir", String.valueOf(makeDir));
                        boolean directCode = ftpClient.changeWorkingDirectory(remotePath);
                        Log.d("change directory", String.valueOf(directCode));
                    }

                    ftpClient.setBufferSize(0);
                    ftpClient.setControlEncoding("UTF-8");


                    String fileName = motionList[motion_Index]+"AccData.db3";
                    fis = new FileInputStream(filenamePath+fileName);
                    Log.d("filesize", String.valueOf(fis.available()));
                    boolean flag = ftpClient.storeFile(fileName,fis);
                    fis.close();

                    fis = new FileInputStream(filenamePath+fileName);
                    Log.d("filesize", String.valueOf(fis.available()));
                    flag = ftpClient.storeFile("1-"+fileName,fis);
                    fis.close();

                    String returnString = ftpClient.getReplyString();
                    Log.d("Upload: ",returnString);

//                    fileName = motionList[motion_Index]+"AccData.db3-journal";
//                    fis = new FileInputStream(filenamePath+fileName);
//                    Log.d("filesize", String.valueOf(fis.available()));
//                    flag = ftpClient.storeFile(fileName,fis);
//                    returnString = ftpClient.getReplyString();
//                    Log.d("Upload: ",returnString);

                    fileName = motionList[motion_Index]+"GyroData.db3";
                    fis = new FileInputStream(filenamePath+fileName);
                    Log.d("filesize", String.valueOf(fis.available()));
                    flag = ftpClient.storeFile(fileName,fis);
                    fis.close();

                    fis = new FileInputStream(filenamePath+fileName);
                    Log.d("filesize", String.valueOf(fis.available()));
                    flag = ftpClient.storeFile("1-"+fileName,fis);
                    fis.close();

                    returnString = ftpClient.getReplyString();
                    Log.d("Upload: ",returnString);

//                    fileName = motionList[motion_Index]+"GyroData.db3-journal";
//                    fis = new FileInputStream(filenamePath+fileName);
//                    Log.d("filesize", String.valueOf(fis.available()));
//                    flag = ftpClient.storeFile(fileName,fis);
//                    returnString = ftpClient.getReplyString();
//                    Log.d("Upload: ",returnString);

                    fileName = motionList[motion_Index]+"MagData.db3";
                    fis = new FileInputStream(filenamePath+fileName);
                    Log.d("filesize", String.valueOf(fis.available()));
                    flag = ftpClient.storeFile(fileName,fis);
                    fis.close();

                    fis = new FileInputStream(filenamePath+fileName);
                    Log.d("filesize", String.valueOf(fis.available()));
                    flag = ftpClient.storeFile("1-"+fileName,fis);
                    fis.close();

                    returnString = ftpClient.getReplyString();
                    Log.d("Upload: ",returnString);

//                    fileName = motionList[motion_Index]+"MagData.db3-journal";
//                    fis = new FileInputStream(filenamePath+fileName);
//                    Log.d("filesize", String.valueOf(fis.available()));
//                    flag = ftpClient.storeFile(fileName,fis);
//                    returnString = ftpClient.getReplyString();
//                    Log.d("Upload: ",returnString);


//                    Log.d("returnCode", returnString);
                    Log.d("Upload: ", String.valueOf(flag));
                    returnMessage = "Successfully uploaded!";
//                ftpClient.logout();
                    Message message = new Message();
                    message.what = UPDATE_TEXT;
                    handler.sendMessage(message);
                }
                else{
                    returnMessage = "Failed to upload!";
                    Log.d("Upload:","Failed to upload!");
                    Message message = new Message();
                    message.what = FAILED_UPDATE_TEXT;
                    handler.sendMessage(message);
                }
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                Message message = new Message();
                message.what = FILE_NOT_EXIST;
                handler.sendMessage(message);
            } finally {
                try{
                    ftpClient.disconnect();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}

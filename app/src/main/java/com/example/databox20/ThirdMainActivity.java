package com.example.databox20;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;

public class ThirdMainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnSubmit;
    private EditText name;
    private EditText age;
    private EditText gender;
    private EditText payment;
    private EditText account;
    private EditText phone;
    private ProgressBar progressBar;

    private String url = "121.5.61.117";
    private String port = "21";
    private String username = "xiezhiyuan";
    private String password = "ouyangxiaomin";
    private String filenamePath = "/data/data/com.example.databox20/files/";
    private String filenamePath_data = "/data/data/com.example.databox20/databases/";

    public String motionList[] = {
            "Walk", "Hop", "Call", "Wave", "typing"
    };
    public static final int CHECK_DATA = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.third_activity_main);
        initialization();
    }

    public void initialization() {
        btnSubmit = (Button)findViewById(R.id.information_submit);
        btnSubmit.setOnClickListener(this);
        name = (EditText)findViewById(R.id.true_name);
        age = (EditText)findViewById(R.id.age);
        gender = (EditText) findViewById(R.id.gender);
        payment = (EditText) findViewById(R.id.payment);
        account = (EditText) findViewById(R.id.account);
        phone = (EditText) findViewById(R.id.phone);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.information_submit:
                new Thread(new InDataThread(url,port,username,password,SecondMainActivity.remotePath,filenamePath)).start();
                Toast.makeText(getBaseContext(), "提交成功，感谢您的参与！",
                        Toast.LENGTH_LONG).show();
                break;

            default:
                break;
        }
    }

    class InDataThread implements Runnable{
        private String url;
        private String port;
        private String username;
        private String password;
        private String remotePath;
        private String filenamePath;
        private String returnMessage;

        public InDataThread(String url, String port, String username, String password, String remotePath, String filenamePath){
            this.url = url;
            this.port = port;
            this.username = username;
            this.password = password;
            this.remotePath = remotePath;
            this.filenamePath = filenamePath;
        }
        @Override
        public void run() {
            FTPClient ftpClient = new FTPClient();
            FileInputStream fis = null;
            try {
                FileOutputStream fileout=openFileOutput("basic_information.txt", MODE_PRIVATE);
                OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
                outputWriter.write(name.getText().toString()+"\n");
                outputWriter.write(age.getText().toString()+"\n");
                outputWriter.write(gender.getText().toString()+"\n");
                outputWriter.write(payment.getText().toString()+"\n");
                outputWriter.write(account.getText().toString()+"\n");
                outputWriter.write(phone.getText().toString()+"\n");
                outputWriter.close();

                ftpClient.connect(url,Integer.parseInt(port));
                boolean loginResult = ftpClient.login(username, password);
                int returnCode = ftpClient.getReplyCode();
                Log.d("returnCode", String.valueOf(returnCode));
                if(loginResult && FTPReply.isPositiveCompletion(returnCode)) {
                    ftpClient.enterLocalPassiveMode();
                    ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE, ftpClient.BINARY_FILE_TYPE);

                    boolean changeDir = ftpClient.changeWorkingDirectory(remotePath);
                    Log.d("change directory", String.valueOf(changeDir));
                    if (!changeDir) {
                        boolean makeDir = ftpClient.makeDirectory(remotePath);
                        Log.d("change directory", String.valueOf(changeDir));
                        Log.d("makeDir", String.valueOf(makeDir));
                        boolean directCode = ftpClient.changeWorkingDirectory(remotePath);
                        Log.d("change directory", String.valueOf(directCode));
                    }
                    ftpClient.setBufferSize(0);
                    ftpClient.setControlEncoding("UTF-8");

                    String fileName = "basic_information.txt";
                    fis = new FileInputStream(filenamePath+fileName);
                    Log.d("filesize", String.valueOf(fis.available()));
                    boolean flag = ftpClient.storeFile(fileName, fis);
                    fis.close();

//                    for(int i=0;i<5;i++) {
//                        fileName = motionList[i] + "GyroData.db3";
//                        fis = new FileInputStream(filenamePath_data + fileName);
//                        Log.d("filesize", String.valueOf(fis.available()));
//                        flag = ftpClient.storeFile("2-"+fileName, fis);
//                        fis.close();
//                    }
                    String returnString = ftpClient.getReplyString();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
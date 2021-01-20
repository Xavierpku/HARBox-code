package com.example.databox20;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnAgree, btnDisAgree;
    private TextView consentText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialization();
    }

    public void initialization() {
        btnAgree = (Button) findViewById(R.id.agree);
        btnDisAgree = (Button) findViewById(R.id.disagree);
        btnAgree.setOnClickListener(this);
        btnDisAgree.setOnClickListener(this);
        consentText = (TextView) findViewById(R.id.consent_form);
//        consentText.setText("  Our project aims to propose a new federated learning framework called " +
//                "CMTFL that captures the cluster structure of different data distribution on multiple " +
//                "computing nodes so that the nodes in the same cluster can assist with each other to" +
//                " get overall better performance.\n" +
//                "\n" +
//                "  This App is to use the inertial measurement unit (IMU) of your smartphone to " +
//                "record the motion of your actions for validation purpose. The raw data we collect " +
//                "will not contain any personally sensitive information. After the collection, the " +
//                "data will be securely stored in the computing devices managed by the PI's research " +
//                "group at the Department of Information Engineering. You will have an option to " +
//                "examine the data collected and remove any portion of the data.");
        String str="&emsp 我们正在收集智能手机运动传感器数据，用于验证我们提出的机器学习算法。<br/>" +
                "&emsp 该应用程序将使用智能手机的惯性测量单元（IMU），记录您做各种动作时手机的加速度等数据。<br/>" +
                "&emsp 本实验包含5个动作，每个动作耗时一分钟左右，总耗时在8分钟以内。每个活动开始45秒左右，手机会震动提示您结束活动。<font color='#B02318'>实验过程中请关闭手机屏幕‘自动旋转’ 功能</font>，避免影响数据收集。<font color='#B02318'>同时请保持网络畅通</font>，因为我们需要将数据上传到服务器，数据总大小在5M以内，使用流量也无需担心。<font color='#B02318'>每个小实验结束后均需提交数据。</font><br/>" +
                "&emsp 完成实验后，请填写您的收款方式，以便于我们对您发放报酬。完成实验并成功上传数据后即可获得20元RMB的基础报酬，并将视数据的质量追加奖励，最高可获得总计40元RMB。因需要审核数据质量，实验奖励将提交数据后3个工作日内发放。<br/>" +
                "&emsp 我们收集的数据将不包含任何个人的敏感信息。收集之后，您的数据将被安全地存储在香港中文大学信息工程学系该研究小组管理的计算设备中。您有权要求检查我们收集到的您的数据，并在有强力理由的情况下删除数据中的任何部分。您对实验过程有任何疑问，请联系香港中文大学信息工程学系欧阳小姐(852-52102071/86-13774660402)，谢先生(852-63707145/86-18810001276)。<br/>" +
                "&emsp 点击下方同意按钮即表示您已了解上述《知情同意书》的内容，并开始实验.";
        consentText.setText(Html.fromHtml(str));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.agree:
                Intent intent = new Intent(MainActivity.this, SecondMainActivity.class);
                startActivity(intent);
                Log.d("start second activity: ","True");
                break;

            case R.id.disagree:
                android.os.Process.killProcess(android.os.Process.myPid());
                break;

            default:
                break;
        }
    }
}
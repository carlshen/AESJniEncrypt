package com.androidyuan.aesjniencrypt;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.androidyuan.aesjni.AESEncrypt;

import android.util.Log;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public final String TAG = "tongfang";

    private final String str = "123abcABC*%!~#+_/中文测试";
    private final String ecode = "SkiDk/JC5F/BXKf/np7rWNub7ibxzYMjKwkQ7A6AqPw=";
    TextView deviceLog;
    Button devRefresh;
    Button devTest;
    Button devExit;
    File[] appsDir;
    Spinner devSpinner;
    ArrayAdapter<String> adapter;
    String[] arrDevices = {}; //{"深圳","上海","北京","山西"};        //创建ArrayAdapter对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deviceLog = (TextView) findViewById(R.id.device_log);
        devRefresh = (Button) findViewById(R.id.device_refresh);
        devTest = (Button) findViewById(R.id.device_test);
        devExit = (Button) findViewById(R.id.device_exit);
        devRefresh.setOnClickListener(this);
        devTest.setOnClickListener(this);
        devExit.setOnClickListener(this);

        devSpinner = (Spinner) findViewById(R.id.device_spinner);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, arrDevices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        devSpinner.setAdapter(adapter);
        devSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "点击了" + arrDevices[position], Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //下面的密文对应的原文：123abcABC&*(@#@#@)+_/中文测试
        //String code = AESEncrypt.encode(this, str);
//        String code = AESEncrypt.decode(this, ecode);
//        Log.i("code", "after    code: " + code);
        Log.i("code", "original code: " + ecode);

        // need init
        callTongfang();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                    finish();
                }
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void callTongfang() {
        appsDir = getExternalFilesDirs("/");
        long result = AESEncrypt.setPackageName(getPackageName());
        Log.i(TAG, "getPackageName(): " + getPackageName());
        Log.i(TAG, "setPackageName result: " + result);
    }
    private void SetDevList(String devs) {
        arrDevices = devs.split(",");
        if (arrDevices == null || arrDevices.length < 1) {
            Log.i(TAG, "SetDevList with no device.");
            toast("SetDevList with no device.");
            return;
        }
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, arrDevices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        devSpinner.setAdapter(adapter);
        devSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "点击了" + arrDevices[position], Toast.LENGTH_SHORT).show();
//                handle = AESEncrypt.ConnectDev(arrDevices[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    int handle = 0;
    String dev = null;
    String firmVersion = null;
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.device_refresh:
                toast("device_refresh");
                dev = AESEncrypt.RefreshDev();
                Log.i(TAG, "-----------RefreshDev dev: " + dev);
                if (!TextUtils.isEmpty(dev)) {
                    SetDevList(dev);
                } else {
                    toast("SetDevList with no device.");
                }
                break;
            case R.id.device_test:
//                firmVersion = AESEncrypt.GetFirmVer(handle);
                toast("device_test firmVersion = " + firmVersion);
                int result = AESEncrypt.ConnectDev("/storage/75A9-69A4");
                Log.i(TAG, "-----------ConnectDev result: " + result);
                toast("ConnectDev result = " + result);
                break;
            case R.id.device_exit:
                toast("device_exit");
                String ver = AESEncrypt.GetSDKVer();
                Log.i(TAG, "-----------GetSDKVer result: " + ver);
//                long discon = AESEncrypt.DisconnectDev(handle);
//                Log.i(TAG, "-----------DisconnectDev result: " + discon);
                break;
        }
    }

    private void toast(String str)
    {
        Toast.makeText(this,str, Toast.LENGTH_LONG).show();
    }
}

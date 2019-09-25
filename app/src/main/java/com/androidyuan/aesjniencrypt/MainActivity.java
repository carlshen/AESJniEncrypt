package com.androidyuan.aesjniencrypt;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidyuan.aesjni.AESEncrypt;

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
    EditText editCommand;
    Button runCommand;
    AppCompatSpinner devSpinner;
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
        editCommand = (EditText) findViewById(R.id.edit_command);
        runCommand = (Button) findViewById(R.id.device_command);
        runCommand.setOnClickListener(this);
        devRefresh.setOnClickListener(this);
        devTest.setOnClickListener(this);
        devExit.setOnClickListener(this);

        devSpinner = (AppCompatSpinner) findViewById(R.id.device_spinner);
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
        String[] temp = devs.split(",");
        if (temp == null || temp.length < 1) {
            Log.i(TAG, "SetDevList with no device.");
            toast("SetDevList with no device.");
            return;
        }
        arrDevices = new String[temp.length + 1];
        arrDevices[0] = "Device List";
        for(int i = 0; i < temp.length; i++) {
            arrDevices[i+1] = temp[i];
        }
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, arrDevices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        devSpinner.setAdapter(adapter);
        devSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(MainActivity.this, "点击了" + arrDevices[position], Toast.LENGTH_SHORT).show();
                if (position > 0) {
                    handle = AESEncrypt.ConnectDev(arrDevices[position]);
                    deviceLog.setText(deviceLog.getText() + "connect return " + handle + "\n");
                }
                Log.i(TAG, "onItemSelected position = " + position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    int handle = 0;
    String dev = null;
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.device_refresh:
//                toast("device_refresh");
                dev = AESEncrypt.RefreshDev();
                deviceLog.setText(deviceLog.getText() + "Refresh return " + dev + "\n");
                Log.i(TAG, "-----------RefreshDev dev: " + dev);
                if (!TextUtils.isEmpty(dev)) {
                    SetDevList(dev);
                } else {
                    toast("Refresh with no device.");
                }
                break;
            case R.id.device_test:
                if (arrDevices == null || arrDevices.length < 2) {
                    toast("Please refresh first.");
                    return;
                }
                handle = AESEncrypt.ConnectDev(arrDevices[1]);
                deviceLog.setText(deviceLog.getText() + "connect return " + handle + "\n");
                Log.i(TAG, "-----------ConnectDev result: " + handle);
//                toast("ConnectDev result = " + result);
                break;
            case R.id.device_command:
                if (TextUtils.isEmpty(editCommand.getText().toString().trim())) {
                    toast("Please enter command first.");
                    return;
                }
                Log.i(TAG, "-----------TransmitSd editCommand: " + editCommand.getText().toString().trim());
                try {
                    long transaction = AESEncrypt.BeginTransaction(handle);
                    deviceLog.setText(deviceLog.getText() + "transaction return " + transaction + "\n");
                    String ver = AESEncrypt.TransmitSd(handle, editCommand.getText().toString().trim(), 0);
                    deviceLog.setText(deviceLog.getText() + "Transmit return " + ver + "\n");
                    transaction = AESEncrypt.EndTransaction(handle);
                    deviceLog.setText(deviceLog.getText() + "EndTransaction return " + transaction + "\n");
                    Log.i(TAG, "-----------TransmitSd result: " + ver);
                } catch (Exception arg3) {
                    arg3.printStackTrace();
                    deviceLog.setText(deviceLog.getText() + arg3.getMessage() + "\n");
                }
                break;
            case R.id.device_exit:
//                toast("device_exit");
                deviceLog.setText("");
                long discon = AESEncrypt.DisconnectDev(handle);
                deviceLog.setText(deviceLog.getText() + "Disconnect return " + discon + "\n");
                Log.i(TAG, "-----------DisconnectDev result: " + discon);
                break;
        }
    }

    private void toast(String str)
    {
        Toast.makeText(this,str, Toast.LENGTH_LONG).show();
    }
}

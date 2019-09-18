package com.androidyuan.aesjniencrypt;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    final String[] arrDevices = {"深圳","上海","北京","山西"};        //创建ArrayAdapter对象

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
        String code = AESEncrypt.decode(this, ecode);

        Log.i("code", "original code: " + ecode);
        Log.i("code", "after    code: " + code);

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void callTongfang() {
        appsDir = getExternalFilesDirs("/");
        long result = AESEncrypt.setPackageName(getPackageName());
        Log.i(TAG, "setPackageName result: " + result);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.device_refresh:
                toast("device_refresh");
                break;
            case R.id.device_test:
                callTongfang();
                toast("device_test");
                break;
            case R.id.device_exit:
                toast("device_exit");
                break;
        }
    }

    private void toast(String str)
    {
        Toast.makeText(this,str, Toast.LENGTH_LONG).show();
    }
}

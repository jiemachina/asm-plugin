package com.canzhang.asmdemo;

import android.app.Service;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import asm.canzhang.com.asmdemo.R;

public class MainActivity extends AppCompatActivity {
    static {
        try {
            //测试 loadLibrary 调用
            System.loadLibrary("android_jpeg");
        } catch (UnsatisfiedLinkError e) {

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.bt_test0).setOnClickListener(v -> {
            testMethodCallOrFieldLod();
            System.out.println("哈哈0");
        });

        findViewById(R.id.bt_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("哈哈1");

                try{
                    //测试敏感函数调用
                    TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
                    tm.getDeviceId();
                }catch (Exception e){

                }

            }
        });

    }

    private void testMethodCallOrFieldLod() {
//        try{
//            //测试敏感函数调用
//            TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
//            tm.getDeviceId();
//        }catch (Exception e){
//
//        }
    }

}

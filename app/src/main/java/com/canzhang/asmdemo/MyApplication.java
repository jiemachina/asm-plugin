package com.canzhang.asmdemo;

import android.app.Application;

import com.gamehelper.method_call_record_lib.MethodRecordSDK;
import com.gamehelper.method_call_record_lib.RecordCallListener;


public class MyApplication extends Application {
    static {

        //测试敏感函数调用
        MethodRecordSDK.setRecordCallListener(new RecordCallListener() {
            @Override
            public void onRecordMethodCall(String s) {
                android.util.Log.e("MethodRecordSDK", "调用的方法是：" + s);
                android.util.Log.e("MethodRecordSDK", String.format("\n\n----------------------%s调用堆栈开始------------------------\n\n", "函数"));
                StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

                for(int i = 0; i < stackTraceElements.length; ++i) {
                    android.util.Log.d("MethodRecordSDK", stackTraceElements[i].toString());
                }

                android.util.Log.e("MethodRecordSDK", String.format("\n\n----------------------%s调用堆栈结束------------------------\n\n", "函数"));
            }

        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Runnable test = () -> System.out.println("xxxxxxx");
    }
}

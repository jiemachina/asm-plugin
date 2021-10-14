package com.canzhang.asmdemo;

import android.app.Application;
import android.util.Log;

import com.gamehelper.method.call.lib.MethodRecordSDK;
import com.gamehelper.method.call.lib.RecordCallListener;


public class MyApplication extends Application {
    static {

        //测试敏感函数调用
        MethodRecordSDK.setRecordCallListener(new RecordCallListener() {
            @Override
            public void onRecordMethodCall(String s) {
                Log.e("MethodRecordSDK", "调用的方法是：" + s);
                Log.e("MethodRecordSDK",
                        String.format("\n\n----------------------%s调用堆栈开始------------------------\n\n", "函数"));
                StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

                for (StackTraceElement stackTraceElement : stackTraceElements) {
                    Log.d("MethodRecordSDK", stackTraceElement.toString());
                }

                Log.e("MethodRecordSDK",
                        String.format("\n\n----------------------%s调用堆栈结束------------------------\n\n", "函数"));
            }

        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Runnable test = () -> System.out.println("xxxxxxx");
    }
}

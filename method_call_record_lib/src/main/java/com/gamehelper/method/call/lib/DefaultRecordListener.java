package com.gamehelper.method.call.lib;

import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

public class DefaultRecordListener implements RecordCallListener {
    private static AtomicInteger methodCallNum = new AtomicInteger(0);//可能涉及多个进程 累加就好

    @Override
    public void onRecordMethodCall(String from) {
        Log.e("MethodRecordSDK", "调用的方法是：" + from);
        printStackTrace("函数:" + methodCallNum.addAndGet(1));
    }


    private synchronized static void printStackTrace(String tips) {
        Log.e("MethodRecordSDK", String.format("\n\n----------------------%s调用堆栈开始------------------------\n\n", tips));
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stackTraceElements.length; i++) {
            Log.d("MethodRecordSDK", stackTraceElements[i].toString());
        }
        Log.e("MethodRecordSDK", String.format("\n\n----------------------%s调用堆栈结束------------------------\n\n", tips));
    }

}

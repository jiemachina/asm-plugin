package com.canzhang.asmdemo.sdk;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * 变量引用替换为方法实现范例
 */
public class ReplaceFieldApi {

    /**
     * 实例方法测试
     * @param myTest
     * @return
     */
    public static String getMyTestField(MyTest myTest) {
        return myTest.myTestField;
    }


    /**
     * 静态方法测试
     * @return
     */
    public static String getBrand() {
        return Build.BRAND;
    }


}

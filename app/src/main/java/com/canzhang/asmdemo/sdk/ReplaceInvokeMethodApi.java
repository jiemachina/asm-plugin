package com.canzhang.asmdemo.sdk;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.net.wifi.WifiInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class ReplaceInvokeMethodApi {

    @SuppressLint("MissingPermission")
    public static String getPhoneNumberImpl(TelephonyManager telephonyManager) {
//        return telephonyManager.getLine1Number();
        Log.e("MethodRecordSDK","敏感函数 getPhoneNumberImpl 方法被调用了");
        return "我是测试数据  from getPhoneNumberImpl";
    }

    public static String getDeviceIdImpl(TelephonyManager telephonyManager) {
        Log.e("MethodRecordSDK","敏感函数 getDeviceIdImpl 方法被调用了");
//        return telephonyManager.getDeviceId();
        return "我是测试数据 from  getDeviceIdImpl";
    }

    public static String getSimSerialNumberImpl(TelephonyManager telephonyManager) {
        Log.e("MethodRecordSDK","敏感函数 getSimSerialNumberImpl 方法被调用了");
//        return telephonyManager.getSimSerialNumber();
        return "我是测试数据 from  getSimSerialNumberImpl";
    }

    public static String getSubscriberIdImpl(TelephonyManager telephonyManager) {
        Log.e("MethodRecordSDK","敏感函数 getSubscriberIdImpl 方法被调用了");
//        return telephonyManager.getSubscriberId();
        return "我是测试数据 from  getSubscriberIdImpl";
    }


    public static String getMacAddressImpl(WifiInfo wifiInfo) {
        Log.e("MethodRecordSDK","敏感函数 getMacAddressImpl 方法被调用了");
//        return wifiInfo.getMacAddress();
        return "我是测试数据 from  getMacAddressImpl";
    }

    public static String getSSIDImpl(WifiInfo wifiInfo) {
        Log.e("MethodRecordSDK","敏感函数 getSSIDImpl 方法被调用了");
//        return wifiInfo.getSSID();
        return "我是测试数据  from getSSIDImpl";
    }


    public static Enumeration<InetAddress> getInetAddressesImpl(NetworkInterface networkInterface) {
        Log.e("MethodRecordSDK","敏感函数 getInetAddressesImpl 方法被调用了");
        return networkInterface.getInetAddresses();
//        return null;
    }


    public static String getHostAddressImpl(InetAddress inetAddress) {
        Log.e("MethodRecordSDK","敏感函数 getHostAddressImpl 方法被调用了");
//        return inetAddress.getHostAddress();
        return "我是测试数据 from getHostAddressImpl";
    }


    public static String getStringImpl(ContentResolver resolver, String name) {
        Log.e("MethodRecordSDK","敏感函数 getStringImpl 方法被调用了");
        return "我是测试数据 from  getStringImpl";
    }


}

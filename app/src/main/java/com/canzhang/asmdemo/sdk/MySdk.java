package com.canzhang.asmdemo.sdk;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.net.wifi.WifiInfo;
import android.telephony.TelephonyManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class MySdk {

    @SuppressLint("MissingPermission")
    public static String getPhoneNumberImpl(TelephonyManager telephonyManager) {
//        return telephonyManager.getLine1Number();
        return "我是测试数据  from getPhoneNumberImpl";
    }

    public static String getDeviceIdImpl(TelephonyManager telephonyManager) {
//        return telephonyManager.getDeviceId();
        return "我是测试数据 from  getDeviceIdImpl";
    }

    public static String getSimSerialNumberImpl(TelephonyManager telephonyManager) {
//        return telephonyManager.getSimSerialNumber();
        return "我是测试数据 from  getSimSerialNumberImpl";
    }

    public static String getSubscriberIdImpl(TelephonyManager telephonyManager) {
//        return telephonyManager.getSubscriberId();
        return "我是测试数据 from  getSubscriberIdImpl";
    }


    public static String getMacAddressImpl(WifiInfo wifiInfo) {
//        return wifiInfo.getMacAddress();
        return "我是测试数据 from  getMacAddressImpl";
    }

    public static String getSSIDImpl(WifiInfo wifiInfo) {
//        return wifiInfo.getSSID();
        return "我是测试数据  from getSSIDImpl";
    }


    public static Enumeration<InetAddress> getInetAddressesImpl(NetworkInterface networkInterface) {
        return networkInterface.getInetAddresses();
//        return null;
    }


    public static String getHostAddressImpl(InetAddress inetAddress) {
//        return inetAddress.getHostAddress();
        return "我是测试数据 from getHostAddressImpl";
    }


    public static String getStringImpl(ContentResolver resolver, String name) {
        return "我是测试数据 from  getStringImpl";
    }


}

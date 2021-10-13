package com.canzhang.asmdemo.sdk;

import android.content.ContentResolver;
import android.net.wifi.WifiInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * 替换方法实现范例
 * 需要注意全部使用的是静态方法进行实现的，另外如果调用的方法是实例方法，需要把实例传入进来，有参数也需要把参数进行传入
 */
public class ReplaceInvokeMethodApi {
    /**
     * 静态方法范例（针对静态方法，按照原有参数描述，进行填写即可）
     *
     * @param resolver
     * @param name
     * @return
     */
    public static String getStringImpl(ContentResolver resolver, String name) {
        //getString 有两个方法实现，归属不同类，注意调整
        Log.e("MethodRecordSDK", "敏感函数 getStringImpl 方法被调用了");
        return "我是测试数据 from  getStringImpl";
    }

    /**
     * 实例方法测试（针对实例方法，需要在原有参数描述的基础上，新增实例入参，并放置在第一位）
     *
     * @param telephonyManager （当前方法调用对应的实例）
     * @return
     */
    public static String getPhoneNumberImpl(TelephonyManager telephonyManager) {
//        return telephonyManager.getLine1Number();
        Log.e("MethodRecordSDK", "敏感函数 getPhoneNumberImpl 方法被调用了");
        return "我是测试数据  from getPhoneNumberImpl";
    }

    public static String getDeviceIdImpl(TelephonyManager telephonyManager) {
        Log.e("MethodRecordSDK", "敏感函数 getDeviceIdImpl 方法被调用了");
//        return telephonyManager.getDeviceId();
        return "我是测试数据 from  getDeviceIdImpl";
    }

    public static String getSimSerialNumberImpl(TelephonyManager telephonyManager) {
        Log.e("MethodRecordSDK", "敏感函数 getSimSerialNumberImpl 方法被调用了");
//        return telephonyManager.getSimSerialNumber();
        return "我是测试数据 from  getSimSerialNumberImpl";
    }

    public static String getSubscriberIdImpl(TelephonyManager telephonyManager) {
        Log.e("MethodRecordSDK", "敏感函数 getSubscriberIdImpl 方法被调用了");
//        return telephonyManager.getSubscriberId();
        return "我是测试数据 from  getSubscriberIdImpl";
    }


    public static String getMacAddressImpl(WifiInfo wifiInfo) {
        Log.e("MethodRecordSDK", "敏感函数 getMacAddressImpl 方法被调用了");
//        return wifiInfo.getMacAddress();
        return "我是测试数据 from  getMacAddressImpl";
    }

    public static String getSSIDImpl(WifiInfo wifiInfo) {
        Log.e("MethodRecordSDK", "敏感函数 getSSIDImpl 方法被调用了");
//        return wifiInfo.getSSID();
        return "我是测试数据  from getSSIDImpl";
    }


    public static Enumeration<InetAddress> getInetAddressesImpl(NetworkInterface networkInterface) {
        Log.e("MethodRecordSDK", "敏感函数 getInetAddressesImpl 方法被调用了");
        return networkInterface.getInetAddresses();
//        return null;
    }


    public static String getHostAddressImpl(InetAddress inetAddress) {
        Log.e("MethodRecordSDK", "敏感函数 getHostAddressImpl 方法被调用了");
//        return inetAddress.getHostAddress();
        return "我是测试数据 from getHostAddressImpl";
    }

}

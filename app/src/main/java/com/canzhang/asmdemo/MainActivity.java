package com.canzhang.asmdemo;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.canzhang.asmdemo.sdk.MyTest;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import asm.canzhang.com.asmdemo.R;

public class MainActivity extends AppCompatActivity {
    static {
        try {
            //测试 loadLibrary 调用
            System.loadLibrary("android_jpeg");
            Object o = new Object();
            o.toString();
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String brand = Build.BRAND;
        String myTestField = new MyTest().myTestField;

        findViewById(R.id.bt_test0).setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "哈哈0", Toast.LENGTH_SHORT).show();
        });


        findViewById(R.id.bt_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "收到点击", Toast.LENGTH_SHORT).show();

                StringBuilder builder = new StringBuilder();
                builder.append("\n\n-------敏感函数替换调用测试---开始-------");
                builder.append("\ngetPhoneNumber：")
                        .append(getPhoneNumber(MainActivity.this))
                        .append("\ngetDeviceId：")
                        .append(getDeviceId())
                        .append("\ngetSimSerialNumber：")
                        .append(getSimSerialNumber())
                        .append("\ngetSubscriberId：")
                        .append(getSubscriberId())
                        .append("\ngetMacAddress：")
                        .append(getMacAddress())
                        .append("\ngetSSID：")
                        .append(getSSID())
                        .append("\ngetLocalIpAddress：")
                        .append(getLocalIpAddress())
                        .append("\ngetOrigAndroidID：")
                        .append(getOrigAndroidID(MainActivity.this));

                builder.append("\n-------敏感函数替换调用测试---开始-------");

                Log.e("MethodRecordSDK", builder.toString());

            }
        });

    }


    /**
     * 测试常用敏感函数 hook 以及替换实现方法
     */

    @SuppressLint("MissingPermission")
    public static String getPhoneNumber(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getLine1Number();
    }

    public String getDeviceId() {
        //测试敏感函数调用
        TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }


    public String getSimSerialNumber() {
        //测试敏感函数调用
        TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        return tm.getSimSerialNumber();
    }

    public String getSubscriberId() {
        //测试敏感函数调用
        TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        return tm.getSubscriberId();
    }

    public String getMacAddress() {
        //测试敏感函数调用
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifi.getConnectionInfo().getMacAddress();
    }

    public String getSSID() {
        //测试敏感函数调用
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifi.getConnectionInfo().getSSID();
    }


    /**
     * 获取ip
     * @return
     */
    public static String getLocalIpAddress() {
        String ip = "127.0.0.1";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && !inetAddress.isLinkLocalAddress()) {
                        ip = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ip;
    }

    static String s = "android_id";

    static String wwww = "xxx";

    //Android_ID
    public static String getOrigAndroidID(Context context) {
        wwww = "666";
        String aid = wwww;
        try {
            aid = Settings.Secure.getString(context.getContentResolver(), s);
            //方案2
            aid = Settings.System.getString(context.getContentResolver(), "android_id");
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return aid;
    }

}

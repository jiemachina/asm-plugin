package com.canzhang.asmdemo;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import asm.canzhang.com.asmdemo.R;

public class MainActivity extends AppCompatActivity {
    static {
        try {
            //这里报找不到 jpeg so(2020年11月23日19:59:01)，代码没有追溯到之前的代码，这里是参考的以下文章，重新添加的jpeg.so库
            //https://www.cnblogs.com/mc-ksuu/p/6443254.html
            //https://github.com/mcksuu/jpeg-android
            //https://github.com/libjpeg-turbo/libjpeg-turbo
            String s ;
            System.loadLibrary(s = "android_jpeg");
            System.out.println(s);
        } catch (UnsatisfiedLinkError e) {

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.bt_test0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testMethodCallOrFieldLod();
                System.out.println("哈哈");
            }
        });

    }

    private void testMethodCallOrFieldLod() {
    }

    private static native float nativeMse(Bitmap originalBitmap, Bitmap distortedBitmap);
    private static native int nativeCompress(Bitmap bitmap, int quality, String outFileName);
    public static void testCan(){
        nativeCompress(null,0,null);
    }
}

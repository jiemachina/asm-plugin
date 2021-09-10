package com.gamehelper.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodCallRecordExtension {


    /**
     * 方法调用监控（注意需非系统api调用，才能监控的到，因为插桩是无法插入到系统api的，比如常见的点击回调就是在系统api调用的）
     * @ key :类名  如：android/telephony/TelephonyManager
     * @ list item value :方法名+方法描述  如：getLine1Number()Ljava/lang/String; 支持多个方法
     */
    public static Map<String, List<String>> hookMethodInvokeMap = new HashMap<>();

    /**
     * 方法进入监控（比如常见的点击事件，调用处是系统api，这个时候我们就监控接口实现，也就是方法体的进入）
     *
     * @ key :类名  如：android/view/View$OnClickListener
     * @ list item value :方法名+方法描述  如：getLine1Number()Ljava/lang/String; 支持多个方法
     */
    public static Map<String,  List<String>> hookMethodEnterMap = new HashMap<>();

    /**
     * 不知类的路径，和方法描述怎么写，可以在这里添加方法名，然后build一下会自动打印出来（无论是方法调用，还是方法体进入，这里都会打印，以保证监控到所有情况，可能会重复打印）
     *
     * item value: 方法名：如 getLine1Number
     */
    public static List<String> methodTest = new ArrayList<>();
}

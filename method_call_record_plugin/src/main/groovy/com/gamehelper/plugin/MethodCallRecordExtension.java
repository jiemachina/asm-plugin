package com.gamehelper.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodCallRecordExtension {


    /**
     * 方法调用监控（注意需非系统api调用，才能监控的到，因为插桩是无法插入到系统api的，比如常见的点击回调就是在系统api调用的）
     *
     * @ key :类名  如：android/telephony/TelephonyManager
     * @ list item value :方法名+方法描述  如：getLine1Number()Ljava/lang/String; 支持多个方法
     */
    public static Map<String, List<String>> hookMethodInvokeMap = new HashMap<>();


    /**
     * 替换调用的方法
     *
     * @ key : 需要替换的 方法归属类+"."+方法名+方法描述   如：android/telephony/TelephonyManager.getLine1Number()Ljava/lang/String;
     * @ list item value : 替换成  index0=类名，如：com/canzhang/ImplTelephonyManager；  index1=方法名，如：getLine1Number；  index2=方法描述符，如：  ()Ljava/lang/String; ； 严格按照顺序填入
     */
    public static Map<String, List<String>> replaceMethodInvokeMap = new HashMap<>();


    /**
     * 替换变量调用
     * @ key : 需要替换的 变量归属类+"."+变量名+"."+变量类型描述   如：android/os/Build.BRAND.Ljava/lang/String;
     * @ list item value : 替换成  index0=类名，如：com/canzhang/asmdemo/sdk/ReplaceFieldApi；  index1=获取方法名，如：getBrand；  index2=方法描述符，如：  ()V; ； 严格按照顺序填入
     */
    public static Map<String, List<String>> replaceFieldInvokeMap = new HashMap<>();

    /**
     * 方法进入监控（比如常见的点击事件，调用处是系统api，这个时候我们就监控接口实现，也就是方法体的进入）
     *
     * @ key :类名  如：android/view/View$OnClickListener
     * @ list item value :方法名+方法描述  如：getLine1Number()Ljava/lang/String; 支持多个方法
     */
    public static Map<String, List<String>> hookMethodEnterMap = new HashMap<>();


    /**
     * 不知类的路径，和方法描述怎么写，可以在这里添加方法名，然后build一下会自动打印出来（无论是方法调用，还是方法体进入，这里都会打印，以保证监控到所有情况，可能会重复打印）
     * <p>
     * item value: 方法名：如 getLine1Number8
     */
    public static List<String> methodTest = new ArrayList<>();
    /**
     * 不知变量归属类，变量描述怎么写，可以在这里添加变量名，然后build一下会自动打印出来
     * <p>
     * item value: 变量名：如 BRAND
     */
    public static List<String> fieldTest = new ArrayList<>();

    /**
     * 不需要插桩的路径，可以传递全路径，也可以传递父级文件夹  例如 ：com/canzhang/asmdemo/sdk/MySdk
     */
    public static List<String> ignorePath = new ArrayList<>();
}

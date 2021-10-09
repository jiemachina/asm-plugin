package com.canzhang.asmdemo.sdk;

import android.os.Build;

/**
 * 变量引用替换为方法实现范例
 */
public class ReplaceFieldApi {

    private static String sBrand = null;

    /**
     * 实例方法测试
     *
     * @param myTest
     * @return
     */
    public static String getMyTestField(MyTest myTest) {
        return myTest.myTestField;
    }


    /**
     * 静态方法测试
     *
     * @return
     */
    public static String getBrand() {
        if(sBrand==null){
            return sBrand=Build.BRAND;
        }
        return sBrand;

    }
}

package com.canzhang.asmdemo;

import android.view.View;

public class MainTest {
    public static void main(String[] args) {
    }

    public void onClick(View v) {
        System.out.println("哈哈1");
    }


    public static String woshiceshi(MainActivity mainActivity) {
        return "我是测试代码";
    }


    public static String test02(String s) {
        return "hahah" + s;
    }


    public  int calc() {
        int a = 100;
        int b = 2;
        int c = 3000;
        return (a + b) * c;
    }

}

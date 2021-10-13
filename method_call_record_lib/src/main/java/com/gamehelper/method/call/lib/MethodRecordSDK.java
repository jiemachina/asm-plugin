package com.gamehelper.method.call.lib;

/**
 * 监控sdk
 */
public class MethodRecordSDK {

    private static RecordCallListener recordCallListener = new DefaultRecordListener();


    /**
     * 记录敏感函数调用
     * 对于实例方法，可以简单通过插入我们的方法记录堆栈
     *
     * @param from
     */
    public static synchronized void recordMethodCall(String from) {
        recordCallListener.onRecordMethodCall(from);
    }


    /**
     * 设置回调
     *
     * @param recordCallListener
     */
    public static void setRecordCallListener(RecordCallListener recordCallListener) {
        MethodRecordSDK.recordCallListener = recordCallListener;
    }
}

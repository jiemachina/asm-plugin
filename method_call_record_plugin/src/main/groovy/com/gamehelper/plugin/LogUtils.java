package com.gamehelper.plugin;

public class LogUtils {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    /**
     * 日志打印
     *
     * @param str
     */
    static synchronized void log(String str) {
        System.out.println(str);
        WriteLogUtils.writeLogToLocal(str);
    }

    /**
     * 带颜色的日志打印
     *
     * @param str
     */
    static synchronized void loge(String str) {
        System.out.println(ANSI_RED + str + ANSI_RESET);
        WriteLogUtils.writeLogToLocal(str);
    }


}

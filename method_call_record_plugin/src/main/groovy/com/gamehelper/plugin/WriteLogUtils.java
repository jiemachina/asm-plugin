package com.gamehelper.plugin;

import org.apache.http.util.TextUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class WriteLogUtils {
    public static final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();
    private static final File desFile = new File("method_call_plugin_hook_log.txt");
    private static BufferedWriter bw;


    public static void writeLogToLocal(String log) {
        if (!MethodCallRecordExtension.isNeedWriteLogToLocal) {
            return;
        }
        if (TextUtils.isEmpty(log)) {
            return;
        }
        try {
            LOCK.writeLock().lock();
            if (bw == null) {
                bw = new BufferedWriter(new FileWriter(desFile));
            }

            bw.write(log);
            bw.newLine();
            bw.flush();//不能关掉  关掉就不能持续写入了
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LOCK.writeLock().unlock();
        }

    }

    public static void releaseIO() {
        if (bw != null) {
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bw = null;
        }
    }

    public static void deleteHistoryLog() {
        desFile.delete();
    }
}

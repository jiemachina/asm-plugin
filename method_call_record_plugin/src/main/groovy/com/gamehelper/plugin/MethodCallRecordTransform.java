package com.gamehelper.plugin;

import com.android.build.api.transform.Context;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformOutputProvider;
import com.quinn.hunter.transform.HunterTransform;

import org.gradle.api.Project;

import java.io.IOException;
import java.util.Collection;

/**
 * Google官方在Android Gradle的1.5.0 版本以后提供了 Transfrom API,
 * 允许第三方 Plugin 在打包 dex 文件之前的编译过程中操作 .class 文件，
 * 我们做的就是实现Transform进行.class文件遍历拿到所有方法，修改完成对原文件进行替换。
 */
public class MethodCallRecordTransform extends HunterTransform {
    private Project project;


    public MethodCallRecordTransform(Project project) {
        super(project);
        this.project = project;
        this.bytecodeWeaver = new MethodCallRecordWeaver();
    }


    @Override
    public void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        WriteLogUtils.deleteHistoryLog();
        long startTime = System.currentTimeMillis();
        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental);
        LogUtils.log("\n\n\n\n方法检测插件：" + getName() + " 总耗时： " + (System.currentTimeMillis() - startTime) + "ms");
        WriteLogUtils.releaseIO();
    }

}

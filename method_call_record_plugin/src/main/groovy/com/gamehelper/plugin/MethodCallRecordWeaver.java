package com.gamehelper.plugin;

import com.quinn.hunter.transform.asm.BaseWeaver;
import com.quinn.hunter.transform.asm.ExtendClassWriter;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Quinn on 09/07/2017.
 */
public final class MethodCallRecordWeaver extends BaseWeaver {

    /**
     * 判断是否需要拦截处理此class（从transform调用过来的）
     *
     * @param fullQualifiedClassName
     * @return
     */
    @Override
    public boolean isWeavableClass(String fullQualifiedClassName) {
        return super.isWeavableClass(fullQualifiedClassName);
    }

    @Override
    protected ClassVisitor wrapClassWriter(ClassWriter classWriter) {
        return new MethodCallRecordClassAdapter(classWriter);
    }

    @Override
    public byte[] weaveSingleClassToByteArray(InputStream inputStream) throws IOException {
        ClassReader classReader = new ClassReader(inputStream);
        /**
         * bug fix 编译失败 {com.android.dx.cf.code.SimException} stack: overflow
         * 修改方案参考自：https://github.com/Tencent/matrix/pull/201
         *
         * ClassWriter.COMPUTE_MAXS:表示会自动计算操作数栈的最大深度，和局部变量空间，如果设置了这个  visitMaxs 方法就被忽略了，会自动根据方法签名和字节码计算。
         * ClassWriter.COMPUTE_FRAMES：表示会自动从头计算方法的堆栈映射帧，如果设置了这个  visitFrame 和  visitMaxs   方法都会被忽略，换句话说，这个标志也意味着涵盖上面标志的能力
         */
        ClassWriter classWriter = new ExtendClassWriter(classLoader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        ClassVisitor classWriterWrapper = wrapClassWriter(classWriter);
        classReader.accept(classWriterWrapper, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }
}

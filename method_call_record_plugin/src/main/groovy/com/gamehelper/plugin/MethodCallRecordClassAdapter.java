package com.gamehelper.plugin;

import static org.objectweb.asm.Opcodes.ASM6;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ClassVisitor:主要负责遍历类的信息，包括类上的注解、构造方法、字段等等。
 */
public final class MethodCallRecordClassAdapter extends ClassVisitor {

    private String className;
    private String sdkClassPath = "com/gamehelper/method_call_record_lib/MethodRecordSDK";
    private String[] mInterfaces;//当前所扫描的类实现的接口

    MethodCallRecordClassAdapter(final ClassVisitor cv) {
        //注意这里的版本号要留意，不同版本可能会抛出异常，仔细观察异常
        super(ASM6, cv);
    }

    /**
     * 这里可以拿到关于.class的所有信息，比如当前类所实现的接口类表等
     *
     * @param version    表示jdk的版本
     * @param access     当前类的修饰符 （这个和ASM 和 java有些差异，比如public 在这里就是ACC_PUBLIC）
     * @param name       当前类名
     * @param signature  泛型信息
     * @param superName  当前类的父类
     * @param interfaces 当前类实现的接口列表
     */
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = name;
        mInterfaces = interfaces;
        super.visit(version, access, name, signature, superName, interfaces);
    }


    @Override
    public void visitEnd() {
        super.visitEnd();

    }

    /**
     * 这里可以拿到关于method的所有信息，比如方法名，方法的参数描述等
     *
     * @param access     方法的修饰符
     * @param outName    方法名
     * @param desc       方法描述（就是（参数列表）返回值类型拼接）
     * @param signature  泛型相关信息
     * @param exceptions 方法抛出的异常信息
     * @return
     */
    @Override
    public MethodVisitor visitMethod(final int access, final String outName,
                                     final String desc, final String signature, final String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, outName, desc, signature, exceptions);
        final AtomicBoolean isInvokeLoadLibrary = new AtomicBoolean(false);
        List<String> mLdcList = new ArrayList<>();
        mv = new AdviceAdapter(ASM6, mv, access, outName, desc) {

            @Override
            public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
                super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);

//                try {
//                    String desc2 = bsmArgs[0].toString();
//                    //返回类型（方法所属类）、调用方法的方法名、调用方法的描述符
//                    String key = Type.getReturnType(desc).getDescriptor() + name + desc2;
//                    LogUtils.log("xxxxxxxxx===========>>>>>>>>>：" + desc2 + "\nkey:" + key);
//                } catch (Exception e) {
//
//                }
//
//
//                LogUtils.log("---------visitInvokeDynamicInsn------>>>>>\nname:" + name + "\ndesc:" + desc + "\noutMethodName（上层类名_方法名）:" + className + "_" + outName);
//                if (bsmArgs != null) {
//                    for (int i = 0; i < bsmArgs.length; i++) {
//                        Object arg = bsmArgs[i];
//                        LogUtils.log("参数列表：" + arg + "  类型：" + arg.getClass().getSimpleName());
//                    }
//                }
            }

            @Override
            public void visitLdcInsn(Object cst) {//访问一些常量
                if (MethodCallRecordExtension.methodTest != null && MethodCallRecordExtension.methodTest.contains("loadLibrary") && cst instanceof String) {
                    mLdcList.add((String) cst);
                }
                super.visitLdcInsn(cst);
            }

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
//                if("com/canzhang/asmdemo/sdk/MyTest".equals(className)){
//                    LogUtils.log("--------------->>>>>\n\nopcode(操作码):" + opcode + "\n\nowner:" + owner + "\n\nname（:" + name + "\n\ndesc:" + desc + "\n\noutMethodName（上层类名_方法名）:" +className+"_"+ outName);
//                }
//                if (opcode == Opcodes.GETSTATIC && "android/os/Build".equals(owner)) {
//                    //加载一个常量
//                    mv.visitLdcInsn(className + "_" + outName + "_load: fieldName:" + name + " fieldDesc:" + desc + " fieldOwner:" + owner);
//                    //调用我们自定义的方法 (注意用/,不是.; 方法描述记得；也要)
//                    mv.visitMethodInsn(INVOKESTATIC, sdkClassPath, "recordLoadFiled", "(Ljava/lang/String;)V", false);
//                }
                super.visitFieldInsn(opcode, owner, name, desc);


            }

            @Override
            protected void onMethodEnter() {
                super.onMethodEnter();
                //打印方法信息
                if (MethodCallRecordExtension.methodTest != null && MethodCallRecordExtension.methodTest.contains(outName)) {
                    LogUtils.log("----------测试打印数据---form 方法进入 -->>>>>"
                            + "\n\naccess（方法修饰符）:" + access
                            + "\n\noutName（方法名）:" + outName
                            + "\n\ndesc（方法描述（就是（参数列表）返回值类型拼接））:" + desc
                            + "\n\nsignature（方法泛型信息：）:" + signature
                            + "\n\nclassName（当前扫描的类名）:" + className);
                }
                hookMethod(className, outName, desc, MethodCallRecordExtension.hookMethodEnterMap);
            }


            @Override
            protected void onMethodExit(int opcode) {
                super.onMethodExit(opcode);
                if (isInvokeLoadLibrary.get() && mLdcList.size() > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    //用于判断加载了什么so
                    stringBuilder.append("\n------loadLibrary所在方法体加载的常量 开始--------\n");
                    for (String item : mLdcList) {
                        stringBuilder.append(item).append("\n");
                    }
                    stringBuilder.append("------loadLibrary所在方法体加载的常量 结束--------");
                    LogUtils.log(stringBuilder.toString());
                }
            }

            /**
             * 访问调用方法的指令（这里仅针对调用方法的指令，其他指令还有返回指令，异常抛出指令一类的） 像接口回调这一类的是调用不到的（因为回调的点是系统api，这里捕获不到）
             * @param opcode 指令
             * @param owner  指令所调用的方法归属的类
             * @param name   方法名
             * @param descriptor 方法描述（就是（参数列表）返回值类型拼接）
             * @param isInterface 是否接口
             */
            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                //打印方法信息
                if (MethodCallRecordExtension.methodTest != null && MethodCallRecordExtension.methodTest.contains(name)) {
                    LogUtils.log("----------测试打印数据---方法调用（与onMethodEnter 可能存在重复打印） -->>>>>"
                            + "\n\nopcode（方法调用指令）:" + opcode
                            + "\n\nowner（方法归属类）:" + owner
                            + "\n\naccess（方法修饰符）:" + access
                            + "\n\nname（方法名）:" + name
                            + "\n\nisInterface（是否接口方法）:" + isInterface
                            + "\n\ndescriptor（方法描述（就是（参数列表）返回值类型拼接））:" + descriptor
                            + "\n\nsignature（方法泛型信息：）:" + signature
                            + "\n\nclassName（当前扫描的类名）:" + className);

                    //针对loadLibrary 的特别处理，因为我们想筛查load的是什么so，一般我们写法会loadLibrary（"xxxx"）,我们把所有的字符串类型常量都打印出来，协助我们筛查
                    if ("java/lang/System".equals(owner) && "loadLibrary".equals(name) && "(Ljava/lang/String;)V".equals(descriptor)) {
                        isInvokeLoadLibrary.set(true);
                    }
                }

                hookMethod(owner, name, descriptor, MethodCallRecordExtension.hookMethodInvokeMap);
                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            }

            /**
             * 匹配方法，进行插桩
             * @param owner 方法实现或归属类
             * @param name 方法名
             * @param descriptor 方法描述符
             * @param map 用户配置信息
             */
            private void hookMethod(String owner, String name, String descriptor, Map<String, List<String>> map) {
                if (map != null && map.size() > 0) {

                    String methodNameAndDesc = name + descriptor;
                    /**
                     * 下面三个是并集，如果填入重复（比如空值key填入的方法和下面两个有重复的情况），则会多次插入
                     */
                    //仅方法匹配，不关心方法归属类或者实现接口
                    hook(map, methodNameAndDesc, "");
                    //匹配方法实现类
                    hook(map, methodNameAndDesc, owner);
                    //匹配方法归属的接口
                    for (String anInterface : mInterfaces) {
                        if (hook(map, methodNameAndDesc, anInterface)) {
                            break;
                        }
                    }
                }
            }

            private boolean hook(Map<String, List<String>> map, String methodNameAndDesc, String key) {
                if (map.containsKey(key)) {
                    List<String> methodList = map.get(key);
                    if (methodList != null && methodList.contains(methodNameAndDesc)) {
                        //命中插桩
                        if (!isSdkPath() && methodNameAndDesc != null) {
                            //LogUtils.log("----------命中----->>>"+className + "_" + outName + "_call:" + recordMethodName);
                            //加载一个常量(当前所在类、调用处的方法、被调用的方法)
                            mv.visitLdcInsn(className + "." + outName + " call: " + methodNameAndDesc);
                            //调用我们自定义的方法 (注意用/,不是.; 方法描述记得；也要)
                            mv.visitMethodInsn(INVOKESTATIC, sdkClassPath, "recordMethodCall", "(Ljava/lang/String;)V", false);
                        }
                    }
                    return true;
                }
                return false;
            }


        };
        return mv;

    }

    private boolean isSdkPath() {
        return sdkClassPath.equals(className);
    }


}
package com.gamehelper.plugin;

import static org.objectweb.asm.Opcodes.ASM6;

import org.apache.http.util.TextUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
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
    private final String sdkClassPath = "com/gamehelper/method_call_record_lib/MethodRecordSDK";
    private String[] mInterfaces;//当前所扫描的类实现的接口
    int mLastLine = -1;

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
        if (isSdkPath()) {
            return mv;
        }
        final AtomicBoolean isInvokeLoadLibrary = new AtomicBoolean(false);
        List<String> mLdcList = new ArrayList<>();

        mv = new AdviceAdapter(ASM6, mv, access, outName, desc) {


            /**
             * 访问到 InvokeDynamic 指令
             * @param name 方法名    例如：onClick
             * @param desc 方法描述  例如：(Lcom/canzhang/asmdemo/MainActivity;)Landroid/view/View$OnClickListener;
             * @param bsm 引导方法（指的是在执行 invokedynamic 指令时，该指令所指向的、需要去执行的 Java 方法）
             * @param bsmArgs 引导方法的常量参数
             */
            @Override
            public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
                super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
//                LogUtils.log("----------visitInvokeDynamicInsn---->>>>>"
//                        //例：onClick
//                        + "\n\nname（方法名）:" + name
//                        //例：(Lcom/canzhang/asmdemo/MainActivity;)Landroid/view/View$OnClickListener;
//                        + "\n\ndesc（可以从 desc 中获取函数式接口，以及动态参数的内容）:" + desc
//                        //例：java/lang/invoke/LambdaMetafactory.metafactory(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; (6)
//                        + "\n\nHandle（引导方法：）:" + bsm);
//                if (bsmArgs != null) {
//                    //例：参数：0   (Landroid/view/View;)V  类型：Type
//                    //例：参数：1   com/canzhang/asmdemo/MainActivity.lambda$onCreate$0(Landroid/view/View;)V (7)  类型：Handle
//                    //例：参数：2   (Landroid/view/View;)V  类型：Type
//                    for (int i = 0; i < bsmArgs.length; i++) {
//                        Object arg = bsmArgs[i];
//                        LogUtils.log("参数：" +i+"   "+ arg + "  类型：" + arg.getClass().getSimpleName());
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
                if (MethodCallRecordExtension.fieldTest != null && MethodCallRecordExtension.fieldTest.contains(name)) {
                    LogUtils.log("\n\n\n\n----------测试打印数据---form 变量引用 -->>>>>"
                            + "\nopcode(要访问的类型指令的操作码):" + opcode
                            + "\nowner（变量归属类）:" + owner
                            + "\nname（变量名）:" + name
                            + "\ndesc（变量描述）:" + desc
                            + "\noutMethodName（引用处类名_方法名）:" + className + "_" + outName);
                }

                if (MethodCallRecordExtension.replaceFieldInvokeMap != null) {
                    String key = owner + "." + name + "." + desc;
                    if (MethodCallRecordExtension.replaceFieldInvokeMap.containsKey(key)) {
                        List<String> list = MethodCallRecordExtension.replaceFieldInvokeMap.get(key);
                        if (list != null && list.size() == 3) {
                            String classOwner = list.get(0);
                            String methodName = list.get(1);
                            String methodDesc = list.get(2);

                            //安全处理：避免同一个类替换，防止陷入死循环，另外也可以通过配置 MethodCallRecordExtension.ignorePath 来主动过滤（这里这么写主要是防止用户忘记配置ignorePath的情况）
                            if (className.equals(classOwner)) {
                                LogUtils.loge("\n\n\n\n-----提醒----返回安全处理：字段替换禁止同一个类的变量引用替换，避免陷入死循环-->>>>>"
                                        + "\n当前扫描类：" + className
                                        + "\n替换方法：" + classOwner + "." + methodName + methodDesc);
                            } else if (!TextUtils.isEmpty(classOwner) && !TextUtils.isEmpty(methodName) && !TextUtils.isEmpty(methodDesc)) {
                                LogUtils.log("\n\n\n\n----------开始替换变量引用为方法引用-->>>>>"
                                        + "\n当前命中:" + key
                                        + "\n替换成:" + classOwner + "." + methodName + methodDesc
                                        + "\noutMethodName（引用处类名_方法名）:" + className + "_" + outName);
                                //把变量访问修改为调用方法
                                visitMethodInsn(Opcodes.INVOKESTATIC, classOwner, methodName, methodDesc, false);
                                //返回，不再插入访问变量指令
                                return;
                            }
                        }
                    }
                }

                super.visitFieldInsn(opcode, owner, name, desc);

            }

            @Override
            public void visitCode() {
                super.visitCode();
            }


            @Override
            public void visitLineNumber(int line, Label start) {
                super.visitLineNumber(line, start);
                //记录最后的行号，用于插桩行号定位
                mLastLine = line;
            }

            @Override
            public void visitLabel(Label label) {
                super.visitLabel(label);
            }

            @Override
            protected void onMethodEnter() {
                super.onMethodEnter();
                //打印方法信息
                if (MethodCallRecordExtension.methodTest != null && MethodCallRecordExtension.methodTest.contains(outName)) {
                    LogUtils.log("\n\n\n\n----------测试打印数据---form 方法进入 -->>>>>"
                            + "\naccess（方法修饰符）:" + access
                            + "\noutName（方法名）:" + outName
                            + "\ndesc（方法描述（就是（参数列表）返回值类型拼接））:" + desc
                            + "\nsignature（方法泛型信息：）:" + signature
                            + "\nclassName（当前扫描的类名）:" + className);
                }
                hookMethod(className, outName, desc, MethodCallRecordExtension.hookMethodEnterMap);
            }


            @Override
            protected void onMethodExit(int opcode) {
                super.onMethodExit(opcode);
                if (isInvokeLoadLibrary.get() && mLdcList.size() > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    //用于判断加载了什么so
                    stringBuilder.append("------loadLibrary所在方法体加载的常量 开始--------\n");
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
                //打印方法调用信息
                printMethodInfo(opcode, owner, name, descriptor, isInterface);
                //方法调用处插桩（不影响原有运行逻辑）
                hookMethod(owner, name, descriptor, MethodCallRecordExtension.hookMethodInvokeMap);
                //替换方法调用（替换了原有方法调用，自行实现方法逻辑）
                if (replaceInvokeMethod(owner, name, descriptor)) {
                    return;
                }
                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);

            }

            /**
             * 打印方法调用信息
             */
            private void printMethodInfo(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                if (MethodCallRecordExtension.methodTest != null && MethodCallRecordExtension.methodTest.contains(name)) {
                    LogUtils.log("\n\n\n\n----------测试打印数据---方法调用（与onMethodEnter 可能存在重复打印） -->>>>>"
                            + "\nopcode（方法调用指令）:" + opcode
                            + "\nowner（方法归属类）:" + owner
                            + "\naccess（方法修饰符）:" + access
                            + "\nname（方法名）:" + name
                            + "\nisInterface（是否接口方法）:" + isInterface
                            + "\ndescriptor（方法描述（就是（参数列表）返回值类型拼接））:" + descriptor
                            + "\nsignature（方法泛型信息：）:" + signature
                            + "\nclassName（当前扫描的类名）:" + className);

                    //针对loadLibrary 的特别处理，因为我们想筛查load的是什么so，一般我们写法会loadLibrary（"xxxx"）,我们把所有的字符串类型常量都打印出来，协助我们筛查
                    if ("java/lang/System".equals(owner) && "loadLibrary".equals(name) && "(Ljava/lang/String;)V".equals(descriptor)) {
                        isInvokeLoadLibrary.set(true);
                    }
                }
            }

            /**
             * 替换方法调用
             * @return 是否命中替换
             */
            private boolean replaceInvokeMethod(String owner, String name, String descriptor) {
                String replaceInvokeMethodKey = owner + "." + name + descriptor;
                if (MethodCallRecordExtension.replaceMethodInvokeMap != null
                        && MethodCallRecordExtension.replaceMethodInvokeMap.containsKey(replaceInvokeMethodKey)) {
                    List<String> list = MethodCallRecordExtension.replaceMethodInvokeMap.get(replaceInvokeMethodKey);
                    if (list != null && list.size() == 3) {
                        String replaceClassOwner = list.get(0);
                        String replaceMethodName = list.get(1);
                        String replaceMethodDesc = list.get(2);


                        //安全处理：避免同一个类替换，防止陷入死循环，另外也可以通过配置 MethodCallRecordExtension.ignorePath 来主动过滤（这里这么写主要是防止用户忘记配置ignorePath的情况）
                        if (className.equals(replaceClassOwner)) {
                            LogUtils.loge("\n\n\n\n-----提醒----返回安全处理：方法替换禁止同一个类的方法替换，避免陷入死循环-->>>>>"
                                    + "\n当前扫描类：" + className
                                    + "\n替换方法：" + replaceClassOwner + "." + replaceMethodName + replaceMethodDesc);
                            return false;
                        }
                        if (!TextUtils.isEmpty(replaceClassOwner) && !TextUtils.isEmpty(replaceMethodName) && !TextUtils.isEmpty(replaceMethodDesc)) {
                            LogUtils.log("\n\n\n\n----------replaceInvokeMethod 开始替换方法调用-->>>>>"
                                    + "\n当前命中:" + replaceInvokeMethodKey
                                    + "\n替换成:" + replaceClassOwner + "." + replaceMethodName + replaceMethodDesc);
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, replaceClassOwner, replaceMethodName, replaceMethodDesc, false);
                            return true;
                        }

                    }
                }
                return false;
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
                        if (methodNameAndDesc != null) {
                            LogUtils.log("\n\n----------开始插桩,用于记录调用点----->>>"
                                    + "\n引用处类名_方法名：" + className + "_" + outName
                                    + "\n调用的方法归属类_方法:" + key +"_"+methodNameAndDesc);
                            //加载一个常量(当前所在类、调用处的方法、被调用的方法)
                            //这里插入和上一指令相同的行号，方便快速定位到代码（为了简单实现没有新增行号，避免逻辑过于复杂）
                            if (mLastLine != -1) {
                                try {
                                    //标签标示字节码的位置，用于指定紧随其后的指令
                                    Label label = new Label();
                                    mv.visitLabel(label);
                                    mv.visitLineNumber(mLastLine, label);
                                } catch (Exception e) {
                                    LogUtils.loge("发生异常：" + e.getMessage() + " mLastLine：" + mLastLine);
                                    e.printStackTrace();
                                }

                            }
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
        return sdkClassPath.equals(className) || isIgnore();
    }

    private boolean isIgnore() {
        for (String path : MethodCallRecordExtension.ignorePath) {
            if (className.startsWith(path)) {
                return true;
            }
        }
        return false;
    }


}
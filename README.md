# ASMDemo
ASM简单用法范例，主要用于学习和简单测试使用。

如果同一个版本发布，android studio 未更新，可以执行如下命令进行更新，避免频繁升级：
./gradlew clean --refresh-dependencies

## module 说明
* app:主module,用于写一些测试插桩效果的的测试代码


### 插桩实现敏感方法调用监控
* method_call_record_lib ：插桩代码库（就是插桩所要插入的代码，不集成的话插桩依然会成功，但是运行找不到类会崩溃）
* method_call_record_plugin：插桩插件


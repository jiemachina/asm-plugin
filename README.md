# 项目介绍
本项目主要是一些基于asm实现的字节码操作插件，目前有如下插件：
## 方法调用检测插件
### 功能
提供方法调用检测能力，能快速知道方法调用的位置，协助定位问题，或者提高工作效率。

### 应用场景举例

#### 敏感函数调用筛查
android 隐私权限相关的api或者字段要求越来越严格，我们需要配合监管部门处理相关的函数调用，这就需要我们找到：
* 在哪里调用的？
* 调用的方法是什么？
* 具体的调用堆栈是什么？
* 改完了自后，怎么辅助自校验是否改好了？（很多第三方平台耗时较久，不适合快速测试）

#### 快速找到点击的位置
随着项目推进，业务和页面都越来越复杂，在定位问题的时候，我们找一个点击位置往往会遇到如下问题：
* 页面层层嵌套
* 几十种item类型
* 子父类各种方法复写
* ...


针对上面的场景我们就可以使用方法调用监控来快速找到调用的位置。

### 涉及module
* app:主module,用于写一些测试插桩效果的的测试代码
* method_call_record_lib ：插桩代码库（就是插桩所要插入的代码，不集成的话插桩依然会成功，但是运行找不到类会崩溃）
* method_call_record_plugin：插桩插件

### 使用方式

* 1、添加仓库
```
maven {url 'https://mirrors.tencent.com/repository/maven/tencent_public/'}
```
* 2、在工程gradle中引入插件库
```
dependencies {
    classpath 'com.gamehelper.android:method_call_plugin:1.0.0-SNAPSHOT'
}
```
* 3、在主module中引入lib库
```
dependencies {
    implementation 'com.gamehelper.android:method_call_record_lib:1.0.0-SNAPSHOT'
}
```
* 4、在主module中注册插件，并添加配置信息
```
apply plugin: 'com.gamehelper.method_call_record_plugin'
methodCallRecordExtension {
    //日志打印测试，不知道方法描述怎么写可以在这里填写下方法名，build一下即可看到日志（模糊匹配）
    methodTest = ["nativeCompress"]
    //模糊匹配，只关注方法名 和入参、返回参数（模糊匹配不传入方法描述，则只匹配方法名）
    //模糊匹配，只关注方法名、入参、返回参数（可传入空集合[]，传入空集合的时候默认仅匹配方法名）
    fuzzyMethodMap = ["onClick": ["(Landroid/view/View;)V","(Landroid/content/DialogInterface;I)V","(Landroid/content/DialogInterface;IZ)V"],
                      "onMenuItemClick": ["(Landroid/view/MenuItem;)Z"],
                      "onCheckedChanged": ["(Landroid/widget/RadioGroup;I)V","(Landroid/widget/CompoundButton;Z)V"],
                      "onChildClick": ["(Landroid/widget/ExpandableListView;Landroid/view/View;IIJ)Z"],
                      "onItemSelected": ["(Landroid/widget/AdapterView;Landroid/view/View;IJ)V"],
                      "onListItemClick": ["(Landroid/widget/ListView;Landroid/view/View;IJ)V"],
                      "onStopTrackingTouch": ["(Landroid/widget/SeekBar;)V"],
                      "onRatingChanged": ["(Landroid/widget/RatingBar;FZ)V"],
                      "onTabChanged": ["(Ljava/lang/String;)V"],
                      "onNavigationItemSelected": ["(Landroid/view/MenuItem;)Z"],
                      "onTabSelected": ["(Landroid/support/design/widget/TabLayout\$Tab;)V","(Lcom/google/android/material/tabs/TabLayout\$Tab;)V"],
                      "onGroupClick": ["(Landroid/widget/ExpandableListView;Landroid/view/View;IJ)Z"],
                      "onItemClick": ["(Landroid/widget/AdapterView;Landroid/view/View;IJ)V"]
    ]
    //精准匹配，关注方法名、入参、返回参数、类名(仅适配非系统api调用的场景)
    accurateMethodMap = [
            "android/telephony/TelephonyManager": ["getLine1Number()Ljava/lang/String;",
                                                   "getDeviceId()Ljava/lang/String;",
                                                   "getSimSerialNumber()Ljava/lang/String;",
                                                   "getSubscriberId()Ljava/lang/String;"],
            "android/net/wifi/WifiInfo"         : ["getMacAddress()Ljava/lang/String;",
                                                   "getSSID()Ljava/lang/String;"],
            "java/net/NetworkInterface"         : ["getInetAddresses()Ljava/util/Enumeration;"],
            "java/net/InetAddress"              : ["getHostAddress()Ljava/lang/String;"],
            "android/provider/Settings\$System" : ["getString(Landroid/content/ContentResolver;Ljava/lang/String;)Ljava/lang/String;"],
            "android/provider/Settings\$Secure" : ["getString(Landroid/content/ContentResolver;Ljava/lang/String;)Ljava/lang/String;"]
    ]

}
```

上面的配置已经涵盖上方两种场景，`敏感函数`调用和`快速找到你点击的位置`,可根据自己的需求额外进行其他配置。
* 5、在主工程中的 `gradle.properties`配置开关
```
#注意不要有空格,如果想关闭插件，则设置为false即可，默认为关闭状态
isOpenMethodCallRecordPlugin=true
```
* 6、在 `Application`中可配置回调，自行打印堆栈
```
//示例
public class MyApplication extends Application {
    static {//注意这里是在静态代码块配置的，这样可以尽量提前加载，保证都能回调到。

        //测试敏感函数调用
        MethodRecordSDK.setRecordCallListener(new RecordCallListener() {
            @Override
            public void onRecordMethodCall(String s) {
                android.util.Log.e("MethodRecordSDK", "调用的方法是：" + s);
                android.util.Log.e("MethodRecordSDK", String.format("\n\n----------------------%s调用堆栈开始------------------------\n\n", "敏感函数"));
                StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

                for(int i = 0; i < stackTraceElements.length; ++i) {
                    android.util.Log.d("MethodRecordSDK", stackTraceElements[i].toString());
                }

                android.util.Log.e("MethodRecordSDK", String.format("\n\n----------------------%s调用堆栈结束------------------------\n\n", "敏感函数"));
            }

        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
```

然后就可以buuild 进行使用了



## 附录
### 测试阶段同一个版本的aar发布后，androidStudio未更新问题
如果同一个版本发布(SNAPSHOT后缀的)，android studio 未更新，可以执行如下命令进行更新，避免频繁升级：
./gradlew clean --refresh-dependencies



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
    /**
     * 日志打印测试，不知道方法描述怎么写可以在这里填写下方法名，build一下即可看到日志（模糊匹配）
     * 额外说明：特殊处理：loadLibrary 会额外打印一些所在方法体的调用常量，便于定位加载的什么so 
     */
    methodTest = ["loadLibrary"]

    /**
     * 方法体插桩（对于一些接口实现，比如常见的点击事件，其调用处是系统api，这导致我们同样无法插桩，这时候就需要我们在方法体，也就是接口实现处进行插桩监控，所用asm api :onMethodEnter）
     * key：所调用方法的归属类,或者是归属类所实现的接口（比如常见的接口，其实方法归属类都是内部类，名字就比较多了，但是都会实现统一的接口，这种场景我们就填入接口），可填写空值表示仅匹配方法名和方法描述符。
     * value:所调用方法的方法名+描述符（描述符指的是方法的入参和返回值描述，不会写的话可以使用上方的methodTest 打印出来）
     */
    hookMethodEnterMap = ["android/view/View\$OnClickListener"                                                                 : ["onClick(Landroid/view/View;)V"],
                          "android/content/DialogInterface\$OnClickListener"                                                   : ["onClick(Landroid/content/DialogInterface;I)V"],
                          "android/content/DialogInterface\$OnMultiChoiceClickListener"                                        : ["onClick(Landroid/content/DialogInterface;IZ)V"],
                          "android/widget/CompoundButton\$OnCheckedChangeListener"                                             : ["onCheckedChanged(Landroid/widget/CompoundButton;Z)V"],
                          "android/widget/RadioGroup\$OnCheckedChangeListener"                                                 : ["onCheckedChanged(Landroid/widget/RadioGroup;I)V"],
                          "android/widget/RatingBar\$OnRatingBarChangeListener"                                                : ["onRatingChanged(Landroid/widget/RatingBar;FZ)V"],
                          "android/widget/SeekBar\$OnSeekBarChangeListener"                                                    : ["onStopTrackingTouch(Landroid/widget/SeekBar;)V"],
                          "android/widget/AdapterView\$OnItemSelectedListener"                                                 : ["onItemSelected(Landroid/widget/AdapterView;Landroid/view/View;IJ)V"],
                          "android/widget/TabHost\$OnTabChangeListener"                                                        : ["onTabChanged(Ljava/lang/String;)V"],
                          "android/widget/AdapterView\$OnItemClickListener"                                                    : ["onItemClick(Landroid/widget/AdapterView;Landroid/view/View;IJ)V"],
                          "android/widget/ExpandableListView\$OnGroupClickListener"                                            : ["onGroupClick(Landroid/widget/ExpandableListView;Landroid/view/View;IJ)Z"],
                          "android/widget/ExpandableListView\$OnChildClickListener"                                            : ["onChildClick(Landroid/widget/ExpandableListView;Landroid/view/View;IIJ)Z"],
                          "android/widget/Toolbar\$OnMenuItemClickListener"                                                    : ["onMenuItemClick(Landroid/view/MenuItem;)Z"],
                          "android/support/v7/widget/Toolbar\$OnMenuItemClickListener"                                         : ["onMenuItemClick(Landroid/view/MenuItem;)Z"],
                          "androidx/appcompat/widget/Toolbar\$OnMenuItemClickListener"                                         : ["onMenuItemClick(Landroid/view/MenuItem;)Z"],
                          "android/widget/PopupMenu\$OnMenuItemClickListener"                                                  : ["onMenuItemClick(Landroid/view/MenuItem;)Z"],
                          "androidx/appcompat/widget/PopupMenu\$OnMenuItemClickListener"                                       : ["onMenuItemClick(Landroid/view/MenuItem;)Z"],
                          "android/support/v7/widget/PopupMenu\$OnMenuItemClickListener"                                       : ["onMenuItemClick(Landroid/view/MenuItem;)Z"],
                          "com/google/android/material/navigation/NavigationView\$OnNavigationItemSelectedListener"            : ["onNavigationItemSelected(Landroid/view/MenuItem;)Z"],
                          "android/support/design/widget/NavigationView\$OnNavigationItemSelectedListener"                     : ["onNavigationItemSelected(Landroid/view/MenuItem;)Z"],
                          "android/support/design/widget/BottomNavigationView\$OnNavigationItemSelectedListene"                : ["onNavigationItemSelected(Landroid/view/MenuItem;)Z"],
                          "com/google/android/material/bottomnavigation/BottomNavigationView\$OnNavigationItemSelectedListener": ["onNavigationItemSelected(Landroid/view/MenuItem;)Z"],
                          "android/support/design/widget/TabLayout\$OnTabSelectedListener"                                     : ["onTabSelected(Landroid/support/design/widget/TabLayout\$Tab;)V"],
                          "com/google/android/material/tabs/TabLayout\$OnTabSelectedListener"                                  : ["onTabSelected(Lcom/google/android/material/tabs/TabLayout\$Tab;)V"],
    ]
    /**
     * 方法调用插桩：精准匹配（用于监控方法调用情况，因为很多api是系统api，我们无法插桩到系统api的方法体里面，所以这里筛查的是方法调用指令，所用 asm api visitMethodInsn）
     * key：所调用方法的归属类
     * value:所调用方法的方法名+描述符（描述符指的是方法的入参和返回值描述）
     *
     * 下面配置的是一些常见敏感api，用于监控敏感api的调用情况。
     */
    hookMethodInvokeMap = [
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

* 7、支持lambda表达式hook
java 8支持了 lambda表达式，这涉及了一个脱糖流程，如果不关闭D8脱糖的话，我们拿到的.class 是未脱糖的， 这样按照现有逻辑，
  就无法hook到，因为本插件仅测试场景应用，所以这里我们可以配置临时关闭D8脱糖,使用原来的`desugar`进行处理即可hook到。

可以通过在 gradle.properties 里配置
```
android.enableD8.desugaring=false
```
当然也是可以在通过某些手段直接支持脱糖hook的，具体可参考以下文章：
**lambda 脱糖流程参考**
https://opensource.sensorsdata.cn/opensource/lambda-%e8%ae%be%e8%ae%a1%e5%8f%82%e8%80%83/
https://opensource.sensorsdata.cn/opensource/asm-%e5%ae%9e%e7%8e%b0-hook-lambda-%e5%92%8c%e6%96%b9%e6%b3%95%e5%bc%95%e7%94%a8-%e6%95%b0%e6%8d%ae%e9%87%87%e9%9b%86/



## 升级日志
### 1.0.0-SNAPSHOT (2021-08-18)
#### Features
插件首次发布，支持以下能力：
* 方法调用检测（模糊匹配、精准匹配、打印静态筛查日志）

### 1.0.1-SNAPSHOT (2021-09-10)
#### Features
* 内部类调用方式支持严格匹配，可以匹配方法归属的接口

## 附录
### 测试阶段同一个版本的aar发布后，androidStudio未更新问题
如果同一个版本发布(SNAPSHOT后缀的)，android studio 未更新，可以执行如下命令进行更新，避免频繁升级：
./gradlew clean --refresh-dependencies

### 参考文章





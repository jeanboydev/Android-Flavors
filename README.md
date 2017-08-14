# Android-Flavors
## 概述
该项目演示了在 Android Studio 中使用 gradle 构建渠道包。

## 渠道号

以友盟 SDK 为例，打包多渠道：GooglePlay，小米，友盟，360，豌豆荚，应用宝。 在 AndroidManifest.xml 中加入渠道区分标识。

````XML
<meta-data 
    android:name="UMENG_CHANNEL"
    android:value="${UMENG_CHANNEL_VALUE}" />
```

然后在 build.gradle(Module: app) 中加入渠道打包替换对应的 UMENG_CHANNEL_VALUE 代码。

```Java
// 渠道Flavors，配置不同的渠道
productFlavors {
    GooglePlay {}
    xiaomi {}
    umeng {}
    qihu360 {}
    wandoujia {}
    yingyongbao {}
    //其他...
}

// 批量配置渠道
productFlavors.all {
    flavor -> flavor.manifestPlaceholders = [UMENG_CHANNEL_VALUE: name]
}
```

## 自定义 apk 名字

我们可以指定不同渠道号生成的 apk 的名字，这样方便打包出来区别哪个 apk 是对应哪个渠道的。
如下命名格式为：** 渠道名-v版本号-打包时间.apk **

```Java
//打包重命名
applicationVariants.all { variant ->
    if (variant.buildType.name == "release") {
        variant.outputs.each { output ->
            def filePath = output.outputFile.parent + "/${variant.buildType.name}"
            def fileName = output.outputFile.name
            if (fileName.endsWith(".apk")) {
                def apkName = "${variant.productFlavors[0].name}-v${variant.versionName}-${releaseTime()}.apk";
                output.outputFile = new File(filePath, apkName)
            }
        }
    }
}
```

## 渠道自定义

不同的渠道定义不同的 applicationId, versionCode, versionName

```Java
productFlavors {
    main_test {
        applicationId "com.jeanboy.app.flavors"
        versionCode rootProject.ext.mainTestVersionCode
        versionName rootProject.ext.mainTestVersionName
        //定义manifest中替换值，如：渠道号
        resValue("string", "test_app_id", "2017-8-14 12:09:35")
        //定义混淆文件
        proguardFiles getDefaultProguardFile('proguard-android.txt'), './src/main_test/proguard-rules.pro'
    }
    main_test1 {
        applicationId "com.jeanboy.app.flavorstest1"
        versionCode rootProject.ext.mainTest1VersionCode
        versionName rootProject.ext.mainTest1VersionName
        resValue("string", "test_app_id", "2017-8-14 12:09:35")
        proguardFiles getDefaultProguardFile('proguard-android.txt'), './src/main_test1/proguard-rules.pro'
    }
    main_test2 {
        applicationId "com.jeanboy.app.flavorstest2"
        versionCode rootProject.ext.mainTest2VersionCode
        versionName rootProject.ext.mainTest2VersionName
        resValue("string", "test_app_id", "2017-8-14 12:09:35")
        proguardFiles getDefaultProguardFile('proguard-android.txt'), './src/main_test2/proguard-rules.pro'
    }
}
```

## 不同渠道不同签名文件

定义渠道包签名文件

```Java
signingConfigs {
    test {
        storeFile file('../resources/test.jks')//密钥文件位置
        storePassword 'test123'//密钥密码
        keyAlias 'test'//密钥别名
        keyPassword 'test123'//别名密码
    }
    test1 {
        storeFile file('../resources/test1.jks')
        storePassword 'test123'
        keyAlias 'test'
        keyPassword 'test123'
    }
    test2 {
        storeFile file('../resources/test2.jks')
        storePassword 'test123'
        keyAlias 'test'
        keyPassword 'test123'
    }
}
```

指定不同渠道使用的签名文件

```Java
buildTypes {
    debug {
        minifyEnabled false
        shrinkResources false
        zipAlignEnabled true
        versionNameSuffix "-debug"//版本命名后缀
        buildConfigField "boolean", "LOG_DEBUG", "true"
        //定义debug时使用的签名文件
        signingConfig signingConfigs.test
        signingConfig signingConfigs.test1
        signingConfig signingConfigs.test2
    }

    release {
        minifyEnabled true//是否开启代码混淆
        shrinkResources true//移除无用的资源文件，依赖于minifyEnabled必须一起用
        multiDexEnabled true//解决65535
        zipAlignEnabled true//对齐zip
        debuggable false // 是否debug
        buildConfigField "boolean", "LOG_DEBUG", "false"
        signingConfig signingConfigs.test
        signingConfig signingConfigs.test1
        signingConfig signingConfigs.test2
    }
}
```

## 不同渠道不同资源文件

例如：不同渠道需要不同的应用名

```Xml
app
└──src
	├──main
	│	└──res
	│		└──values
	│			└──strings.xml
	│				└──<string name="app_name">Android-Flavors</string>
	├──main_test
	│	└──res
	│		└──values
	│			└──strings.xml
	│				└──<string name="app_name">Android-Flavors-test</string>
	├──main_test1
	│	└──res
	│		└──values
	│			└──strings.xml
	│				└──<string name="app_name">Android-Flavors-test1</string>
	├──main_test2
	│	└──res
	│		└──values
	│			└──strings.xml
	│				└──<string name="app_name">Android-Flavors-test2</string>
```

在 src 下创建与 main 同级的渠道目录，里面可创建与 main 目录下对应的目录或文件，打包时会以增量或覆盖的方式替换。
res 目录下的文件可以同名覆盖，java 或其他代码目录中类名不允许重复。

编译某个渠道包的时候遵循以下4条准则：
- 所有的源码(src/*/java)会用来共同编译生成一个 Apk，不允许覆盖，会提示 duplicate class found
- 所有的 Manifests 都将会合并，这样一来就允许渠道包中可以定义不同的组件与权限，具体可参考官方 Manifest Merger
- 渠道中的资源会以覆盖或增量的形式与 main 合并，优先级为 Build Type > Product Flavor > Main sourceSet
- 每个 Build Variant 都会生成自己的R文件


## 第三方 SDK

例如：test1 渠道中需要使用某个 SDK，而其他渠道不需要使用。
```Java
android {
    productFlavors {
        test1 {
        }
    }
}
...
dependencies {
    provided 'com.xxx.sdk:xxx:1.0'//提供 sdk
    test1Compile 'com.xxx.sdk:xxx:1.0'//指定 test1 渠道编译
}
```

接下来，需要在代码中使用反射技术判断应用程序是否添加了该SDK，从而决定是否要使用 SDK。部分代码如下：

```Java
class MyActivity extends Activity {
    private boolean useSdk;

    @override
    public void onCreate(Bundle savedInstanceState) {
        try {
            Class.forName("com.xxx.sdk.XXX");
            useSdk = true;
        } catch (ClassNotFoundException ignored) {

        }
    }
}
```

## 参考资料

[美团Android自动化之旅—适配渠道包](https://tech.meituan.com/mt-apk-adaptation.html)

[Gradle App项目的多渠道打包实现](http://blog.csdn.net/codezjx/article/details/49516151)

[多渠道打包](http://saiwu-bigkoo.github.io/2015/10/16/android/)

## License

    Copyright 2017 jeanboy

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
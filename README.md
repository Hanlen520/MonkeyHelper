# MonkeyHelper
A tool to assist monkey testing
## 引言

通常，在Android中，压力测试是指用Monkey + LeakCanary工具来发现应用的异常以及内存泄漏。

原始的Monkey存在的缺点有：1、不能限定Activity页面；2、不能避免WIFI意外关闭；

原始的LeakCanary存在的缺点有：在检测到内存泄漏后自动跳转至Leak Trace页；

二者均不可避免对测试过程造成不良影响，如：打开非相关页面及WIFI断开。而MonkeyHelper即是为解决以上问题而开发的一个工具。

## 原理

由于Android系统中每个进程都存在于各自独立的虚拟机中，进程之间无法直接通信，因此若想从一个应用外部对应用的运行过程做更改，理论上说是无法实现的。对应到一个实际应用，在不修改自身源代码时，无法做到禁用特定Activity。想要实现更改应用运行流程的目的，必须从Android应用程序框架层入手，改变Framework层进而影响程序运行流程。当前，可以从改变系统源码或Hook的方式来实现该目的，修改系统源码所受限制太大因而不现实，显然Hook是合理的，所以本应用基于Xposed框架来Hook。

## 使用条件

1. 系统必须Root，因为需要安装Xposed框架；
2. 必须安装并激活Xposed框架，Android4.4.4及以前的用户请安装Xposed Installer 2.7版本（ [robv.android.xposed.installer\_v33\_36570c.apk](http://dl-xda.xposed.info/modules/de.robv.android.xposed.installer_v33_36570c.apk)），Android5.0及以后的用户请安装Xposed Installer 3.1版本（ [XposedInstaller\_3.1.4.apk](https://forum.xda-developers.com/attachment.php?s=faa52d15f37d3b27980556c4625b4b69&amp;attachmentid=4319220&amp;d=1509453299)）；
3. 必须在Xposed Installer中激活MonkeyHelper，见下图；

<img src="https://github.com/waitshang/MonkeyHelper/blob/master/res/1.png" width="480" height="800" alt="图片加载失败时，显示这段字"/>

## 功能及使用方法

1. **使用悬浮按钮添加黑名单Activity**

打开悬浮按钮；

<img src="https://github.com/waitshang/MonkeyHelper/blob/master/res/2.png" width="480" height="800" alt="图片加载失败时，显示这段字"/> <img src="https://github.com/waitshang/MonkeyHelper/blob/master/res/3.png" width="480" height="800" alt="图片加载失败时，显示这段字"/>

在需要屏蔽的页面点击悬浮按钮；

<img src="https://github.com/waitshang/MonkeyHelper/blob/master/res/4.png" width="480" height="800" alt="图片加载失败时，显示这段字"/>

1. **编辑Activity黑名单**

在首页点击&quot;Activity 黑名单&quot;，即进入&quot;黑名单&quot;页，展示所有黑名单Activity，刚刚添加的Activity也在此，见图中红框处；

<img src="https://github.com/waitshang/MonkeyHelper/blob/master/res/5.png" width="480" height="800" alt="图片加载失败时，显示这段字"/>

点击&quot;编辑&quot;（画笔）即可编辑黑名单，目前使用文本的方式编辑，Activity名称之间用半角逗号分隔，左右用半角方括号包裹，点击确定可以将修改后的名单保存起来；

<img src="https://github.com/waitshang/MonkeyHelper/blob/master/res/6.png" width="480" height="800" alt="图片加载失败时，显示这段字"/>

添加至黑名单中的Activity如需要生效，就必须将Activity对应的应用重启，因为所做的修改仍在应用外部，重启应用将重新初始化一份虚拟机，新的虚拟机环境即是修改后的环境。

1. **通知栏下拉禁用/启用**

1. 不重启手机的方式

点击&quot;通知栏已允许下拉&quot;即可禁用下拉，点击&quot;通知栏已禁止下拉&quot;即可允许下拉。

1. 重启手机的方式、

点击&quot;Root方式启用/禁用通知栏&quot;即可启用或禁用通知栏（视通知栏状态而定），此种方式将彻底禁用通知栏、APP切换界面、音量调节界面，推荐使用此方式。

1. **禁止自动进入Leak Trace页**

该功能是为了防止发生内存泄漏时自动进入Leak Trace页，导致后续操作浪费在该页。同Activity黑名单功能，需要重启集成了LeakCanary的应用。

如果想查看Leak Trace，点击&quot;Leak Trace已禁止展示&quot;按钮，并重启集成了LeakCanary的应用，再点击Leaks图标即可。

<img src="https://github.com/waitshang/MonkeyHelper/blob/master/res/7.png" width="480" height="800" alt="图片加载失败时，显示这段字"/>

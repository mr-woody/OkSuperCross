
## 需求：如何实现跨进程的通信
### 寻找解决方案


#### 在Android中开启多进程只有一种方法:

* 那就是给4大组件(Activity,Service,Receiver,ContentProvider)在Menifest中指定android:process属性,除此之外没有其他的方法(通过JNI在native层去fork一个新进程除外)

* 实例1如下:

```
<activity
    android:name=".activity.Main2Activity"
    android:process=":remote1" />
实例2如下:

<activity
    android:name=".activity.Main3Activity"
    android:process="com.woodys.demo.remote2" />

```
* 说明:
1. 首先实例1中 ":“的含义是指:这是一种简写
+ 当前的进程名称前面要附加上当前的包名 进程完整名称:com.hlj.demo:remote1
+ 以”:"开头的进程属于当前应用的私有进程,其他应用的组件不可以和它跑在同一个进程中
2. 实例2中的进程名称是一种完整的命名方式,不会附加包名信息,其次它属于全局进程,其他应用通过ShareUID方式可以和它跑在同一个进程中.(2个应用的签名也要相同)

* 注意:

+ 程序入口MainActivity,默认没有给它process属性,那么它就运行在默认的进程中,默认的进程名称是包名,如果给它指定了process属性,那么它就运行在该指定进程当中

#### 在android跨进程通信的方式，通常有如下办法：

1. Activity方式

*  Activity是四大组件中使用最频繁的，咱们先从它说起。使用Activity方式实现，就是使用startActivity()来启动另外一个进程的Activity。

    > 我们知道，在调用startActivity(Intent intent)的时候，intent有两个类型：显式Intent和隐式Intent。

       1）显式Intent的使用方式如下，用于进程内组件间通信：

        ```
        1 Intent intent = new Intent(this,OtherActivity.class);
        2 startActivity(intent);
        ```
   
       这种方式显式地指定了要跳转的Activtiy的class名称，这种方式用于进程内Activity的跳转，是跨模块间通信，而不是跨进程间通信。

       2）隐式intent的使用方式如下，用于IPC：

        ```
        1 Intent intent = new Intent();
        2 intent.setAction(Intent.ACTION_CALL);
        3 startActivity(intent);//startActivityForResult()同样，这里不赘述

        ```

   * Intent.ACTION_CALL就是字符串常量“android.intent.action.CALL”，这种方式通过setAction的方式来启动目标app的Activity，上述代码就是启动电话app的拨号界面，有时候还可以带上电话号码等参数。

   * 由上可知，Activity实现跨进程通信的方式，适合于不同App之间功能界面的跳转,如果跨进程需要界面上的交互操作，用隐式startActivity()方式实现。


2. Broadcase方式

    >  Broadcast使用非常简单，注册好广播，添加上action，就可以等着接收其他进程发出的广播。发送和接收广播时，还可以借助Intent来携带数据。但是广播的使用存在很多问题，下面盘点一下Broadcast的槽点：

    （1）Broadcast是一种单向的通信方式。当一个程序发送广播后，其他应用只能被动地接收，无法向发送者反馈。

    （2）Broadcast非常消耗系统资源，会导致系统性能下降。

    （3）速度慢，容易造成系统ANR。且除了Parall Broadcast外，无法保证接收到的时间，甚至不一定能收得到。

    （4）如果使用Ordered Broadcast，一个Receiver执行时间过长，会影响后面接收者的接收时间，甚至还有可能被中间某个Receiver拦截，导致后面Receiver无法接收到。

    （5）发送者无法确定谁会接收该广播，而接收者也无发确认是谁发来的广播。

    （6）如果是静态注册的广播，一个没有开启的进程，都有可能被该广播激活。

     ......

   * 总而言之，言而总之，使用Broadcast来实现跨进程通信，是下下之策！

3. ContentProvider方式

    + 系统四大组件之一，底层也是Binder实现，主要用来为其他APP提供数据，可以说天生就是为进程通信而生的。
    + 自己实现一个ContentProvider需要实现6个方法，其中onCreate是主线程中回调的，其他方法是运行在Binder之中的。
    + 自定义的ContentProvider注册时要提供authorities属性，应用需要访问的时候将属性包装成Uri.parse("content://authorities")。
    + 还可以设置permission，readPermission，writePermission来设置权限。
    + ContentProvider有query，delete，insert等方法，看起来貌似是一个数据库管理类，但其实可以用文件，内存数据等等一切来充当数据源，query返回的是一个Cursor，可以自定义继承AbstractCursor的类来实现。



4. Service方式

    > 启动Service的方式有多种，有的用于跨进程通信，有的用于进程内部模块之间的通信，下面仅简单介绍一下跨进程通信的方式。

    （1）startService()方式

     ```
       Intent startIntent = new Intent ();
       ComponentName componentName = new ComponentName(string packageName，string serviceClassName);
       startIntent.setComponent(componentName );
       startService( startIntent) ;

     ```

      该方式启动远程Service实现跨进程通信，耦合度比较低，功能及代码结构清晰，但是存在以下缺点：

      1）没有好的机制立即返回执行结果，往往Service完成任务后，还需要其他方式向Client端反馈。

      2）Service端无法识别Client端是谁，只知道有启动命令，但无法知道是谁下的命令。

      3）在已经创建好Service情况下，每次调用startService，就会执行onStartCommand()生命周期方法，相比于bindService，效率低下。

      4）如果Client端忘记调用stopService()了，那么该Service会一直运行下去，这也是一个隐患。


5. 文件共享：

   对同一个文件先后写读，从而实现传输，Linux机制下，可以对文件并发写，所以要注意同步。顺便一提，Windows下不支持并发读或写。


6. Socket：

  Android不允许在主线程中请求网络，而且请求网络必须要注意声明相应的permission。然后，在服务器中定义ServerSocket来监听端口，客户端使用Socket来请求端口，连通后就可以进行通信。



7. Messenger：

Messenger是基于AIDL实现的，服务端（被动方）提供一个Service来处理客户端（主动方）连接，维护一个Handler来创建Messenger，在onBind时返回Messenger的binder。

双方用Messenger来发送数据，用Handler来处理数据。Messenger处理数据依靠Handler，所以是串行的，也就是说，Handler接到多个message时，就要排队依次处理。

其中对于AIDL的简单介绍：

AIDL：

AIDL通过定义服务端暴露的接口，以提供给客户端来调用，AIDL使服务器可以并行处理，而Messenger封装了AIDL之后只能串行运行，所以Messenger一般用作消息传递。

通过编写aidl文件来设计想要暴露的接口，编译后会自动生成响应的java文件，服务器将接口的具体实现写在Stub中，用iBinder对象传递给客户端，客户端bindService的时候，用asInterface的形式将iBinder还原成接口，再调用其中的方法。




### 时间：2019/6/20(周日)

1. 之前采用的bindService的方式解决跨进程通信问题，但是发现每个进程都需要bindService处理，导致库的实现很笨重，主要的问题是当bindService太多以后出现了异常：Android Watchdog Timeout

2. 变换思路：ContentProvider以在不同的应用程序之间共享数据，ContentProvider底层实现是Binder,它为存储和获取数据提供统一的接口

而在ContentProvider跨进程通信的方式有两种：

1. 利用ContentProviderClient的call方法实现跨进程调用，这里有个问题就是ContentProviderClient版本兼容问题挺郁闷，所以放弃
2. 借助ContentResolver的query()方法，将binder放在Cursor中
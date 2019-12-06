# OkSuperCross for Android 跨进程通信交叉组件

## 跨进程组件库

1. 跨进程事件通信组件,用于解决安卓（模块化/组件化）下各（模块／组件）间交叉通讯的问题
2. 支持（同模块／同进程）跨模块／跨进程注册业务方法、调用业务实现方法和注销业务实现方法的操作
3. 支持（同模块／同进程）跨模块／跨进程实现"事件总线"通信方式，提供事件注册，事件解除和事件发送，使用方式类似：Otto 和 BusEvent
4. 支持远程服务的Callback回调处理

## 特色

1. 无需开发者进行bindService()操作,也不用定义Service,不需要定义任何aidl接口即可实现IPC通信。
2. 同步获取远程服务,抛弃了bindService()这种异步获取的方式，改造成了同步获取。
3. 采用"接口+数据结构"的方式来实现组件间通信，这种方式相比协议的方式在于实现简单，维护方便。
4. 可在任意进程通过接口类型获取服务调用。

## 缺点

* 暂时不支持跨app的场景使用，后续会完善这块功能。

### 使用库注意事项
* [Todo](http://git.okjiaoyu.cn/stu/oksupercross/blob/master/document/TODO.md)


### 示例介绍

1. 服务获取

* 注册跨组件服务
* 调用跨组件服务
* 注销远程服务
* 跳转到其他进程使用远程服务

2. 事件总线

* 订阅事件A
* 退订事件A
* 发布事件A
* 去其他进程发布事件A


### 演示下载
[*Sample Apk*](http://git.okjiaoyu.cn/stu/oksupercross/raw/master/apk/app-debug.apk)


### 如何接入


##### 1. 接入gradle插件库，在buildscript中添加classpath

```
buildscript {
    repositories {
        ......
        //本地maven地址，
        //maven { url 'file:///Users/woodys/.m2/repository' }
        //服务器的maven地址
        maven{ url "http://10.60.0.100:8081/repository/okayclient_snapshot/"}
    }
    dependencies {
        ......
        classpath 'com.okay.supercross.plugin:supercross-plugin:1.0.3-SNAPSHOT'
    }
}
```


##### 2. 在全局build里添加仓库

```
allprojects {
    repositories {
        ......
        maven{ url "http://10.60.0.100:8081/repository/okayclient_snapshot/"}
    }
}
```

##### 3. Application或library Module中添加跨进程通信库，在app的build里添加依赖

```

dependencies {
    ......
    implementation "com.okay.supercross:supercross-api:1.0.4-SNAPSHOT"
}

```
##### 4. 在application Module中使用gradle插件

```
......
apply plugin: 'com.okay.supercross.plugin'

```

##### 5. 为Dispatcher配置进程,在app的build.gradle文件中配置 `Dispatcher` 所在的进程名称，填`""`空为主进程，我们所有远程服务均是通过Dispatcher获取

由于Dispatcher负责管理所有进程信息，所以它应该运行在存活时间最长的进程中,其中process可以指定为存活最长的那个进程

```
dispatcher{
    process ":main"
}

```


###  如何使用

##### 1. 在Application的attachBaseContext方法中初始化
```
@Override
protected void onCreate() {
  ......
  SuperCross.setEnableLog(BuildConfig.DEBUG);
  SuperCross.init(this);
}

```
##### 2. 定义对外提供服务功能的接口和实现

如果注册本地服务，参数以及回调接口没有限制；
如果注册远程服务，参数类型必须为基本数据类型或者可序列化类型(serializable/parcelable),并且异步回调接口需要使用提供的`IPCCallback`。

```

public interface RemoteService {
    /**
     * 登录（有返回值）
     *
     * @param username          姓名
     * @param password          密码
     * return @String        回调返回token
     */
    String login(String username, String password);

    /**
     * 登录（有异步回调）
     *
     * @param username          姓名
     * @param password          密码
     * @param callBack   远程服务调用，异步回调接口需要使用提供的ServiceCallback
     */
    void login2(String username, String password,ServiceCallback callBack);
}
  
```

##### 3. 注册或者反注册服务，可在任意进程调用；注册的进程本地服务需要在本进程取消注册

```
//本进程注册，所有进程都使用
//远程服务
SuperCross.registerRemoteService(RemoteService.class, new RemoteServiceImpl());
//取消注册远程服务
SuperCross.unregisterRemoteService(RemoteService.class);

//本进程注册，仅在本进程使用：

//注册本地服务，此时没有跨进程使用，无需考虑数据类型限制
SuperCross.registerLocalService(RemoteService.class, new RemoteServiceImpl());

//取消注册本地服务
SuperCross.unregisterLocalService(RemoteService.class);
  
```

##### 4. 可在任意进程通过接口类型获取服务调用

```
//获取远程服务
RemoteService service = SuperCross.getRemoteService(RemoteService.class);

//获取本地服务
//RemoteService service = SuperCross.getLocalService(RemoteService.class);

//服务未注册时，service为null
if(null!=service){

//同步获取返回值
String token = remoteService.login("岳涛","不知道密码");


//异步回调. 推荐使用提供的 SimpleServiceCallback 已将回调结果切换至主线程

service.login2("plug2.activitys.SecondActivity 测试", "这个是密码", new SimpleServiceCallback() {
    @Override
    public void onSucceed(Bundle result) {
        //Main thread 回调结果在主线程
    }

    @Override
    public void onFailed(String reason) {
        //Main thread
    }
});


 //或者
 
 service.login2("plug2.activitys.SecondActivity 测试", "这个是密码", ServiceCallback.Stub() {
     @Override
     public void onSucceed(Bundle result) {
         // binder thread
     }
 
     @Override
     public void onFailed(String reason) {
         // binder thread
     }
 });
 
}

```
##### 5. 事件总线的订阅和发布

```
//订阅事件，可在任意进程的多个位置订阅

//推荐使用提供的 SimpleEventCallback 已将回调结果切换至主线程
SuperCross.subscribe("key", new SimpleEventCallback() {
    @Override
    public void onEvent(Bundle eventBundle) {
        //main thread
    }
});

或者

SuperCross().subscribe("key", new EventCallback() {
    @Override
    public void onEventCallBack(Bundle event) {
        // binder thread
    }
});


//发布事件

Bundle bundle = new Bundle();
bundle.putString("content", "事件A");
bundle.putString("process", ProcessUtils.getProcessName(MainActivity.this));
SuperCross.publish(new Event("key",bundle));

//取消订阅
SuperCross.unsubscribe("key");

或者

SuperCross.unsubscribe(EventCallback listener);

```


### 其他文档
* [ChangeLog](http://git.okjiaoyu.cn/stu/oksupercross/blob/master/document/CHANGE_LOG.md)


![](http://git.okjiaoyu.cn/stu/oksupercross/raw/master/image/author.png)

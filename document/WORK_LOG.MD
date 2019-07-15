## Wroklog

### 时间:2019/6/18(周二 下午)

> 发现的问题

1. 周一到周二整理的项目OkSuperCross，git地址：http://git.okjiaoyu.cn/stu/oksupercross.git  这个库存在一个缺陷就是跨应用无非通信
2. 参杂了页面路由相关的大量操作，整理修改代价太大

> 解决方案及思路调整

1. 综合上诉问题，我思考再三，决定重新开发一个新的组件库，结合最近技术分析和方案了解，整合成自己的思路，那么重启一个新的库SuperProcessRoute，git地址：http://git.okjiaoyu.cn/stu/supersupercross.git

> 工作完成情况

1. 定义好注解类，通用注解存放在：supercross-annotation
2. 完成动态注解器AbstractProcessor，开发工作，主要代码查看：supercross-compiler

    * ApplicationProcessor 使用注解器的方式，统一管理子module或者子plug的初始化入口工作，实现各组件的Application共享，在Plug1模块中新建Plug1Application 并实现ApplicationDelegate协议，并添加RegistApplication注解，框架将自动将注册共享Application
    * BindMethodProcessor 通过@ProcessId、RemoteServiceImpl动态生成搭建组件通讯桥梁，通过接口约束远程方法、请求参数、回调参数为双方共同遵守的协议


### 时间：2019/6/19(周三)

1. 实现RemoteService的远程方法AOP编程，例如：在Plug1模块的RemoteServiceOfPlug1远程协议实现类中，在需要拦截的远程方法中加入@Processor({LoginProcessor.class})即可实现拦截，AOP处理器支持多个，以","号隔开即可。如多个处理器中有1个返回false，则被拦截
2. 添加错误日志的输出方式，可通过控制台看到错误信息
3. 新建 supercross-api Library,内部核心实现：通过Service的bindService来实现跨进程桥梁搭建，使用AIDL方式进行跨进程通信沟通
4. 完成了最基础的通信处理及其回调处理

### 时间：2019/6/20(周四)

1. 完善 SuperCross 和 AsynConnectionProcessor 内部的功能实现
2. 调通了整合基础通信环节，添加了简单的测试场景和demo
3. 进行测试机兼容测试，来验证组件的兼容问题，发现问题：在android6.0及其以上的系统，限制了当应用没有开启时，不能通过service和ContentProvider进行数据传递，好尬啊。。。
4. 整体组件完成度达到了：70%

### 时间：2019/6/21(周五)

1. 解决android高版本限制应用没有开启时，不能通过service和ContentProvider进行数据传递问题


### 时间：2019/6/24(周一)

1. 打通跨app通信方案，配合BindMethodProcessor注解管理器，自动生成类，达到通信的目的
2. 开始构思 supercross-plugin 插件的实现，主要完成功能是：

    * 动态修改AndroidManifest.xml文件
    * 收集项目中所有四大组件对应的android:process，并且自动和ConnectMultiCrossService$ConnectMultiCrossService${index}进行关联
    * 需要动态修改class字节码，插入android:process和ConnectMultiCrossService$ConnectMultiCrossService${index}的对应map集合关联关系
3. 完善细节实现，组件完成度达到：90%
4. 插件完成度达到：40%

### 时间：2019/6/26(周三)

1. 完成插件剩余部分开发，完成度：100%
2. 整体组件完成度达到：95%
3. 未完成：文档和注释补充

### 时间：2019/6/27(周四)

1. 构思路由组件的设计，这里发现通信组件库设计存在缺陷，因为当前的跨进程通信组件，考虑跨app,考虑的进程通信，
但是没有考虑到进程间的事件总线处理，并且后面的路由库没有办法实现在任意进程中进行调转

2. 需要调整下现在的跨进程通信框架，让所有的进程通信的iBinder对象都集中起来管理，使用Dispatcher类进行集中管理，然后在下发事件或者服务


### 时间：2019/6/28(周五)

> 问题：
    * SuperProcessRoute通信组件库的通信方式存在局限性，虽然能做到跨应用通信，但是有个弊端就是需要业务开发人员维护ProcessId，并且有个局限性就是每次都需要bindService()或者unbindService(),假如一个应用内有多个进程，那么实现起来就很麻烦

1. 打算重新修改下通信方式，打算采用ContentProvider做进程之间的binder同步获取
2. 新建项目：OkSuperCross
3. 完成通信改造进度：30%

### 时间：2019/6/30(周日)

1. 完成跨进程“事件总线”的订阅和发送功能
2. 完成通信方式改造
3. 添加新的demo示例,基本功能自测完成
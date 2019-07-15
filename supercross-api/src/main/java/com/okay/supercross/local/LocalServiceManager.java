package com.okay.supercross.local;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地服务管理类，因为是同进程之间的访问，所以采用map进行保存管理即可
 */
public class LocalServiceManager {

    private static LocalServiceManager sInstance;

    private Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    private LocalServiceManager() {
    }

    public static LocalServiceManager getInstance() {
        if (null == sInstance) {
            synchronized (LocalServiceManager.class) {
                if (null == sInstance) {
                    sInstance = new LocalServiceManager();
                }
            }
        }
        return sInstance;
    }


    /**
     * 获取本地服务类
     * @param interfaceClassName  接口类全路径名称
     * @return 返回其接口类的实现类对象
     */
    public Object getLocalService(String interfaceClassName) {
        return serviceMap.get(interfaceClassName);
    }

    /**
     * 注册本地服务类
     * @param interfaceClassName 接口类全路径名称
     * @param serviceImpl   其接口类的实现类对象
     */
    public void registerService(String interfaceClassName, Object serviceImpl) {
        serviceMap.put(interfaceClassName, serviceImpl);
    }


    /**
     * 取消注册本地服务类
     * @param interfaceClassName 接口类全路径名称
     */
    public void unregisterService(String interfaceClassName) {
        serviceMap.remove(interfaceClassName);
    }
}

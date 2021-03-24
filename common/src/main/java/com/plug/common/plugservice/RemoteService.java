package com.plug.common.plugservice;

import com.woodys.supercross.ServiceCallback;

/**
 * Plug1的远程协议类
 * 参数禁止使用基本类型，可用包装类代替，防止反射无法找不到该方法
 */
public interface RemoteService {
    /**
     * 登录
     *
     * @param username          姓名
     * @param password          密码
     */
    String login(String username, String password);

    /**
     * 登录
     *
     * @param username          姓名
     * @param password          密码
     * @param callBack @String 回调返回token
     */
    void login2(String username, String password,ServiceCallback callBack);
}

package com.pujun.teammate_backend.constant;

/**
 * 用户常量
 * @author pujun
 */
public interface UserConstant {

    /**
     *  用户登录态
     */
    String USER_LOGIN_STATE = "userLoginState";

    // -------权限-------
    /**
     * 0-普通用户权限
     * 1-管理员权限
     */
    int DEFAULT_ROLE = 0;
    int ADMIN_ROLE = 1;

}

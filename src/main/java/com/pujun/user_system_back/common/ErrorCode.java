package com.pujun.user_system_back.common;

/**
 * 错误码
 * 作为参数被引用
 */
public enum ErrorCode {
    SUCCESS(1, "success", ""),
    PARAM_ERROR(400, "请求参数错误", ""),
    NULL_ERROR(401 , "请求数据为空", ""),
    NO_AUTH_ERROR(402 , "无权限", ""),
    NOT_LOGIN_ERROR (403 , "未登录", ""),
    SYSTEM_ERROR(404 , "系统异常", "");
    /**
     * 状态码
     */
    private final int code;

    /**
     * 状态信息
     */
    private final String msg;

    /**
     * 状态详细信息
     */
    private final String description;

    ErrorCode(int code, String msg, String description) {
        this.code = code;
        this.msg = msg;
        this.description = description;
    }

    public String getMsg() {
        return msg;
    }

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }
}

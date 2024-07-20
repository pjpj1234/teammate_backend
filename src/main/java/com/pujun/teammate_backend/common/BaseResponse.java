package com.pujun.teammate_backend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 回复类
 * @author pujun
 * @since 2024/6/24
 */
@Data
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = 7946265676392328231L;
    /**
     * 状态码
     */
    private int code;

    /**
     * 数据
     */
    private T data;
    /**
     * 状态信息
     */
    private String msg;

    /**
     * 状态详细信息
     */
    private String description;

    public BaseResponse(int code, T data, String msg, String description) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.description = description;
    }

    /**
     * 成功
     * @param code
     * @param data
     * @param msg
     */
    public BaseResponse(int code, T data, String msg) {
        this(code, data, msg, "");
    }

    /**
     * 成功
     * @param code
     * @param data
     */
    public BaseResponse(int code, T data) {
        this(code, data, "", "");
    }

    /**
     * 失败
     *
     */
    public BaseResponse(int code, String msg, String description) {
        this(code, null, msg, description);
    }

    /**
     * 失败
     * @param errorCode
     */
    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMsg(), errorCode.getDescription());
    }

    /**
     * 失败
     * @param errorCode
     * @param description
     */
    public BaseResponse(ErrorCode errorCode, String description) {
        this(errorCode.getCode(),null, errorCode.getMsg(), description);
    }

    /**
     * 失败
     * @param errorCode
     * @param description
     */
    public BaseResponse(ErrorCode errorCode, String msg, String description) {
        this(errorCode.getCode(),null, msg, description);
    }

}

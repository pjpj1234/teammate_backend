package com.pujun.teammate_backend.exception;

import com.pujun.teammate_backend.common.ErrorCode;

/**
 * 自定义异常类
 * 不用setter 只用构造函数方法传值
 * 所以用final 它们只能在构造函数中被赋值一次，之后不能再被修改
 * 全部用ResultUtils.error返回太麻烦，直接抛异常，这个自定义异常
 */
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = -8045127086584815442L;

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误详细描述
     */
    private final String description;

    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode){
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode, String description){
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}

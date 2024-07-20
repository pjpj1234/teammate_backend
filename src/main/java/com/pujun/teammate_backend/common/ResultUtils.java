package com.pujun.teammate_backend.common;

/**
 * 结果处理类
 * @author pujun
 * @since 2024/6/24
 */
public class ResultUtils {
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(1, data, "success");
    }

    public static BaseResponse error(int code, String msg, String description){
        return new BaseResponse(code, msg, description);
    }

    public static BaseResponse error(ErrorCode errorCode){
        return new BaseResponse(errorCode);
    }

    public static BaseResponse error(ErrorCode errorCode, String description){
        return new BaseResponse(errorCode, description);
    }
}

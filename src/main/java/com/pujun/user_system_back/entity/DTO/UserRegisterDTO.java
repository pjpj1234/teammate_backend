package com.pujun.user_system_back.entity.DTO;

import lombok.Data;

import java.io.Serializable;
@Data
public class UserRegisterDTO implements Serializable {
    private static final long serialVersionUID = 4393234008910569008L;

    /**
     * 账户
     */
    private String userAccount;
    /**
     * 密码
     */
    private String userPassword;
    /**
     * 二次密码
     */
    private String secPassword;
    /**
     * 校验码
     */
    private String validCode;
}

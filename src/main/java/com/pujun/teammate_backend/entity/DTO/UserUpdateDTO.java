package com.pujun.teammate_backend.entity.DTO;

import lombok.Data;

import java.io.Serializable;
@Data
public class UserUpdateDTO implements Serializable {
    private static final long serialVersionUID = -7708570273297634974L;

    private Long id;
    private String userAccount;
    private String userName;
//    private String userPassword;// 你可以决定是否允许通过此API修改密码
    private Integer gender;
    private String avatarUrl;
    private String phone;
    private String email;
    private Integer userStatus;
    private String validCode;
}

package com.pujun.user_system_back.entity.DTO;

import lombok.Data;

import java.io.Serializable;
@Data
public class UserFindDTO implements Serializable {

    private static final long serialVersionUID = -6609600440562531441L;

    private String userName;

    private String gender;
}

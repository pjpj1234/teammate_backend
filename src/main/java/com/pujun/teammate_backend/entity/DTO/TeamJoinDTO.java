package com.pujun.teammate_backend.entity.DTO;


import lombok.Data;

import java.io.Serializable;

/**
 * 加入队伍 DTO
 */
@Data
public class TeamJoinDTO implements Serializable {

    private static final long serialVersionUID = -6354817104428836190L;

    /**
     * 队伍id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;
}

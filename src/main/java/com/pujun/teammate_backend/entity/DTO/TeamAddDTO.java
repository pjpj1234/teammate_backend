package com.pujun.teammate_backend.entity.DTO;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 添加队伍DTO
 */
@Data
public class TeamAddDTO implements Serializable {
    private static final long serialVersionUID = 7985534582582683707L;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 创始人id
     */
    private Long userId;

    /**
     * 状态 0 -公开、1 - 私有、2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;
}

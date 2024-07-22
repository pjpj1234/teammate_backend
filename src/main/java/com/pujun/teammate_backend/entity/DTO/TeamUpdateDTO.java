package com.pujun.teammate_backend.entity.DTO;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 队伍更新 DTO
 */
@Data
public class TeamUpdateDTO implements Serializable {
    private static final long serialVersionUID = 5860488452976419001L;

    /**
     * id
     */
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 状态 0 -公开、1 - 私有、2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;
}

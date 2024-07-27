package com.pujun.teammate_backend.entity.VO;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 队伍和用户信息封装类（脱敏）
 * @author pujun
 */
@Data
public class TeamUserVO implements Serializable {

    private static final long serialVersionUID = 5491286592332047159L;

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
     * 创始人VO
     */
    private UserVO createUser;

    /**
     * 是否已加入该队伍
     */
    private boolean hasJoin = false;

    /**
     * 已经加入该队伍的人数
     */
    private int hasJoinNum;

    /**
     * 状态 0 -公开、1 - 私有、2 - 加密
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

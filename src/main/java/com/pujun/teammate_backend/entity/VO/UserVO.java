package com.pujun.teammate_backend.entity.VO;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户包装类（脱敏）
 */
@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = -1451343649298373434L;
    /**
     * id
     */
    private Long id;

    /**
     * 账户
     */
    private String userAccount;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态 0 -正常
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 0- 普通用户 1-管理员
     */
    private Integer userRole;

    /**
     * 校验码
     */
    private String validCode;

    /**
     * 标签
     */
    private String tags;
}

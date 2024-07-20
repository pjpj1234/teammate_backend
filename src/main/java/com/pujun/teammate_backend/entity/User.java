package com.pujun.teammate_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;



/**
 * <p>
 *
 * </p>
 *
 * @author pujun
 * @since 2024-06-22
 */
@Data
public  class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
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
     * 密码
     */
    private String userPassword;

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
     * 逻辑删除
     */
    @TableLogic
    private Integer isDelete;

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


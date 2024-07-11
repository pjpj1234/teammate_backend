package com.pujun.user_system_back.service;

import com.pujun.user_system_back.entity.DTO.UserUpdateDTO;
import com.pujun.user_system_back.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author pujun
 * @since 2024-06-20
 */
public interface UserService extends IService<User> {


    /**
     * @param userAccount 账户
     * @param userPassword 密码
     * @param secPassword 二次密码
     * @param validCode 校验码
     * @return 结果
     */
    long userRegister(String userAccount, String userPassword, String secPassword, String validCode);

    /**
     * 登录
     * @param userAccount 账户
     * @param userPassword 密码
     * @param request 网络传输
     * @return 用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户注销
     * @param request
     * @return
     */
    Integer userLogout(HttpServletRequest request);

    /**
     * 得到脱敏的user
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 更新用户信息
     *
     * @param userUpdateDTO
     * @param loginUser
     */
    boolean updateUser(UserUpdateDTO userUpdateDTO, User loginUser);

    /**
     * 通过标签搜索用户（SQL查询）
     * @param tagNameList
     * @return
     */
    List<User> searchUsersByTagsBySQL(List<String> tagNameList);

    /**
     * 根据标签搜索用户 （内存搜索）
     * @param tagList
     * @return
     */
    List<User> searchUsersByTags(List<String> tagList);

    /**
     *  是否为管理员
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     * @param loginUser
     * @return
     */
    boolean isAdmin(User loginUser);

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);
//    List<User> userFind(String userName, String gender, HttpServletRequest request);

//    int userDelete(long id, HttpServletRequest request);
}

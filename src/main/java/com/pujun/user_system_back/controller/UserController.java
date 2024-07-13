package com.pujun.user_system_back.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pujun.user_system_back.common.BaseResponse;
import com.pujun.user_system_back.common.ErrorCode;
import com.pujun.user_system_back.common.ResultUtils;
import com.pujun.user_system_back.entity.DTO.UserFindDTO;
import com.pujun.user_system_back.entity.DTO.UserLoginDTO;
import com.pujun.user_system_back.entity.DTO.UserRegisterDTO;
import com.pujun.user_system_back.entity.DTO.UserUpdateDTO;
import com.pujun.user_system_back.entity.User;
import com.pujun.user_system_back.exception.BusinessException;
import com.pujun.user_system_back.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.pujun.user_system_back.constant.UserConstant.ADMIN_ROLE;
import static com.pujun.user_system_back.constant.UserConstant.USER_LOGIN_STATE;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author pujun
 * @since 2024-06-20
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://211.159.150.239"},allowCredentials = "true")
public class UserController {

    @Autowired
    UserService userService;

    /**
     * 用户注册
     * @param userRegisterDTO 注册DTO
     * @return 用户id
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterDTO userRegisterDTO){
        if(userRegisterDTO == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"参数为空");
        }
        String userAccount = userRegisterDTO.getUserAccount();
        String userPassword = userRegisterDTO.getUserPassword();
        String secPassword = userRegisterDTO.getSecPassword();
        String validCode = userRegisterDTO.getValidCode();

        if(StringUtils.isAnyBlank(userAccount,userPassword,secPassword, validCode)){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"有至少一个参数为空");
        }
        long result = userService.userRegister(userAccount, userPassword, secPassword, validCode);
        return ResultUtils.success(result);
    }

    /**
     * 登录
     * @param userLoginDTO 传输DTO
     * @param httpServletRequest
     * @return 用户信息
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginDTO userLoginDTO, HttpServletRequest httpServletRequest){
        if(userLoginDTO == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "参数为空");
        }
        String userAccount = userLoginDTO.getUserAccount();
        String userPassword = userLoginDTO.getUserPassword();



        if(StringUtils.isAnyBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"有至少一个参数为空");
        }
        User safetyUser = userService.userLogin(userAccount, userPassword, httpServletRequest);
        return ResultUtils.success(safetyUser);
    }

    /**
     * 查找
     * @param userFindDTO 查找DTO
     * @param request
     * @return 用户列表
     */
    @PostMapping("/find")
    public BaseResponse<List<User>> userFind(@RequestBody UserFindDTO userFindDTO, HttpServletRequest request){
        if(!userService.isAdmin(request)){ //先鉴权
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "您不是管理员，没有该权限！");
        }

        List<User> userList;
        if(userFindDTO == null){
            userList = userService.list();
        }else{
            String userName = userFindDTO.getUserName();
            String gender = userFindDTO.getGender();
            //设置条件器
            QueryWrapper<User> findWrapper = new QueryWrapper<>();
            findWrapper.like(StringUtils.isNotBlank(userName), "userName", userName) //注意这里是notblank
                    .eq(StringUtils.isNotBlank(gender), "gender", gender);
            userList = userService.list(findWrapper);
        }
        userList.forEach(userService::getSafetyUser); //转为VO 去掉密码
        return ResultUtils.success(userList);
    }

    /**
     * 根据标签搜索用户
     * @param tagNameList
     * @returngit pull
     */
    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUsersByTags(@RequestParam(required = false) List<String> tagNameList){
        if(CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "标签未输入");
        }
        List<User> userList = userService.searchUsersByTags(tagNameList);
        return ResultUtils.success(userList);
    }

    /**
     * 推荐页面
     * @param request
     * @return
     */
    @GetMapping("recommend")
    public BaseResponse<List<User>> recommendUsers(long pageSize, long pageNum, HttpServletRequest request){
        Page<User> users = userService.page(new Page<>(pageNum, pageSize), null);
        List<User> userList = users.getRecords().stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(userList);
    }

    /**
     * 用户信息更新
     * @param userUpdateDTO 封装的userDTO
     * @param request
     * @return
     */
    @PutMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateDTO userUpdateDTO, HttpServletRequest request){
        if(userUpdateDTO == null || request == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = userService.updateUser(userUpdateDTO, loginUser);
        return ResultUtils.success(result); //成功返回的data就是1
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> userDelete(@RequestParam long id, HttpServletRequest request){
        if(!userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        if(id < 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "没有该用户");
        }
        boolean result = userService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 获得当前登录用户
     * @param request
     * @return
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        if(request == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "网络请求出错");
        }

        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User originUser = (User) userObj;
        if(originUser == null){ //判空，不然会下面报错
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录"); //未登录
        }

        User user = userService.getById(originUser.getId());
        // todo 判断该用户是否合法

        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    @PostMapping("logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        if(request == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "网络请求错误");
        }
        Integer result = userService.userLogout(request);
        return ResultUtils.success(result);
    }


}

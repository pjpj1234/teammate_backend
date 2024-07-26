package com.pujun.teammate_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pujun.teammate_backend.common.ErrorCode;
import com.pujun.teammate_backend.entity.DTO.UserUpdateDTO;
import com.pujun.teammate_backend.entity.User;
import com.pujun.teammate_backend.exception.BusinessException;
import com.pujun.teammate_backend.mapper.UserMapper;
import com.pujun.teammate_backend.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pujun.teammate_backend.utils.AlgorithmUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.pujun.teammate_backend.constant.UserConstant.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author pujun
 * @since 2024-06-20
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 盐值 混淆密码
     */
    public static final String SALT = "pujun";

    @Autowired
    UserMapper userMapper;

    /**
     * @param userAccount 账户
     * @param userPassword 密码
     * @param secPassword 二次密码
     * @param validCode 校验码
     * @return 结果
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String secPassword, String validCode) {
        if(StringUtils.isAnyBlank(userAccount, userPassword, secPassword, validCode)){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "有至少一个参数为空");
        }
        if(userAccount.length() < 4 || userPassword.length() < 8 || validCode.length() > 5){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "参数长度错误");
        }
        if(!userPassword.equals(secPassword)){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "两次密码输入不一致");
        }

        // 账户不能重复
        QueryWrapper<User> accountWrapper = new QueryWrapper<>();
        accountWrapper.eq("userAccount",userAccount);
        Long count = userMapper.selectCount(accountWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "账户名重复");
        }

        //校验码不能重复
        QueryWrapper<User> validWrapper = new QueryWrapper<>();
        validWrapper.eq("validCode",validCode);
        count = userMapper.selectCount(validWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "校验码重复");
        }

        // 查看非法字符
        String REG_EX = "[`~!@#$%^*()+\n\r|{}\\[\\]<>/？！（）【】‘；：”“’。，、\\\\]";
        if(Pattern.compile(REG_EX).matcher(userAccount).find()){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "账户包含非法字符");
        }

        // 密码加密
        String encytryPassword = DigestUtils.md5Hex((SALT + userPassword).getBytes());
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encytryPassword);
        user.setValidCode(validCode);
        user.setUserRole(1); // 这里娱乐 把每个注册的人设为管理员
        boolean result = this.save(user);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "用户未保存到系统");
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if(StringUtils.isAnyBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "有至少一个参数为空");
        }
        if(userAccount.length() < 4 || userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "参数长度错误");
        }
        // 不能有非法字符
        String REG_EX = "[`~!@#$%^*()+\n\r|{}\\[\\]<>/？！（）【】‘；：”“’。，、\\\\]";
        if(Pattern.compile(REG_EX).matcher(userAccount).find()){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "账户包含非法字符");
        }

        // 加密 比对
        String encytryPassword = DigestUtils.md5Hex((SALT + userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount)
                .eq("userPassword", encytryPassword);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null){
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户名或密码错误");
        }

        //脱敏
        User safetyUser = getSafetyUser(user);

        // 设置session
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    @Override
    public Integer userLogout(HttpServletRequest request) {
        //移出登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    @Override
    public User getSafetyUser(User originUser){
        if(originUser == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        // 用户脱敏
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setUserName(originUser.getUserName());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setValidCode(originUser.getValidCode());
        safetyUser.setTags(originUser.getTags());
        return safetyUser;
    }

    @Override
    public boolean updateUser(UserUpdateDTO userUpdateDTO, User loginUser) {
        long userId = userUpdateDTO.getId();
        if(userId <= 0){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        // 只有管理员和自己才能修改用户信息
        if(!isAdmin(loginUser) && userId != loginUser.getId()){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        if(userUpdateDTO.getUserAccount().length() < 4){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "账号长度不能小于等于4位");
        }
        if(userUpdateDTO.getValidCode().length() > 5){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "校验码长度不能大于5位");
        }

        // 账户不能和其他人重复
        QueryWrapper<User> accountWrapper = new QueryWrapper<>();
        accountWrapper.eq("userAccount",userUpdateDTO.getUserAccount());
        User otherUser = userMapper.selectOne(accountWrapper);
        //首先判断是否为空 为空说明没人用这个账户
        if(otherUser != null){
            if(otherUser.getId() != userUpdateDTO.getId()){ /* 其他人与自己账户相同 */
                throw new BusinessException(ErrorCode.PARAM_ERROR, "账户名重复");
            }
        }

        //校验码不能重复
        QueryWrapper<User> validWrapper = new QueryWrapper<>();
        validWrapper.eq("validCode",userUpdateDTO.getValidCode());
        otherUser = userMapper.selectOne(validWrapper);
        if(otherUser != null){
            if(!otherUser.getId().equals(userUpdateDTO.getId())){
                throw new BusinessException(ErrorCode.PARAM_ERROR, "校验码重复");
            }
        }

        // 查看非法字符
        String REG_EX = "[`~!@#$%^*()+\n\r|{}\\[\\]<>/？！（）【】‘；：”“’。，、\\\\]";
        if(Pattern.compile(REG_EX).matcher(userUpdateDTO.getUserAccount()).find()){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "账户包含非法字符");
        }

        // 账户被删除
        User oldUser = userMapper.selectById(userId);
        if(oldUser == null){
            throw new BusinessException(ErrorCode.NULL_ERROR, "账户不存在或已被删除");
        }

        //更新信息
        User newUser = new User();
        BeanUtils.copyProperties(userUpdateDTO, newUser); //此时newUser里面有些字段是空值
        newUser.setUpdateTime(LocalDateTime.now()); //更新时间
        int result = userMapper.updateById(newUser); //空值不会覆盖原有的数据

        return result > 0;
    }

    @Deprecated  //已过时
    public List<User> searchUsersByTagsBySQL(List<String> tagNameList){
        if(CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        long startTime = System.currentTimeMillis(); //开始时间
        // 拼接 and 查询
        // like ‘%Java%' and like '%python%'
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        for (String tagName : tagNameList) {
//            if(tagName.equals("男") || tagName.equals("女")){ //新加了一个男女搜索
//                queryWrapper = queryWrapper.eq("gender", tagName.equals("男") ? 0 : 1);
//            }
//            else{
//                queryWrapper = queryWrapper.like("tags", tagName);
//            }
                queryWrapper.and(qw -> qw.eq(tagName.equals("男"),"gender", 0)
                        .eq(tagName.equals("女"),"gender", 1)
                        .or()
                        .like("tags", tagName)); // 用or也会对后面的进行搜索 性能不好
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        log.info("SQL time = " + (System.currentTimeMillis()- startTime)); //结束时间
        // 每个转为safetyUser 再连接成list
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    @Override
    public List<User> searchUsersByTags(List<String> tagNameList){ //需要自己实现一遍（面试考点）
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        long startTime = System.currentTimeMillis();
        //1.先查询所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        //2.判断内存中是否包含要求的标签 parallelStream()
        return userList.stream().filter(user -> {
            String tagstr = user.getTags();
//            if (StringUtils.isBlank(tagstr)){
//                return false;
//            }
            Set<String> tempTagNameSet =  gson.fromJson(tagstr,new TypeToken<Set<String>>(){}.getType());
            //java8  Optional 来判断空
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());

            for (String tagName : tagNameList){
                if (!tempTagNameSet.contains(tagName)){
                    return false;
                }
            }
            log.info("memory time = " + (System.currentTimeMillis() - startTime));
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    public boolean isAdmin(HttpServletRequest request) {
        if(request == null){
            return false;
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

    /**
     * 是否为管理员
     * @param loginUser
     * @return
     */
    public boolean isAdmin(User loginUser) {
        return loginUser != null && loginUser.getUserRole() == ADMIN_ROLE;
    }

    /**
     * 获取当前用户信息
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        if(request == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        Object loginUser = request.getSession().getAttribute(USER_LOGIN_STATE);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return (User) loginUser;
    }

    @Override
    public List<User> matchUsers(Long num, User loginUser) {
        //取数据库中所有拥有标签的用户列表
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags")
                .isNotNull("tags");
        List<User> userList = this.list(queryWrapper);
        //取出自己的tags 转为List类型
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> loginUserTagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        //用 List<Pair<User,Long>>存 用户 -> 相似度(也可以存下标id，目的是为了通过id查找所有数据)
        List<Pair<User, Long>> list = new ArrayList<>();
        //依次计算所有用户和当前用户的相似度
        for (User user : userList) {
            String userTags = user.getTags();
            //无标签 或 该用户是自己
            if(StringUtils.isBlank(userTags) || user.getId().equals(loginUser.getId())){
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            //计算分数 并存入 用户和对应分数
            long distance = AlgorithmUtils.minDistance(userTagList, loginUserTagList);
            list.add(new Pair<>(user, distance));
        }
        //按编辑距离由小到大排序
        List<Pair<User, Long>> topUserPairList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
//        List<User> result = topUserPairList.stream().map(pair -> pair.getKey()).collect(Collectors.toList());
        //再根据 id 把所有数据查出来
        //原本顺序的 userId 列表
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", userIdList);
        // 这里的 in 方法 有bug
        // 原本1 3 2 查时 1 2 3
        //根据id查数据，用一个map来装 id 和 User实体，根据 userId 得到 User 实体
        Map<Long, List<User>> userIdUserListMap =
        this.list(userQueryWrapper).stream()
                .map(this::getSafetyUser)
                .collect(Collectors.groupingBy(User::getId));//groupingBY只是为了得到map 不加上也是根据id排列
        List<User> finalUserList = new ArrayList<>();
        for (Long userId : userIdList) { //根据原本顺序 查找对应的 User实体
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return finalUserList;
    }
}

package com.pujun.teammate_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.pujun.teammate_backend.common.ErrorCode;
import com.pujun.teammate_backend.common.TeamStatusEnum;
import com.pujun.teammate_backend.entity.DTO.*;
import com.pujun.teammate_backend.entity.Team;
import com.pujun.teammate_backend.entity.User;
import com.pujun.teammate_backend.entity.UserTeam;
import com.pujun.teammate_backend.entity.VO.TeamUserVO;
import com.pujun.teammate_backend.entity.VO.UserVO;
import com.pujun.teammate_backend.exception.BusinessException;
import com.pujun.teammate_backend.mapper.TeamMapper;
import com.pujun.teammate_backend.mapper.UserTeamMapper;
import com.pujun.teammate_backend.service.TeamService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pujun.teammate_backend.service.UserService;
import com.pujun.teammate_backend.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author pujun
 * @since 2024-07-20
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {

    @Resource
    private UserTeamMapper userTeamMapper;

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addTeam(TeamAddDTO teamAddDTO, User loginUser) {

//  1. 请求参数是否为空？
        if(teamAddDTO == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

//  2. 是否登录，未登录不允许创建
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

//  3. 校验信息
//    - 队伍人数 > 1 && <= 20
        if(teamAddDTO.getMaxNum() < 1 || teamAddDTO.getMaxNum() > 20){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "队伍人数不符合要求");
        }

//    - 队伍名称 <= 20
        if(teamAddDTO.getName().length() > 20){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "队伍名称过长");
        }

//    - 描述 <= 512
        if(teamAddDTO.getDescription().length() > 512){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "队伍描述过长");
        }

//    - 过期时间 > 现在时间
//        Date expireTime = new Date(String.valueOf(teamAddDTO.getExpireTime()));
//        if(!new Date().before(expireTime)){
        LocalDateTime expireTime = teamAddDTO.getExpireTime();
        if(!LocalDateTime.now().isBefore(expireTime)){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "过期时间应大于当前时间");
        }

//    - 状态 status 是否公开（int） 默认为 0 公开（新建 TeamStatusEnum 枚举）
        int status = Optional.ofNullable(teamAddDTO.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getTeamStatusByValue(status);
        if(statusEnum == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "队伍状态不满足要求");
        }

//    - 如果 status 是加密状态，那一定要有密码，且密码 <= 32
        String password = teamAddDTO.getPassword();
        if(statusEnum.equals(TeamStatusEnum.SECRET)){
            if(password == null || password.length() > 32){
                throw new BusinessException(ErrorCode.PARAM_ERROR, "密码设置不正确");
            }
        }
//    - 校验一个用户最多创建 5 个属于他自己的队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId" , loginUser.getId());
        long hasTeamNum = this.count(queryWrapper);
        if(hasTeamNum >= 5){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户最多创建 5 个队伍");
        }

//  4. 插入队伍信息到队伍表中
        Long userId = loginUser.getId();
        Team team = new Team();
        BeanUtils.copyProperties(teamAddDTO, team);
        team.setUserId(userId); //这个放后面！！！ 小bug造成大错
        boolean save = this.save(team);
        Long teamId = team.getId();
        if(!save || teamId == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败");
        }

//  5. 插入用户 => 队伍关系到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(teamId);
        userTeam.setUserId(userId);
        userTeam.setJoinTime(LocalDateTime.now());
//        int result = userTeamMapper.insert(userTeam);
        boolean result = userTeamService.save(userTeam);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败");
        }

        return teamId;
    }

    @Override
    public List<TeamUserVO> listTeams(TeamQueryDTO teamQueryDTO, boolean isAdmin, boolean isMy) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        if(teamQueryDTO != null){
        //    private Long id;
            Long id = teamQueryDTO.getId();
            if(id != null && id > 0){
                queryWrapper.eq("id", id);
            }
        //    private List<Long> idList; 我自己加入的队伍 列表（复用该方法）
            List<Long> idList = teamQueryDTO.getIdList();
            if(CollectionUtils.isNotEmpty(idList)){
                queryWrapper.in("id", idList);
            }
        //    private String searchText;
            String searchText = teamQueryDTO.getSearchText();
            if(StringUtils.isNotBlank(searchText)){
                queryWrapper.and(qw -> qw.like("name", searchText).or()
                        .like("description", searchText));
            }
        //    private String name;
            String name = teamQueryDTO.getName();
            if(StringUtils.isNotBlank(name)){
                queryWrapper.like("name", name);
            }
        //    private String description;
            String description = teamQueryDTO.getDescription();
            if(StringUtils.isNotBlank(description)){
                queryWrapper.like("description", description);
            }
            //    private Integer maxNum;
            Integer maxNum = teamQueryDTO.getMaxNum();
            if(maxNum != null && maxNum > 0){
                queryWrapper.eq("maxNum", maxNum);
            }
            //    private Long userId;
            Long userId = teamQueryDTO.getUserId();
            if(userId != null && userId > 0){
                queryWrapper.eq("userId", userId);
            }
            //    private Integer status;
            if(!isMy) { // 不是获得自己的队伍才添加搜索状态
                Integer status = teamQueryDTO.getStatus();
                TeamStatusEnum statusEnum = TeamStatusEnum.getTeamStatusByValue(status);
                if(statusEnum == null){
                    statusEnum = TeamStatusEnum.PUBLIC;
                }
                if(!isAdmin && statusEnum != TeamStatusEnum.PUBLIC){ //管理员才能查看加密队伍
                    throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
                }
                queryWrapper.eq("status", statusEnum.getValue());
            }
        }

        //不展示过期的队伍 (没有过期时间的相当于永久）
        queryWrapper.and(qw -> qw.gt("expireTime", LocalDateTime.now())
                .or().isNull("expireTime"));

        //关联查询已加入的队伍信息
        //1. SQL select * from Team t left join User u on t.userId=u.id
        List<Team> teamList = this.list(queryWrapper);
        if(CollectionUtils.isEmpty(teamList)){ // 用CollectionUtils判断！！！
            return new ArrayList<>();
        }
        //for循环列表 根据 userId来查找 user中的数据 再设置到 VO中
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if(userId == null){
                continue;
            }
            User user = userService.getById(userId);

            //装入VO
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            //创始人不为空 脱敏放入
            if(user != null){
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                teamUserVO.setCreateUser(userVO);
            }

            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    @Override
    public List<TeamUserVO> addHasJoinField(List<TeamUserVO> teamList, User loginUser) {
        // 给每个自己已加入的队伍加上hasJoin
        // 1.得到 teamId 列表
        final List<Long> teamIdList = teamList.stream()
                .map(TeamUserVO::getId).collect(Collectors.toList());
        // 2.根据 userId 和 teamId 查询在user_team里已加入队伍的数据
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", loginUser.getId())
                .in("teamId", teamIdList);
        List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
        // 3.给查询到的 team 分组（用set去重），遍历判断 加入 hasJoin 字段
        Set<Long> hasJoinTeamIdSet = userTeamList.stream()
                .map(UserTeam::getTeamId).collect(Collectors.toSet());
        teamList.forEach(team -> {
            boolean hasJoin = hasJoinTeamIdSet.contains(team.getId());
            team.setHasJoin(hasJoin);
        });
        return teamList;
    }

    @Override
    public List<TeamUserVO> countTeamHasJoin(List<TeamUserVO> teamList) {
        // 初始版（多次查询数据库）
//        teamList.forEach(team -> {
//            QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq("teamId", team.getId());
//            long hasJoinNum = userTeamService.count(queryWrapper);
//            team.setHasJoinNum(hasJoinNum);
//        });
        // 改良版（一次查询数据库）
        //每个userTeam用 Map<Long, List<UserTeam>> 根据teamId分组 得到每个team有多少人
        final List<Long> teamIdList = teamList.stream()
                .map(TeamUserVO::getId).collect(Collectors.toList());
        QueryWrapper<UserTeam> userTeamJoinQueryWrapper = new QueryWrapper<>();
        userTeamJoinQueryWrapper.in("teamId", teamIdList);
        List<UserTeam> userTeamJoinList = userTeamService.list(userTeamJoinQueryWrapper);

        Map<Long, List<UserTeam>> teamIdUserTeamMap = userTeamJoinList.stream()
                .collect(Collectors.groupingBy(UserTeam::getTeamId));
        teamList.forEach(team -> team.setHasJoinNum(teamIdUserTeamMap.getOrDefault(team.getId(), new ArrayList<>()).size()));
        return teamList;
    }

    @Override
    public boolean updateTeam(TeamUpdateDTO teamUpdateDTO, User loginUser) {
//    1. 判断请求参数是否为空
        if(teamUpdateDTO == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
//    2. 修改队伍是否存在
        Long id = teamUpdateDTO.getId();
        Team oldTeam = this.getById(id);
        if(oldTeam == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "修改队伍不存在");
        }
//    3. 只有管理员和自己创建的队伍能修改
        if(!userService.isAdmin(loginUser) && !oldTeam.getUserId().equals(loginUser.getId())){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
//    4. 如果用户传入的新值和老值一样，就不用 update 了（可自行实现，降低使用数据库次数）TODO
        Team newTeam = new Team();
        BeanUtils.copyProperties(oldTeam,newTeam); //把 原来的值 + 多余字段 放入
        BeanUtils.copyProperties(teamUpdateDTO, newTeam); //覆盖原来的值 多余字段不变 组成新的team
        if(newTeam.equals(oldTeam)){ //原来的值与现在的值相等 这里用equals 对象之间比较用equals 别用==
            return true;
        }
//    5. 如果队伍状态为加密，那必须要有密码
        Integer status = teamUpdateDTO.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getTeamStatusByValue(status);
        if(statusEnum != TeamStatusEnum.PUBLIC){
            if(StringUtils.isBlank(teamUpdateDTO.getPassword())){
                throw new BusinessException(ErrorCode.PARAM_ERROR, "加密状态必须有密码");
            }
        }
//    6. 更新完成
        newTeam.setUpdateTime(LocalDateTime.now());
        return this.updateById(newTeam);
    }

    @Override
    public boolean joinTeam(TeamJoinDTO teamJoinDTO, User loginUser) {
        if(teamJoinDTO == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
//      2. 队伍必须存在，只能加入未满、未过期的队伍
        Long teamId = teamJoinDTO.getTeamId();
        if(teamId == null || teamId < 0){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "请求队伍不存在");
        }
        Team team = this.getById(teamId);
        if(team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR, "请求队伍不存在");
        }
        LocalDateTime expireTime = team.getExpireTime();
        if(expireTime != null && LocalDateTime.now().isAfter(expireTime)){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "请求队伍已过期");
        }
        Long teamHasJoinNum = countTeamUserByTeamId(teamId);
        if(teamHasJoinNum >= team.getMaxNum()){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "队伍人数已满");
        }
//      3. 不能加入自己的队伍，不能加入已加过的队伍
        Long userId = loginUser.getId();
        if(team.getUserId().equals(userId)){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "不能加入自己队伍");
        }
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId).eq("teamId", teamId);
        Long hasUserJoinTeam = userTeamMapper.selectCount(queryWrapper);
        if(hasUserJoinTeam > 0){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "不能加入已加入的队伍");
        }
//      1. 用户最多参加 5 个队伍 （数据库查询放在下面，减少查询时间）
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        Long hasJoinNum = userTeamMapper.selectCount(queryWrapper);
        if(hasJoinNum >= 5){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户最多创建和加入 5 个队伍");
        }
//      4. 禁止加入 私有 的队伍
        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getTeamStatusByValue(status);
        if (TeamStatusEnum.PRIVATE.equals(teamStatusEnum)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "禁止加入私有队伍");
        }
//      5. 如果加入队伍是 私密状态， 必须密码匹配
        String password = teamJoinDTO.getPassword();
        if (TeamStatusEnum.SECRET.equals(teamStatusEnum)) {
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword())) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "密码错误");
            }
        }
//      6. 新增队伍 - 用户关联信息
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(LocalDateTime.now());
        return userTeamService.save(userTeam);
    }

    /**
     * 获取某队伍当前人数
     * @param teamId
     * @return
     */
    private Long countTeamUserByTeamId(Long teamId) {
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        Long teamHasJoinNum = userTeamMapper.selectCount(queryWrapper);
        return teamHasJoinNum;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitDTO teamQuitDTO, User loginUser) {
        // 1.  校验请求参数
        if (teamQuitDTO == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        // 2.  校验队伍是否存在
        Long teamId = teamQuitDTO.getTeamId();
        Team team = getTeamById(teamId);
        // 3.  校验我是否已加入队伍
        long userId = loginUser.getId();
        UserTeam queryUserTeam = new UserTeam();
        queryUserTeam.setTeamId(teamId);
        queryUserTeam.setUserId(userId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>(queryUserTeam);
        long count = userTeamService.count(queryWrapper);
        if (count == 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "未加入队伍");
        }
        // 4.  如果队伍
        long teamHasJoinNum = this.countTeamUserByTeamId(teamId);
        // 队伍只剩下一个人，解散
        if (teamHasJoinNum == 1) {
            //删除队伍
            this.removeById(teamId);
        } else {
            //队伍至少还剩下两人
            //是队长
            if (team.getUserId() == userId) {
                //把队伍转移给第二早加入的用户
                //1.查询已加入队伍的所有用户和加入时间
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId", teamId);
                userTeamQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextTeamLeaderId = nextUserTeam.getUserId();
                //更新当前队伍的队长
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextTeamLeaderId);
                boolean result = this.updateById(updateTeam);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队伍队长失败");
                }
            }
        }
        //移除关系
        return userTeamService.remove(queryWrapper);
    }

    /**
     * 根据 id 获取队伍信息
     * @param teamId
     * @return
     */
    private Team getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        return team;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeTeam(Long id, User loginUser) {
        // 校验队伍是否存在
        Team team = getTeamById(id);
        long teamId = team.getId();
        // 校验你是不是队伍的队长
        if (!team.getUserId().equals(loginUser.getId())){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"无访问权限");
        }
        // 移除所有加入队伍的关联信息
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        boolean result = userTeamService.remove(userTeamQueryWrapper);
        if (!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除队伍关联信息失败");
        }
        // 删除队伍
        return this.removeById(teamId);
    }


}

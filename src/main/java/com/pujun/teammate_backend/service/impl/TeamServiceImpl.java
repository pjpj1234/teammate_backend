package com.pujun.teammate_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pujun.teammate_backend.common.ErrorCode;
import com.pujun.teammate_backend.common.TeamStatusEnum;
import com.pujun.teammate_backend.entity.DTO.TeamAddDTO;
import com.pujun.teammate_backend.entity.DTO.TeamQueryDTO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        queryWrapper.eq("userId" , teamAddDTO.getUserId());
        long hasTeamNum = this.count(queryWrapper);
        if(hasTeamNum >= 5){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户最多创建 5 个队伍");
        }

//  4. 插入队伍信息到队伍表中
        Long userId = loginUser.getId();
        Team team = new Team();
        team.setUserId(userId);
        BeanUtils.copyProperties(teamAddDTO, team);
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
    public List<TeamUserVO> listTeams(TeamQueryDTO teamQueryDTO, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        if(teamQueryDTO != null){
        //    private Long id;
            Long id = teamQueryDTO.getId();
            if(id != null && id > 0){
                queryWrapper.eq("id", id);
            }
        //    private List<Long> idList; todo
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

        //不展示过期的队伍 (没有过期时间的相当于永久）
        queryWrapper.and(qw -> qw.gt("expireTime", LocalDateTime.now())
                .or().isNull("expireTime"));

        //关联查询已加入的队伍信息
        //1. SQL select * from Team t left join User u on t.userId=u.id
        List<Team> teamList = this.list(queryWrapper);
        if(teamList == null){
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


}

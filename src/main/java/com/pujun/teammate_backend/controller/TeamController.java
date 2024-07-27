package com.pujun.teammate_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pujun.teammate_backend.common.BaseResponse;
import com.pujun.teammate_backend.common.ErrorCode;
import com.pujun.teammate_backend.common.ResultUtils;
import com.pujun.teammate_backend.entity.DTO.*;
import com.pujun.teammate_backend.entity.Team;
import com.pujun.teammate_backend.entity.User;
import com.pujun.teammate_backend.entity.UserTeam;
import com.pujun.teammate_backend.entity.VO.TeamUserVO;
import com.pujun.teammate_backend.exception.BusinessException;
import com.pujun.teammate_backend.service.TeamService;
import com.pujun.teammate_backend.service.UserService;
import com.pujun.teammate_backend.service.UserTeamService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author pujun
 * @since 2024-07-20
 */
@RestController
@RequestMapping("/team")
@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
public class TeamController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private TeamService teamService;

    @Resource
    private UserTeamService userTeamService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddDTO teamAddDTO, HttpServletRequest request){
        if(teamAddDTO == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Long teamId = teamService.addTeam(teamAddDTO, loginUser);
        return ResultUtils.success(teamId);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody TeamDeleteDTO teamDeleteDTO, HttpServletRequest request){
        long id = teamDeleteDTO.getId();
        if(id <= 0){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.removeTeam(id, loginUser);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
        }
        return ResultUtils.success(true);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateDTO teamUpdateDTO, HttpServletRequest request){
        if(teamUpdateDTO == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateDTO, loginUser);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(Long id){
        if(id <= 0){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        Team team = teamService.getById(id);
        if(team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR, "该队伍不存在");
        }
        return ResultUtils.success(team);
    }

//    @GetMapping("/list")
//    public BaseResponse<List<Team>> listTeams(TeamQueryDTO teamQueryDTO){//get请求路径中怎么加DTO？
//        if(teamQueryDTO == null){
//            throw new BusinessException(ErrorCode.PARAM_ERROR);
//        }
//
//        Team team = new Team();
//        BeanUtils.copyProperties(teamQueryDTO, team);
//        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>(team);
//        List<Team> teamList = teamService.list(teamQueryWrapper);
//
//        return ResultUtils.success(teamList);
//    }

    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(TeamQueryDTO teamQueryDTO, HttpServletRequest request){//get请求路径中怎么加DTO？
        if(teamQueryDTO == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        boolean isAdmin = userService.isAdmin(request);
        User loginUser = userService.getLoginUser(request);
        List<TeamUserVO> teamList = teamService.listTeams(teamQueryDTO, isAdmin, false);
        // 给每个自己已加入的队伍加上hasJoin
        List<TeamUserVO> teamList1 = teamService.addHasJoinField(teamList, loginUser);
        //计算每个队伍已加入人数
        List<TeamUserVO> finalTeamList = teamService.countTeamHasJoin(teamList1);

        return ResultUtils.success(finalTeamList);
    }

    /**
     * 获取我加入过的队伍 复用方法
     * @param teamQueryDTO
     * @param request
     * @return
     */
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVO>> listMyJoinTeams(TeamQueryDTO teamQueryDTO, HttpServletRequest request){
        if(teamQueryDTO == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        //找出自己加入的队伍
        Long userId = userService.getLoginUser(request).getId();
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        //取出不重复的队伍 id （保险起见 去重）
        Map<Long, List<UserTeam>> listMap = userTeamList.stream() //分组为 teamId、UserTeam的map
                .collect(Collectors.groupingBy(UserTeam::getTeamId)); //之前 easyExcel导入时使用过 理解过
        List<Long> idList = new ArrayList<>(listMap.keySet()); //取key（teamId）出来
        teamQueryDTO.setIdList(idList); //设置进去
        List<TeamUserVO> teamList = teamService.listTeams(teamQueryDTO, true, true);//复用方法

        List<TeamUserVO> teamList1 = teamService.addHasJoinField(teamList, userService.getLoginUser(request));
        List<TeamUserVO> finalTeamList = teamService.countTeamHasJoin(teamList1);
        return ResultUtils.success(finalTeamList);
    }

    /**
     * 获取我创建的队伍 复用方法
     * @param teamQueryDTO
     * @param request
     * @return
     */
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamUserVO>> listMyCreateTeams(TeamQueryDTO teamQueryDTO, HttpServletRequest request){
        if(teamQueryDTO == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        //找出自己创建的队伍
        Long userId = userService.getLoginUser(request).getId();
        teamQueryDTO.setUserId(userId);
        List<TeamUserVO> teamList = teamService.listTeams(teamQueryDTO, true, true);//复用方法
        List<TeamUserVO> finalTeamList = teamService.countTeamHasJoin(teamList);
        return ResultUtils.success(finalTeamList);
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinDTO teamJoinDTO, HttpServletRequest request){
        if(teamJoinDTO == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.joinTeam(teamJoinDTO, loginUser);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "加入队伍失败");
        }
        return ResultUtils.success(true);
    }

    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitDTO teamQuitDTO, HttpServletRequest request){
        if (teamQuitDTO == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.quitTeam(teamQuitDTO, loginUser);
        return ResultUtils.success(result);
    }

    @GetMapping("/list/page")
    //小技巧：把teamQueryDTO设为继承pageRequest 并开启EqualAndHashCode 可直接比较赋值到父类的页码和大小
    // 这样就可以用 teamQueryDTO 来查 带标签、页码
    public BaseResponse<Page<Team>> listTeamsByPage(@RequestBody TeamQueryDTO teamQueryDTO){
        if(teamQueryDTO == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        Page<Team> teamPage = new Page<>(teamQueryDTO.getPageNum(),teamQueryDTO.getPageSize());
        Team team = new Team();
        BeanUtils.copyProperties(teamQueryDTO, team);
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team); //直接放进去的缺点：不能做模糊查询
        Page<Team> teamListPage = teamService.page(teamPage, queryWrapper); //Service是用page mapper是用selectPage

        return ResultUtils.success(teamListPage);
    }

}

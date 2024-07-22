package com.pujun.teammate_backend.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pujun.teammate_backend.common.BaseResponse;
import com.pujun.teammate_backend.common.ErrorCode;
import com.pujun.teammate_backend.common.PageRequest;
import com.pujun.teammate_backend.common.ResultUtils;
import com.pujun.teammate_backend.entity.DTO.TeamAddDTO;
import com.pujun.teammate_backend.entity.DTO.TeamQueryDTO;
import com.pujun.teammate_backend.entity.Team;
import com.pujun.teammate_backend.entity.User;
import com.pujun.teammate_backend.entity.VO.TeamUserVO;
import com.pujun.teammate_backend.exception.BusinessException;
import com.pujun.teammate_backend.service.TeamService;
import com.pujun.teammate_backend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
@CrossOrigin(origins = {"http://211.159.150.239"}, allowCredentials = "true")
public class TeamController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private TeamService teamService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddDTO teamAddDTO, HttpServletRequest request){
        if(teamAddDTO == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Long teamId = teamService.addTeam(teamAddDTO, loginUser);
        return ResultUtils.success(teamId);
    }

    @DeleteMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestParam Long id){
        if(id <= 0){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        boolean result = teamService.removeById(id);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
        }
        return ResultUtils.success(true);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody Team team){
        if(team == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        boolean result = teamService.updateById(team);
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
        List<TeamUserVO> teamList = teamService.listTeams(teamQueryDTO, isAdmin);
        return ResultUtils.success(teamList);
    }

    @GetMapping("/list/page")
    //小技巧：把teamQueryDTO设为继承pageRequest 并开启EqualAndHashCode 可直接比较赋值到父类的页码和大小
    // 这样就可以用 teamQueryDTO 来查 带标签、页码
    public BaseResponse<Page<Team>> listTeamsByPage(TeamQueryDTO teamQueryDTO){
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

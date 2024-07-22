package com.pujun.teammate_backend.service;

import com.pujun.teammate_backend.entity.DTO.TeamAddDTO;
import com.pujun.teammate_backend.entity.DTO.TeamQueryDTO;
import com.pujun.teammate_backend.entity.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pujun.teammate_backend.entity.User;
import com.pujun.teammate_backend.entity.VO.TeamUserVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author pujun
 * @since 2024-07-20
 */
public interface TeamService extends IService<Team> {

    /**
     * 添加队伍
     * @param teamAddDTO
     * @return
     */
    Long addTeam(TeamAddDTO teamAddDTO, User loginUser);

    /**
     * 查询队伍列表
     * @param teamQueryDTO
     * @param isAdmin
     * @return
     */
    List<TeamUserVO> listTeams(TeamQueryDTO teamQueryDTO, boolean isAdmin);
}

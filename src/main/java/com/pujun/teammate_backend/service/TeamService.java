package com.pujun.teammate_backend.service;

import com.pujun.teammate_backend.entity.DTO.*;
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
     *
     * @param teamQueryDTO
     * @param isAdmin
     * @param isMy 是否为获得自己加入或创建的队伍
     * @return
     */
    List<TeamUserVO> listTeams(TeamQueryDTO teamQueryDTO, boolean isAdmin,boolean isMy);

    /**
     * 更新队伍
     * @param teamUpdateDTO
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateDTO teamUpdateDTO, User loginUser);

    /**
     * 加入队伍
     * @param teamJoinDTO
     * @param loginUser
     */
    boolean joinTeam(TeamJoinDTO teamJoinDTO, User loginUser);

    /**
     * 退出队伍
     * @param teamQuitDTO
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitDTO teamQuitDTO, User loginUser);

    /**
     * 删除队伍
     * @param id
     * @param loginUser
     * @return
     */
    boolean removeTeam(Long id, User loginUser);
}

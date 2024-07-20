package com.pujun.teammate_backend.service.impl;

import com.pujun.teammate_backend.entity.Team;
import com.pujun.teammate_backend.mapper.TeamMapper;
import com.pujun.teammate_backend.service.TeamService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author pujun
 * @since 2024-07-20
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {

}

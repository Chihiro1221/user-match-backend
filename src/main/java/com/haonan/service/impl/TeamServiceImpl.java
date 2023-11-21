package com.haonan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haonan.enums.TeamStatusEnum;
import com.haonan.exception.BusinessException;
import com.haonan.exception.ErrorCode;
import com.haonan.mapper.TeamMapper;
import com.haonan.mapper.TeamUserMapper;
import com.haonan.model.dto.TeamAddDto;
import com.haonan.model.entity.Team;
import com.haonan.model.entity.TeamUser;
import com.haonan.model.entity.User;
import com.haonan.service.TeamService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author haonan
 * @description 针对表【team(队伍表)】的数据库操作Service实现
 * @createDate 2023-11-21 16:49:55
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {
    @Resource
    private TeamMapper teamMapper;
    @Resource
    private TeamUserMapper teamUserMapper;

    @Override
    @Transactional
    public Long addTeam(TeamAddDto teamAddDto, User loginUser) {
        // 用户最多创建5个队伍
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
        teamQueryWrapper.eq("user_id", loginUser.getId());
        Long count = teamMapper.selectCount(teamQueryWrapper);
        if (count >= 5) {
            throw new BusinessException(ErrorCode.CLIENT_ERROR, "最多创建5个队伍");
        }
        // 如果设置的为加密类型，则必须要设置密码
        if (teamAddDto.getStatus() != null && teamAddDto.getStatus().equals(TeamStatusEnum.SECRET) && StringUtils.isBlank(teamAddDto.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "必须要设置密码");
        }
        // 队伍表中插入数据
        Team team = new Team();
        BeanUtils.copyProperties(teamAddDto, team);
        team.setUserId(loginUser.getId());
        int result = teamMapper.insert(team);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败");
        }
        // 队伍用户关联表中插入数据
        TeamUser teamUser = new TeamUser();
        teamUser.setUserId(loginUser.getId());
        teamUser.setTeamId(team.getUserId());
        teamUser.setJoinTime(new Date());
        result = teamUserMapper.insert(teamUser);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败");
        }
        return team.getId();
    }
}





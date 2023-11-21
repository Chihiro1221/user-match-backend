package com.haonan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haonan.model.dto.TeamAddDto;
import com.haonan.model.entity.Team;
import com.haonan.model.entity.User;

/**
* @author haonan
* @description 针对表【team(队伍表)】的数据库操作Service
* @createDate 2023-11-21 16:49:55
*/
public interface TeamService extends IService<Team> {

    /**
     * 添加队伍
     * @param teamAddDto
     * @param loginUser
     * @return
     */
    Long addTeam(TeamAddDto teamAddDto, User loginUser);
}

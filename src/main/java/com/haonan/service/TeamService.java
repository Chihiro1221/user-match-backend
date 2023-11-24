package com.haonan.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haonan.model.dto.TeamAddDto;
import com.haonan.model.dto.TeamJoinDto;
import com.haonan.model.dto.TeamPageDto;
import com.haonan.model.dto.TeamUpdateDto;
import com.haonan.model.entity.Team;
import com.haonan.model.entity.User;
import com.haonan.model.vo.PageVo;
import com.haonan.model.vo.TeamVO;

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

    /**
     * 条件动态查询
     *
     * @param teamPageDto
     * @param loginUser
     * @return
     */
    PageVo<TeamVO> pageQuery(TeamPageDto teamPageDto, User loginUser);

    /**
     * 修改队伍信息
     * @param teamUpdateDto
     * @param loginUser
     */
    void updateTeam(Long id, TeamUpdateDto teamUpdateDto, User loginUser);

    /**
     * 加入队伍
     * @param teamJoinDto
     * @param loginUser
     */
    void joinTeam(TeamJoinDto teamJoinDto, User loginUser);
}

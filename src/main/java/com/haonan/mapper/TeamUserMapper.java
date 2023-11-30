package com.haonan.mapper;

import com.haonan.model.entity.Team;
import com.haonan.model.entity.TeamUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author haonan
* @description 针对表【team_user(队伍用户关联表)】的数据库操作Mapper
* @createDate 2023-11-21 16:53:24
* @Entity com.haonan.model.entity.TeamUser
*/
public interface TeamUserMapper extends BaseMapper<TeamUser> {

    /**
     * 查询已加入的队伍信息
     * @param id
     * @return
     */
    List<Team> selectJointedTeam(Long id);
}





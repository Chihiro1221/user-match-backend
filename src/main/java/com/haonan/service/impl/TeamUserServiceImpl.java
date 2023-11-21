package com.haonan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haonan.model.entity.TeamUser;
import com.haonan.service.TeamUserService;
import com.haonan.mapper.TeamUserMapper;
import org.springframework.stereotype.Service;

/**
* @author haonan
* @description 针对表【team_user(队伍用户关联表)】的数据库操作Service实现
* @createDate 2023-11-21 16:53:24
*/
@Service
public class TeamUserServiceImpl extends ServiceImpl<TeamUserMapper, TeamUser>
    implements TeamUserService{

}





package com.haonan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haonan.constant.UserConstant;
import com.haonan.enums.TeamStatusEnum;
import com.haonan.exception.BusinessException;
import com.haonan.exception.ErrorCode;
import com.haonan.mapper.TeamMapper;
import com.haonan.mapper.TeamUserMapper;
import com.haonan.mapper.UserMapper;
import com.haonan.model.dto.TeamAddDto;
import com.haonan.model.dto.TeamJoinDto;
import com.haonan.model.dto.TeamPageDto;
import com.haonan.model.dto.TeamUpdateDto;
import com.haonan.model.entity.Team;
import com.haonan.model.entity.TeamUser;
import com.haonan.model.entity.User;
import com.haonan.model.vo.PageVo;
import com.haonan.model.vo.TeamVO;
import com.haonan.model.vo.UserVO;
import com.haonan.service.TeamService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    @Resource
    private UserMapper userMapper;

    /**
     * 添加队伍
     *
     * @param teamAddDto
     * @param loginUser
     * @return
     */
    @Override
    @Transactional(rollbackFor = BusinessException.class)
    public Long addTeam(TeamAddDto teamAddDto, User loginUser) {
        // 用户最多创建5个队伍
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
        teamQueryWrapper.eq("user_id", loginUser.getId());
        Long count = teamMapper.selectCount(teamQueryWrapper);
        if (count >= 5) {
            throw new BusinessException(ErrorCode.CLIENT_ERROR, "最多创建5个队伍");
        }
        // 如果设置的为加密类型，则必须要设置密码
        if (teamAddDto.getStatus() != null && teamAddDto.getStatus().equals(TeamStatusEnum.SECRET.getValue()) && StringUtils.isBlank(teamAddDto.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "必须要设置密码");
        }
        // 队伍表中插入数据
        Team team = new Team();
        BeanUtils.copyProperties(teamAddDto, team);
        team.setUserId(loginUser.getId());
        // Todo:可能同时添加100个用户，有bug
        int result = teamMapper.insert(team);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败");
        }
        // 队伍用户关联表中插入数据
        TeamUser teamUser = new TeamUser();
        teamUser.setUserId(loginUser.getId());
        teamUser.setTeamId(team.getId());
        teamUser.setJoinTime(new Date());
        result = teamUserMapper.insert(teamUser);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败");
        }
        return team.getId();
    }

    /**
     * 动态查询
     *
     * @param teamPageDto
     * @param loginUser
     * @return
     */
    @Override
    public PageVo<TeamVO> pageQuery(TeamPageDto teamPageDto, User loginUser) {
        // 动态查询
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
        // id不为空
        teamQueryWrapper.eq(teamPageDto.getId() != null, "id", teamPageDto.getId());
        // 过期时间大于当前时间
        teamQueryWrapper.gt("expire_time", new Date());
        // 根据队伍名或描述查询
        teamQueryWrapper.like(StringUtils.isNotBlank(teamPageDto.getName()), "name", teamPageDto.getName());
        teamQueryWrapper.like(StringUtils.isNotBlank(teamPageDto.getDescription()), "description", teamPageDto.getDescription());
        // 根据最大人数查询
        teamQueryWrapper.eq(teamPageDto.getMaxNum() != null, "max_num", teamPageDto.getMaxNum());
        // 根据创建人id查询
        teamQueryWrapper.eq(teamPageDto.getUserId() != null, "user_id", teamPageDto.getUserId());
        if (teamPageDto.getStatus() != null) {
            // 如果不是管理员并且查询的是私有队伍
            if (!UserConstant.ADMIN.equals(loginUser.getRole()) && TeamStatusEnum.PRIVATE.getValue().equals(teamPageDto.getStatus())) {
                throw new BusinessException(ErrorCode.NO_PERMISSION_ERROR);
            }
            teamQueryWrapper.eq("status", teamPageDto.getStatus());
        } else if (!UserConstant.ADMIN.equals(loginUser.getRole())) {
            // 如果没有设置并且不是管理员则不查询私有队伍
            teamQueryWrapper.ne("status", TeamStatusEnum.PRIVATE.getValue());
        }
        Page<Team> teamPage = new Page<>(teamPageDto.getPageNum(), teamPageDto.getPageSize());
        Page<Team> page = teamMapper.selectPage(teamPage, teamQueryWrapper);
        ArrayList<TeamVO> teamVOList = new ArrayList<>();
        PageVo<TeamVO> teamVOPageVo = new PageVo<>();
        BeanUtils.copyProperties(page, teamVOPageVo);
        // 关联查询出队伍创建人信息
        teamVOPageVo.setRecords(page.getRecords().stream().map(team -> {
            // 查询创建人信息
            Long createUserId = team.getUserId();
            User user = userMapper.selectById(createUserId);
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            // 封装TeamVO对象
            TeamVO teamVO = new TeamVO();
            BeanUtils.copyProperties(team, teamVO);
            teamVO.setCreateUser(userVO);
            return teamVO;
        }).collect(Collectors.toList()));
        return teamVOPageVo;
    }

    /**
     * 修改队伍信息
     *
     * @param teamUpdateDto
     * @param loginUser
     */
    @Override
    public void updateTeam(Long id, @Valid TeamUpdateDto teamUpdateDto, User loginUser) {
        Team team = teamMapper.selectById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User user = userMapper.selectById(team.getUserId());
        if (user == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        // 如果登录用户不是创建人也不是管理员则不允许修改
        if (!loginUser.getId().equals(user.getId()) && !UserConstant.ADMIN.equals(loginUser.getRole())) {
            throw new BusinessException(ErrorCode.NO_PERMISSION_ERROR);
        }
        // 如果要修改房间状态为加密，但是又不设置密码则不允许修改
        if (TeamStatusEnum.SECRET.getValue().equals(teamUpdateDto.getStatus()) && StringUtils.isBlank(teamUpdateDto.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请设置密码！");
        }
        Team updateTeamEntity = new Team();
        updateTeamEntity.setId(team.getId());
        BeanUtils.copyProperties(teamUpdateDto, updateTeamEntity);
        int result = teamMapper.updateById(updateTeamEntity);

        if (result < 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队伍失败");
        }
    }

    /**
     * 加入队伍
     *
     * @param teamJoinDto
     * @param loginUser
     */
    @Override
    public void joinTeam(TeamJoinDto teamJoinDto, User loginUser) {
        Team team = teamMapper.selectById(teamJoinDto.getTeamId());
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        // 最多加入5个队伍
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", loginUser.getId());
        Long count = teamUserMapper.selectCount(queryWrapper);
        if (count >= 5) {
            throw new BusinessException(ErrorCode.CLIENT_ERROR, "最多创建和加入5个队伍");
        }
        // 不能加入相同的队伍
        queryWrapper.eq("team_id", teamJoinDto.getTeamId());
        count = teamUserMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.CLIENT_ERROR, "不能加入已加入的队伍");
        }
        // 不能加入已过期的队伍
        if (team.getExpireTime().before(new Date())) {
            throw new BusinessException(ErrorCode.CLIENT_ERROR, "队伍已过期");
        }
        // 私人队伍不能加入
        if (TeamStatusEnum.PRIVATE.getValue().equals(team.getStatus())) {
            throw new BusinessException(ErrorCode.CLIENT_ERROR, "不能加入私人队伍");
        }
        // 要加入加密队伍，但是密码不正确则不允许加入
        if (TeamStatusEnum.SECRET.getValue().equals(team.getStatus()) && !team.getPassword().equals(teamJoinDto.getPassword())) {
            throw new BusinessException(ErrorCode.CLIENT_ERROR, "密码错误");
        }
        TeamUser teamUser = new TeamUser();
        teamUser.setUserId(loginUser.getId());
        teamUser.setTeamId(team.getId());
        teamUser.setJoinTime(new Date());
        int insert = teamUserMapper.insert(teamUser);
        if (insert < 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "加入队伍失败");
        }
    }

}





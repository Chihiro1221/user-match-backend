package com.haonan.service.impl;

import ch.qos.logback.core.joran.spi.NoAutoStart;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haonan.constant.UserConstant;
import com.haonan.context.BaseContext;
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
        teamQueryWrapper.orderByDesc("create_time");
        Page<Team> teamPage = new Page<>(teamPageDto.getPageNum(), teamPageDto.getPageSize());
        Page<Team> page = teamMapper.selectPage(teamPage, teamQueryWrapper);
        ArrayList<TeamVO> teamVOList = new ArrayList<>();
        PageVo<TeamVO> teamVOPageVo = new PageVo<>();
        BeanUtils.copyProperties(page, teamVOPageVo);
        // 关联查询出队伍创建人信息
        teamVOPageVo.setRecords(page.getRecords().stream().map(team -> {
            // 关联查询出所有的加入队伍的用户id
            QueryWrapper<TeamUser> teamUserQueryWrapper = new QueryWrapper<>();
            teamUserQueryWrapper.select("user_id");
            teamUserQueryWrapper.eq("team_id", team.getId());
            List<TeamUser> teamUsers = teamUserMapper.selectList(teamUserQueryWrapper);
            // 查询创建人信息
            Long createUserId = team.getUserId();
            User user = userMapper.selectById(createUserId);
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            // 封装TeamVO对象
            TeamVO teamVO = new TeamVO();
            BeanUtils.copyProperties(team, teamVO);
            teamVO.setCreateUser(userVO);
            teamVO.setUsers(teamUsers.stream().map(TeamUser::getUserId).toList());
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
    public synchronized void joinTeam(TeamJoinDto teamJoinDto, User loginUser) {
        Team team = teamMapper.selectById(teamJoinDto.getTeamId());
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        // 不能加入已满的队伍
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("team_id", teamJoinDto.getTeamId());
        Long count = teamUserMapper.selectCount(queryWrapper);
        if (count >= team.getMaxNum()) {
            throw new BusinessException(ErrorCode.CLIENT_ERROR, "队伍已满");
        }
        // 不能加入相同的队伍
        queryWrapper.eq("user_id", loginUser.getId());
        count = teamUserMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.CLIENT_ERROR, "不能重复加入");
        }
        // 最多加入5个队伍
        queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", loginUser.getId());
        count = teamUserMapper.selectCount(queryWrapper);
        if (count >= 5) {
            throw new BusinessException(ErrorCode.CLIENT_ERROR, "最多创建和加入5个队伍");
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

    /**
     * 退出队伍
     *
     * @param teamId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void quitTeam(Long teamId) {
        User currentUser = BaseContext.getCurrentUser();
        Long userId = currentUser.getId();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("team_id", teamId);
        queryWrapper.eq("user_id", userId);
        // 没有加入队伍则不支持退出
        Long count = teamUserMapper.selectCount(queryWrapper);
        if (count < 1) {
            throw new BusinessException(ErrorCode.CLIENT_ERROR, "尚未加入该队伍");
        }
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        // 不能退出已过期的队伍
        if (team.getExpireTime().before(new Date())) {
            throw new BusinessException(ErrorCode.CLIENT_ERROR, "队伍已过期");
        }
        queryWrapper = new QueryWrapper();
        queryWrapper.eq("team_id", teamId);
        // 根据id升序排序，越早加入队伍的id越小，只要2条数据（1原队长2新队长）
        queryWrapper.last("order by id asc limit 2");
        List<TeamUser> teamUserList = teamUserMapper.selectList(queryWrapper);
        // 如果队伍只剩下一人（当前登录用户），则直接解散
        if (teamUserList.size() == 1) {
            teamMapper.deleteById(teamId);
            teamUserMapper.delete(queryWrapper);
        } else {
            // 如果是队长退出则将队长职位转交给第二加入的用户
            if (team.getUserId().equals(currentUser.getId())) {
                TeamUser nextTeamUser = teamUserList.get(1);
                team.setUserId(nextTeamUser.getUserId());
                // 将队伍的队长更新为下一个用户
                int result = teamMapper.updateById(team);
                if (result < 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队长失败");
                }
                // 将原先队长与队伍的关系删除
                teamUserMapper.deleteById(teamUserList.get(0).getId());
            } else {
                // 不是队长直接删除关系即可
                queryWrapper = new QueryWrapper();
                queryWrapper.eq("user_id", userId);
                teamUserMapper.delete(queryWrapper);
            }
        }
    }

    /**
     * 解散队伍
     *
     * @param teamId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTeam(Long teamId) {
        Team team = teamMapper.selectById(teamId);
        User currentUser = BaseContext.getCurrentUser();
        Long userId = currentUser.getId();
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        // 如果不是队长不能解散队伍
        if (!userId.equals(team.getUserId())) {
            throw new BusinessException(ErrorCode.NO_PERMISSION_ERROR);
        }
        // 过期队伍不支持解散
        if (team.getExpireTime().before(new Date())) {
            throw new BusinessException(ErrorCode.CLIENT_ERROR, "队伍已过期");
        }
        // 删除队伍
        int result = teamMapper.deleteById(teamId);
        if (result < 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "解散队伍失败");
        }
        // 删除所有用户队伍关系
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("team_id", teamId);
        result = teamUserMapper.delete(queryWrapper);
        if (result < 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "解散队伍失败");
        }
    }

    /**
     * 获取自己创建的队伍
     *
     * @return
     */
    @Override
    public List<TeamVO> getCreatedTeam() {
        User currentUser = BaseContext.getCurrentUser();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", currentUser.getId());
        queryWrapper.orderByDesc("expire_time");
        List<Team> teamList = teamMapper.selectList(queryWrapper);
        ArrayList<TeamVO> teamVOList = new ArrayList<>();
        for (Team team : teamList) {
            QueryWrapper<TeamUser> teamUserQueryWrapper = new QueryWrapper<>();
            teamUserQueryWrapper.select("user_id");
            teamUserQueryWrapper.eq("team_id", team.getId());
            List<TeamUser> teamUsers = teamUserMapper.selectList(teamUserQueryWrapper);
            TeamVO teamVO = new TeamVO();
            BeanUtils.copyProperties(team, teamVO);
            teamVO.setUsers(teamUsers.stream().map(TeamUser::getUserId).toList());
            teamVOList.add(teamVO);
        }
        return teamVOList;
    }

    /**
     * 获取已加入的队伍
     *
     * @return
     */
    @Override
    public List<TeamVO> getJointedTeam() {
        User currentUser = BaseContext.getCurrentUser();
        List<Team> jointedTeam = teamUserMapper.selectJointedTeam(currentUser.getId());
        ArrayList<TeamVO> teamVOList = new ArrayList<>();
        for (Team team : jointedTeam) {
            // 关联查询出所有的加入队伍的用户id
            QueryWrapper<TeamUser> teamUserQueryWrapper = new QueryWrapper<>();
            teamUserQueryWrapper.select("user_id");
            teamUserQueryWrapper.eq("team_id", team.getId());
            List<TeamUser> teamUsers = teamUserMapper.selectList(teamUserQueryWrapper);
            TeamVO teamVO = new TeamVO();
            BeanUtils.copyProperties(team, teamVO);
            teamVO.setUsers(teamUsers.stream().map(TeamUser::getUserId).toList());
            teamVOList.add(teamVO);
        }
        return teamVOList;
    }

}





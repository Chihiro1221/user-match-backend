package com.haonan.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haonan.common.BaseResponse;
import com.haonan.constant.UserConstant;
import com.haonan.exception.BusinessException;
import com.haonan.exception.ErrorCode;
import com.haonan.model.dto.*;
import com.haonan.model.entity.Team;
import com.haonan.model.entity.TeamUser;
import com.haonan.model.entity.User;
import com.haonan.model.vo.PageVo;
import com.haonan.model.vo.TeamVO;
import com.haonan.service.TeamService;
import com.haonan.service.TeamUserService;
import com.haonan.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/team")
@Slf4j
public class TeamController {
    @Resource
    private UserService userService;
    @Resource
    private TeamService teamService;
    @Resource
    private TeamUserService teamUserService;

    /**
     * 添加队伍
     * @param teamAddDto
     * @param request
     * @return
     */
    @PostMapping
    public BaseResponse addTeam(@RequestBody @NotNull @Valid TeamAddDto teamAddDto, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long teamId = teamService.addTeam(teamAddDto, loginUser);
        return BaseResponse.success(teamId);
    }


    /**
     * 队伍动态分页查询
     * @param teamPageDto
     * @param request
     * @return
     */
    @GetMapping
    public BaseResponse<PageVo<TeamVO>> page(@Valid TeamPageDto teamPageDto, HttpServletRequest request) {
        PageVo<TeamVO> page = teamService.pageQuery(teamPageDto, userService.getLoginUser(request));
        return BaseResponse.success(page);
    }

    /**
     * 查询自己创建的队伍
     * @return
     */
    @GetMapping("/created")
    public BaseResponse<List<TeamVO>> getCreatedTeam() {
        List<TeamVO> teamVOList = teamService.getCreatedTeam();
        return BaseResponse.success(teamVOList);
    }

    /**
     * 查询已加入的队伍
     * @return
     */
    @GetMapping("/jointed")
    public BaseResponse<List<TeamVO>> getJointedTeam() {
        List<TeamVO> teamVOList = teamService.getJointedTeam();
        return BaseResponse.success(teamVOList);
    }

    /**
     * 加入队伍
     * @param teamJoinDto
     * @param request
     * @return
     */
    @PostMapping("/join")
    public BaseResponse joinTeam(@RequestBody @Valid TeamJoinDto teamJoinDto, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        teamService.joinTeam(teamJoinDto, loginUser);

        return BaseResponse.success();
    }

    /**
     * 退出队伍
     * @param teamId
     * @return
     */
    @PostMapping("/quit/{teamId}")
    public BaseResponse quitTeam(@PathVariable @NotNull @Min(1) Long teamId) {
        teamService.quitTeam(teamId);
        return BaseResponse.success();
    }

    /**
     * 解散队伍
     * @param teamId
     * @return
     */
    @DeleteMapping("/{teamId}")
    public BaseResponse deleteTeam(@PathVariable @NotNull @Min(1) Long teamId) {
        teamService.deleteTeam(teamId);
        return BaseResponse.success();
    }

    /**
     * 更新队伍信息
     * @param id
     * @param teamUpdateDto
     * @param request
     * @return
     */
    @PutMapping("/{id}")
    public BaseResponse updateTeam(@PathVariable @NotNull Long id, @RequestBody @Valid @NotNull TeamUpdateDto teamUpdateDto, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        teamService.updateTeam(id, teamUpdateDto, loginUser);
        return BaseResponse.success();
    }

    @GetMapping("/{id}")
    public BaseResponse<TeamVO> getTeamById(@PathVariable @NotNull Long id) {
        Team team = teamService.getById(id);
        QueryWrapper<TeamUser> teamUserQueryWrapper = new QueryWrapper<>();
        teamUserQueryWrapper.select("user_id");
        teamUserQueryWrapper.eq("team_id", team.getId());
        List<TeamUser> teamUsers = teamUserService.list(teamUserQueryWrapper);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        TeamVO teamVO = new TeamVO();
        BeanUtils.copyProperties(team, teamVO);
        teamVO.setUsers(teamUsers.stream().map(TeamUser::getUserId).toList());
        return BaseResponse.success(teamVO);
    }
}


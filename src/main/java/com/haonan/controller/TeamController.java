package com.haonan.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haonan.common.BaseResponse;
import com.haonan.constant.UserConstant;
import com.haonan.exception.BusinessException;
import com.haonan.exception.ErrorCode;
import com.haonan.model.dto.*;
import com.haonan.model.entity.Team;
import com.haonan.model.entity.User;
import com.haonan.service.TeamService;
import com.haonan.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/team")
@Slf4j
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class TeamController {
    @Resource
    private UserService userService;
    @Resource
    private TeamService teamService;

    @PostMapping
    public BaseResponse addTeam(@RequestBody @NotNull @Valid TeamAddDto teamAddDto, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long teamId = teamService.addTeam(teamAddDto, loginUser);
        return BaseResponse.success(teamId);
    }

    @DeleteMapping("/{id}")
    public BaseResponse deleteTeam(@PathVariable @NotNull Long id, HttpServletRequest request) {
        Team team = teamService.getById(id);
        User loginUser = userService.getLoginUser(request);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        // 只有创建人或者管理员才能删除队伍
        if (!team.getUserId().equals(loginUser.getId()) || !loginUser.getRole().equals(UserConstant.ADMIN)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION_ERROR);
        }

        boolean result = teamService.removeById(id);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return BaseResponse.success();
    }

    @PutMapping("/{id}")
    public BaseResponse updateTeam(@PathVariable @NotNull Long id, @RequestBody @Valid @NotNull TeamUpdateDto teamUpdateDto) {
        Team team = new Team();
        BeanUtils.copyProperties(teamUpdateDto, team);
        boolean result = teamService.updateById(team);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        return BaseResponse.success();
    }

    @GetMapping("/{id}")
    public BaseResponse<Team> getTeamById(@PathVariable @NotNull Long id) {
        Team team = teamService.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return BaseResponse.success(team);
    }

    @GetMapping
    public BaseResponse<Page<Team>> page(TeamPageDto teamPageDto) {
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
        Page<Team> teamPage = new Page<>(teamPageDto.getPageNum(), teamPageDto.getPageSize());
        Page<Team> page = teamService.page(teamPage, teamQueryWrapper);
        return BaseResponse.success(page);
    }
}


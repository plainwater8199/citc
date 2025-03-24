package com.citc.nce.authcenter.user.service;

import com.citc.nce.authcenter.user.entity.UserViolationDo;

import java.util.List;

public interface UserViolationService {
    /**
     * 根据相关信息查询用户的违规信息
     * @param userId 用户id
     * @param plate 平台
     * @param violationType 违规类型默认-0
     * @return
     */
    List<UserViolationDo> selectListByInfo(String userId, Integer plate, Integer violationType);
}

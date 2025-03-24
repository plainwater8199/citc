package com.citc.nce.aim.privatenumber.service;

import com.citc.nce.aim.privatenumber.entity.PrivateNumberProjectDo;
import com.citc.nce.aim.privatenumber.vo.*;
import com.citc.nce.aim.privatenumber.vo.req.PrivateNumberProjectTestReq;
import com.citc.nce.aim.privatenumber.vo.resp.PrivateNumberProjectTestResp;
import com.citc.nce.aim.vo.*;
import com.citc.nce.aim.vo.req.AimProjectTestReq;
import com.citc.nce.aim.vo.resp.AimProjectTestResp;
import com.citc.nce.common.core.pojo.PageResult;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 18:00
 */
public interface PrivateNumberProjectService {

    int save(PrivateNumberProjectSaveReq req);

    int edit(PrivateNumberProjectEditReq req);

    int updateStatus(PrivateNumberProjectUpdateStatusReq req);

    PrivateNumberProjectResp queryProject(PrivateNumberProjectQueryReq req);

    PageResult<PrivateNumberProjectResp> queryProjectList(PrivateNumberProjectQueryListReq req);

    PrivateNumberProjectTestResp privateNumberProjectTest(PrivateNumberProjectTestReq req);

    PrivateNumberProjectDo queryValidProject(String appKey);

    PrivateNumberProjectDo queryByProjectId(String projectId);
}

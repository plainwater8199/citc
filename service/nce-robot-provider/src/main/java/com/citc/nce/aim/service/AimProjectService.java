package com.citc.nce.aim.service;

import com.citc.nce.aim.vo.AimProjectEditReq;
import com.citc.nce.aim.vo.AimProjectQueryListReq;
import com.citc.nce.aim.vo.AimProjectQueryReq;
import com.citc.nce.aim.vo.AimProjectResp;
import com.citc.nce.aim.vo.AimProjectSaveReq;
import com.citc.nce.aim.vo.AimProjectUpdateStatusReq;
import com.citc.nce.aim.vo.req.AimProjectTestReq;
import com.citc.nce.aim.vo.resp.AimProjectTestResp;
import com.citc.nce.common.core.pojo.PageResult;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 18:00
 */
public interface AimProjectService {

    int save(AimProjectSaveReq req);

    int edit(AimProjectEditReq req);

    AimProjectResp queryProject(AimProjectQueryReq req);

    PageResult<AimProjectResp> queryProjectList(AimProjectQueryListReq req);

    int updateStatus(AimProjectUpdateStatusReq req);

    AimProjectTestResp aimProjectTest(AimProjectTestReq req);
}

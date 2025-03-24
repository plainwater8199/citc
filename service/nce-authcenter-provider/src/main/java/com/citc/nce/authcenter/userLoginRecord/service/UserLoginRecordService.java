package com.citc.nce.authcenter.userLoginRecord.service;

import com.citc.nce.authcenter.userLoginRecord.vo.FindUserReq;
import com.citc.nce.authcenter.userLoginRecord.vo.FindUserResp;
import com.citc.nce.authcenter.userLoginRecord.vo.QueryUserLoginRecordReq;
import com.citc.nce.authcenter.userLoginRecord.vo.QueryUserLoginRecordResp;
import com.citc.nce.common.core.pojo.PageResult;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 14:32
 */
public interface UserLoginRecordService {

    PageResult<QueryUserLoginRecordResp> queryList(QueryUserLoginRecordReq req);

    /**
     * 根据用户查询最新登陆信息
     * @param req
     * @return
     */
    FindUserResp findUser(FindUserReq req);
}

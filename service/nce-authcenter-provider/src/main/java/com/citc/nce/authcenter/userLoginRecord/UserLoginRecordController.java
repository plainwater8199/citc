package com.citc.nce.authcenter.userLoginRecord;

import com.citc.nce.authcenter.userLoginRecord.service.UserLoginRecordService;
import com.citc.nce.authcenter.userLoginRecord.vo.FindUserReq;
import com.citc.nce.authcenter.userLoginRecord.vo.FindUserResp;
import com.citc.nce.authcenter.userLoginRecord.vo.QueryUserLoginRecordReq;
import com.citc.nce.authcenter.userLoginRecord.vo.QueryUserLoginRecordResp;
import com.citc.nce.common.core.pojo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserLoginRecordController implements UserLoginRecordApi {

    @Autowired
    UserLoginRecordService service;

    @Override
    public PageResult<QueryUserLoginRecordResp> queryList(QueryUserLoginRecordReq req) {
        return service.queryList(req);
    }

    @Override
    public FindUserResp findUser(FindUserReq req) {
        return service.findUser(req);
    }


}

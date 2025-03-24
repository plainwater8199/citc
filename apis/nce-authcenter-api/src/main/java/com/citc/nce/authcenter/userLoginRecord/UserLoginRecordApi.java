package com.citc.nce.authcenter.userLoginRecord;

import com.citc.nce.authcenter.userLoginRecord.vo.FindUserReq;
import com.citc.nce.authcenter.userLoginRecord.vo.FindUserResp;
import com.citc.nce.authcenter.userLoginRecord.vo.QueryUserLoginRecordReq;
import com.citc.nce.authcenter.userLoginRecord.vo.QueryUserLoginRecordResp;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.Api;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/27 16:28
 */
@Api(tags = "用户中心--用户登录信息")
@FeignClient(value = "authcenter-service", contextId = "UserLoginRecordApi", url = "${authCenter:}")
public interface UserLoginRecordApi {

    /**
     * 获取用户登录信息
     * 传入pageSize = -1 时查全部数据
     * @param req
     * @return list
     */
    @PostMapping("/queryUserLoginRecord")
    PageResult<QueryUserLoginRecordResp> queryList(@RequestBody QueryUserLoginRecordReq req);


    /**
     * 根据用户查询最新登陆信息
     * @param req
     * @return resp
     */
    @PostMapping("/findUserLoginRecord")
    FindUserResp findUser(@RequestBody FindUserReq req);
}

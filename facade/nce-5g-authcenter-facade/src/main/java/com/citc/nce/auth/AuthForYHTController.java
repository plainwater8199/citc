package com.citc.nce.auth;


import com.citc.nce.authcenter.auth.AdminAuthApi;
import com.citc.nce.authcenter.auth.vo.req.ClientUserDetailsReq;
import com.citc.nce.authcenter.auth.vo.req.QueryCommunityUserBaseInfoListReq;
import com.citc.nce.authcenter.auth.vo.resp.ClientUserDetailsResp;
import com.citc.nce.authcenter.auth.vo.resp.QueryCommunityUserBaseInfoListResp;
import com.citc.nce.authcenter.identification.IdentificationApi;
import com.citc.nce.authcenter.identification.vo.req.GetClientUserIdentificationReq;
import com.citc.nce.authcenter.identification.vo.resp.GetClientUserIdentificationResp;
import com.citc.nce.common.util.SessionContextUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;


@Api(tags = "硬核桃-认证管理")
@RestController
public class AuthForYHTController {

    @Resource
    private AdminAuthApi adminAuthApi;

    @Resource
    private IdentificationApi identificationApi;



    @ApiOperation("社区用户列表查询列表")
    @PostMapping("/user/queryCommunityUserBaseInfoList")
    QueryCommunityUserBaseInfoListResp queryCommunityUserBaseInfoList(@RequestBody @Valid QueryCommunityUserBaseInfoListReq req) {
        return adminAuthApi.queryCommunityUserBaseInfoList(req);
    }


    /**
     * 管理端--获取客户端用户认证信息
     */
    @ApiOperation("获取客户端用户认证信息")
    @PostMapping("/user/getClientUserIdentifications")
    public GetClientUserIdentificationResp getClientUserIdentifications() {
        GetClientUserIdentificationReq req = new GetClientUserIdentificationReq();
        req.setUserId(SessionContextUtil.getUser().getUserId());
        return identificationApi.getClientUserIdentifications(req);
    }


    /**
     * 管理端 获取客户端用户详情
     * @return 用户信息
     */
    @ApiOperation("管理端-获取客户端用户详情")
    @PostMapping("/user/getClientUserDetails")
    public ClientUserDetailsResp getClientUserDetails() {
        ClientUserDetailsReq req = new ClientUserDetailsReq();
        req.setUserId(SessionContextUtil.getUser().getUserId());
        return adminAuthApi.getClientUserDetails(req);
    }

}

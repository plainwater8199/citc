package com.citc.nce.systemmsg;

import com.citc.nce.authcenter.systemmsg.UserSysMsgApi;
import com.citc.nce.authcenter.systemmsg.vo.req.QueryMsgDetailsReq;
import com.citc.nce.authcenter.systemmsg.vo.req.QuerySysMsgReq;
import com.citc.nce.authcenter.systemmsg.vo.req.UnreadSysMsgQueryReq;
import com.citc.nce.authcenter.systemmsg.vo.resp.QueryMsgDetailsResp;
import com.citc.nce.authcenter.systemmsg.vo.resp.UnreadSysMsgQueryResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@Api(tags = "用户端--站内信模块")
@RestController
@RequestMapping("/user/sysMsg")
public class UserSysMsgController {

    @Resource
    private UserSysMsgApi userSysMsgApi;

    /**
     * 条件查询用户对应的信息list
     * @param req 请求消息
     * @return 响应消息
     */
    @ApiOperation("用户--站内信列表(根据标题模糊查询)")
    @PostMapping("/querySysMsgList")
    public Object querySysMsgList(@RequestBody @Valid QuerySysMsgReq req) {
        return userSysMsgApi.querySysMsgList(req);

    }

    /**
     * 条件查询用户对应的信息
     * @param req 请求消息
     * @return 响应消息
     */
    @ApiOperation("用户--站内信详情查询")
    @PostMapping("/querySysMsgDetails")
    public QueryMsgDetailsResp querySysMsgDetails(@RequestBody @Valid QueryMsgDetailsReq req) {
        return userSysMsgApi.querySysMsgDetails(req);
    }

    /**
     * 未读站内信查询
     * @return 响应消息
     */
    @ApiOperation("用户--未读站内信查询")
    @PostMapping("/unreadSysMsgQuery")
    public UnreadSysMsgQueryResp unreadSysMsgQuery() {
        return userSysMsgApi.unreadSysMsgQuery();
    }

}

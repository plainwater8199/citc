package com.citc.nce.auth.usermessage;

import com.citc.nce.auth.usermessage.service.IUserMsgDetailService;
import com.citc.nce.auth.usermessage.vo.req.*;
import com.citc.nce.auth.usermessage.vo.resp.ReadResp;
import com.citc.nce.auth.usermessage.vo.resp.SendSystemMessageResp;
import com.citc.nce.auth.usermessage.vo.resp.UserMsgDetailResp;
import com.citc.nce.auth.usermessage.vo.resp.UserMsgResp;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年7月15日09:58:41
 * @Version: 1.0
 * @Description:
 */
@RestController
public class UserMsgController implements UserMsgApi {

    @Resource
    private IUserMsgDetailService service;

    @Override
    @PostMapping("/user/msg/save")
    public void saveMsg(@RequestBody @Valid UserMsgReq req) {
        service.saveMsg(req);
    }

    @Override
    @PostMapping("/user/msg/batchSaveMsg")
    public void batchSaveMsg(@RequestBody @Valid List<UserMsgReq> req) {
        service.batchSaveMsg(req);
    }

    @Override
    @PostMapping("/user/msg/selectByPage")
    public PageResult<UserMsgResp> selectByPage(@RequestBody @Valid PageReq req) {
        return service.selectByPage(req);
    }

    @Override
    @PostMapping("/user/msg/selectById")
    public UserMsgDetailResp selectById(@RequestBody @Valid IdReq req) {
        return service.selectById(req);
    }

    @PostMapping("/user/msg/read")
    @Override
    public ReadResp readOrUnread(@RequestBody @Valid UserIdReq req) {
        return service.readOrUnread(req);
    }

    @ApiOperation("生成系统消息")
    @PostMapping("/user/msg/sendSystemMessage")
    @Override
    public SendSystemMessageResp sendSystemMessage(@RequestBody @Valid SendSystemMessageReq sendSystemMessageReq) {
        return service.sendSystemMessage(sendSystemMessageReq);
    }

    @PostMapping("/user/msg/addMsgIntoUser")
    @Override
    public void addMsgIntoUser(@RequestBody @Valid MsgReq req) {
        service.addMsgIntoUser(req);
    }
}

package com.citc.nce.auth.usermessage;

import com.citc.nce.auth.usermessage.vo.req.*;
import com.citc.nce.auth.usermessage.vo.resp.*;
import com.citc.nce.auth.usermessage.vo.resp.ReadResp;
import com.citc.nce.auth.usermessage.vo.resp.UserMsgDetailResp;
import com.citc.nce.auth.usermessage.vo.resp.UserMsgResp;
import com.citc.nce.common.core.pojo.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年7月15日09:59:35
 * @Version: 1.0
 * @Description:
 */
@FeignClient(value = "auth-service", contextId = "UserMsgApi", url = "${auth:}")
public interface UserMsgApi {

    /**
     * 保存用户消息
     *
     * @param req
     */
    @PostMapping("/user/msg/save")
    void saveMsg(@RequestBody UserMsgReq req);

    /**
     * 批量保存用户消息
     *
     * @param req
     */
    @PostMapping("/user/msg/batchSaveMsg")
    void batchSaveMsg(@RequestBody List<UserMsgReq> req);

    /**
     * 分页查询用户消息
     *
     * @param req
     * @return {@link PageResult}<{@link UserMsgResp}>
     */
    @PostMapping("/user/msg/selectByPage")
    PageResult<UserMsgResp> selectByPage(@RequestBody PageReq req);

    /**
     * 查看单个消息详情
     *
     * @param req
     * @return {@link UserMsgDetailResp}
     */
    @PostMapping("/user/msg/selectById")
    UserMsgDetailResp selectById(@RequestBody IdReq req);

    /**
     * 查看单个消息详情
     *
     * @param req
     * @return {@link ReadResp}
     */
    @PostMapping("/user/msg/read")
    ReadResp readOrUnread(@RequestBody @Valid UserIdReq req);

    @PostMapping("/user/msg/sendSystemMessage")
    SendSystemMessageResp sendSystemMessage(@RequestBody @Valid SendSystemMessageReq sendSystemMessageReq);


    /**
     * 新增用户发送系统消息
     *
     * @param req
     */
    @PostMapping("/user/msg/addMsgIntoUser")
    void addMsgIntoUser(@RequestBody @Valid MsgReq req);

}

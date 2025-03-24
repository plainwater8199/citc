package com.citc.nce.auth.usermessage.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.usermessage.entity.UserMsgDetailDo;
import com.citc.nce.auth.usermessage.vo.req.*;
import com.citc.nce.auth.usermessage.vo.resp.ReadResp;
import com.citc.nce.auth.usermessage.vo.resp.SendSystemMessageResp;
import com.citc.nce.auth.usermessage.vo.resp.UserMsgDetailResp;
import com.citc.nce.auth.usermessage.vo.resp.UserMsgResp;
import com.citc.nce.common.core.pojo.PageResult;

import java.util.List;


/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年7月15日09:59:21
 * @Version: 1.0
 * @Description:
 */
public interface IUserMsgDetailService extends IService<UserMsgDetailDo> {

    void saveMsg(UserMsgReq req);

    void batchSaveMsg(List<UserMsgReq> req);

    PageResult<UserMsgResp> selectByPage(PageReq req);

    UserMsgDetailResp selectById(IdReq req);

    ReadResp readOrUnread(UserIdReq req);

    SendSystemMessageResp sendSystemMessage(SendSystemMessageReq sendSystemMessageReq);

    void addMsgIntoUser(MsgReq req);

}

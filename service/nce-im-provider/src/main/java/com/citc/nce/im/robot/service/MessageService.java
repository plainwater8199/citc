package com.citc.nce.im.robot.service;

import com.citc.nce.im.robot.dto.message.MsgDto;
import com.citc.nce.robot.vo.UpMsgReq;
import org.springframework.stereotype.Service;

/**
 * 消息处理
 */
@Service
public interface MessageService {

    /**
     * 消息解析，将调试窗口或者网关发过来的消息进行统一的解析
     * @param upMsgReq 上行消息
     * @return 消息解析结果
     */
    MsgDto messageParse(UpMsgReq upMsgReq);
}

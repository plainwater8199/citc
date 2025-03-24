package com.citc.nce.im.session;

import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.im.robot.dto.message.MsgDto;
import com.citc.nce.im.robot.service.ConversationService;
import com.citc.nce.im.robot.service.MessageService;
import com.citc.nce.im.robot.service.RobotService;
import com.citc.nce.im.session.processor.SessionProcessorService;
import com.citc.nce.robot.api.RobotProcessorApi;
import com.citc.nce.robot.common.ResponsePriority;
import com.citc.nce.robot.vo.NodeActResult;
import com.citc.nce.robot.vo.UpMsgReq;
import com.citc.nce.robot.vo.UpMsgResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/4 19:18
 * @Version: 1.0
 * @Description: 会话流程处理
 */
@RestController
@RequestMapping("/im")
@Slf4j
public class SessionProcessController implements RobotProcessorApi {

    @Resource
    SessionProcessorService sessionProcessorService;
    @Resource
    private MessageService messageService;

    @Autowired(required = false)
    private ConversationService conversationService;

    @Resource
    private RobotService robotService;

    /**
     * 上行消息处理
     *
     * @param upMsgReq
     */

    @PostMapping("/robot/receiveMsg")
    public NodeActResult receiveMsg(@RequestBody UpMsgReq upMsgReq) {
        // 手机触发时没有登录，会空指针
        //1、解析消息
        // 如果入口是网关, 查找该用户(phone)与chatbot是否已经存在会话,有的话,重新设置该conversationId,没有的话表明是新会话
        MsgDto msgDto = messageService.messageParse(upMsgReq);

        //根据回复优先级去判断是否发送成功，如果发送成功则不再发送默认回复
        boolean replyByResponsePriority = robotService.replyByResponsePriority(msgDto, null);
        if(!replyByResponsePriority){
            //2、根据消息获取会话id，加载会话阶段需要的会话资源(变量和指令)
            conversationService.loadResources(msgDto);
            //3、机器人初始化
            log.info("初始化机器人:{}", msgDto);
            boolean isEnd = robotService.processInitialization(msgDto);
            if (isEnd) {
                log.warn("跳过机器人执行");
                return null;
            }
            //4、机器人执行
            robotService.exec(msgDto);

        }
        return null;
    }

    /**
     * 关闭会话
     *
     * @param conversationId
     */
    @PostMapping("/closeConversation")
    public boolean close(String conversationId) {
        return sessionProcessorService.closeConversation(conversationId);
    }

    /**
     * 发送消息处理
     *
     * @param param
     */
    @PostMapping("/robot/sendMsg")
    public UpMsgResp sendMsg(@RequestBody String param) {
        return sessionProcessorService.sendMsg(param);
    }

    @PostMapping("/robot/sendGatewayMsg")
    public UpMsgResp sendGatewayMsg(@RequestBody String param) {
        return sessionProcessorService.sendGatewayMsg(param);
    }

    /**
     * java消息处理
     *
     * @param upMsgReq
     */
    @PostMapping("/robot/javaSendMsg")
    public void javaSendMsg(@RequestBody UpMsgReq upMsgReq) {
        sessionProcessorService.javaSendMsg(upMsgReq);
    }
}

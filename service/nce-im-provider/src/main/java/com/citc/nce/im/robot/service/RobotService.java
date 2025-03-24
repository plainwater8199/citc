package com.citc.nce.im.robot.service;

import com.citc.nce.im.robot.dto.message.MsgDto;
import com.citc.nce.im.session.processor.bizModel.RebotSettingModel;
import com.citc.nce.robot.common.ResponsePriority;
import org.springframework.stereotype.Service;

/**
 * 机器人执行阶段，流程的初始化
 */
@Service
public interface RobotService {

    /**
     * 主要通过会话Id在redis中查询是否存在正在执行的机器人流程（状态是正在执行）
     * 1、如果存在则不做任何处理
     * 2、如果不存在则选需要“触发关键字”去数据库中查询。
     *  如果存在多个则需要根据优先级获取，如果优先级高的存在多个，
     *  则以按钮的形式最多返回4个
     *3、获取到具体的机器人流程后，解析流程并缓存到redis中
     * @param msgDto 消息内容
     */
    boolean processInitialization(MsgDto msgDto);

    /**
     * 1、主要是机器人的执行
     * @param msgDto 上行消息
     */
    void exec(MsgDto msgDto);

    /**
     * 根据优先级回复消息
     * @param msgDto 消息内容
     * @param currentStatus 当前状态
     * @return 是否回复成功
     */
    boolean replyByResponsePriority(MsgDto msgDto, ResponsePriority currentStatus) ;
}

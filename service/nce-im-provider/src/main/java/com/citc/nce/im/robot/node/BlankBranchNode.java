package com.citc.nce.im.robot.node;

import com.citc.nce.im.robot.dto.message.MsgDto;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author jcrenc
 * @since 2023/7/17 10:25
 */
@Slf4j
public class BlankBranchNode extends Node {

    @Override
    protected void beforeExec(MsgDto msgDto) {
    }

    /**
     * 分支节点在执行中处理节点跳转逻辑，因为节点跳转就是分支节点的业务
     *
     * @param msgDto 上行消息
     */
    @Override
    void exec(MsgDto msgDto) {
    }

    /**
     * 分支节点执行后不做任何逻辑，因为在执行中已经处理完节点跳转
     */
    @Override
    void afterExec(MsgDto msgDto) {
    }
}

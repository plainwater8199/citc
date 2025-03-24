package com.citc.nce.im.robot.node;

import com.citc.nce.auth.contactlist.ContactListApi;
import com.citc.nce.auth.contactlist.vo.ContactListReq;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.im.robot.dto.message.MsgDto;
import com.citc.nce.im.robot.dto.robot.RobotDto;
import com.citc.nce.im.robot.enums.NodeStatus;
import com.citc.nce.im.robot.node.Node;
import com.citc.nce.im.robot.process.Process;
import com.citc.nce.im.robot.util.RobotUtils;
import com.citc.nce.im.session.ApplicationContextUil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jcrenc
 * @since 2023/7/17 10:26
 */
@Data
@Slf4j
public class ConcatNode extends Node {

    /**
     * 1:新增 2：删除
     */
    private Integer operateType;

    private List<Long> contactGroup;

    /**
     * 结点执行前操作
     *
     * @param msgDto 上行消息
     */
    @Override
    void beforeExec(MsgDto msgDto) {

    }

    /**
     * 新增或删除联系人
     *
     * @param msgDto 上行消息
     */
    @Override
    void exec(MsgDto msgDto) {
        //只有网关消息才能执行联系人操作
        if (msgDto.getMessageSource().equals("1")) {
            String phone = msgDto.getPhone();
            Long groupId = contactGroup.get(0);
            ContactListReq contactListReq = new ContactListReq();
            contactListReq.setGroupId(groupId);
            contactListReq.setPhoneNum(phone);
            contactListReq.setCreator(msgDto.getCreate());
            ContactListApi contactListApi = ApplicationContextUil.getBean(ContactListApi.class);
            try {
                if (1 == operateType) {
                    int save = contactListApi.saveContactList(contactListReq);
                    log.info("concat save:{}", save);
                } else {
                    int delete = contactListApi.delContactListByPhone(contactListReq);
                    log.info("concat delete:{}", delete);
                }
            } catch (Exception e) {
                log.error("联系人操作异常：{}", e.getMessage(), e);
            }
        } else {
            log.info("非网关消息，跳过联系人操作,MessageSource:{}", msgDto.getMessageSource());
        }
    }

    /**
     * 执行结点跳转
     *
     * @param msgDto 上行消息
     */
    @Override
    void afterExec(MsgDto msgDto) {
        RobotDto robot = RobotUtils.getLocalRobot(msgDto.getConversationId());
        List<RouterInfo> outRouters = this.getOutRouters();
        String nextNodeId = null;
        if (!CollectionUtils.isEmpty(outRouters)) {
            outRouters = outRouters.stream().filter(route -> route.getTailNodeId() != null).collect(Collectors.toList());
            if (outRouters.size() > 1)
                throw new BizException(500, "联系人节点至多只能有一个出站路由，节点ID:" + this.getNodeId());
            else if (outRouters.size() == 1) {
                RouterInfo routerInfo = outRouters.get(0);
                nextNodeId = routerInfo.getTailNodeId();
            }
        }
        robot.setCurrentNodeId(nextNodeId);
        this.setNodeStatus(NodeStatus.SUCCESS);
        Process process = RobotUtils.getCurrentProcess(robot);
        process.getNodeMap().put(this.getNodeId(), this);
        RobotUtils.saveRobot(robot);
    }
}

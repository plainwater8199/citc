package com.citc.nce.im.session.processor.ncenode.impl;

import com.citc.nce.auth.contactlist.ContactListApi;
import com.citc.nce.auth.contactlist.vo.ContactListReq;
import com.citc.nce.im.session.ApplicationContextUil;
import com.citc.nce.im.session.processor.NceRobotNode;
import com.citc.nce.im.session.processor.NodeProcessor;
import com.citc.nce.im.session.processor.ParsedProcessorConfig;
import com.citc.nce.im.session.processor.SessionContext;
import com.citc.nce.robot.vo.NodeActResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ContactNode extends NceRobotNode {
    private Logger log = LoggerFactory.getLogger(ContactNode.class);

    ContactListApi contactListApi = ApplicationContextUil.getBean(ContactListApi.class);

    public PresonConnect presonConnect;

    @Override
    public NodeActResult act(SessionContext sessionContext, NodeProcessor nodeProcessor) throws Exception {
        //只有终端才能执行联系人操作
        if(nodeProcessor.getUpMsgReq().getFalg()!=null&&nodeProcessor.getUpMsgReq().getFalg()==1){
            ContactListReq contactListReq = new ContactListReq();
            contactListReq.setGroupId(presonConnect.getPresonGroup());
            if(StringUtils.isNotEmpty(nodeProcessor.getUpMsgReq().getPhone())){
                contactListReq.setPhoneNum(nodeProcessor.getUpMsgReq().getPhone());
            }else{
                contactListReq.setPhoneNum("13098886666");
            }

            contactListReq.setCreator(nodeProcessor.getUpMsgReq().getCreate());
            try {
                if ("1".equals(presonConnect.getOperatType())){
                    contactListApi.saveContactList(contactListReq);
                    log.info("执行新增联系人操作");
                }else {
                    contactListApi.delContactListByPhone(contactListReq);
                    log.info("执行删除联系人操作");
                }
            }catch (Exception e){
                log.error("执行新增、删除联系人操作异常：",e,e.getMessage());
            }
        }

        ParsedProcessorConfig parsedProcessorConfig = nodeProcessor.getParsedProcessorConfig();
        List<String> list = parsedProcessorConfig.getNodeIdRelations().get(nodeProcessor.getCurrentNodeId());
        String childIdx = "";
        boolean hasNext = true;
        if (list.size() > 0) {
            childIdx = list.get(0);
        } else {
            nodeProcessor.checkAndClean();
            hasNext = false;
        }
        return NodeActResult.builder().next(hasNext).childIdx(childIdx).result("").build();
    }

    @Data
    public static class PresonConnect {
        /**
         * 操作类型
         */
        private String operatType;
        /**
         * 分组id
         */
        private Long presonGroup;
    }
}

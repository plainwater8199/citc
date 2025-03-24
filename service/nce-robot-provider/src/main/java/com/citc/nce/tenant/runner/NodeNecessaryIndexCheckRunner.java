//package com.citc.nce.tenant.runner;
//
//import com.citc.nce.authcenter.csp.CspApi;
//import com.citc.nce.authcenter.csp.vo.QueryAllCspIdResp;
//import com.citc.nce.tenant.robot.dao.MsgRecordDDLDao;
//import com.citc.nce.utils.constants.BeanOrderConstants;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import java.util.*;
//import java.util.function.Consumer;
//import java.util.stream.Stream;
//
///**
// * 检查分表的真实节点的必要索引是否存在，如果不存在则创建
// *
// * @author jcrenc
// * @since 2025/1/15 9:35
// */
//@Component
//@Order(BeanOrderConstants.CHECK_NECESSARY_NODE_INDEX)
//@Slf4j
//@RequiredArgsConstructor
//public class NodeNecessaryIndexCheckRunner implements ApplicationRunner {
//    private final MsgRecordDDLDao msgRecordDDLDao;
//    private final CspApi cspApi;
//
//    @Override
//    public void run(ApplicationArguments args){
//
//    }
//
//    private void checkMsgRecord(String cspId) {
//        final String tableName = "msg_record_" + cspId;
//        Stream.<Consumer<String>>of(
//                        msgRecordDDLDao::addIndexForAccountType,
//                        msgRecordDDLDao::addIndexForPlanDetailId,
//                        msgRecordDDLDao::addIndexForCreator,
//                        msgRecordDDLDao::addIndexForSendResult,
//                        msgRecordDDLDao::addIndexForPhoneNum,
//                        msgRecordDDLDao::addIndexForMessageId,
//                        msgRecordDDLDao::addIndexForAccountId
//                )
//                .forEach(consumer -> {
//                    try {
//                        consumer.accept(tableName);
//                    } catch (Exception e) {
//                        log.debug("创建索引失败: {}", e.getMessage());
//                    }
//                });
//    }
//}

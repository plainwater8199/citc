package com.citc.nce.tenant.robot.dao;


import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.robot.enums.MessageResourceType;
import com.citc.nce.tenant.service.MsgRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

/**
 * @author jcrenc
 * @since 2024/7/25 17:26
 */
@SpringBootTest
public class MsgRecordDaoTest {
    @Autowired
    private MsgRecordDao msgRecordDao;
    @Autowired
    private MsgRecordService msgRecordService;

    @Test
    void querySendAccountTypeListBetween() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end = now.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);
        String customerId = "040818919440475";
        List<?> list = msgRecordDao.querySendAccountTypeListBetween(customerId, start, end);
        System.out.println(list);

        Map<MsgTypeEnum, List<MessageResourceType>> msgTypeEnumMessageResourceTypeMap = msgRecordService.querySendMsgTypeListBetween(customerId, start, end);
        System.out.println(msgTypeEnumMessageResourceTypeMap);
    }
}
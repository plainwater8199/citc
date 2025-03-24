package com.citc.nce.aim.waterTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citc.nce.aim.privatenumber.dao.PrivateNumberSentDataDao;
import com.citc.nce.aim.privatenumber.dao.TableDao;
import com.citc.nce.aim.privatenumber.entity.PrivateNumberSentDataDo;
import com.citc.nce.aim.utils.api.AimCallUtils;
import com.citc.nce.common.util.UUIDUtils;
import com.citc.nce.seata.ShardingJdbcNodesAutoConfigurator;
import com.citc.nce.utils.DateUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@RestController
public class SmsController {

    @Resource
    private ShardingJdbcNodesAutoConfigurator shardingJdbcNodesAutoConfigurator;

    @Resource
    private PrivateNumberSentDataDao privateNumberSentDataDao;

    @Resource
    private TableDao tableDao;
    @PostMapping("smsTest")
    void smsTest(HttpServletRequest request, HttpServletResponse response){

        PrivateNumberSentDataDo privateNumberSentDataDo = new PrivateNumberSentDataDo();
        privateNumberSentDataDo.setRecordId(UUIDUtils.generateUUID());
        privateNumberSentDataDo.setAppKey("18611111111");
        privateNumberSentDataDo.setProjectId("AIM20240812155847717");
        privateNumberSentDataDo.setOrderId(1823169381257969665L);
        privateNumberSentDataDo.setCreator("admin");
        privateNumberSentDataDo.setCreateTime(new Date());

        privateNumberSentDataDao.insert(privateNumberSentDataDo);



        LambdaQueryWrapper<PrivateNumberSentDataDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.between(PrivateNumberSentDataDo::getCreateTime, "2024-08-11 15:58:47", "2024-08-22 20:58:47");
        List<PrivateNumberSentDataDo> privateNumberSentDataDos2 = privateNumberSentDataDao.selectList(queryWrapper);
        System.out.println("查询结果：" + privateNumberSentDataDos2.size());
    }

    private List<String> obtainAllTableNames(LocalDateTime startTime, LocalDateTime endTime) {
        List<String> tableNames = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        // 当前时间
        LocalDateTime currentTime = startTime;
        while (currentTime.isBefore(endTime) || currentTime.isEqual(endTime)) {
            // 取整分钟为 00、10、20、30、40、50
            int minute = (currentTime.getMinute() / 10) * 10;
            currentTime = currentTime.withMinute(minute);
            // 格式化并保存
            tableNames.add("robot.aim_private_number_sent_data_"+currentTime.format(formatter));
            // 增加 10 分钟
            currentTime = currentTime.plusMinutes(10);
        }
        return tableNames;
    }

}

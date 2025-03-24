package com.citc.nce.robotfile.scheduled;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.filecenter.vo.ExamineOneReq;
import com.citc.nce.filecenter.vo.UploadResp;
import com.citc.nce.misc.schedule.ScheduleApi;
import com.citc.nce.robotfile.entity.ExamineResultDo;
import com.citc.nce.robotfile.mapper.ExamineResultMapper;
import com.citc.nce.robotfile.service.IExamineResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;


@EnableScheduling
@Slf4j
@RestController
public class ExamineScheduled {

    @Resource
    ExamineResultMapper examineResultMapper;

    @Resource
    FileApi fileApi;

    @Resource
    IExamineResultService examineResultService;

    @Resource
    ScheduleApi scheduleApi;

//    @Scheduled(cron = "0 0 */1 * * ?")
//    @RequestMapping("/examine/nearExpired/autoReport")
    public void examine() {
        try {
            boolean canExec = scheduleApi.addRecord("examineResult", "H");
            if (canExec) {
                //查询数据库信息和当前时间对比
                Date checkTime = Date.from(LocalDateTime.now().plusDays(1).plusHours(10).atZone(ZoneId.systemDefault()).toInstant());
                List<ExamineOneReq> examineOneReqs = examineResultMapper.getOverdueIds(checkTime);
                for (ExamineOneReq examineOneReq : examineOneReqs) {
                    try {
                        log.info("自动送审临期素材:{}", examineOneReq.getFileUUID());
                        UploadResp uploadResp = fileApi.examineOne(examineOneReq);
                        ExamineResultDo examineResult = new ExamineResultDo();
                        examineResult.setChatbotAccountId(uploadResp.getAccountId())
                                .setChatbotName(uploadResp.getChatbotName())
                                .setOperator(uploadResp.getOperator())
                                .setFileId(uploadResp.getFileTid())
                                .setFileUuid(uploadResp.getUrlId());
                        examineResultService.save(examineResult);
                    } catch (Throwable throwable) {
                        log.error("送审临期素材失败", throwable);
                    }
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }
}

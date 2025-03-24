package com.citc.nce.dataStatistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.dataStatistics.service.DataStatisticsForFastGroupMessageService;
import com.citc.nce.dataStatistics.vo.req.StartAndEndTimeReq;
import com.citc.nce.dataStatistics.vo.resp.FastGroupMessageStatisticsResp;
import com.citc.nce.dataStatistics.vo.resp.SendMsgCircleStatisticsResp;
import com.citc.nce.robot.api.FastGroupMessageApi;
import com.citc.nce.robot.enums.MessageResourceType;
import com.citc.nce.tenant.robot.dao.MsgRecordDao;
import com.citc.nce.tenant.robot.entity.MsgRecordDo;
import com.citc.nce.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.citc.nce.dataStatistics.constant.StatisticsConstants.END;
import static com.citc.nce.dataStatistics.constant.StatisticsConstants.START;

@Service
@Slf4j
public class DataStatisticsForFastGroupMessageServiceImpl implements DataStatisticsForFastGroupMessageService {

    @Resource
    private MsgRecordDao msgRecordDao;
    @Resource
    private FastGroupMessageApi fastGroupMessageApi;


    @Override
    public FastGroupMessageStatisticsResp fastGroupMessageStatistics(StartAndEndTimeReq req) {
        FastGroupMessageStatisticsResp resp = new FastGroupMessageStatisticsResp();
        String customerId = SessionContextUtil.getUser().getUserId();
        Date startTime = DateUtils.obtainDayTime(START,DateUtils.obtainDate(req.getStartTime()));
        Date endTime = DateUtils.obtainDayTime(END,DateUtils.obtainDate(req.getEndTime()));
        //查询创建并启动的数量
        Long createCount = fastGroupMessageApi.createStatistics(customerId, startTime, endTime);
        //查询发送的数量
        LambdaQueryWrapper<MsgRecordDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MsgRecordDo::getCreator, customerId)
               .between(MsgRecordDo::getCreateTime, startTime, endTime)
                .eq(MsgRecordDo::getMessageResource, MessageResourceType.FAST_GROUP.getCode());
        long sendCount = msgRecordDao.selectCount(queryWrapper);
        resp.setSendNum(sendCount);
        resp.setCount(createCount);
        return resp;
    }

    @Override
    public SendMsgCircleStatisticsResp sendMsgCircleStatistics(StartAndEndTimeReq req) {

        SendMsgCircleStatisticsResp resp = new SendMsgCircleStatisticsResp();
        resp.setFastGroupMsgSum(0L);
        resp.setGroupPlanSum(0L);
        resp.setDeveloperSum(0L);
        resp.setRobotSum(0L);
        resp.setKeywordReplySum(0L);
        String customerId = SessionContextUtil.getUser().getUserId();
        Date startTime = DateUtils.obtainDayTime(START,DateUtils.obtainDate(req.getStartTime()));
        Date endTime = DateUtils.obtainDayTime(END,DateUtils.obtainDate(req.getEndTime()));

        // 构建查询条件
        List<Map<String, Object>> resultMaps = msgRecordDao.selectStatisticsByMessageResource(customerId, startTime, endTime);
        if(!CollectionUtils.isEmpty(resultMaps)){
            for(Map<String, Object> resultMap : resultMaps){
                Object messageResource = resultMap.get("message_resource");
                Object count = resultMap.get("count");
                if("6".equals(messageResource+"")){
                    resp.setFastGroupMsgSum(Long.parseLong(count+""));
                }else if("4".equals(messageResource+"")){
                    resp.setDeveloperSum(Long.parseLong(count+""));
                }else if("7".equals(messageResource+"")){
                    resp.setKeywordReplySum(Long.parseLong(count+""));
                }else if("2".equals(messageResource+"")){
                    resp.setRobotSum(Long.parseLong(count+""));
                }else if("1".equals(messageResource+"")){
                    resp.setGroupPlanSum(Long.parseLong(count+""));
                }
            }
        }
        return resp;
    }
}

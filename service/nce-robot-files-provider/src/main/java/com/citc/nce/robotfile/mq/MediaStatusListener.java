package com.citc.nce.robotfile.mq;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.dto.FileAccept;
import com.citc.nce.dto.FileAcceptData;
import com.citc.nce.robotfile.entity.ExamineResultDo;
import com.citc.nce.robotfile.exp.RobotFileExp;
import com.citc.nce.robotfile.mapper.ExamineResultMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * 接收发送回调的Mq
 */
@Service
@RocketMQMessageListener( consumerGroup = "${rocketmq.group}", topic = "${rocketmq.topic}" )
@Slf4j
public class MediaStatusListener implements RocketMQListener<String> {

    @Resource
    ExamineResultMapper examineResultMapper;

    @Override
    public void onMessage(String requestStr) {
        FileAccept fileAccept = JSONObject.parseObject(requestStr, FileAccept.class);
        log.info(JSONObject.toJSONString(fileAccept));
        String fileId = fileAccept.getFileId();
        //通过fileId查询数据库是否有这条数据
        LambdaQueryWrapper<ExamineResultDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExamineResultDo::getFileId,fileId).or().eq(ExamineResultDo::getThumbnailTid,fileId);
        ExamineResultDo result = examineResultMapper.selectOne(queryWrapper);
        if (null == result){
            //如果数据没有保存就抛异常进行重试
            throw new RuntimeException("消息还未保存");
        }
        // 如果是缩略图，等主文件返回再更新状态
        if (Objects.equals(result.getThumbnailTid(), fileAccept.getFileId())) {
            return;
        }
        ExamineResultDo examineResultDo = new ExamineResultDo();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date validity = null;
        try {
            validity = StringUtils.isNotEmpty(fileAccept.getUntil())
                    ? sdf.parse(fileAccept.getUntil())
                    : sdf.parse("2099-12-31 23:59:59"); // 蜂动没有过期时间，默认设置一个大值
        } catch (ParseException e) {
            throw new BizException(RobotFileExp.DATE_ERROR);
        }
        examineResultDo.setValidity(validity);
        examineResultDo.setFileId(fileId);
        if (fileAccept.getUseable() == 1){
            examineResultDo.setFileStatus(2);
        }else {
            examineResultDo.setFileStatus(3);
        }
        examineResultMapper.updateStatus(examineResultDo);
    }
}

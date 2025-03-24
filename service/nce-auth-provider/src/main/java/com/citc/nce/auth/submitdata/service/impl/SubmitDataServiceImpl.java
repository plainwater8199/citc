package com.citc.nce.auth.submitdata.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.auth.configure.RocketMQConfigure;
import com.citc.nce.auth.submitdata.dao.SubmitDataDao;
import com.citc.nce.auth.submitdata.entity.SubmitDataDo;
import com.citc.nce.auth.submitdata.service.SubmitDataService;
import com.citc.nce.auth.submitdata.vo.PageResultResp;
import com.citc.nce.auth.submitdata.vo.SubmitDataOneReq;
import com.citc.nce.auth.submitdata.vo.SubmitDataPageReq;
import com.citc.nce.auth.submitdata.vo.SubmitDataReq;
import com.citc.nce.auth.submitdata.vo.SubmitDataResp;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.thread.ThreadTaskUtils;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.misc.shortUrl.ShortUrlApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @Author: yangchuang
 * @Date: 2022/10/13 16:32
 * @Version: 1.0
 * @Description:
 */
@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(RocketMQConfigure.class)
public class SubmitDataServiceImpl implements SubmitDataService {

    @Resource
    private SubmitDataDao submitDataDao;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private ShortUrlApi shortUrlApi;

    private final RocketMQConfigure rocketMQConfigure;


    @Override
    public PageResultResp getSubmitDatas(SubmitDataPageReq submitDataPageReq) {
        PageParam pageParam = new PageParam();
        pageParam.setPageSize(submitDataPageReq.getPageSize());
        pageParam.setPageNo(submitDataPageReq.getPageNo());
        PageResult<SubmitDataDo> SubmitDataDoPageResult = submitDataDao.selectPage(pageParam, (Wrappers.<SubmitDataDo>lambdaQuery()
                .eq(SubmitDataDo::getDeleted, 0)
                .eq(SubmitDataDo::getFormId, submitDataPageReq.getFormId())
                .orderByDesc(SubmitDataDo::getCreateTime)));
        List<SubmitDataResp> SubmitDataResps = BeanUtil.copyToList(SubmitDataDoPageResult.getList(), SubmitDataResp.class);
        return new PageResultResp(SubmitDataResps,SubmitDataDoPageResult.getTotal(),submitDataPageReq.getPageNo());
    }

    @Override
    public int saveSubmitData(SubmitDataReq submitDataReq) {

        //将请求体转化为json字符串
        String requestStr = JsonUtils.obj2String(submitDataReq);
        Message<String> message = MessageBuilder.withPayload(requestStr).build();

        //异步发送该消息，获取发送结果
        rocketMQTemplate.asyncSend(rocketMQConfigure.getTopic(), message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("saveSubmitData发送到mq 结果为 ： {}", sendResult);
            }

            @Override
            public void onException(Throwable e) {
                log.info("saveSubmitData发送到mq 失败 ： {}", e);
            }
        });

        return 1;
    }

    @Override
    public int delSubmitDataById(SubmitDataOneReq submitDataOneReq) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", submitDataOneReq.getId());
        map.put("deleted", 1);
        map.put("deleteTime", DateUtil.date());
        return submitDataDao.delSubmitDataById(map);
    }

    @Override
    public SubmitDataResp getSubmitDataById(SubmitDataOneReq submitDataOneReq) {
        SubmitDataDo submitDataDo = submitDataDao.selectOne(Wrappers.<SubmitDataDo>lambdaQuery()
                .eq(SubmitDataDo::getId, submitDataOneReq.getId()));
        SubmitDataResp SubmitDataResp = BeanUtil.copyProperties(submitDataDo, SubmitDataResp.class);
        return SubmitDataResp;
    }

    @Override
    public PageResultResp downloadPageList(SubmitDataPageReq submitDataPageReq) {
        PageParam pageParam = new PageParam();
        pageParam.setPageSize(submitDataPageReq.getPageSize());
        pageParam.setPageNo(submitDataPageReq.getPageNo());
        PageResult<SubmitDataDo> SubmitDataDoPageResult = submitDataDao.selectPage(pageParam, (Wrappers.<SubmitDataDo>lambdaQuery()
                .eq(SubmitDataDo::getDeleted, 0)
                .eq(SubmitDataDo::getFormId, submitDataPageReq.getFormId())
                .orderByAsc(SubmitDataDo::getCreateTime)));
        List<SubmitDataResp> SubmitDataResps = BeanUtil.copyToList(SubmitDataDoPageResult.getList(), SubmitDataResp.class);
        return new PageResultResp(SubmitDataResps,SubmitDataDoPageResult.getTotal(),submitDataPageReq.getPageNo());
    }

    @Override
    public String getShortUrl(Long id) {
        return shortUrlApi.getShortUrlByIdAndType(id, "01");
    }
}

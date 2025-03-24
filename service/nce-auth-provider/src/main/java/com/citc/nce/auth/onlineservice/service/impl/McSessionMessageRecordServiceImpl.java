package com.citc.nce.auth.onlineservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.citc.nce.auth.onlineservice.dao.McSessionMessageRecordDao;
import com.citc.nce.auth.onlineservice.entity.McSessionMessageRecordDo;
import com.citc.nce.auth.onlineservice.service.McSessionMessageRecordService;
import com.citc.nce.auth.onlineservice.vo.req.MessageRecordReq;
import com.citc.nce.auth.onlineservice.vo.resp.MessageRecordResp;
import com.citc.nce.misc.constant.NumCode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @BelongsPackage: com.citc.nce.auth.onlineservice.service.impl
 * @Author: litao
 * @CreateTime: 2023-01-06  10:09
 
 * @Version: 1.0
 */
@Service
public class McSessionMessageRecordServiceImpl implements McSessionMessageRecordService {
    @Resource
    private McSessionMessageRecordDao mcSessionMessageRecordDao;

    @Override
    public MessageRecordResp findUnReadCountByUserId(MessageRecordReq req) {
        MessageRecordResp messageRecordResp = new MessageRecordResp();
        QueryWrapper<McSessionMessageRecordDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("receiver_id",req.getUserId());
        queryWrapper.eq("read_status", NumCode.ONE.getCode());
        List<McSessionMessageRecordDo> list = mcSessionMessageRecordDao.selectList(queryWrapper);
        messageRecordResp.setUnReadCount(list.size());
        return messageRecordResp;
    }
}

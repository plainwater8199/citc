package com.citc.nce.misc.record.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.misc.constant.MiscErrorCode;
import com.citc.nce.misc.record.dto.ProcessingRecordDto;
import com.citc.nce.misc.record.entity.ProcessingRecordDo;
import com.citc.nce.misc.record.mapper.ProcessingRecordMapper;
import com.citc.nce.misc.record.req.BusinessIdReq;
import com.citc.nce.misc.record.req.BusinessIdsReq;
import com.citc.nce.misc.record.req.ProcessingRecordReq;
import com.citc.nce.misc.record.resp.ProcessingRecordResp;
import com.citc.nce.misc.record.service.ProcessingRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ProcessingRecordServiceImpl implements ProcessingRecordService {
    @Resource
    private ProcessingRecordMapper processingRecordMapper;

    private static final String COMMON_DATE = "yyyy-MM-dd HH:mm:ss";
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRecord(ProcessingRecordReq req) {
        SimpleDateFormat sdf = new SimpleDateFormat(COMMON_DATE);
        try {
            ProcessingRecordDo processingRecordDo = new ProcessingRecordDo();
            BeanUtil.copyProperties(req, processingRecordDo);
            processingRecordDo.setOperateTime(sdf.parse(sdf.format(new Date())));
            processingRecordDo.setCreator(processingRecordDo.getProcessingUserId());
            processingRecordDo.setCreateTime(new Date());
            processingRecordDo.setUpdater(processingRecordDo.getProcessingUserId());
            processingRecordDo.setUpdateTime(new Date());
            processingRecordMapper.insert(processingRecordDo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BizException.build(MiscErrorCode.ADD_RECORD_FAIL);
        }
    }

    @Override
    public List<ProcessingRecordResp> findProcessingRecordList(BusinessIdReq req) {
        ProcessingRecordDto processingRecordDto = new ProcessingRecordDto();
        processingRecordDto.setBusinessId(req.getBusinessId());
        if (req.getBusinessType() != null) {
            processingRecordDto.setBusinessType(req.getBusinessType());
        }
        if (CollectionUtils.isNotEmpty(req.getBusinessTypeList())) {
            processingRecordDto.setBusinessTypeList(req.getBusinessTypeList());
        }
        List<ProcessingRecordDto> recordList = processingRecordMapper.findProcessingRecordList(processingRecordDto);
        List<ProcessingRecordResp> processingRecordRespList = new ArrayList<>();
        recordList.forEach(item -> {
            ProcessingRecordResp resp = new ProcessingRecordResp();
            BeanUtil.copyProperties(item, resp);
            processingRecordRespList.add(resp);
        });
        return processingRecordRespList;
    }


    @Override
    public List<ProcessingRecordResp> findProcessingRecordListByIds(BusinessIdsReq req) {
        QueryWrapper<ProcessingRecordDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("business_id", req.getBusinessIds());
        if (CollectionUtil.isEmpty(req.getBusinessTypeList())) {
            queryWrapper.eq("business_type", req.getBusinessType());
        } else {
            queryWrapper.in("business_type", req.getBusinessTypeList());
        }
        List<ProcessingRecordDo> processingRecordDoList = processingRecordMapper.selectList(queryWrapper);
        List<ProcessingRecordResp> processingRecordRespList = new ArrayList<>();
        processingRecordDoList.forEach(item -> {
            ProcessingRecordResp resp = new ProcessingRecordResp();
            BeanUtil.copyProperties(item, resp);
            processingRecordRespList.add(resp);
        });
        return processingRecordRespList;
    }

    @Override
    public void addBatchRecord(List<ProcessingRecordReq> reqList) {
        if (!CollectionUtils.isEmpty(reqList)) {
            SimpleDateFormat sdf = new SimpleDateFormat(COMMON_DATE);
            List<ProcessingRecordDo> saveList = new ArrayList<>();
            reqList.forEach(item -> {
                try {
                    ProcessingRecordDo processingRecordDo = new ProcessingRecordDo();
                    BeanUtil.copyProperties(item, processingRecordDo);
                    processingRecordDo.setOperateTime(sdf.parse(sdf.format(new Date())));
                    processingRecordDo.setCreator(processingRecordDo.getProcessingUserId());
                    processingRecordDo.setCreateTime(new Date());
                    processingRecordDo.setUpdater(processingRecordDo.getProcessingUserId());
                    processingRecordDo.setUpdateTime(new Date());
                    saveList.add(processingRecordDo);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw BizException.build(MiscErrorCode.ADD_RECORD_FAIL);
                }
            });
            processingRecordMapper.insertBatch(saveList);
        }
    }

}

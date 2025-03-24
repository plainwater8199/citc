package com.citc.nce.misc.legal.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.misc.constant.BusinessTypeEnum;
import com.citc.nce.misc.constant.MiscErrorCode;
import com.citc.nce.misc.constant.NumCode;
import com.citc.nce.misc.constant.ProcessingContentEnum;
import com.citc.nce.misc.legal.entity.LegalFileDo;
import com.citc.nce.misc.legal.mapper.LegalFileMapper;
import com.citc.nce.misc.legal.req.IdReq;
import com.citc.nce.misc.legal.req.PageReq;
import com.citc.nce.misc.legal.req.UpdateReq;
import com.citc.nce.misc.legal.resp.LegalFileResp;
import com.citc.nce.misc.legal.service.LegalFileService;
import com.citc.nce.misc.record.req.ProcessingRecordReq;
import com.citc.nce.misc.record.service.ProcessingRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @BelongsPackage: com.citc.nce.misc.legal.service.impl
 * @Author: litao
 * @CreateTime: 2023-02-09  16:06
 
 * @Version: 1.0
 */
@Service
@Slf4j
public class LegalFileServiceImpl implements LegalFileService {
    @Resource
    private LegalFileMapper legalFileMapper;
    @Resource
    private ProcessingRecordService processingRecordService;

    @Override
    public PageResult<LegalFileResp> listByPage(PageReq req) {
        PageResult<LegalFileResp> pageResult = new PageResult<>();
        Page<LegalFileDo> page = new Page<>(req.getPageNo(),req.getPageSize());
        QueryWrapper<LegalFileDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_latest", NumCode.ONE.getCode());
        Page<LegalFileDo> selectPage = legalFileMapper.selectPage(page,queryWrapper);
        List<LegalFileDo> legalFileDoList = selectPage.getRecords();
        List<LegalFileResp> legalFileRespList = new ArrayList<>();
        legalFileDoList.forEach(item->{
            LegalFileResp resp = new LegalFileResp();
            BeanUtil.copyProperties(item,resp);
            legalFileRespList.add(resp);
        });
        pageResult.setList(legalFileRespList);
        pageResult.setTotal(selectPage.getTotal());
        return pageResult;
    }

    @Override
    public LegalFileResp findById(IdReq req) {
        LegalFileResp resp = new LegalFileResp();
        BeanUtil.copyProperties(legalFileMapper.selectById(req.getId()),resp);
        return resp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateById(UpdateReq req) {
        BaseUser baseUser = SessionContextUtil.getUser();

        LegalFileDo existDo = legalFileMapper.selectById(req.getId());
        //判断文件是否已被其他人修改过
        if(!existDo.getIsLatest().equals(NumCode.ONE.getCode())){
            throw BizException.build(MiscErrorCode.HAVE_UPDATED);
        }

        //添加一条最新版本的文件
        LegalFileDo newDo = new LegalFileDo();
        BeanUtil.copyProperties(existDo,newDo);
        newDo.setId(null);
        newDo.setFileContent(req.getFileContent());
        newDo.setIsConfirm(req.getIsConfirm());
        newDo.setFileVersion(existDo.getFileVersion()+1);
        newDo.setIsLatest(NumCode.ONE.getCode());
        legalFileMapper.insert(newDo);

        //查询原来所有版本 并将目前版本标记为老版本
        List<LegalFileDo> oldList = legalFileMapper.selectList("parent_id",req.getId());
        if(CollectionUtil.isEmpty(oldList)){
            oldList = new ArrayList<>();
        }
        oldList.add(existDo);
        oldList.forEach(item->{
            item.setParentId(newDo.getId());
            item.setIsLatest(NumCode.ZERO.getCode());
            item.setUpdateTime(new Date());
        });
        legalFileMapper.updateBatch(oldList);

        //添加处理记录
        ProcessingRecordReq processingRecordReq = new ProcessingRecordReq();
        processingRecordReq.setBusinessId(newDo.getId().toString());
        processingRecordReq.setBusinessType(BusinessTypeEnum.FWWJGL.getCode());
        processingRecordReq.setProcessingContent(ProcessingContentEnum.GXWD.getName());
        processingRecordReq.setProcessingUserId(baseUser.getUserId());
        processingRecordService.addRecord(processingRecordReq);
    }

    @Override
    public PageResult<LegalFileResp> historyVersionList(PageReq req) {
        PageResult<LegalFileResp> pageResult = new PageResult<>();
        Page<LegalFileDo> page = new Page<>(req.getPageNo(),req.getPageSize());
        QueryWrapper<LegalFileDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_latest", NumCode.ZERO.getCode());
        queryWrapper.eq("parent_id",req.getId());
        queryWrapper.orderByDesc("file_version");
        Page<LegalFileDo> selectPage = legalFileMapper.selectPage(page,queryWrapper);
        List<LegalFileDo> legalFileDoList = selectPage.getRecords();
        List<LegalFileResp> legalFileRespList = new ArrayList<>();
        legalFileDoList.forEach(item->{
            LegalFileResp resp = new LegalFileResp();
            BeanUtil.copyProperties(item,resp);
            legalFileRespList.add(resp);
        });
        pageResult.setList(legalFileRespList);
        pageResult.setTotal(selectPage.getTotal());
        return pageResult;
    }
}

package com.citc.nce.authcenter.legalaffairs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.authcenter.constant.AuthCenterError;
import com.citc.nce.authcenter.constant.NumCode;
import com.citc.nce.authcenter.legalaffairs.dao.LegalFileDao;
import com.citc.nce.authcenter.legalaffairs.entity.LegalFileDo;
import com.citc.nce.authcenter.legalaffairs.service.LegalAffairsService;
import com.citc.nce.authcenter.legalaffairs.vo.req.IdReq;
import com.citc.nce.authcenter.legalaffairs.vo.req.PageReq;
import com.citc.nce.authcenter.legalaffairs.vo.req.UpdateReq;
import com.citc.nce.authcenter.legalaffairs.vo.resp.LegalFileNewestResp;
import com.citc.nce.authcenter.legalaffairs.vo.resp.LegalFileResp;
import com.citc.nce.authcenter.legalaffairs.vo.resp.LegalRecordResp;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 *
 *  代码迁移！！！！！！！！！！！！！！！！非本人所写！！！！！！！！！！！！勿喷！！！！！！！！！！！！！
 *  代码迁移！！！！！！！！！！！！！！！！非本人所写！！！！！！！！！！！！勿喷！！！！！！！！！！！！！
 *  代码迁移！！！！！！！！！！！！！！！！非本人所写！！！！！！！！！！！！勿喷！！！！！！！！！！！！！
 *  代码迁移！！！！！！！！！！！！！！！！非本人所写！！！！！！！！！！！！勿喷！！！！！！！！！！！！！
 *  代码迁移！！！！！！！！！！！！！！！！非本人所写！！！！！！！！！！！！勿喷！！！！！！！！！！！！！
 *  代码迁移！！！！！！！！！！！！！！！！非本人所写！！！！！！！！！！！！勿喷！！！！！！！！！！！！！
 *  代码迁移！！！！！！！！！！！！！！！！非本人所写！！！！！！！！！！！！勿喷！！！！！！！！！！！！！
 *
 */

@Service
@Slf4j
public class LegalAffairsServiceImpl implements LegalAffairsService {
    @Resource
    private LegalFileDao legalFileMapper;

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
        LegalFileDo fileDo = legalFileMapper.selectById(req.getId());
        if(Objects.isNull(fileDo)){
            throw BizException.build(AuthCenterError.NOT_QUERY_DATA);
        }
        BeanUtil.copyProperties(fileDo,resp);
        return resp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateById(UpdateReq req) {
        BaseUser baseUser = SessionContextUtil.getUser();

        LegalFileDo existDo = legalFileMapper.selectById(req.getId());
        if(Objects.isNull(existDo)){
            throw BizException.build(AuthCenterError.NOT_QUERY_DATA);
        }

        //判断文件是否已被其他人修改过
        if(!existDo.getIsLatest().equals(NumCode.ONE.getCode())){
            throw BizException.build(AuthCenterError.HAVE_UPDATED);
        }

        //添加一条最新版本的文件
        Integer version = existDo.getVersion();
        LegalFileDo newDo = new LegalFileDo();
        BeanUtil.copyProperties(existDo,newDo);
        newDo.setId(null);
        newDo.setFileContent(req.getFileContent());
        newDo.setIsConfirm(req.getIsConfirm());
        newDo.setFileVersion(existDo.getFileVersion()+1);
        newDo.setIsLatest(NumCode.ONE.getCode());
        newDo.setVersion(version+1);
        newDo.setCreateTime(new Date());
        newDo.setCreator(baseUser.getUserName());
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

        //修改成功其他最新文件版本号version+1
        List<LegalFileDo> fileDoList = legalFileMapper.selectList(Wrappers.<LegalFileDo>lambdaQuery()
                .eq(LegalFileDo::getIsLatest, NumCode.ONE.getCode())
                .eq(LegalFileDo::getVersion,version));
        if(CollUtil.isNotEmpty(fileDoList)){
            fileDoList.forEach(data->data.setVersion(version+1));
            legalFileMapper.updateBatch(fileDoList);
        }

    }

    @Override
    public PageResult<LegalFileResp> historyVersionList(PageReq req) {
        PageResult<LegalFileResp> pageResult = new PageResult<>();
        Page<LegalFileDo> page = new Page<>(req.getPageNo(),req.getPageSize());
        LambdaQueryWrapper<LegalFileDo> query = Wrappers.<LegalFileDo>lambdaQuery();
        query.and(wrapper->wrapper.eq(LegalFileDo::getParentId,req.getId()).or().eq(LegalFileDo::getId,req.getId()));
        query.orderByDesc(LegalFileDo::getFileVersion);
        Page<LegalFileDo> selectPage = legalFileMapper.selectPage(page,query);
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
    public List<LegalFileNewestResp> newestList() {
        LambdaQueryWrapper<LegalFileDo> queryWrapper = Wrappers.<LegalFileDo>lambdaQuery()
                .eq(LegalFileDo::getIsLatest, com.citc.nce.misc.constant.NumCode.ONE.getCode());
        List<LegalFileDo> fileDoList = legalFileMapper.selectList(queryWrapper);
        if(CollectionUtil.isEmpty(fileDoList)){
            return Collections.emptyList();
        }
        return BeanUtil.copyToList(fileDoList,LegalFileNewestResp.class);
    }

    @Override
    public List<LegalRecordResp> findRecord(IdReq req) {
        LambdaQueryWrapper<LegalFileDo> queryWrapper = Wrappers.<LegalFileDo>lambdaQuery()
                .and(query->query.eq(LegalFileDo::getParentId, req.getId()).or().eq(LegalFileDo::getId,req.getId()))
                .select(LegalFileDo::getId, LegalFileDo::getCreator, LegalFileDo::getCreateTime,LegalFileDo::getFileVersion);
        List<LegalFileDo> fileDoList = legalFileMapper.selectList(queryWrapper);
        if (CollectionUtil.isEmpty(fileDoList)) {
            return Collections.emptyList();
        }
        List<LegalRecordResp> respList = Lists.newArrayList();
        fileDoList.forEach(a -> {
            List<Long> newIds = fileDoList.stream().filter(b -> b.getFileVersion().equals(NumCode.ONE.getCode())).map(LegalFileDo::getId).collect(Collectors.toList());
            LegalRecordResp resp = new LegalRecordResp();
            resp.setProcessingUserName(a.getCreator());
            resp.setOperateTime(a.getCreateTime());
            if (!newIds.contains(a.getId())) {
                resp.setProcessingContent("更新文档");
            }else {
                resp.setProcessingContent("创建文档");
            }
            resp.setRemark("-");
            respList.add(resp);
        });
        return respList;
    }
}

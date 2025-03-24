package com.citc.nce.h5.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.h5.dao.H5TplMapper;
import com.citc.nce.h5.entity.H5TplDo;
import com.citc.nce.h5.service.H5TplService;
import com.citc.nce.h5.vo.H5TplInfo;
import com.citc.nce.h5.vo.req.H5TplQueryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Transactional(rollbackFor = Exception.class)
@Service
public class H5TplServiceImpl extends ServiceImpl<H5TplMapper, H5TplDo> implements H5TplService, IService<H5TplDo> {
    @Override
    public void create(H5TplInfo tpl) {
        H5TplDo tplDo = new H5TplDo();
        tplDo.setH5Id(tpl.getH5Id());
        tplDo.setTpl(tpl.getTpl());
        tplDo.setCustomerId(SessionContextUtil.getUserId());
        tplDo.setTitle(tpl.getTitle());
        tplDo.setTplDesc(tpl.getTplDesc());
        tplDo.setShareIcon(tpl.getShareIcon());
        tplDo.setGlobalStyle(tpl.getGlobalStyle());
        tplDo.setStatus(tpl.getStatus());
        tplDo.setCreator(SessionContextUtil.getUserId());
        tplDo.setCreateTime(new Date());
        save(tplDo);
    }

    @Override
    public H5TplInfo previewData(Long id) {
        //1、检查H5是否存在
        H5TplDo tpl = checkTpl(id,SessionContextUtil.getUserId());
        H5TplInfo h5TplInfo = new H5TplInfo();
        BeanUtils.copyProperties(tpl, h5TplInfo);
        return h5TplInfo;
    }

    @Override
    public PageResult<H5TplInfo> page(H5TplQueryVO h5Vo) {
        Page<H5TplDo> page = new Page<>(h5Vo.getPageNo(), h5Vo.getPageSize());
        page(page, new LambdaQueryWrapper<H5TplDo>()
                .eq(h5Vo.getStatus() != null, H5TplDo::getStatus, h5Vo.getStatus())
                .eq(StringUtils.hasLength(h5Vo.getCustomerId()), H5TplDo::getCustomerId, h5Vo.getCustomerId())
                .like(StringUtils.hasLength(h5Vo.getTitle()), H5TplDo::getTitle, h5Vo.getTitle())
                .like(StringUtils.hasLength(h5Vo.getKeyword()), H5TplDo::getTplDesc, h5Vo.getKeyword())
                .orderByDesc(H5TplDo::getCreateTime)
        );

        List<H5TplInfo> result = page.getRecords().stream()
                .map(item -> {
                    H5TplInfo info = new H5TplInfo();
                    BeanUtils.copyProperties(item, info);
                    return info;
                }).collect(Collectors.toList());

        return new PageResult<>(result, page.getTotal());
    }

    @Override
    public void update(H5TplInfo h5Info) {
        //1、检查H5是否存在
        H5TplDo tpl = checkTpl(h5Info.getId(),SessionContextUtil.getUserId());
        if(!tpl.getStatus().equals(1)){
            BeanUtils.copyProperties(h5Info, tpl);
            tpl.setUpdateTime(new Date());
            tpl.setUpdater(SessionContextUtil.getUserId());
            boolean success = updateById(tpl);
        }else{
            throw new BizException("状态异常");
        }
    }

    @Override
    public void delete(Long id) {
        //1、检查H5是否存在
        H5TplDo tpl = checkTpl(id,SessionContextUtil.getUserId());
        if(!tpl.getStatus().equals(1)){
            removeById(id);
        }else{
            throw new BizException("状态异常，不能删除");
        }

    }

    private H5TplDo checkTpl(Long id, String customerId) {
        H5TplDo one = this.lambdaQuery().eq(H5TplDo::getId, id).eq(H5TplDo::getCustomerId, customerId).one();
        if(one == null){
            throw new BizException("H5模版不存在！");
        }
        return one;
    }
}

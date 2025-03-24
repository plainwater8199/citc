package com.citc.nce.auth.onlineservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.citc.nce.auth.onlineservice.dao.OnlineServiceConfigDao;
import com.citc.nce.auth.onlineservice.entity.OnlineServiceConfigDo;
import com.citc.nce.auth.onlineservice.service.OnlineServiceConfigService;
import com.citc.nce.auth.onlineservice.vo.req.OnlineServiceConfigReq;
import com.citc.nce.auth.onlineservice.vo.resp.OnlineServiceConfigResp;
import com.citc.nce.misc.constant.NumCode;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @BelongsPackage: com.citc.nce.auth.onlineservice.service.impl
 * @Author: litao
 * @CreateTime: 2023-01-04  14:52
 
 * @Version: 1.0
 */
@Service
public class OnlineServiceConfigServiceImpl implements OnlineServiceConfigService {
    @Resource
    private OnlineServiceConfigDao onlineServiceConfigDao;

    @Override
    public OnlineServiceConfigResp findConfigByUserId(OnlineServiceConfigReq req) {
        OnlineServiceConfigResp resp = new OnlineServiceConfigResp();
        QueryWrapper<OnlineServiceConfigDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",req.getUserId());
        OnlineServiceConfigDo onlineServiceConfigDo = onlineServiceConfigDao.selectOne(queryWrapper);
        if(onlineServiceConfigDo==null){
            onlineServiceConfigDo = new OnlineServiceConfigDo();
            onlineServiceConfigDo.setUserId(req.getUserId());
            onlineServiceConfigDo.setStatus(NumCode.ZERO.getCode());
            onlineServiceConfigDo.setIsEnabled(NumCode.ZERO.getCode());
            onlineServiceConfigDo.setAutoReplyContent("");
            onlineServiceConfigDao.insert(onlineServiceConfigDo);
        }
        BeanUtils.copyProperties(onlineServiceConfigDo,resp);
        return resp;
    }

    @Override
    public void updateConfigById(OnlineServiceConfigReq req) {
        OnlineServiceConfigDo onlineServiceConfigDo = new OnlineServiceConfigDo();
        BeanUtils.copyProperties(req,onlineServiceConfigDo);
        onlineServiceConfigDao.updateById(onlineServiceConfigDo);
    }
}

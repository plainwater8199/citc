package com.citc.nce.auth.csp.csp.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.citc.nce.auth.constant.AuthError;
import com.citc.nce.auth.csp.csp.dao.CspDao;
import com.citc.nce.auth.csp.csp.entity.CspDo;
import com.citc.nce.auth.csp.csp.service.CspService;
import com.citc.nce.common.core.exception.BizException;
import com.google.common.base.Strings;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CspServiceImpl implements CspService {

    @Resource
    private CspDao cspDao;
    @Override
    public String obtainCspId(String userId) {
        if(!Strings.isNullOrEmpty(userId)){
            if(userId.length() == 10){
                return userId;
            }else{
                //  cspid，如果userId是10位，则cspid和userid一致
                LambdaQueryWrapper<CspDo> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(CspDo::getUserId,userId);
                List<CspDo> cspDos = cspDao.selectList(queryWrapper);
                if(!CollectionUtils.isEmpty(cspDos)){
                    return cspDos.get(0).getCspId();
                }else{
                    throw new BizException(AuthError.CSP_NOT_EXIST);
                }
            }
        }else{
            throw new BizException(AuthError.USER_ID_NOT_NULL);
        }
    }
}

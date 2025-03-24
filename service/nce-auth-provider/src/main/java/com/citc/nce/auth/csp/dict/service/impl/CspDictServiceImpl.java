package com.citc.nce.auth.csp.dict.service.impl;

import com.citc.nce.auth.csp.dict.dao.CspDictDao;
import com.citc.nce.auth.csp.dict.entity.CspDictDo;
import com.citc.nce.auth.csp.dict.service.CspDictService;
import com.citc.nce.auth.csp.dict.vo.CspDictReq;
import com.citc.nce.auth.csp.dict.vo.CspDictResp;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>csp-首页</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 14:51
 */
@Service
@Slf4j
public class CspDictServiceImpl implements CspDictService {

    @Autowired
    private CspDictDao dictDao;

    @Override
    public PageResult<CspDictResp> queryList(CspDictReq req) {
        LambdaQueryWrapperX<CspDictDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(CspDictDo::getDictType, req.getDictType());
        Long count = dictDao.selectCount(queryWrapperX);
        PageResult result = new PageResult();
        result.setTotal(0L);
        if (count > 0) {
            List<CspDictDo> cspDictDoList = dictDao.selectPageList(req, queryWrapperX);
            result.setList(cspDictDoList);
            result.setTotal(count);
        }
        return result;
    }
}

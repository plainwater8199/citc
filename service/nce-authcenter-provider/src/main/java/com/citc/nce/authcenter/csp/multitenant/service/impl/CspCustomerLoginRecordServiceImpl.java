package com.citc.nce.authcenter.csp.multitenant.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.authcenter.csp.customerLoginRecord.vo.CspCustomerLoginRecordReq;
import com.citc.nce.authcenter.csp.customerLoginRecord.vo.CspCustomerLoginRecordResp;
import com.citc.nce.authcenter.csp.multitenant.dao.CspCustomerLoginRecordMapper;
import com.citc.nce.authcenter.csp.multitenant.entity.CspCustomerLoginRecord;
import com.citc.nce.authcenter.csp.multitenant.service.CspCustomerLoginRecordService;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author jiancheng
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CspCustomerLoginRecordServiceImpl extends ServiceImpl<CspCustomerLoginRecordMapper, CspCustomerLoginRecord> implements CspCustomerLoginRecordService {

    @Autowired
    CspCustomerLoginRecordMapper mapper;
    @Override
    public List<CspCustomerLoginRecordResp> queryList(CspCustomerLoginRecordReq req) {
        PageParam page = new PageParam();
        page.setPageSize(req.getPageSize());
        page.setPageNo(req.getPageNo());
        LambdaQueryWrapperX<CspCustomerLoginRecord> queryWrapperX = new LambdaQueryWrapperX<>();
        if (CollectionUtils.isNotEmpty(req.getCustomerIdList())) {
            queryWrapperX.in(CspCustomerLoginRecord::getCustomerId, req.getCustomerIdList());
        }
        if (null != req.getStartTime()) {
            queryWrapperX.ge(CspCustomerLoginRecord::getCreateTime, req.getStartTime());
        }
        if (null != req.getEndTime()) {
            queryWrapperX.le(CspCustomerLoginRecord::getCreateTime, req.getEndTime());
        }
        List<CspCustomerLoginRecord> loginRecords = mapper.selectList(queryWrapperX);
        if (CollectionUtils.isNotEmpty(loginRecords)) {
            List<CspCustomerLoginRecordResp> resp = BeanUtil.copyToList(loginRecords, CspCustomerLoginRecordResp.class);
            return resp;
        }
        return null;
    }
}

package com.citc.nce.auth.identification.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.citc.nce.auth.identification.dao.IdentificationAuditRecordDao;
import com.citc.nce.auth.identification.entity.IdentificationAuditRecordDo;
import com.citc.nce.auth.identification.service.IdentificationAuditRecordService;
import com.citc.nce.auth.identification.vo.req.ViewRemarkReq;
import com.citc.nce.auth.identification.vo.resp.IdentificationAuditResp;
import com.citc.nce.misc.constant.NumCode;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @authoer:ldy
 * @createDate:2022/7/14 22:23
 * @description:
 */
@Service
public class IdentificationAuditRecordServiceImpl implements IdentificationAuditRecordService {

    @Resource
    private IdentificationAuditRecordDao identificationAuditRecordDao;


    @Override
    public List<IdentificationAuditResp> getIdentificationAudits(Long identificationId) {
        List<IdentificationAuditRecordDo> identificationAuditRecordDos = identificationAuditRecordDao.selectList(IdentificationAuditRecordDo::getIdentificationId, identificationId);
        return BeanUtil.copyToList(identificationAuditRecordDos, IdentificationAuditResp.class);
    }

    /**
     * admin 端view 备注 审核list
     *
     * @param req
     * @return
     */
    @Override
    public List<IdentificationAuditResp> viewRemarkHistory(ViewRemarkReq req) {
        LambdaQueryWrapperX<IdentificationAuditRecordDo> queryWrapper = new LambdaQueryWrapperX<IdentificationAuditRecordDo>();
        queryWrapper.eq(IdentificationAuditRecordDo::getUserId, req.getClientUserId())
                .eq(IdentificationAuditRecordDo::getIdentificationId, req.getIdentificationId())
                .gt(IdentificationAuditRecordDo::getAuditStatus, NumCode.ONE.getCode())
                .orderByDesc(IdentificationAuditRecordDo::getUpdateTime);
        List<IdentificationAuditRecordDo> list = identificationAuditRecordDao.selectList(queryWrapper);
        List<IdentificationAuditResp> resultList = new ArrayList<IdentificationAuditResp>();
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(i ->
                    resultList.add(new IdentificationAuditResp()
                            .setAuditTime(i.getUpdateTime())
                            .setAuditAccount(i.getReviewer())
                            .setAuditRemark(i.getAuditRemark()))
            );
        }
        return resultList;
    }

    @Override
    public void addAuditRecord(IdentificationAuditRecordDo recordDo) {
        identificationAuditRecordDao.insert(recordDo);
    }

}

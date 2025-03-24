package com.citc.nce.auth.certificate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.citc.nce.auth.certificate.dao.CertificateOptionsDao;
import com.citc.nce.auth.certificate.dao.UserCertificateDao;
import com.citc.nce.auth.certificate.dao.UserCertificateOptionsDao;
import com.citc.nce.auth.certificate.entity.CertificateOptionsDo;
import com.citc.nce.auth.certificate.entity.UserCertificateDo;
import com.citc.nce.auth.certificate.entity.UserCertificateOptionsDo;
import com.citc.nce.auth.certificate.service.UserCertificateService;
import com.citc.nce.auth.certificate.vo.resp.UserCertificateResp;
import com.citc.nce.auth.ticket.vo.req.GetCertificateOptionsReq;
import com.citc.nce.auth.user.vo.resp.ListResp;
import com.citc.nce.misc.constant.NumCode;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserCertificateServiceImpl implements UserCertificateService {

    @Resource
    private UserCertificateOptionsDao userCertificateOptionsDao;

    @Resource
    private UserCertificateDao userCertificateDao;

    @Resource
    private CertificateOptionsDao certificateOptionsDao;


    /**
     * 获取用户资质列表 包含用户未有的资质也返回，只是做了未申请的标记
     *
     * @param userId
     * @return
     */
    @Override
    public List<UserCertificateResp> getCertificateOptionsAll(String userId) {
        return userCertificateOptionsDao.selectCertificateOptionsList(userId);
    }

    @Override
    public ListResp getUserCertificateByUserId(GetCertificateOptionsReq req) {
        LambdaQueryWrapperX<CertificateOptionsDo> queryWrapper = new LambdaQueryWrapperX<CertificateOptionsDo>();
        queryWrapper.eq(CertificateOptionsDo::getUserId, req.getUserId()).eq(CertificateOptionsDo::getDeleted, NumCode.ZERO.getCode()).eq(CertificateOptionsDo::getCertificateApplyStatus, NumCode.THREE.getCode()).eq(CertificateOptionsDo::getCertificateStatus, NumCode.ZERO.getCode());
        List<CertificateOptionsDo> dataList = certificateOptionsDao.selectList(queryWrapper);
        List<Integer> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dataList))
            dataList.forEach(i -> list.add(i.getCertificateId()));
        return new ListResp().setList(list);
    }

    /**
     * 获取用户资质列表
     *
     * @param userId
     * @return
     */
    @Override
    public List<UserCertificateResp> getCertificateOptions(String userId) {
        return userCertificateOptionsDao.selectCertificateOptionsList(userId);
    }

    /**
     * @return
     */
    @Override
    public List<UserCertificateDo> getUserCertificate() {
        LambdaQueryWrapperX<UserCertificateDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(UserCertificateDo::getProtal, NumCode.ONE.getCode());
        return userCertificateDao.selectList(queryWrapper);
    }

    @Override
    public Integer getRemark() {
        QueryWrapper<UserCertificateOptionsDo> wrapper = new QueryWrapper<>();
//        (1待审核,2审核不通过,3审核通过)
        wrapper.eq("certificate_apply_status", 1);
        wrapper.eq("deleted", 0);

        Long acount = userCertificateOptionsDao.selectCount(wrapper);

        return acount.intValue();
    }
}

package com.citc.nce.auth.csp.videoSms.account;

import com.citc.nce.auth.csp.recharge.vo.TariffOptions;
import com.citc.nce.auth.csp.videoSms.account.dao.CspVideoSmsAccountDao;
import com.citc.nce.auth.csp.videoSms.account.entity.CspVideoSmsAccountDo;
import com.citc.nce.auth.csp.videoSms.account.service.CspVideoSmsAccountService;
import com.citc.nce.auth.csp.videoSms.account.vo.*;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.auth.prepayment.vo.MessageAccountSearchVo;
import com.citc.nce.auth.prepayment.vo.VideoSmsMessageAccountListVo;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.RsaUtil;
import com.citc.nce.conf.CommonKeyPairConfig;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p></p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/27 11:27
 */
@RestController
public class CspVideoSmsAccountController implements CspVideoSmsAccountApi {

    @Resource
    private CommonKeyPairConfig keyPairConfig;
    @Resource
    private CspVideoSmsAccountService service;

    @Autowired
    private CspVideoSmsAccountDao cspVideoSmsAccountDao;

    @Override
    public PageResult<CspVideoSmsAccountResp> queryList(CspVideoSmsAccountReq req) {
        return service.queryList(req);
    }

    @Override
    public PageResult<CspVideoSmsAccountResp> queryListByLoginUser() {
        return service.queryListByLoginUser();
    }

    @Override
    public List<String> queryAccountIdListByUserList(CspVideoSmsAccountQueryAccountIdReq req) {
        return service.queryAccountIdListByUserList(req.getUserIds());
    }

    @Override
    public List<String> queryAccountIdListByCspUserId(CspVideoSmsAccountQueryAccountIdReq req) {
        return service.queryAccountIdListByCspUserId(req.getUserIds());
    }

    @Override
    public int save(CspVideoSmsAccountSaveReq req) {
        return service.save(req);
    }

    @Override
    public int edit(CspVideoSmsAccountEditReq req) {
        return service.edit(req);
    }

    @Override
    public int updateStatus(CspVideoSmsAccountUpdateStatusReq req) {
        return service.updateStatus(req);
    }

    @Override
    public int delete(CspVideoSmsAccountDeleteReq req) {
        return service.delete(req);
    }

    @Override
    public CspVideoSmsAccountDetailResp queryDetail(CspVideoSmsAccountQueryDetailReq req) {
        return service.queryDetail(req);
    }

    @Override
    public List<CspVideoSmsAccountResp> queryListByAccountIds(List<String> accountIds) {
        return service.queryListByAccountIds(accountIds);
    }

    @Override
    public void deductResidue(CspVideoSmsAccountDeductResidueReq req) {
        service.deductResidue(req);
    }

    @Override
    public CspVideoSmsAccountDetailResp queryDetailInner(String accountId) {
        if (StringUtils.isBlank(accountId)) {
            throw new BizException("accountId 不能为空");
        }
        CspVideoSmsAccountDo cspVideoSmsAccountDo = cspVideoSmsAccountDao.selectOne(CspVideoSmsAccountDo::getAccountId, accountId);
        CspVideoSmsAccountDetailResp detailResp = new CspVideoSmsAccountDetailResp();
        BeanUtils.copyProperties(cspVideoSmsAccountDo, detailResp);
        detailResp.setAppSecret(RsaUtil.decryptByPrivateKey(keyPairConfig.getPrivateKey(), cspVideoSmsAccountDo.getAppSecret()));
        return detailResp;
    }

    @Override
    public PageResult<VideoSmsMessageAccountListVo> selectVideoSmsMessageAccountByCustomer(MessageAccountSearchVo searchVo) {
        return service.selectVideoSmsMessageAccountByCustomer(searchVo);
    }

    @Override
    public Map<String, String> queryAccountIdNameMapByCustomerId(String customerId) {
        return service.queryAccountINameMapByCustomerId(customerId);
    }

    @Override
    public List<CspVideoSmsAccountDetailResp> queryDetailInnerByAccountIds(List<String> mediaSmsAccountIds) {
        if (CollectionUtils.isEmpty(mediaSmsAccountIds)) {
            throw new BizException("accountId 不能为空");
        }
        List<CspVideoSmsAccountDetailResp> respList = new ArrayList<>();
        LambdaQueryWrapperX<CspVideoSmsAccountDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.in(CspVideoSmsAccountDo::getAccountId, mediaSmsAccountIds);
        List<CspVideoSmsAccountDo> cspVideoSmsAccountDos = cspVideoSmsAccountDao.selectList(queryWrapperX);
        if(!CollectionUtils.isEmpty(cspVideoSmsAccountDos)){
            for(CspVideoSmsAccountDo cspVideoSmsAccountDo : cspVideoSmsAccountDos) {
                CspVideoSmsAccountDetailResp detailResp = new CspVideoSmsAccountDetailResp();
                BeanUtils.copyProperties(cspVideoSmsAccountDo, detailResp);
                detailResp.setAppSecret(RsaUtil.decryptByPrivateKey(keyPairConfig.getPrivateKey(), cspVideoSmsAccountDo.getAppSecret()));
                respList.add(detailResp);
            }
        }
        return respList;
    }
}

package com.citc.nce.auth.csp.sms.account;

import com.citc.nce.auth.csp.sms.account.dao.CspSmsAccountDao;
import com.citc.nce.auth.csp.sms.account.entity.CspSmsAccountDo;
import com.citc.nce.auth.csp.sms.account.service.CspSmsAccountService;
import com.citc.nce.auth.csp.sms.account.vo.*;
import com.citc.nce.auth.csp.videoSms.account.entity.CspVideoSmsAccountDo;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountDetailResp;
import com.citc.nce.auth.prepayment.vo.MessageAccountSearchVo;
import com.citc.nce.auth.prepayment.vo.SmsMessageAccountListVo;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.RsaUtil;
import com.citc.nce.conf.CommonKeyPairConfig;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class CspSmsAccountController implements CspSmsAccountApi {
    @Resource
    private CommonKeyPairConfig keyPairConfig;

    @Resource
    private CspSmsAccountService cspSmsAccountService;

    @Resource
    private CspSmsAccountDao cspSmsAccountDao;

    @Override
    public int save(CspSmsAccountSaveReq req) {
        return cspSmsAccountService.save(req);
    }

    @Override
    public int edit(CspSmsAccountEditReq req) {
        return cspSmsAccountService.edit(req);
    }

    @Override
    public int updateStatus(CspSmsAccountUpdateStatusReq req) {
        return cspSmsAccountService.updateStatus(req);
    }

    @Override
    public int delete(CspSmsAccountDeleteReq req) {
        return cspSmsAccountService.delete(req);
    }

    @Override
    public CspSmsAccountDetailResp queryDetail(CspSmsAccountQueryDetailReq req) {
        return cspSmsAccountService.queryDetail(req);
    }

    @Override
    public PageResult<CspSmsAccountResp> queryListByLoginUser() {
        return cspSmsAccountService.queryListByLoginUser();
    }

    @Override
    public PageResult<CspSmsAccountResp> queryList(CspSmsAccountReq req) {
        return cspSmsAccountService.queryList(req);
    }

    @Override
    public List<String> queryAccountIdList() {
        return cspSmsAccountDao.selectList().stream().map(CspSmsAccountDo::getAccountId).collect(Collectors.toList());
    }

    @Override
    public PageResult<CspSmsAccountChatbotResp> queryListChatbot(CspSmsAccountChatbotReq req) {
        return cspSmsAccountService.queryListChatbot(req);
    }

    @Override
    public List<String> queryAccountIdListByUserList(CspSmsAccountQueryAccountIdReq req) {
        return cspSmsAccountService.queryAccountIdListByUserList(req.getUserIds());
    }

    @Override
    public List<String> queryAccountIdListByCspUserId(CspSmsAccountQueryAccountIdReq req) {
        return cspSmsAccountService.queryAccountIdListByCspUserId(req.getUserIds());
    }

    @Override
    public List<CspSmsAccountResp> queryListByAccountIds(List<String> accountIds) {
        return cspSmsAccountService.queryListByAccountIds(accountIds);
    }

    @Override
    public void deductResidue(CspSmsAccountDeductResidueReq req) {
        cspSmsAccountService.deductResidue(req);
    }

    @Override
    public PageResult<CspSmsAccountChatbotResp> queryListChatbotSelectOption(CspSmsAccountChatbotReq req) {
        return cspSmsAccountService.queryListChatbot(req);
    }

    @Override
    public CspSmsAccountDetailResp queryDetailInner(String accountId) {
        CspSmsAccountDo cspSmsAccountDo = cspSmsAccountDao.selectOne(CspSmsAccountDo::getAccountId, accountId);
        if (Objects.isNull(cspSmsAccountDo)) return null;
        CspSmsAccountDetailResp detailResp = new CspSmsAccountDetailResp();
        BeanUtils.copyProperties(cspSmsAccountDo, detailResp);
        detailResp.setAppSecret(RsaUtil.decryptByPrivateKey(keyPairConfig.getPrivateKey(), cspSmsAccountDo.getAppSecret()));
        return detailResp;
    }

    @Override
    public PageResult<SmsMessageAccountListVo> selectSmsMessageAccountByCustomer(MessageAccountSearchVo searchVo) {
        return cspSmsAccountService.selectSmsMessageAccountByCustomer(searchVo);
    }

    @Override
    public Map<String, String> queryAccountIdNameMapByCustomerId(String customerId) {
        return cspSmsAccountService.queryAccountIdNameMapByCustomerId(customerId);
    }

    @Override
    public List<CspSmsAccountDetailResp> queryDetailInnerByAccountIds(List<String> smsAccountIds) {
        List<CspSmsAccountDetailResp> respList = new ArrayList<>();
        if (CollectionUtils.isEmpty(smsAccountIds)) {
            throw new BizException("smsAccountIds 不能为空");
        }
        LambdaQueryWrapperX<CspSmsAccountDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.in(CspSmsAccountDo::getAccountId, smsAccountIds);
        List<CspSmsAccountDo> smsAccountDos = cspSmsAccountDao.selectList(queryWrapperX);
        if(!CollectionUtils.isEmpty(smsAccountDos)){
            for(CspSmsAccountDo smsAccountDo : smsAccountDos) {
                CspSmsAccountDetailResp detailResp = new CspSmsAccountDetailResp();
                BeanUtils.copyProperties(smsAccountDo, detailResp);
                detailResp.setAppSecret(RsaUtil.decryptByPrivateKey(keyPairConfig.getPrivateKey(), smsAccountDo.getAppSecret()));
                respList.add(detailResp);
            }
        }
        return respList;
    }
}

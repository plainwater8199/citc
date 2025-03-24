package com.citc.nce.auth.csp.videoSms.signature.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.citc.nce.auth.csp.videoSms.account.dao.CspVideoSmsAccountDao;
import com.citc.nce.auth.csp.videoSms.account.entity.CspVideoSmsAccountDo;
import com.citc.nce.auth.csp.videoSms.signature.dao.CspVideoSmsSignatureDao;
import com.citc.nce.auth.csp.videoSms.signature.entity.CspVideoSmsSignatureDo;
import com.citc.nce.auth.csp.videoSms.signature.service.CspVideoSmsSignatureService;
import com.citc.nce.auth.csp.videoSms.signature.vo.CspVideoSmsSignatureReq;
import com.citc.nce.auth.csp.videoSms.signature.vo.CspVideoSmsSignatureResp;
import com.citc.nce.auth.csp.videoSms.signature.vo.CspVideoSmsSignatureSaveReq;
import com.citc.nce.auth.csp.videoSms.signature.vo.CspVideoSmsSignatureSubmitReq;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p></p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 14:51
 */
@Service
@Slf4j
public class CspVideoSmsSignatureServiceImpl implements CspVideoSmsSignatureService {
    @Autowired
    private CspVideoSmsSignatureDao cspVideoSmsSignatureDao;
    @Autowired
    private CspVideoSmsAccountDao cspVideoSmsAccountDao;

    @Override
    public List<CspVideoSmsSignatureResp> getSignatureByAccountId(CspVideoSmsSignatureReq req) {
        CspVideoSmsAccountDo account = cspVideoSmsAccountDao.selectAccountId(req.getAccountId());
        if (Objects.isNull(account)) {
            throw new BizException("账号不存在");
        }
        BaseUser user = SessionContextUtil.getUser();
        if (user != null && user.getIsCustomer() != null && user.getIsCustomer()) {
            System.out.println("userId = " + user.getUserId()+", customerId = " + account.getCustomerId()+", accountId = " + account.getAccountId());
            if (!user.getUserId().equals(account.getCustomerId())) {
                throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
            }
        } else if (user != null && user.getIsCustomer() != null && !user.getIsCustomer()) {
            if (!user.getUserId().equals(account.getCspId())) {
                throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
            }
        }

        List<CspVideoSmsSignatureDo> list = getCspVideoSmsSignatureDo(req.getAccountId(), req.getType());
        if (CollectionUtils.isNotEmpty(list)) {
            return BeanUtil.copyToList(list, CspVideoSmsSignatureResp.class);
        }
        return null;
    }

    @Override
    public List<CspVideoSmsSignatureResp> getSignatureByAccountIdsAndType(List<String> accountIds, Integer type) {
        if (CollectionUtils.isNotEmpty(accountIds)) {
            LambdaQueryWrapperX<CspVideoSmsSignatureDo> queryWrapperX = new LambdaQueryWrapperX<>();
            queryWrapperX.in(CspVideoSmsSignatureDo::getAccountId, accountIds)
                    .eq(CspVideoSmsSignatureDo::getType, type);
            List<CspVideoSmsSignatureDo> cspVideoSmsSignatureDoList = cspVideoSmsSignatureDao.selectList(queryWrapperX);
            if (CollectionUtils.isNotEmpty(cspVideoSmsSignatureDoList)) {
                List<CspVideoSmsSignatureResp> cspVideoSmsSignatureResps = BeanUtil.copyToList(cspVideoSmsSignatureDoList, CspVideoSmsSignatureResp.class);
                return cspVideoSmsSignatureResps;
            }
        }
        return null;
    }

    private List<CspVideoSmsSignatureDo> getCspVideoSmsSignatureDo(String accountId, Integer type) {
        LambdaQueryWrapperX<CspVideoSmsSignatureDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(CspVideoSmsSignatureDo::getAccountId, accountId)
                .eq(CspVideoSmsSignatureDo::getType, type);
        return cspVideoSmsSignatureDao.selectList(queryWrapperX);
    }

    @Override
    public int submit(CspVideoSmsSignatureSubmitReq req) {
        CspVideoSmsAccountDo account = cspVideoSmsAccountDao.selectAccountId(req.getAccountId());

        if (!SessionContextUtil.getLoginUser().getUserId().equals(account.getCreator())) {
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }
        // 需要删除的数据
        List<Long> deleteList = new ArrayList<>();
        // 需要更新的数据
        List<CspVideoSmsSignatureDo> updateList = new ArrayList<>();
        List<Long> updateIdList = new ArrayList<>();
        // 新插入的数据
        List<CspVideoSmsSignatureDo> insertLit = new ArrayList<>();
        // 处理需要新增和更新的数据
        if (CollectionUtils.isNotEmpty(req.getSignatureList())) {
            for (CspVideoSmsSignatureSaveReq save :
                    req.getSignatureList()) {
                CspVideoSmsSignatureDo cspVideoSmsSignatureDo = new CspVideoSmsSignatureDo();
                if (ObjectUtils.isNotEmpty(save.getId())) {
                    BeanUtils.copyProperties(save, cspVideoSmsSignatureDo);
                    updateList.add(cspVideoSmsSignatureDo);
                    updateIdList.add(save.getId());
                } else {
                    BeanUtils.copyProperties(save, cspVideoSmsSignatureDo);
                    insertLit.add(cspVideoSmsSignatureDo);
                }
            }
        }
        // 已经存在库中的数据
        List<CspVideoSmsSignatureDo> smsSignatureDoList = getCspVideoSmsSignatureDo(req.getAccountId(), req.getType());
        // 如果库中不存在数据，则本次是首次写入签名，直接新增并返回
        if (CollectionUtils.isEmpty(smsSignatureDoList)) {
            cspVideoSmsSignatureDao.insertBatch(insertLit);
            return 0;
        }
        // 获取需要删除的数据
        for (CspVideoSmsSignatureDo signatureDo :
                smsSignatureDoList) {
            // 更新id里面没有的数据就是需要被删除的
            if (!updateIdList.contains(signatureDo.getId())) {
                deleteList.add(signatureDo.getId());
            }
        }

        if (CollectionUtils.isNotEmpty(insertLit)) {
            cspVideoSmsSignatureDao.insertBatch(insertLit);
        }
        if (CollectionUtils.isNotEmpty(updateList)) {
            cspVideoSmsSignatureDao.updateBatch(updateList);
        }
        if (CollectionUtils.isNotEmpty(deleteList)) {
            cspVideoSmsSignatureDao.deleteBatchIds(deleteList);
        }

        return 0;
    }

    @Override
    public List<CspVideoSmsSignatureResp> getSignatureByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        LambdaQueryWrapperX<CspVideoSmsSignatureDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.in(CspVideoSmsSignatureDo::getId, ids);
        List<CspVideoSmsSignatureDo> cspVideoSmsSignatureDoList = cspVideoSmsSignatureDao.selectList(queryWrapperX);
        if (CollectionUtils.isNotEmpty(cspVideoSmsSignatureDoList)) {
            List<CspVideoSmsSignatureResp> cspVideoSmsSignatureResps = BeanUtil.copyToList(cspVideoSmsSignatureDoList, CspVideoSmsSignatureResp.class);
            return cspVideoSmsSignatureResps;
        }
        return null;
    }
}

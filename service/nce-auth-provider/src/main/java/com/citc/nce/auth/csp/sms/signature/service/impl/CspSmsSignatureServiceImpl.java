package com.citc.nce.auth.csp.sms.signature.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.citc.nce.auth.csp.sms.account.dao.CspSmsAccountDao;
import com.citc.nce.auth.csp.sms.account.entity.CspSmsAccountDo;
import com.citc.nce.auth.csp.sms.signature.dao.CspSmsSignatureDao;
import com.citc.nce.auth.csp.sms.signature.entity.CspSmsAccountSignatureDo;
import com.citc.nce.auth.csp.sms.signature.service.CspSmsSignatureService;
import com.citc.nce.auth.csp.sms.signature.vo.CspSmsSignatureReq;
import com.citc.nce.auth.csp.sms.signature.vo.CspSmsSignatureResp;
import com.citc.nce.auth.csp.sms.signature.vo.CspSmsSignatureSaveReq;
import com.citc.nce.auth.csp.sms.signature.vo.CspSmsSignatureSubmitReq;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.mybatis.core.entity.BaseDo;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
public class CspSmsSignatureServiceImpl implements CspSmsSignatureService {

    @Autowired
    private CspSmsAccountDao cspSmsAccountDao;
    @Resource
    private CspSmsSignatureDao cspSmsSignatureDao;

    @Override
    public List<CspSmsSignatureResp> getSignatureByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        LambdaQueryWrapperX<CspSmsAccountSignatureDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.in(CspSmsAccountSignatureDo::getId, ids);
        List<CspSmsAccountSignatureDo> cspSmsSignatureDoList = cspSmsSignatureDao.selectList(queryWrapperX);
        if (CollectionUtils.isNotEmpty(cspSmsSignatureDoList)) {
            List<CspSmsSignatureResp> cspSmsSignatureResps = BeanUtil.copyToList(cspSmsSignatureDoList, CspSmsSignatureResp.class);
            return cspSmsSignatureResps;
        }
        return null;
    }

    public List<CspSmsSignatureResp> getSignatureByIdsDelete(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        List<CspSmsAccountSignatureDo> cspSmsSignatureDoList = cspSmsSignatureDao.getCspSmsAccountSignatureDoList(ids.stream().map(String::valueOf).collect(Collectors.joining(",")));
        if (CollectionUtils.isNotEmpty(cspSmsSignatureDoList)) {
            List<CspSmsSignatureResp> cspSmsSignatureResps = BeanUtil.copyToList(cspSmsSignatureDoList, CspSmsSignatureResp.class);
            return cspSmsSignatureResps;
        }
        return null;
    }

    @Override
    @Transactional
    public int submit(CspSmsSignatureSubmitReq req) {
        // 需要更新的数据
        List<CspSmsAccountSignatureDo> updateList = new ArrayList<>();
        List<Long> updateIdList = new ArrayList<>();
        // 新插入的数据
        List<CspSmsAccountSignatureDo> insertLit = new ArrayList<>();
        // 处理需要新增和更新的数据
        if (CollectionUtils.isNotEmpty(req.getSignatureList())) {
            for (CspSmsSignatureSaveReq save : req.getSignatureList()) {
                CspSmsAccountSignatureDo cspSmsSignatureDo = new CspSmsAccountSignatureDo();
                BeanUtils.copyProperties(save, cspSmsSignatureDo);
                cspSmsSignatureDo.setAccountId(req.getAccountId());
                cspSmsSignatureDo.setType(req.getType());
                if (ObjectUtils.isNotEmpty(save.getId())) {
                    updateList.add(cspSmsSignatureDo);
                    updateIdList.add(save.getId());
                } else {
                    insertLit.add(cspSmsSignatureDo);
                }
            }
        }
        //越权 第一次添加签名不验证
        if (CollectionUtils.isNotEmpty(updateIdList)) {
            List<String> createrList = cspSmsSignatureDao.selectBatchIds(updateIdList).stream()
                    .map(BaseDo::getCreator).distinct()
                    .collect(Collectors.toList());
            //存在多个签名创建者或者不是当前用户创建的
            if (createrList.size() > 1 || !SessionContextUtil.getLoginUser().getUserId().equals(createrList.get(0))) {
                throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
            }
        }

        // 获取需要删除的数据 数据库中存储，更新列表中不存在
        Set<Long> deleteList = getCspSmsSignatureDo(req.getAccountId(), req.getType(), SessionContextUtil.getLoginUser().getUserId())
                .stream()
                .map(BaseDo::getId)
                .filter(s -> !updateIdList.contains(s)).collect(Collectors.toSet());

        if (CollectionUtils.isNotEmpty(insertLit)) {
            cspSmsSignatureDao.insertBatch(insertLit);
        }
        if (CollectionUtils.isNotEmpty(updateList)) {
            cspSmsSignatureDao.updateBatch(updateList);
        }
        if (CollectionUtils.isNotEmpty(deleteList)) {
            cspSmsSignatureDao.deleteBatchIds(deleteList);
        }
        return 0;
    }

    @Override
    public List<CspSmsSignatureResp> getSignatureByAccountIdsAndType(List<String> accountIds, Integer type) {
        if (CollectionUtils.isNotEmpty(accountIds)) {
            LambdaQueryWrapperX<CspSmsAccountSignatureDo> queryWrapperX = new LambdaQueryWrapperX<>();
            queryWrapperX.in(CspSmsAccountSignatureDo::getAccountId, accountIds)
                    .eq(CspSmsAccountSignatureDo::getType, type);
            List<CspSmsAccountSignatureDo> cspSmsSignatureDoList = cspSmsSignatureDao.selectList(queryWrapperX);
            if (CollectionUtils.isNotEmpty(cspSmsSignatureDoList)) {
                List<CspSmsSignatureResp> cspSmsSignatureResps = BeanUtil.copyToList(cspSmsSignatureDoList, CspSmsSignatureResp.class);
                return cspSmsSignatureResps;
            }
        }
        return null;
    }

    @Override
    public List<CspSmsSignatureResp> getSignatureByAccountId(CspSmsSignatureReq req) {
        CspSmsAccountDo accountDo = cspSmsAccountDao.selectOne(CspSmsAccountDo::getAccountId, req.getAccountId());
        if (Objects.isNull(accountDo)) {
            throw new BizException("账号不存在");
        }
        BaseUser user = SessionContextUtil.getLoginUser();
        if (user.getIsCustomer()) {
            if (!user.getUserId().equals(accountDo.getCustomerId())) {
                throw new BizException("不是你的账号");
            }
        } else if (!user.getUserId().equals(accountDo.getCreator())) {
            throw new BizException("不是你的账号");
        }
        List<CspSmsAccountSignatureDo> cspSmsSignatureDoList = getCspSmsSignatureDo(req.getAccountId(), req.getType(), null);
        if (CollectionUtils.isNotEmpty(cspSmsSignatureDoList)) {
            return BeanUtil.copyToList(cspSmsSignatureDoList, CspSmsSignatureResp.class);
        }
        return null;
    }

    private List<CspSmsAccountSignatureDo> getCspSmsSignatureDo(String accountId, Integer type, String userId) {
        LambdaQueryWrapperX<CspSmsAccountSignatureDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(CspSmsAccountSignatureDo::getAccountId, accountId)
                .eq(CspSmsAccountSignatureDo::getType, type)
                .eq(StringUtils.hasLength(userId), BaseDo::getCreator, SessionContextUtil.getUser().getUserId())
                .orderByDesc(CspSmsAccountSignatureDo::getCreateTime);
        return cspSmsSignatureDao.selectList(queryWrapperX);
    }

}

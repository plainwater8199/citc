package com.citc.nce.auth.invoice.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.invoice.dao.InvoiceManageOpLogMapper;
import com.citc.nce.auth.invoice.domain.InvoiceManageOpLog;
import com.citc.nce.auth.invoice.enums.InvoiceStatus;
import com.citc.nce.auth.invoice.service.IInvoiceManageOpLogService;
import com.citc.nce.authcenter.auth.AuthApi;
import com.citc.nce.authcenter.auth.vo.resp.UserInfoDetailResp;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.CustomerDetailReq;
import com.citc.nce.authcenter.csp.vo.CustomerDetailResp;
import com.citc.nce.authcenter.csp.vo.UserEnterpriseInfoVo;
import com.citc.nce.common.util.SessionContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 发票csp操作记录 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2024-03-07 03:03:20
 */
@Service
public class InvoiceManageOpLogServiceImpl extends ServiceImpl<InvoiceManageOpLogMapper, InvoiceManageOpLog> implements IInvoiceManageOpLogService {

    @Autowired
    private CspCustomerApi customerApi;

    @Autowired
    private AuthApi authApi;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void saveLog(Long imId, InvoiceStatus invoiceStatus, String remark) {
        InvoiceManageOpLog opLog = new InvoiceManageOpLog();
        opLog.setImId(imId);
        opLog.setOpIdentity(!SessionContextUtil.getLoginUser().getIsCustomer());
        opLog.setProcessContent(invoiceStatus);
        opLog.setRemark(remark);
        save(opLog);
    }

    @Override
    public List<InvoiceManageOpLog> listAuditLog(Long imId) {
        List<InvoiceManageOpLog> list = lambdaQuery()
                .eq(InvoiceManageOpLog::getImId, imId)
                .orderByDesc(InvoiceManageOpLog::getCreateTime)
                .list();
        if (CollectionUtils.isEmpty(list)) return list;

        Map<Boolean, List<InvoiceManageOpLog>> group = list.stream().collect(Collectors.groupingBy(InvoiceManageOpLog::getOpIdentity));
        List<InvoiceManageOpLog> cspList = group.get(true);//只处理csp的
        if (!CollectionUtils.isEmpty(cspList)) {
            cspList.forEach(s -> {
                if (s.getCreator().length() == 15) {
                    CustomerDetailResp detail = customerApi.getDetailByCustomerId(new CustomerDetailReq().setCustomerId(s.getCreator()));
                    if (Objects.nonNull(detail)) {
                        s.setCreatorName(detail.getName());
                    }
                } else {
                    UserInfoDetailResp user = authApi.userInfoDetailByUserId(s.getCreator());
                    if (Objects.nonNull(user)) {
                        s.setCreatorName(user.getName());
                    }
                }
            });
        }

        List<InvoiceManageOpLog> cusList = group.get(false);
        if (!CollectionUtils.isEmpty(cusList)) {
            Map<String, String> map = customerApi.getUserEnterpriseInfoByUserIds(cusList.stream().map(InvoiceManageOpLog::getCreator).collect(Collectors.toSet()))
                    .stream().collect(Collectors.toMap(UserEnterpriseInfoVo::getUserId, UserEnterpriseInfoVo::getEnterpriseAccountName));
            cusList.forEach(s -> s.setCreatorName(map.get(s.getCreator())));
        }
        return list;
    }


    @Override
    public String getLastRemake(Long imId, InvoiceStatus status) {
        List<InvoiceManageOpLog> list = lambdaQuery()
                .eq(InvoiceManageOpLog::getImId, imId)
                .eq(InvoiceManageOpLog::getProcessContent, status)
                .orderByDesc(InvoiceManageOpLog::getCreateTime)
                .list();
        if (CollectionUtils.isEmpty(list)) return "CSP未填写原因";
        InvoiceManageOpLog manageOpLog = list.get(0);
        if (Objects.isNull(manageOpLog) || StrUtil.isBlankIfStr(manageOpLog.getRemark())) return "CSP未填写原因";
        return manageOpLog.getRemark();
    }
}

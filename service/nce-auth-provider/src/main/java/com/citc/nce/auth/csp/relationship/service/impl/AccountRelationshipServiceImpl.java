package com.citc.nce.auth.csp.relationship.service.impl;

import com.citc.nce.auth.csp.csp.service.CspService;
import com.citc.nce.auth.csp.customer.dao.CspCustomerDao;
import com.citc.nce.auth.csp.customer.entity.CspCustomerDo;
import com.citc.nce.auth.csp.relationship.dao.AccountRelationshipDao;
import com.citc.nce.auth.csp.relationship.entity.AccountRelationshipDo;
import com.citc.nce.auth.csp.relationship.service.AccountRelationshipService;
import com.citc.nce.auth.csp.relationship.vo.RelationshipResp;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>csp-客户管理</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 14:51
 */
@Service
@Slf4j
public class AccountRelationshipServiceImpl implements AccountRelationshipService {

    @Autowired
    private AccountRelationshipDao dao;

    @Autowired
    private CspCustomerDao cspCustomerDao;
    @Autowired
    private CspService cspService;
    @Autowired
    private CspCustomerApi cspCustomerApi;

    @Override
    public RelationshipResp getCspUserId(String userId) {
        if (StringUtils.isEmpty(userId)) {
            userId = SessionContextUtil.getUser().getUserId();
        }
        LambdaQueryWrapperX<AccountRelationshipDo> queryWrapperX = new LambdaQueryWrapperX();
        queryWrapperX.eq(AccountRelationshipDo::getUserId, userId);
        List<AccountRelationshipDo> relationshipDoList = dao.selectList(queryWrapperX);
//        List<String> userList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(relationshipDoList)) {
            RelationshipResp resp = new RelationshipResp();
            BeanUtils.copyProperties(resp, relationshipDoList.get(0));
            return resp;
        }

        return null;
    }

    @Override
    public List<String> getUserListByCspUserId(String cspUserId) {
        //auth服务没有分表，使用cspCustomerDao查询不到数据，必须调用authCenterApi查询
//        if (StringUtils.isEmpty(cspUserId)) {
//            cspUserId = SessionContextUtil.getUser().getUserId();
//        }
//        LambdaQueryWrapperX<CspCustomerDo> queryWrapperX = new LambdaQueryWrapperX();
//        queryWrapperX.eq(CspCustomerDo::getCspId, cspService.obtainCspId(cspUserId));
//        List<CspCustomerDo> cspCustomerDoList = cspCustomerDao.selectList(queryWrapperX);
//        List<String> userList = new ArrayList<>();
//        if (CollectionUtils.isNotEmpty(cspCustomerDoList)) {
//            for (CspCustomerDo cspCustomerDo : cspCustomerDoList) {
//                userList.add(cspCustomerDo.getCustomerId());
//            }
//        }
//        return userList;
        return Collections.emptyList();
    }
}

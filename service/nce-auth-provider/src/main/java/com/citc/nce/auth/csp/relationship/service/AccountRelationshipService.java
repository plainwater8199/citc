package com.citc.nce.auth.csp.relationship.service;

import com.citc.nce.auth.csp.relationship.vo.RelationshipResp;

import java.util.List;

/**
 * <p>csp-客户管理</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 14:32
 */
public interface AccountRelationshipService {

    RelationshipResp getCspUserId(String userId);

    List<String> getUserListByCspUserId(String cspUserId);
}

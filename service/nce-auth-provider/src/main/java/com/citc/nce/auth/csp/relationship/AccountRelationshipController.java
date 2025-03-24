package com.citc.nce.auth.csp.relationship;

import com.citc.nce.auth.csp.relationship.service.AccountRelationshipService;
import com.citc.nce.auth.csp.relationship.vo.RelationshipReq;
import com.citc.nce.auth.csp.relationship.vo.RelationshipResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>csp-客户管理</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 14:27
 */
@RestController
public class AccountRelationshipController implements RelationshipApi {

    @Autowired
    private AccountRelationshipService service;


    @Override
    public List<String> getUserListByCspUserId(RelationshipReq req) {
        return service.getUserListByCspUserId(req.getUserId());
    }

    @Override
    public RelationshipResp getCspUserId(RelationshipReq req) {
        return service.getCspUserId(req.getUserId());
    }
}

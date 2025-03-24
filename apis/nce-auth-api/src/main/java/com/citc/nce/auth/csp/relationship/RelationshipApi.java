package com.citc.nce.auth.csp.relationship;

import com.citc.nce.auth.csp.relationship.vo.RelationshipReq;
import com.citc.nce.auth.csp.relationship.vo.RelationshipResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/3/10 19:13
 */
@FeignClient(value = "auth-service", contextId = "RelationshipApi", url = "${auth:}")
public interface RelationshipApi {

    @PostMapping("/csp/relationship/getUserListByCspUserId")
    List<String> getUserListByCspUserId(@RequestBody RelationshipReq req);

    @PostMapping("/csp/relationship/getCspUserId")
    RelationshipResp getCspUserId(@RequestBody RelationshipReq req);

}

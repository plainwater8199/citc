package com.citc.nce.auth.csp.recharge;

import com.citc.nce.auth.csp.recharge.vo.ChargeConsumeRecordVo;
import com.citc.nce.auth.csp.relationship.vo.RelationshipReq;
import com.citc.nce.auth.csp.relationship.vo.RelationshipResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/3/10 19:13
 */
@FeignClient(value = "auth-service", contextId = "RechargeApi", url = "${auth:}")
public interface RechargeApi {


    @GetMapping("/csp/table/InitChargeConsumeRecordTable")
    void InitChargeConsumeRecordTable(@RequestParam("cspId") String  cspId);

    /**
     * 添加扣费或返还记录
     * @param chargeConsumeRecordVo
     * @return
     */
    @PostMapping("/recharge/addRecord")
    boolean addRecord(@RequestBody @Validated ChargeConsumeRecordVo chargeConsumeRecordVo);

    /**
     * 更改扣费记录状态为已处理
     * @param messageId
     * @param phone
     * @param msgType
     * @return
     */
    @GetMapping("/recharge/recordCompleted")
    boolean updateRecordCompleted(@RequestParam("messageId") String messageId,@RequestParam("phone") String phone,@RequestParam("msgType") String msgType);
}

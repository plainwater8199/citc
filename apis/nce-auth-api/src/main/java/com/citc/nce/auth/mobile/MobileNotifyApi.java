package com.citc.nce.auth.mobile;

import com.citc.nce.auth.mobile.req.AgentServiceCode;
import com.citc.nce.auth.mobile.req.AuthInfo;
import com.citc.nce.auth.mobile.req.ChatBotConfigInfo;
import com.citc.nce.auth.mobile.req.ChatBotExamine;
import com.citc.nce.auth.mobile.req.ChatBotInfo;
import com.citc.nce.auth.mobile.req.ChatBotSyncInfo;
import com.citc.nce.auth.mobile.req.CustomerStatus;
import com.citc.nce.auth.mobile.req.ServiceCode;
import com.citc.nce.auth.mobile.req.SignedCustomer;
import com.citc.nce.auth.mobile.req.SignedCustomerExamine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(value = "auth-service", contextId = "MobileNotifyApi", url = "${auth:}")
public interface MobileNotifyApi {


    /**
     * 同步chatBot信息
     * @param chatBotSyncInfo 信息实体类
     */
    @PostMapping("/oc/syncchatbot")
    Map<String,String> syncChatBot(@RequestBody ChatBotSyncInfo chatBotSyncInfo);



    /**
     * 订购信息（服务代码）
     * @param serviceCode 请求实体类
     */
    @PostMapping("/oc/syncproduct")
    Map<String,String> syncProduct(@RequestBody ServiceCode serviceCode);


    /**
     * chatbot chatBot注销接口
     * @param chatBotInfo chatbot信息
     */
    @PostMapping("/oc/cancel")
    Map<String,String> chatBotCancel(@RequestBody ChatBotInfo chatBotInfo);

    /**
     * chatbot chatBot注销接口
     * @param chatBotInfo chatbot信息
     */
    @PostMapping("/oc/v1/cancel")
    Map<String, String> chatBotV1Cancel(@RequestBody ChatBotInfo chatBotInfo);


    /**
     * chatbot chatBot注销接口
     * @param chatBotInfo chatbot信息
     */
    @PostMapping("/oc/status")
    Map<String,String> chatBotStatus(@RequestBody ChatBotInfo chatBotInfo);



    /**
     * 固定菜单审核接口
     * @param authInfo 认证信息
     */
    @PostMapping("/oc/authnotification")
    Map<String,String> authNotification(@RequestBody AuthInfo authInfo);



    /**
     * chatBot 审核
     * @param chatBotExamine chatBot审核信息
     */
    @PostMapping("/oc/audit")
    Map<String,String> chatBotAudit(@RequestBody ChatBotExamine chatBotExamine);


    /**
     * ChatBot 新增/变更配置信息
     * @param chatBotConfigInfo chatBot配置信息
     */
    @PostMapping("/oc/syncconfigchatbot")
    Map<String,String> syncConfigChatBot(@RequestBody ChatBotConfigInfo chatBotConfigInfo);

    /**
     * 新增合同
     * @param signedCustomer 参数
     */
    @PostMapping("/oc/client/new")
    Map<String,String> clientNew(@RequestBody SignedCustomer signedCustomer);



    /**
     * 修改合同
     * @param signedCustomer 参数
     */
    @PostMapping("/oc/client/change")
    Map<String,String> clientChange(@RequestBody SignedCustomer signedCustomer);


    /**
     * 审核结果通知
     * @param signedCustomerExamine 参数
     */
    @PostMapping("/oc/client/audit")
    Map<String,String> clientAudit(@RequestBody SignedCustomerExamine signedCustomerExamine);



    /**
     * 非直签客户状态变更
     * @param customerStatus 参数
     */
    @PostMapping("/oc/client/status")
    Map<String,String> clientStatus(@RequestBody CustomerStatus customerStatus);


    /**
     * 代理商分配服务代码
     * @param agentServiceCode 参数
     */
    @PostMapping("/oc/client/allotServiceCode")
    Map<String,String> allotServiceCode(@RequestBody AgentServiceCode agentServiceCode);

}

package com.citc.nce.auth.unicomAndTelecom;

import com.citc.nce.auth.unicomAndTelecom.req.*;
import com.citc.nce.auth.unicomAndTelecom.resp.ChatbotUploadResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value = "auth-service", contextId = "UnicomAndTelecomApi", url = "${auth:}")
public interface UnicomAndTelecomApi {

    /**
     * 合同文件上传
     * @param chatBotStatus 信息实体类
     */
    @PostMapping(value = "/unicomAndTelecom/",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    ChatbotUploadResp uploadContractFile(UploadReq chatBotStatus);

    /**
     * 合同文件上传
     * @param chatBotStatus 信息实体类
     */
    @PostMapping(value = "/unicomAndTelecom/uploadChatBotFile",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    ChatbotUploadResp uploadChatBotFile(UploadReq chatBotStatus);

    /**
     * 获取文件流
     * @param operatorCodeReq 信息实体类
     */
    @PostMapping(value = "/unicomAndTelecom/getFile")
    ResponseEntity<byte[]> getFile(@RequestBody OperatorCodeReq operatorCodeReq);

    /**
     * 获取联通电信行业类型
     * @return 列表
     */
    @PostMapping("/unicomAndTelecom/getUnicomAndTelecomIndustry")
    Map<Integer,String> getUnicomAndTelecomIndustry();

    /**
     * 获取联通电信行业类型
     * @return 列表
     */
    @PostMapping("/unicomAndTelecom/getUnicomAndTelecomServiceCode")
    CodeResult getUnicomAndTelecomServiceCode(@RequestBody OperatorCodeReq req);

    /**
     * 注销合同
     */
    @PostMapping("/unicomAndTelecom/withdrawContract")
    void withdrawContract(@RequestBody OperatorCodeReq req);


    /**
     * 撤销注销合同
     */
    @PostMapping("/unicomAndTelecom/revokeCancellationContract")
    void revokeCancellation(@RequestBody OperatorCodeReq req);

    /**
     * 撤销变更合同
     */
    @PostMapping("/unicomAndTelecom/revokeChangeContract")
    void revokeChangeContract(@RequestBody OperatorCodeReq req);

    /**
     * 注销ChatBot
     */
    @PostMapping("/unicomAndTelecom/withdrawChatBot")
    void withdrawChatBot(@RequestBody OperatorCodeReq req);


    /**
     * 撤销注销ChatBot
     */
    @PostMapping("/unicomAndTelecom/revokeCancellationChatBot")
    void revokeCancellationChatBot(@RequestBody OperatorCodeReq req);

    /**
     * 撤销变更ChatBot
     */
    @PostMapping("/unicomAndTelecom/revokeChangeChatBot")
    void revokeChangeChatBot(@RequestBody OperatorCodeReq req);


    /**
     * 申请上线ChatBot
     */
    @PostMapping("/unicomAndTelecom/applyOnlineChatBot")
    void applyOnlineChatBot(@RequestBody OperatorCodeReq req);

    /**
     * 撤销上线ChatBot
     */
    @PostMapping("/unicomAndTelecom/revokeOnlineChatBot")
    void revokeOnlineChatBot(@RequestBody OperatorCodeReq req);

    /**
     * ChatBot接入信息
     */
    @PostMapping("/unicomAndTelecom/accessInformation")
    AccessInformation accessInformation(@RequestBody OperatorCodeReq req);

    /**
     * 测试ChatBot（设置白名单）
     */
    @PostMapping("/unicomAndTelecom/testChatBot")
    void testChatBot(@RequestBody OperatorCodeReq req);

    /**
     * 测试ChatBot（设置白名单）
     */
    @PostMapping("/unicomAndTelecom/testContractSchedule")
    void testContractSchedule(@RequestBody OperatorCodeReq req);

    @GetMapping("/unicomAndTelecom/chatbot/delete")
    void deleteChatbot(
            @RequestParam("accessTagNo") String accessTagNo,
            @RequestParam("accessKey")  String accessKey,
            @RequestParam("cspId") String cspId,
            @RequestParam("operatorCode")Integer operatorCode
    );
}

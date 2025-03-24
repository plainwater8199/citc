package com.citc.nce.csp;

import com.citc.nce.auth.unicomAndTelecom.UnicomAndTelecomApi;
import com.citc.nce.auth.unicomAndTelecom.req.*;
import com.citc.nce.auth.unicomAndTelecom.resp.ChatbotUploadResp;
import com.citc.nce.common.log.annotation.Log;
import com.citc.nce.security.annotation.HasCsp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
public class UnicomAndTelecomController {

    @Resource
    UnicomAndTelecomApi unicomAndTelecomApil;

    @PostMapping(
            value = {"/unicomAndTelecom/uploadContractFile"},
            consumes = {"multipart/form-data"}
    )
    public ChatbotUploadResp uploadContractFile( UploadReq uploadReq){
        return unicomAndTelecomApil.uploadContractFile(uploadReq);
    }

    @PostMapping(
            value = {"/unicomAndTelecom/uploadChatBotFile"},
            consumes = {"multipart/form-data"}
    )
    public ChatbotUploadResp uploadChatBotFile( UploadReq uploadReq){
        return unicomAndTelecomApil.uploadChatBotFile(uploadReq);
    }

    @GetMapping({"/unicomAndTelecom/getUnicomAndTelecomIndustry"})
    public Map<Integer, String> getUnicomAndTelecomIndustry(){
        return unicomAndTelecomApil.getUnicomAndTelecomIndustry();
    }

    @PostMapping({"/unicomAndTelecom/getUnicomAndTelecomServiceCode"})
    public CodeResult getUnicomAndTelecomServiceCode(@RequestBody OperatorCodeReq var1){
        return unicomAndTelecomApil.getUnicomAndTelecomServiceCode(var1);
    }

    /**
     * 注销合同
     */
    @PostMapping({"/unicomAndTelecom/withdrawContract"})
    @HasCsp
    @Log(title = "注销合同")
    public void withdrawContract(@RequestBody OperatorCodeReq var1){
         unicomAndTelecomApil.withdrawContract(var1);
    }

    @PostMapping({"/unicomAndTelecom/revokeCancellationContract"})
    @HasCsp
    @Log(title = "撤销注销合同")
    public void revokeCancellation(@RequestBody OperatorCodeReq var1){
         unicomAndTelecomApil.revokeCancellation(var1);
    }

    //撤销变更合同
    @PostMapping({"/unicomAndTelecom/revokeChangeContract"})
    @HasCsp
    @Log(title = "撤销变更合同")
    public void revokeChangeContract(@RequestBody OperatorCodeReq var1){
         unicomAndTelecomApil.revokeChangeContract(var1);
    }

    @PostMapping({"/unicomAndTelecom/withdrawChatBot"})
    public void withdrawChatBot(@RequestBody OperatorCodeReq var1){
        unicomAndTelecomApil.withdrawChatBot(var1);
    }

    @PostMapping({"/unicomAndTelecom/revokeCancellationChatBot"})
    public void revokeCancellationChatBot(@RequestBody OperatorCodeReq var1){
        unicomAndTelecomApil.revokeCancellationChatBot(var1);
    }

    @PostMapping({"/unicomAndTelecom/revokeChangeChatBot"})
    public void revokeChangeChatBot(@RequestBody OperatorCodeReq var1){
        unicomAndTelecomApil.revokeChangeChatBot(var1);
    }

    @PostMapping({"/unicomAndTelecom/applyOnlineChatBot"})
    public void applyOnlineChatBot(@RequestBody OperatorCodeReq var1){
        unicomAndTelecomApil.applyOnlineChatBot(var1);
    }

    @PostMapping({"/unicomAndTelecom/revokeOnlineChatBot"})
    public void revokeOnlineChatBot(@RequestBody OperatorCodeReq var1){
        unicomAndTelecomApil.revokeOnlineChatBot(var1);
    }

    @PostMapping({"/unicomAndTelecom/accessInformation"})
    @HasCsp
    public AccessInformation accessInformation(@RequestBody OperatorCodeReq var1){
        return unicomAndTelecomApil.accessInformation(var1);
    }

    @PostMapping({"/unicomAndTelecom/testChatBot"})
    public void testChatBot(@RequestBody OperatorCodeReq var1){
        unicomAndTelecomApil.testChatBot(var1);
    }


    @PostMapping({"/unicomAndTelecom/getFile"})
    public ResponseEntity<byte[]> getFile(@RequestBody OperatorCodeReq var1){
        return unicomAndTelecomApil.getFile(var1);
    }


    @PostMapping({"/unicomAndTelecom/testContractSchedule"})
    public void testContractSchedule(@RequestBody OperatorCodeReq var1){
        unicomAndTelecomApil.testContractSchedule(var1);
    }

    @GetMapping("/unicomAndTelecom/chabot/delete")
    public void deleteChatbot(@RequestParam("accessTagNo") String accessTagNo,
                              @RequestParam("accessKey")  String accessKey,
                              @RequestParam("cspId") String cspId,
                              @RequestParam("operatorCode")Integer operatorCode){
        unicomAndTelecomApil.deleteChatbot(accessTagNo, accessKey, cspId, operatorCode);
    }

}

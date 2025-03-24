package com.citc.nce.developer;

import com.citc.nce.developer.vo.DeveloperCustomer5gSendVo;
import com.citc.nce.developer.vo.DeveloperCustomerSendDataVo;
import com.citc.nce.developer.vo.DeveloperSend5gSaveDataVo;
import com.citc.nce.developer.vo.DeveloperSendPhoneVo;
import com.citc.nce.developer.vo.DeveloperSendSaveDataVo;
import com.citc.nce.developer.vo.DeveloperSendVideoSaveDataVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * @author ping chen
 */
@FeignClient(value = "rebot-service", contextId = "DeveloperSend", url = "${robot:}")
public interface DeveloperSendApi {

    @PostMapping("/developer/send")
    List<DeveloperSendPhoneVo> send(@RequestBody @Valid DeveloperCustomerSendDataVo developerCustomerSendDataVo, @RequestParam("token") String token);

    @PostMapping("/developer/5g")
    List<DeveloperSendPhoneVo> send5g(@RequestBody @Valid DeveloperCustomer5gSendVo developerCustomer5gSendVo, @RequestParam("token") String token);

    @PostMapping("/developer/send/saveData")
    void saveData(@RequestBody @Valid DeveloperSendSaveDataVo developerSendSaveDataVo);

    @PostMapping("/developer/send/saveMediaData")
    void saveMediaData(@RequestBody @Valid DeveloperSendVideoSaveDataVo developerSendVideoSaveDataVo);

    @PostMapping("/developer/send/save5gData")
    void save5gData(@RequestBody @Valid DeveloperSend5gSaveDataVo developerSend5gSaveDataVo);

    @PostMapping("/developer/callBack/{id}")
    Boolean callBack(@PathVariable("id") String id);

    @PostMapping("/developer/callBack/test")
    void test(@RequestParam("message") String message);

    @GetMapping("/developer/send/isDeveloperMessage")
    Boolean isDeveloperMessage(@RequestParam("messageId") String messageId);

    @PostMapping("/developer/send/updateMessageId")
    void updateMessageId(@RequestParam("oldMessageId") String oldMessageId, @RequestParam("newMessageId") String newMessageId);

    @PostMapping("/developer/send/findSendRecord")
    Long findSendRecord(@RequestParam("oldMessageId") String oldMessageId, @RequestParam("customerId") String customerId);
}

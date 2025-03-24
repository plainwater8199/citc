package com.citc.nce.developer.controller;

import com.citc.nce.developer.DeveloperSendApi;
import com.citc.nce.developer.entity.DeveloperSendDo;
import com.citc.nce.developer.mq.SmsCallBackListener;
import com.citc.nce.developer.service.DeveloperSendService;
import com.citc.nce.developer.vo.DeveloperCustomer5gSendVo;
import com.citc.nce.developer.vo.DeveloperCustomerSendDataVo;
import com.citc.nce.developer.vo.DeveloperSend5gSaveDataVo;
import com.citc.nce.developer.vo.DeveloperSendPhoneVo;
import com.citc.nce.developer.vo.DeveloperSendSaveDataVo;
import com.citc.nce.developer.vo.DeveloperSendVideoSaveDataVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author ping chen
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class DeveloperSendController implements DeveloperSendApi {
    private final DeveloperSendService developerSendService;

    private final SmsCallBackListener smsCallBackListener;

    /**
     * 发送消息
     *
     * @param developerCustomerSendDataVo
     * @return
     */
    @Override
    public List<DeveloperSendPhoneVo> send(@RequestBody @Valid DeveloperCustomerSendDataVo developerCustomerSendDataVo, @RequestParam("token") String token) {
        return developerSendService.send(developerCustomerSendDataVo, token);
    }

    @Override
    public List<DeveloperSendPhoneVo> send5g(@RequestBody @Valid DeveloperCustomer5gSendVo developerCustomer5gSendVo, @RequestParam("token") String token) {
        return developerSendService.send5g(developerCustomer5gSendVo, token);
    }

    @Override
    public void saveData(DeveloperSendSaveDataVo developerSendSaveDataVo) {
        developerSendService.saveData(developerSendSaveDataVo);
    }

    @Override
    public void saveMediaData(DeveloperSendVideoSaveDataVo developerSendVideoSaveDataVo) {
        developerSendService.saveMediaData(developerSendVideoSaveDataVo);
    }

    @Override
    public void save5gData(DeveloperSend5gSaveDataVo developerSend5gSaveDataVo) {
        developerSendService.save5gData(developerSend5gSaveDataVo);
    }

    /**
     * 回调重试
     *
     * @param id
     */
    @Override
    public Boolean callBack(String id) {
        return developerSendService.callBack(id);
    }

    @Override
    public void test(String message) {
        smsCallBackListener.onMessage(message);
    }

    @Override
    public Boolean isDeveloperMessage(String messageId) {
        return developerSendService.isDeveloperMessage(messageId);
    }

    @Override
    public void updateMessageId(String oldMessageId, String newMessageId) {
        developerSendService.lambdaUpdate()
                .eq(DeveloperSendDo::getSmsId, oldMessageId)
                .set(DeveloperSendDo::getSmsId, newMessageId)
                .update(new DeveloperSendDo());
    }

    @Override
    public Long findSendRecord(String oldMessageId, String customerId) {
        return developerSendService.findSendRecord(oldMessageId, customerId);
    }
}

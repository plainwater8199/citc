package com.citc.nce.developer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.csp.smsTemplate.vo.SmsTemplateVariable;
import com.citc.nce.developer.entity.DeveloperSendDo;
import com.citc.nce.developer.vo.DeveloperCustomer5gSendVo;
import com.citc.nce.developer.vo.DeveloperCustomerSendDataVo;
import com.citc.nce.developer.vo.DeveloperSend5gSaveDataVo;
import com.citc.nce.developer.vo.DeveloperSendPhoneVo;
import com.citc.nce.developer.vo.DeveloperSendSaveDataVo;
import com.citc.nce.developer.vo.DeveloperSendVideoSaveDataVo;
import com.citc.nce.robot.req.RichMediaResultArray;
import com.citc.nce.robot.vo.MessageData;

import java.util.List;

/**
 * @author ping chen
 */
public interface DeveloperSendService extends IService<DeveloperSendDo> {

    List<DeveloperSendPhoneVo> send(DeveloperCustomerSendDataVo developerCustomerSendDataVo, String token);

    List<DeveloperSendPhoneVo> send5g(DeveloperCustomer5gSendVo developerCustomer5gSendVo, String token);

    void updateSmsDeveloperSendPlatformResult(List<DeveloperSendPhoneVo> developerSendPhoneVoList, List<SmsTemplateVariable> smsTemplateSendVariableList);

    void updateVideoDeveloperSendPlatformResult(DeveloperSendPhoneVo developerSendPhoneVo, RichMediaResultArray richMediaResultArray);

    void update5gDeveloperSendPlatformResult(MessageData messageData, DeveloperSendPhoneVo developerSendPhoneVo);

    void saveData(DeveloperSendSaveDataVo developerSendSaveDataVo);

    void saveMediaData(DeveloperSendVideoSaveDataVo developerSendVideoSaveDataVo);

    void save5gData(DeveloperSend5gSaveDataVo developerSend5gSaveDataVo);

    Boolean callBack(String id);

    Boolean isDeveloperMessage(String messageId);

    Long findSendRecord(String oldMessageId, String customerId);
}

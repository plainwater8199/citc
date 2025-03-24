package com.citc.nce.module.service;


import com.citc.nce.module.vo.req.ImportContactGroupReq;
import com.citc.nce.module.vo.resp.ImportContactGroupResp;

import java.util.Date;

public interface ModuleService {
    //保存关联关系
    Boolean saveModuleButUuidRelation(Integer moduleType,String moduleId,String butUuid);

    //获取组件ID
    String getModuleIdByButUuid(String butUuid);

    /**
     * 将需要发送的订阅内容加入到MQ中
     */
    void sendSubscribeToMQ();


    /**
     * 向网关发送消息
     * @param messageTemplateId 消息模板ID
     * @param variableValue 发送变量值。模板名称、打卡成功发送的是打卡次数
     * @param phone 发送的手机号
     * @param chatbotId chatbotId
     * @param moduleType 51-订阅，52-打卡
     */
    void sendMessage(Long messageTemplateId, String variableValue,String phone,String chatbotId,int moduleType);


    void updateModuleHandle(int btnType, String butUuid, String sender,String chatbotId);

    /**
     * 组件相关用户导入联系人组
     * @param req 请求信息
     * @return
     */
    ImportContactGroupResp importContactGroup(ImportContactGroupReq req);

    /**
     * 获取组件的时间，星期几
     * @return 时间
     */
    String getNowDay(Date date);
}

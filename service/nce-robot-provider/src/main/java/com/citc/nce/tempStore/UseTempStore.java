package com.citc.nce.tempStore;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.robot.api.tempStore.bean.csp.EditName;
import com.citc.nce.robot.service.RobotOrderService;
import com.citc.nce.robot.service.RobotVariableService;
import com.citc.nce.robot.vo.RobotOrderReq;
import com.citc.nce.robot.vo.RobotVariableReq;
import com.citc.nce.tempStore.vo.UseTempVariableOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@Slf4j
public class UseTempStore implements UseTempStoreApi {

    public final static String type_custom = "custom";
    public final static String type_sys = "sys";
    public final static String key_id = "id";
    public final static String key_variable = "variable";

    @Resource
    private RobotVariableService variableService;
    @Resource
    RobotOrderService robotOrderService;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public UseTempVariableOrder saveVariableOrder(UseTempVariableOrder data) {
        List<Long> existV = variableService.checkListUseTemp(data.getVariableMap());
        List<Long> existO = robotOrderService.checkListUseTemp(data.getOrderMap());
        data.setVariableDuplicate(existV);
        data.setOrderDuplicate(existO);

        if (CollectionUtils.isEmpty(existV) && CollectionUtils.isEmpty(existO)) {
            variableService.saveListUseTemp(data);
            //替换id和名称
            replaceOrderVariable(data.getEditVariable(), data.getVariableMap(), data.getOrderMap());
            robotOrderService.saveListUseTemp(data);
        }

        return data;
    }

    private void replaceOrderVariable(Map<Long, EditName> editVariable, Map<Long, RobotVariableReq> variableDbMap,
                                      Map<Long, RobotOrderReq> orderMap) {
        if (CollectionUtils.isEmpty(orderMap)) return;

        for (Map.Entry<Long, RobotOrderReq> entry : orderMap.entrySet()) {
            //单个指令json
            String jsonString = JSON.toJSONString(entry.getValue());
            for (RobotVariableReq variable : variableDbMap.values()) {
                //编辑过名称吗？
                String oldName = variable.getVariableName();
                String newName = variable.getVariableName();
                EditName edit = editVariable.get(variable.getOldId());
                if (!Objects.isNull(edit)) {//编辑过
                    oldName = edit.getOldName();
                    String oldVar = "{{custom-" + oldName + "}}";
                    String newVar = "{{custom-" + newName + "}}";
                    jsonString = org.apache.commons.lang3.StringUtils.replace(jsonString, oldVar, newVar);
                }
                String oldVarAndId = "{{custom-" + variable.getOldId() + "&" + oldName + "}}";
                String newVarAndId = "{{custom-" + variable.getId() + "&" + newName + "}}";
                jsonString = org.apache.commons.lang3.StringUtils.replace(jsonString, oldVarAndId, newVarAndId);
            }

            RobotOrderReq orderReq = JSON.parseObject(jsonString, RobotOrderReq.class);

            //单独处理response中的变量
            JSONArray jsonArray = JSON.parseArray(orderReq.getResponseList());
            if (!CollectionUtils.isEmpty(jsonArray)) {
                for (Object object : jsonArray) {
                    JSONObject jsonObject = (JSONObject) object;
                    String id = jsonObject.getString(key_id);
                    if (StringUtils.hasLength(id)) {
                        try {
                            Long aLong = Long.valueOf(id);
                            RobotVariableReq editName = variableDbMap.get(aLong);
                            if (Objects.nonNull(editName)) {
                                jsonObject.put(key_id, editName.getId());
                                jsonObject.put(key_variable, editName.getVariableName());
                            }
                        } catch (NumberFormatException formatException) {

                        }
                    }
                }
                orderReq.setResponseList(jsonArray.toJSONString());
            }

            orderMap.put(entry.getKey(), orderReq);
        }
    }
}

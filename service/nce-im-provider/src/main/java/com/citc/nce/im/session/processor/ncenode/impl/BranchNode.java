package com.citc.nce.im.session.processor.ncenode.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.citc.nce.im.session.ApplicationContextUil;
import com.citc.nce.im.session.processor.*;
import com.citc.nce.robot.vo.NodeActResult;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/5 14:34
 * @Version: 1.0
 * @Description: 分支节点
 */
public class BranchNode extends NceRobotNode {

    private Logger log = LoggerFactory.getLogger(BranchNode.class);

    /**
     * 条件集合逻辑
     * 执行逻辑
     * 0任意 满足任意一个条件即可进入
     * 1所有 满足所有条件才能进入
     */
    private Integer executeType=0;

    /**
     * 条件集合
     */
    private List<ConditionExpression> conditionList;


    @Override
    public boolean enterCondition(SessionContext sessionContext, NodeProcessor nodeProcessor) {
        log.info("进入分支节点:" + getNodeName());
        try {
            if(executeType.equals(0)){
                for (ConditionExpression conditionExpression : conditionList) {
                    boolean execute = conditionExpression.execute(sessionContext,nodeProcessor);
                    //任意一个条件满足  则进入分支条件
                    if (execute) {
                        return true;
                    }
                }
            }else {
                for (ConditionExpression conditionExpression : conditionList) {
                    //满足全部条件才能进入，出现false情况，则跳出循环
                    if(!conditionExpression.execute(sessionContext,nodeProcessor)){
                        return false;
                    }
                }
                return true;
            }
        }catch (Exception e){
            log.info("进入分支节点异常");
        }
        return false;
    }

    public Integer getExecuteType() {
        return executeType;
    }

    public void setExecuteType(Integer executeType) {
        this.executeType = executeType;
    }

    public List<ConditionExpression> getConditionList() {
        return conditionList;
    }

    public void setConditionList(List<ConditionExpression> conditionList) {
        this.conditionList = conditionList;
    }

    @Override
    public NodeActResult act(SessionContext sessionContext, NodeProcessor nodeProcessor) {
        ParsedProcessorConfig parsedProcessorConfig = nodeProcessor.getParsedProcessorConfig();
        List<String> list = parsedProcessorConfig.getNodeIdRelations().get(nodeProcessor.getCurrentNodeId());
        String childIdx = "";
        boolean hasNext = true;
        if(list.size()>0){
            childIdx = list.get(0);
        }else {
            nodeProcessor.checkAndClean();
            hasNext = false;
        }
        return NodeActResult.builder().childIdx(childIdx).next(hasNext).build();
    }

    @Override
    public Integer nodeType() {
        return NodeType.BRANCH_NODE.getCode();
    }

    @Data
    public static class ConditionExpression {

        /**
         * 对应条件的第一个变量名称
         */
        private String conditionCodeValue;
        /**
         * 比较符号
         * 0包含、1不包含、2等于、3不等于、4大于、5大等于、6小于、7小于等于、8符合正则、9不符合正则、10为空、11不为空
         */
        private Integer conditionType;
        /**
         * 对应条件的第二个变量名称
         */
        private String conditionContentValue;

        private Variable variable = ApplicationContextUil.getBean(Variable.class);


        public boolean execute(SessionContext sessionContext, NodeProcessor nodeProcessor) {
            try {
            String messageData = variable.translate(conditionCodeValue, nodeProcessor);
            System.out.println("初始值："+conditionContentValue);
            conditionContentValue = variable.translate(conditionContentValue, nodeProcessor);
                switch (conditionType){
                    case 0:
                        return messageData.contains(conditionContentValue);
                    case 1:
                        return !messageData.contains(conditionContentValue);
                    case 2:
                        return messageData.equals(conditionContentValue);
                    case 3:
                        return !messageData.equals(conditionContentValue);
                    case 4:
                        //先判断是否是数字
                        if(StringUtils.isNumeric(conditionContentValue) && StringUtils.isNumeric(messageData)){
                            return Double.parseDouble(messageData) > Double.parseDouble(conditionContentValue);
                        }else {
                            DateTime conditionContentValueDate = DateUtil.parse(conditionContentValue, "yyyy-MM-dd");
                            DateTime messageDataDate = DateUtil.parse(messageData, "yyyy-MM-dd");
                            return messageDataDate.compareTo(conditionContentValueDate) > 0;
                        }
                    case 5:
                        //先判断是否是数字
                        if(StringUtils.isNumeric(conditionContentValue) && StringUtils.isNumeric(messageData)){
                            return Double.parseDouble(messageData) >= Double.parseDouble(conditionContentValue);
                        }else {
                            Date conditionContentValueDate = DateUtils.parseDate(conditionContentValue);
                            Date messageDataDate = DateUtils.parseDate(messageData);
                            return messageDataDate.compareTo(conditionContentValueDate) >= 0;
                        }
                    case 6:
                        //先判断是否是数字
                        if(StringUtils.isNumeric(conditionContentValue) && StringUtils.isNumeric(messageData)){
                            return Double.parseDouble(messageData) < Double.parseDouble(conditionContentValue);
                        }else {
                            Date conditionContentValueDate = DateUtils.parseDate(conditionContentValue);
                            Date messageDataDate = DateUtils.parseDate(messageData);
                            return messageDataDate.compareTo(conditionContentValueDate) < 0;
                        }
                    case 7:
                        //先判断是否是数字
                        if(StringUtils.isNumeric(conditionContentValue) && StringUtils.isNumeric(messageData)){
                            return Double.parseDouble(messageData) <= Double.parseDouble(conditionContentValue);
                        }else {
                            Date conditionContentValueDate = DateUtils.parseDate(conditionContentValue);
                            Date messageDataDate = DateUtils.parseDate(messageData);
                            return messageDataDate.compareTo(conditionContentValueDate) <= 0;
                        }
                    case 8:
                        Pattern pattern1 = Pattern.compile(conditionContentValue);
                        boolean matches = pattern1.matcher(messageData).matches();
                        if(!matches){
                            matches = pattern1.matcher(messageData).find();
                        }
                        System.out.println("case 8 messageData:"+messageData);
                        System.out.println("case 8 conditionContentValue:"+conditionContentValue);
                        System.out.println("case 8:"+matches);
                        return matches;
                    case 9:
                        Pattern pattern2 = Pattern.compile(conditionContentValue);
                        boolean matches2 = pattern2.matcher(messageData).matches();
                        if(!matches2){
                            matches2 = pattern2.matcher(messageData).find();
                        }
                        return matches2;
                    case 10:
                        return StringUtils.isEmpty(messageData);
                    case 11:
                        return !StringUtils.isEmpty(messageData);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
    }
}

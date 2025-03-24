package com.citc.nce.robot.exception;

import com.citc.nce.common.core.exception.ErrorCode;


/**
 * @Author: weilanglang
 * @Contact: llweix
 * @Date: 2022/7/1 10:01
 * @Version: 1.0
 * @Description: 业务异常基类
 * 根据业务划分不同的异常code，具体待定，具体实现可以放在业务系统
 * 全局异常范围1-999
 * 业务异常根据自己服务监听端口+二位子业务+三位异常code
 * 01  变量业务
 * 02  指令业务
 * 03  流程图设计
 */
public interface RobotErrorCode {

    ErrorCode VARIABLE_BAD_REQUEST = new ErrorCode(820701001, "变量名称未填写，操作失败，请重新填写");
    ErrorCode VARIABLE_ID_VARIABLE_DATA_EXIST = new ErrorCode(820701001, "变量id未填写，操作失败，请重新填写");
    ErrorCode VARIABLE_DATA_EXIST = new ErrorCode(820701002, "变量名称重复，请重新填写");
    ErrorCode VARIABLE_SYSTEM_DATA_EXIST = new ErrorCode(820701003, "变量名称和已有系统变量重复，请重新填写");


    ErrorCode ORDER_NAME_ORDER_BAD_REQUEST = new ErrorCode(820702001, "指令名称未填写，操作失败，请重新填写");
    ErrorCode ORDER_URL_DATA_EXIST = new ErrorCode(820702001, "指令请求地址未填写，操作失败，请重新填写");
    ErrorCode ORDER_ID_DATA_EXIST = new ErrorCode(820702001, "指令ID为空，操作失败，请重新填写");


    ErrorCode ORDER_DATA_EXIST = new ErrorCode(820702002, "指令名称重复，请重新填写");
    ErrorCode SceneNodeDATA_EXIST = new ErrorCode(820702003, "场景名称重复，请重新填写");
    ErrorCode RobotProcessSettingNode_DATA_EXIST = new ErrorCode(820702004, "流程名称重复，请重新填写");

    ErrorCode PROCESSDES_BAD_REQUEST = new ErrorCode(820703001, "流程图或序号未填写，操作失败，青重新填写");
    ErrorCode PROCESSDES_REPEAT_NODENAME = new ErrorCode(820703002, "流程图节点名称重复，请修改后重试");
    ErrorCode PROCESSDES_QUESTION_BODY_EMPTY = new ErrorCode(820703002, "提问节点提问内容不能为空，请修改后重试");
    ErrorCode PROCESSDES_REPEAT_SUBPROCESSID = new ErrorCode(820703003, "流程图子流程不能与当前流程一致，请修改后重试");
    ErrorCode PROCESSDES_NOT_EXIST = new ErrorCode(820703004, "该流程已处于删除状态,保存失败");
    ErrorCode RELEASE_PROCESSDES_NOT_EXIST = new ErrorCode(820703004, "该流程已处于删除状态,发布失败");


    ErrorCode RELEASE_NULL_PROCESSDES = new ErrorCode(820703001, "未查询到该流程图");
    ErrorCode POST_REQUEST = new ErrorCode(8207406, "请求参数不能为空");
    ErrorCode EXCEL_ERROR = new ErrorCode(820703006, "Excel解析错误");
    ErrorCode PROCESS_APPENDING_ERROR = new ErrorCode(820703008, "流程发布中，不能修改");
    ErrorCode PARAMETER_ERROR = new ErrorCode(820703007, "参数错误");
    ErrorCode RELATED_ACCOUNT_ERROR = new ErrorCode(820703009, "引用的素材归属账号与关联账号不一致，不能修改");


}

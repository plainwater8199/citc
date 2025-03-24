package com.citc.nce.robot.api.mall.constant;

import com.citc.nce.common.core.exception.ErrorCode;

/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 19:06
 */
public interface MallError {
    ErrorCode GOODS_NAME_DUPLICATE = new ErrorCode(830301001, "商品名称已存在！");
    ErrorCode TEMPLATE_NOT_EXIST = new ErrorCode(830301002, "模板不存在！");
    ErrorCode TEMPLATE_NAME_DUPLICATE = new ErrorCode(830301003, "模板名称已存在！");
    ErrorCode TEMPLATE_CAN_NOT_DELETE_WAIT = new ErrorCode(830301004, "该模板对应的商品状态为待审核，不可删除！");
    ErrorCode TEMPLATE_CAN_NOT_DELETE_ACTIVE_ON = new ErrorCode(830301005, "该模板对应的商品状态为已上架，不可删除！");
    ErrorCode TYPE_ERROR = new ErrorCode(830301006, "模板类型或商品类型错误！");
    ErrorCode VARIABLE_NAME_DUPLICATE = new ErrorCode(830301007, "机器人变量名称已存在！");

    ErrorCode ORDER_NAME_DUPLICATE = new ErrorCode(830301008, "指令名称已存在！");

    ErrorCode PROCESS_NAME_DUPLICATE = new ErrorCode(830301009, "流程名称已存在！");
    ErrorCode GOODS_NOT_EXIST = new ErrorCode(830301010, "商品不存在！");
    ErrorCode SNAPSHOT_NOT_EXIST = new ErrorCode(830301011, "快照不存在！");
    ErrorCode ROBOT_TEMPLATE_CONTENT_NOT_EXIST = new ErrorCode(830301012, "机器人模板未配置内容！");
    ErrorCode GOODS_CAN_NOT_UPDATE = new ErrorCode(830301013, "商品已上架，不允许更新！");

    ErrorCode TEMPLATE_CONTENT_NOT_EXIST = new ErrorCode(830301014, "模板对应快照不存在！");
    ErrorCode CONTENT_FORMAT_ERROR = new ErrorCode(830301015, "快照内容格式异常！");
    ErrorCode PROCESS_TREE_ERROR = new ErrorCode(830301016, "导入流程图错误！");
    ErrorCode VARIABLE_ERROR = new ErrorCode(830301017, "导入变量错误！");
    ErrorCode ORDER_ERROR = new ErrorCode(830301018, "导入指令错误！");
    ErrorCode TRIGGER_ERROR = new ErrorCode(830301019, "导入触发器错误！");
    ErrorCode PRICE_ERROR = new ErrorCode(830301020, "金额错误请输入大于等于0的数字，最大不超过99999999.99（最多支持2位小数）！");
    ErrorCode CHATBOT_ACCOUNT_ERROR = new ErrorCode(830301021, "请选择关联5G消息账号！");

}

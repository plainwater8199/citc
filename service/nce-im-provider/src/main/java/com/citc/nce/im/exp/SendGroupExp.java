package com.citc.nce.im.exp;

import com.citc.nce.common.core.exception.ErrorCode;

public interface SendGroupExp {
    ErrorCode USER_ERROR = new ErrorCode(820301001, "用户群体列表为空");
    ErrorCode PLAN_ERROR = new ErrorCode(820301002, "未配置计划");
    ErrorCode CLIENT_ERROR = new ErrorCode(820301003, "客户端创建失败");
    ErrorCode FILE_ERROR = new ErrorCode(820301004, "创建临时文件失败");
    ErrorCode TIME_FORMAT_ERROR = new ErrorCode(820301005, "日期解析错误");
    ErrorCode EXCEL_FORMAT_ERROR = new ErrorCode(820301006, "excel文件解析错误");
    ErrorCode PARAM_BUILD_ERROR = new ErrorCode(820301007, "发送参数构建错误");
    ErrorCode FILE_UPLOAD_ERROR = new ErrorCode(820301008, "文件上传至网关错误");
    ErrorCode SEND_ERROR = new ErrorCode(820301009, "消息发送至网关错误");
    ErrorCode MATERIAL_ERROR = new ErrorCode(820301010, "素材审核失败");

    ErrorCode SQL_ERROR = new ErrorCode(820301011, "数据库操作失败");
    ErrorCode NODE_ERROR = new ErrorCode(820301012, "保存节点信息失败，请检查节点信息是否完善");
    ErrorCode STATUS_ERROR = new ErrorCode(820301013, "计划不是待启动或暂停状态，不能被启动");
    ErrorCode START_ERROR = new ErrorCode(820301013, "请先配置计划后再启动");
    ErrorCode CLOSE_ERROR = new ErrorCode(820301013, "计划不是暂停状态，不能关闭");
    ErrorCode DELETE_ERROR = new ErrorCode(820301014, "计划不是待启动状态，删除失败");
    ErrorCode PHONE_ERROR = new ErrorCode(820301015, "电话号码格式错误");
    ErrorCode TEMPLATE_ERROR = new ErrorCode(820301016, "模板不存在");
    ErrorCode SEND_SMS_ERROR = new ErrorCode(820301017, "短信发送失败");

    ErrorCode BLACKLIST_ERROR = new ErrorCode(820301018, "黑名单号码，不能发送");

    ErrorCode CHATBOT_OFFLINE_ERROR = new ErrorCode(820301019, "chatbot已下线，不能发送");
    ErrorCode ACCOUNT_OFFLINE_ERROR = new ErrorCode(820301020, "账号被禁用，不能发送");
    ErrorCode ACCOUNT_DELETE_ERROR = new ErrorCode(820301020, "该账号已删除，无法执行测试发送");
}

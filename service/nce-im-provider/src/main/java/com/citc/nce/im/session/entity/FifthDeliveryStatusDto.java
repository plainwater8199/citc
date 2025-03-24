package com.citc.nce.im.session.entity;

import com.citc.nce.robot.req.DeliveryStatusReq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 5G消息抵达状态对象
 *
 * @author jcrenc
 * @since 2024/12/4 10:29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FifthDeliveryStatusDto {
    private String chatbotId;

    private String messageId;

    private String status;

    private String sender;

    private String code;

    private String message;

    private String oldMessageId;

    public static List<FifthDeliveryStatusDto> ofDeliveryReq(String chatbotId, DeliveryStatusReq req) {
        return req.getDeliveryInfoList().stream()
                .map(info ->
                        new FifthDeliveryStatusDto(
                                chatbotId,
                                info.getMessageId(),
                                info.getStatus(),
                                info.getSender(),
                                info.getCode(),
                                info.getMessage(),
                                info.getOldMessageId()
                        ))
                .collect(Collectors.toList());
    }
    // 根据 code 获取错误原因，若无匹配则返回 message
    public String translateCodeToFailReason() {
        return ERROR_CODE_MAP.getOrDefault(this.code, this.message);
    }
    // 错误码与原因的映射
    private static final Map<String, String> ERROR_CODE_MAP = new HashMap<String, String>() {{
        put("20002", "参数异常");
        put("20003", "请求异常");
        put("20004", "请求IP错误");
        put("30001", "超过日发送数量限制");
        put("30002", "超过月发送数量限制");
        put("30003", "超过每秒发送频率限制");
        put("30004", "超过年发送数量限制");
        put("30006", "回调接口验证失败");
        put("30007", "审核结果通知发送失败");
        put("30008", "chatbotId与senderAddress不匹配");
        put("40001", "获取accessToken时appKey错误，或者accessToken无效");
        put("40002", "不合法的凭证类型");
        put("40005", "不合法的文件类型");
        put("40006", "不合法的文件大小");
        put("40007", "不合法的媒体素材url");
        put("40008", "不合法的消息类型");
        put("40011", "Chatbot信息更新失败");
        put("40014", "不合法的accessToken");
        put("40016", "不合法的按钮个数");
        put("40017", "不合法的按钮类型");
        put("40018", "不合法的按钮名字长度");
        put("40019", "不合法的按钮KEY长度");
        put("40020", "不合法的按钮URL长度");
        put("40021", "不合法的文本长度");
        put("40022", "临时文件已过期");
        put("40040", "文件数量超出限制");
        put("40052", "不合法的按钮链接");
        put("41001", "缺少accessToken参数");
        put("41002", "缺少appId参数");
        put("41004", "缺少appKey参数");
        put("41005", "缺少多媒体文件数据");
        put("41006", "缺少素材url参数");
        put("42001", "accessToken超时");
        put("43001", "需要GET请求");
        put("44001", "多媒体文件为空");
        put("44002", "POST的数据包为空");
        put("45001", "接口调用超过限制");
        put("45010", "回复时间超过限制");
        put("46001", "不存在的用户");
        put("46002", "临时用户不存在");
        put("46003", "用户已被限制");
        put("47001", "解析JSON/XML内容错误");
        put("47002", "解析子菜单JSON/XML内容错误");
        put("48001", "api功能未授权");
        put("60001", "不合法的内容");
        put("60002", "需要订阅关系");
        put("60003", "无效的消息类型");
        put("70001", "消息体被管控");
    }};
}

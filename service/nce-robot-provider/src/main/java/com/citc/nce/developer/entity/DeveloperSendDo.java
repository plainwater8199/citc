package com.citc.nce.developer.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author ping chen
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("developer_send")
@Accessors(chain = true)
public class DeveloperSendDo extends BaseDo<DeveloperSendDo> {
    private static final long serialVersionUID = 4264049321711658723L;

    @ApiModelProperty("开发者信息id")
    private String appId;

    @ApiModelProperty("客户账号")
    private String accountId;

    @ApiModelProperty("电话号码")
    private String phone;

    @ApiModelProperty("平台模板Id")
    private String platformTemplateId;
    @ApiModelProperty("平台模板签名")
    private String platformTemplateSign;
    @ApiModelProperty("平台模板名称")
    private String platformTemplateName;
    @ApiModelProperty("平台模板消息类型")
    private Integer platformTemplateMessageType;
    @ApiModelProperty("平台模板快捷按钮")
    private String platformTemplateShortcutButton;
    @ApiModelProperty("平台模板发送内容")
    private String platformTemplateModuleInformation;

    @ApiModelProperty("调用时间")
    private LocalDateTime callTime;

    @ApiModelProperty("客户调用结果,1:用户调用成功，2：调用网关成功，3：网关回调成功，4：回调用户成功")
    private Integer callResult;

    @ApiModelProperty("客户调用结果描述")
    private String callResultMsg;

    @ApiModelProperty("发送MQ结果，0：成功 1：失败")
    private Integer sendMqResult;

    @ApiModelProperty("发送平台结果,0:成功 1:失败")
    private Integer sendPlatformResult;

    @ApiModelProperty("平台回执消息发送结果,0:成功，1:失败,2:未知,3:已阅(5G消息状态),4:转短信发送成功(5G消息状态)")
    private Integer callbackPlatformResult;

    @ApiModelProperty("回调客户结果0:成功，1:失败,2:无回调")
    private Integer callbackResult;

    @ApiModelProperty("回调客户时间")
    private LocalDateTime callbackTime;

    @ApiModelProperty("客户登录账号")
    private String customerId;

    @ApiModelProperty("平台消息Id")
    private String smsId;

    @ApiModelProperty("唯一Id和手机号对应")
    private String customSmsId;

    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty("账号类型：1-5G消息、2-视频短信消息、3-短信消息")
    private Integer accountType;

    @ApiModelProperty("5G消息类型所属应用Id")
    private String applicationUniqueId;

    @ApiModelProperty("cspId")
    private String cspId;

}

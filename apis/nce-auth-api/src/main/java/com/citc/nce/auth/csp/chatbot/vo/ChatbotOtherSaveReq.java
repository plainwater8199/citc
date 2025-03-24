package com.citc.nce.auth.csp.chatbot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class ChatbotOtherSaveReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 归属客户Id
     */
    @ApiModelProperty(value = "归属客户Id")
    @NotNull(message = "归属客户Id不能为空")
    @Length(min = 15, max = 15, message = "客户ID必须为15位")
    private String customerId;
    /**
     * 账户类型，1联通2硬核桃
     */
    @ApiModelProperty(value = "账户类型，联通、硬核桃")
    @NotBlank(message = "账户类型不能为空")
    private String accountType;

    /**
     * 账号名称
     */
    @ApiModelProperty(value = "账号名称")
    @NotBlank(message = "账号名称不能为空")
    private String accountName;
    ;

    /**
     * 账号id
     */
    @ApiModelProperty(value = "账号id")
    @NotBlank(message = "账号id不能为空")
    private String chatbotAccount;

    /**
     * appid
     */
    @ApiModelProperty(value = "appid")
    @NotBlank(message = "appid不能为空")
    private String appId;

    /**
     * appkey
     */
    @ApiModelProperty(value = "appkey")
    @NotBlank(message = "appkey不能为空")
    private String appKey;

    /**
     * token
     */
    @ApiModelProperty(value = "token")
    @NotBlank(message = "token不能为空")
    private String token;

    /**
     * 消息地址
     */
    @ApiModelProperty(value = "消息地址")
    private String messageAddress;

    /**
     * 文件地址
     */
    @ApiModelProperty(value = "文件地址")
    private String fileAddress;

    /**
     * 机器人状态(30：在线，31：已下线，42：已下线（关联的CSP被下线），50：调试)
     */
    @ApiModelProperty(value = "机器人状态(30：在线，31：已下线，42：已下线（关联的CSP被下线），50：调试)")
    private Integer chatbotStatus;

    /**
     * 归属运营商 0：缺省(硬核桃)，1：联通，2：移动，3：电信
     */
    @ApiModelProperty(value = "归属运营商 0：缺省(硬核桃)，1：联通，2：移动，3：电信")
    private Integer accountTypeCode;

}

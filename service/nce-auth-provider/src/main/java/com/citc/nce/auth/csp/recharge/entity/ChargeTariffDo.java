package com.citc.nce.auth.csp.recharge.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@ApiModel(value = "充值资费",description = "")
@Data
@Accessors(chain = true)
@TableName("charge_tariff")
public class ChargeTariffDo extends BaseDo<ChargeTariffDo> implements Serializable {
    /** 订单所属客户ID */
    @ApiModelProperty(name = "订单所属客户ID",notes = "")
    private String customerId ;
    /** 5g消息订单->机器人账号，短信订单->短信账号，视频短信订单->视频短信账号，阅信+订单->阅信+账号 */
    @ApiModelProperty(name = "5g消息订单->机器人账号，短信订单->短信账号，视频短信订单->视频短信账号，阅信+订单->阅信+账号",notes = "")
    private String accountId;
    /** 账号类型 1:5g消息,2:视频短信,3:短信，4 阅信+账号 */
    @ApiModelProperty(name = "账号类型 1:5g消息,2:视频短信,3:短信，4 阅信+账号",notes = "")
    private Integer accountType ;
    /** 回落资费类型 1单一价 2复合价*/
    @ApiModelProperty(name = "回落资费类型 1单一价 2复合价",notes = "")
    private Integer fallbackType;
    /** 批次 */
    @ApiModelProperty(name = "批次",notes = "")
    private String batch ;
    /** 资费内容，json字符串 */
    @ApiModelProperty(name = "资费内容，json字符串",notes = "")
    private String tariffContent ;
}

package com.citc.nce.developer.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author ping chen
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("developer_customer_5g")
@Accessors(chain = true)
public class DeveloperCustomer5gDo extends BaseDo<DeveloperCustomer5gDo> {
    private static final long serialVersionUID = 4264049361741658723L;

    @ApiModelProperty("客户登录账号")
    private String customerId;

    @ApiModelProperty("回调地址")
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String callbackUrl;

    @ApiModelProperty("接口地址")
    private String receiveUrl;

    @ApiModelProperty("csp账号")
    private String cspId;

    @ApiModelProperty("唯一键")
    private String uniqueId;
}

package com.citc.nce.authcenter.csp.multitenant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@TableName("third_login_relations")
public class ThirdLoginRelations extends BaseDo<ThirdLoginRelations> implements Serializable {
    private static final long serialVersionUID = 571041720613611041L;

    @ApiModelProperty("csp id")
    private String cspId;

    @ApiModelProperty("客户ID")
    private String customerId;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("来源")
    private Integer source;
}

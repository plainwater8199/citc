package com.citc.nce.authcenter.csp.multitenant.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author jiancheng
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("csp")
public class Csp extends BaseDo<Csp> implements Serializable {

    private static final long serialVersionUID = 1271142522721020961L;
    @ApiModelProperty("cspId")
    private String cspId;

    private String userId;

    @ApiModelProperty("csp启用状态，0:未启用 1：已启用")
    private Boolean cspActive;

    @TableLogic
    private Boolean deleted;

    @ApiModelProperty("是否已分表，0:未分表 1：已分表")
    private Integer isSplite;
}

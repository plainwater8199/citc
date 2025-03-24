package com.citc.nce.auth.csp.csp.entity;

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
public class CspDo extends BaseDo<CspDo> implements Serializable {

    private static final long serialVersionUID = 1271142522721020961L;
    @ApiModelProperty("cspid，如果userId是10位，则cspid和userid一致")
    private String cspId;

    @ApiModelProperty("userId")
    private String userId;

    @ApiModelProperty("csp启用状态，0:未启用 1：已启用")
    private Integer cspActive;

    @ApiModelProperty("是否已分表哦，0:为分表 1：已分表")
    private Integer isSplite;

    @ApiModelProperty("是否已删除")
    private Boolean deleted;

    @ApiModelProperty("删除时间")
    private Long deletedTime;

}

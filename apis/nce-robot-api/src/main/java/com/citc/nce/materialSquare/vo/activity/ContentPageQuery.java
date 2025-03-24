package com.citc.nce.materialSquare.vo.activity;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 *
 * @author bydud
 * @since 2024-05-14 02:05:31
 */
@Getter
@Setter
@Accessors(chain = true)
public class ContentPageQuery extends PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("活动方案名称")
    private String name;
}

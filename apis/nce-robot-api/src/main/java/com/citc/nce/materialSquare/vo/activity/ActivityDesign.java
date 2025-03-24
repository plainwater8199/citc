package com.citc.nce.materialSquare.vo.activity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author bydud
 * @since 2024-05-14 02:05:31
 */
@Getter
@Setter
@Accessors(chain = true)
public class ActivityDesign implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("活动Id")
    @NotNull(message = "活动id 不能为空")
    private Long msActivityId;


    @ApiModelProperty("活动页面id")
    private Long h5Id;
}

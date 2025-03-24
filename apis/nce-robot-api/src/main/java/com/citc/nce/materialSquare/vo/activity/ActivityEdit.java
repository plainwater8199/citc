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
public class ActivityEdit implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("活动Id")
    @NotNull(message = "活动id 不能为空")
    private Long msActivityId;

    @ApiModelProperty("活动名称")
    @NotBlank(message = "活动名称 不能为空")
    @Length(min = 1, max = 25, message = "活动名称只有是1~25字符")
    private String name;

    @ApiModelProperty("封面文件id")
    private String coverFileId;

    @ApiModelProperty("封面格式")
    private String coverFormat;

    @ApiModelProperty("封面的名称")
    private String coverName;

    @ApiModelProperty("封面的大小以字节(byte)为单位")
    private String coverLength;
}

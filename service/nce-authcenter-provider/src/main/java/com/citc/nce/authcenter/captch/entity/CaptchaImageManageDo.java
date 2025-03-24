package com.citc.nce.authcenter.captch.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@TableName("captcha_image_manage")
@Data
@Accessors(chain = true)
public class CaptchaImageManageDo {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id ;

    @ApiModelProperty("图片文件ID")
    private String fileId ;

    @ApiModelProperty("创建者")
    @TableField(fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty("删除时间")
    private Integer deleted;
}

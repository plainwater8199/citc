package com.citc.nce.materialSquare.vo.activity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.citc.nce.materialSquare.PromotionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author bydud
 * @since 2024/5/14 15:31
 */
@Data
@Accessors(chain = true)
public class ContentUpdate extends ContentAdd implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty("活动方案id")
    @NotNull(message = "活动方案id不能为空")
    private Long msActivityContentId;

}

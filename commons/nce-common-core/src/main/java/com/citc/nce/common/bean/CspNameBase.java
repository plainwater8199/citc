package com.citc.nce.common.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

/**
 * @author bydud
 * @since 9:21
 */
@Getter
@Setter
public abstract class CspNameBase {

    private String creator;

    @TableField(exist = false)
    private String creatorName;

    @TableField(exist = false)
    private String enterpriseName;
}

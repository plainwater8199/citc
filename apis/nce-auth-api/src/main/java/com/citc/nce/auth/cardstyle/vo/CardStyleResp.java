package com.citc.nce.auth.cardstyle.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 17:03
 * @Version: 1.0
 * @Description:
 */
@Data
public class CardStyleResp implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value="id")
    private Long id;

    /**
     * 样式名称
     */
    @ApiModelProperty(value = "样式名称")
    private String styleName;

    /**
     * 样式信息
     */
    @ApiModelProperty(value = "样式信息")
    private String styleInfo;

    /**
     * css样式
     */
    @ApiModelProperty(value = "css样式")
    private String styleCss;

    /**
     * css文件id
     */
    @ApiModelProperty(value = "file_id")
    private String fileId;

    /**
     * 创建者
     */
    @ApiModelProperty("创建者")
    private String creator;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 更新者
     */
    @ApiModelProperty("更新者")
    private String updater;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private Date updateTime;
}

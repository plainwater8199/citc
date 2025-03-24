package com.citc.nce.authcenter.tenantdata.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/8/12 15:00
 * @Version: 1.0
 * @Description:
 */
@Data
@TableName("card_style")
public class CardStyleDo extends BaseDo {

    private static final long serialVersionUID = 1L;

    /**
     * 样式名称
     */
    @TableField(value = "style_name")
    private String styleName;

    /**
     * 样式信息
     */
    @TableField(value = "style_info")
    private String styleInfo;

    /**
     * css样式
     */
    @TableField(value = "style_css")
    private String styleCss;

    /**
     * css文件id
     */
    @TableField(value = "file_id")
    private String fileId;

    /**
     * 0未删除  1已删除
     */
    @TableField(value = "deleted")
    private int deleted;

    /**
     * 删除时间
     */
    @TableField(value = "delete_time")
    private Date deleteTime;

    private Integer isShow;

    @TableField(value = "creator_old")
    private String creatorOld;


    @TableField(value = "updater_old")
    private String updaterOld;


}

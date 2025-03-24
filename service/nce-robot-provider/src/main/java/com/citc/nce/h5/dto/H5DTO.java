package com.citc.nce.h5.dto;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class H5DTO {

    private Integer id;

    private String pid;

    /**
     * 用户唯一id
     */
    private String uid;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    @TableField("`desc`")
    private String desc;

    /**
     * 分享图标
     */
    private String shareIcon;

    /**
     * 模版schema-json
     */
    private JSONArray tpl;

    /**
     * 全局样式
     */
    private JSONObject globalStyle;

    /**
     * 0草稿 1是在线 2是已下线
     */
    private String status;

    /**
     * 页面访问量
     */
    private Integer pv;

    /**
     * 页面独立访问量
     */
    private Integer uv;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    private Integer userId;
    /**
     * 类型
     */
    private String type;
//    /**
//     * 关联活动
//     */
//    private ActivityCreateDTO activity;
}

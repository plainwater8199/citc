package com.citc.nce.misc.dictionary.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("base_area")
public class BaseArea implements Serializable {

    /**
     * 地址ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    private String baseId;

    /**
     * 地区名字
     */
    private String name;

    /**
     * 上级路径ID
     */
    private String parentId;

    /**
     * 顺序
     */
    private Integer viewOrder;


}

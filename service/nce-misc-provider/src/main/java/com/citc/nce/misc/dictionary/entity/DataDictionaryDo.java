package com.citc.nce.misc.dictionary.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022/6/28 16:04
 * @Version: 1.0
 * @Description:
 */
@TableName("data_dictionary")
@Data
public class DataDictionaryDo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
     * id
     */
    private Long id;

    /**
     * code
     */
    private String code;

    /**
     * content
     */
    private String content;

    /**
     * dic_type_id
     */
    private Long dicTypeId;

    /**
     * type_code
     */
    private String typeCode;

    /**
     * rank
     */
    private Integer sort;

    /**
     * parent_id
     */
    private String parentId;

    /**
     * creator
     */
    private String creator;

    /**
     * create_time
     */
    private Date createTime;

    /**
     * updater
     */
    private String updater;

    /**
     * update_time
     */
    private Date updateTime;

    /**
     * deleted
     */
    private Integer deleted;
}

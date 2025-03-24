package com.citc.nce.filecenter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年8月25日14:49:01
 * @Version: 1.0
 * @Description: AvailableQuantity
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_available_quantity")
public class AvailableQuantity extends BaseDo<AvailableQuantity> implements Serializable {

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * appId
     */
    private String appId;

    /**
     * 账户名
     */
    private String chatbotName;

    /**
     * 运营商
     */
    private String operator;

    /**
     * 可用数量
     */
    private Integer fileCount;

    /**
     * 可用总数量
     */
    private Integer totalCount;

    /**
     * 是否删除
     */
    private Long deleted;

    /**
     * 删除时间
     */
    private Date deleteTime;

}

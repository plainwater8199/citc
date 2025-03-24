package com.citc.nce.im.mall.snapshot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 18:06
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("mall_snapshot")
public class MallSnapshotDo extends BaseDo {
    /**
     * uuid
     */
    private String snapshotUuid;
    /**
     * 快照内容
     */
    private String snapshotContent;
    /**
     * 归属用户
     */
    private String belongUserId;
    /**
     * 快照类型 0:默认 1:模板 2:商品 3:订单
     */
    private Integer snapshotType;
    /**
     * 是否删除
     * 0:未删除 1:已删除
     */
    private int deleted;
    /**
     * 删除时间
     */
    private long deletedTime;

    /**
     * 用于csp发布模板时存储模板商城卡片样式
     */
    @TableField(value = "card_style_content")
    private String cardStyleContent;

}

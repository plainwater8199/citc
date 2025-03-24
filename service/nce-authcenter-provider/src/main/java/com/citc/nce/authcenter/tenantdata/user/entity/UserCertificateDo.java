package com.citc.nce.authcenter.tenantdata.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.io.Serializable;

/**
 * @author huangchong
 * @description 系统资质信息表
 * @date 2022-07-18
 */
@Data
@TableName("user_certificate")
public class UserCertificateDo extends BaseDo<UserCertificateDo> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 资质名称 collate utf8mb4_0900_ai_ci
     */
    @TableField("certificate_name")
    private String certificateName;

    /**
     * 资质图标 collate utf8mb4_0900_ai_ci
     */
    @TableField("certificate_img")
    private String certificateImg;

    /**
     * 资质描述 collate utf8mb4_0900_ai_ci
     */
    private String detail;

    /**
     * 平台信息(1硬核桃2核能商城)
     */
    private Integer protal;

    /**
     * 是否删除（1为已删除，0为未删除）
     */
    private Integer deleted;

    /**
     * 未删除默认为0，删除为时间戳
     */
    @TableField("deleted_time")
    private Long deletedTime;

}

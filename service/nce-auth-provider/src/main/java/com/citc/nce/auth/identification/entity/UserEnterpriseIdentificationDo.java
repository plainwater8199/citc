package com.citc.nce.auth.identification.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @authoer:ldy
 * @createDate:2022/7/14 22:48
 * @description:
 */
@Data
@TableName("user_enterprise_identification")
@Accessors(chain = true)
public class UserEnterpriseIdentificationDo extends BaseDo<UserEnterpriseIdentificationDo> {

    private static final long serialVersionUID = 1L;
    /**
     * 用户id
     */
    private String userId;

    /**
     * 企业认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过
     */
    private Integer enterpriseAuthStatus;

    /**
     * 企业账户名
     */
    private String enterpriseAccountName;

    /**
     * 企业名称
     */
    private String enterpriseName;

    /**
     * 营业执照
     */
    private String enterpriseLicense;

    /**
     * 统一社会信用代码/注册号/组织机构代码
     */
    private String creditCode;

    /**
     * 企业认证申请时间
     */
    private Date enterpriseAuthTime;

    /**
     * 企业认证审核时间
     */
    private Date enterpriseAuthAuditTime;

    /**
     * 最新审核备注
     */
    private String auditRemark;

    /**
     * 详细地址
     */
    private String address;
    /**
     * 所在省 数据字典服务
     */
    private String province;
    /**
     * 所在市 数据字典服务
     */
    private String city;
    /**
     * 所在地区 数据字典服务
     */
    private String area;

    /**
     * 是否删除
     */
    private Integer deleted;

    /**
     * 删除时间
     */
    private Long deletedTime;

    /**
     *  protal 平台信息(1核能商城2硬核桃3chatbot)
     * */
    private Integer protal;

    /**
     * CSP启用状态 0:未启用 1：已启用
     */
    private Integer cspActive;
}

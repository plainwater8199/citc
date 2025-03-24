package com.citc.nce.aim.privatenumber.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 18:06
 */
@Data
@TableName("aim_private_number_project")
public class PrivateNumberProjectDo extends BaseDo<PrivateNumberProjectDo> {

    /**
     * 项目名称
     * 同平台名称限制，唯一
     */
    private String projectName;
    /**
     * 项目编码
     * 用来识别项目，项目唯一
     */
    private String projectId;
    /**
     * 客户号码
     */
    @ApiModelProperty("客户appKey")
    private String appKey;
    /**
     * 通道账号
     */
    private String pathKey;
    /**
     * 通道秘钥
     */
    private String secret;
    /**
     * 短信内容
     */
    private String smsTemplate;
    /**
     * 项目状态
     * 0:禁用 1:启用
     */
    private Integer projectStatus;
    /**
     * 是否删除
     * 0:未删除 1:已删除
     */
    private int deleted;
    /**
     * 发消息通道类型 emay 亿美软通 fontdo 蜂动
     */
    private String channelType;

    private String creatorOld;

    private String updaterOld;
    /**
     * 是否被包含短链 0 不包含 1 包含
     */
   private Integer yuexinLinkEnable;

     private String appId;

     private String appSecret;

     private String ctccYxLink;

     private String cuccYxLink;

    private String cmccYxLink;

    private String eventType;
}

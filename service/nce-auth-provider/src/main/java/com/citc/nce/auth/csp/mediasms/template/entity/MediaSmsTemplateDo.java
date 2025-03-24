package com.citc.nce.auth.csp.mediasms.template.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author jiancheng
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("csp_video_sms_template")
@Accessors(chain = true)
public class  MediaSmsTemplateDo extends BaseDo<MediaSmsTemplateDo> {
    private static final long serialVersionUID = 4264049361711558750L;

    /*user id*/
    private String customerId;

    /*模板名称*/
    private String templateName;

    /*主题名称*/
    private String topic;

    /*平台模板ID*/
    private String platformTemplateId;

    /*所属视频短信账号*/
    private String accountId;

    /*关联签名ID*/
    private Long signatureId;

    //模板签名 送审后模板查询出关联签名ID最新的签名 且之后不再改变
    private String signature;

    @TableLogic(value = "null", delval = "now()")
    private LocalDateTime deletedTime;

    /**
     * 模板类型 1:普通模板，2:个性模板
     */
    private Integer templateType;
}

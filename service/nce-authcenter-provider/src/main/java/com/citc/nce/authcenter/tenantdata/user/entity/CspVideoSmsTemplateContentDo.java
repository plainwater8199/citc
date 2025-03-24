package com.citc.nce.authcenter.tenantdata.user.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.auth.csp.mediasms.template.enums.ContentMediaType;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author jiancheng
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("csp_video_sms_template_content")
@Accessors(chain = true)
public class CspVideoSmsTemplateContentDo extends BaseDo<CspVideoSmsTemplateContentDo> {
    private static final long serialVersionUID = -8964961098431342939L;

    /*关联视频模板ID*/
    private Long mediaTemplateId;

    /*模板内容类型,0:文本 1:图片 2:音频 3:视频*/
    private ContentMediaType mediaType;

    /*当type为文本时为文本内容,其他情况下是关联的file_uuid*/
    private String content;

    /*文件类型*/
    private String fileType;

    /*媒体资源所占字节数*/
    private Long size;

    /*媒体资源名称，文本内容可不传*/
    private String name;

    /*排序字段*/
    private Integer sortNum;

    private Date deletedTime;

    private String creatorOld;

    private String updaterOld;
}

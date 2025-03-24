package com.citc.nce.auth.readingLetter.shortUrl.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author zjy
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("reading_letter_short_url")
@Accessors(chain = true)
public class ReadingLetterShortUrlDo extends BaseDo<ReadingLetterShortUrlDo> {
    private static final long serialVersionUID = 4213624912358723L;

    /**
     * 用户Id
     */
    private String customerId;

    /**
     * 模板类型(1 -19分别为图文单卡等)
     */
    private Integer templateType;
    /**
     * 是否删除 默认0 未删除  1 删除
     */
    private Integer deleted;
    /**
     * 删除时间戳
     */
    private Long deleteTime;
    //阅信模板ID
    private Long templateId;
    //短链
    private String shortUrl;
    //域名类型  : 0默认域名  1自定义域名
    private Integer domainNameType;
    //自定义域名
    private String domain;
    //签名列表, 英文逗号链接
    private String signs;
    //申请解析数
    private Integer requestParseNumber;
    //有效时长(1-60)
    private Integer durationOfValidity;
    //跳转链接,申请成功后，该链接会作为短链，链接域名须与自定义域名一致，链接后缀仅支持3-10位的数字或大小写字母及“-”“_”“.”
    private String jumpLink;
    //自定义跳转链接,:自定义跳转链接用于短链跳转的重定向
    private String userDefinedJumpLink;
    //平台短链ID，通过这个ID获取短链详情
    private String shortUrlId;
    //审核状态 0审核中  1审核成功  2审核失败
    private Integer auditStatus;
    //有效期
    private Date validityDate;
    //申请创建短链的账号(阅信+账号ID)
    private String accountId;
    //运营商编码 0：缺省(硬核桃)，1：联通，2：移动，3：电信
    private Integer operatorCode;
    //已解析数量
    private Integer resolvedNumber;
    //模板名
    private String templateName;
    //备注
    private String remark;
    //短链解析资费单价
    private Integer price;
    //短链资费Id
    private Long tarrifId;
    //总扣费金额
    private Long amount;
    //返还金额
    private Long returnAmount;
    //已处理(已返还余额)  0未处理 1已处理
    private Integer processed;
}

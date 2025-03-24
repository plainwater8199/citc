package com.citc.nce.auth.readingLetter.shortUrl.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 * @author zjy
 */
@Data
public class ReadingLetterShortUrlAddReq {
    /**
     * 阅信模板ID
     */
    @NotNull(message = "模板id不能为空")
    @ApiModelProperty("模板id内容")
    private Long templateId;
    //域名类型  : 0默认域名  1自定义域名
    @NotNull(message = "域名类型不能为空")
    @ApiModelProperty("域名类型 0默认域名  1自定义域名")
    private Integer domainNameType;
    @ApiModelProperty("自定义域名")
    private String domain;
    @NotBlank(message = "签名不能为空")
    @ApiModelProperty("签名列表")
    private String signs;
    //申请解析数
    @NotNull(message = "申请解析数错误,必须不为空,且 1 ≤ requestParseNumber ≤ 1000w")
    @ApiModelProperty("申请解析数")
    @Max(10000000)
    @Min(1)
    private Integer requestParseNumber;
    //有效时长(1-60)
    @NotNull(message = "有效时长不能为空")
    @ApiModelProperty("有效时长")
    private Integer durationOfValidity;
    //跳转链接,申请成功后，该链接会作为短链，链接域名须与自定义域名一致，链接后缀仅支持3-10位的数字或大小写字母及“-”“_”“.”
    @ApiModelProperty("跳转链接")
    private String jumpLink;
    //自定义跳转链接,:自定义跳转链接用于短链跳转的重定向
    @ApiModelProperty("自定义跳转链接")
    private String userDefinedJumpLink;
    @NotBlank(message = "阅信+账号ID不能为空")
    @ApiModelProperty("阅信+账号ID")
    private String accountId;
    //运营商编码 0：缺省(硬核桃)，1：联通，2：移动，3：电信
    @ApiModelProperty("运营商编码")
    private Integer operatorCode;
}

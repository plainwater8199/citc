package com.citc.nce.authcenter.auth.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Accessors(chain = true)
public class ManageUserInfo {

    private static final Boolean IS_NOT_EXPORT = true;

    @NotNull(message = "pageNo不能为空")
    @ApiModelProperty(value = "pageNo", dataType = "Integer", required = true)
    private Integer pageNo;

    @NotNull(message = "pageSize不能为空")
    @ApiModelProperty(value = "pageSize", dataType = "Integer", required = true)
    private Integer pageSize;

    @ApiModelProperty(value = "账户名", dataType = "String", required = false)
    private String name;

    @ApiModelProperty(value = "手机号", dataType = "String", required = false)
    private String phone;

    @ApiModelProperty(value = "平台权限(0初始化 默认未开启,1启用,2禁用)", dataType = "Integer", required = false)
    private Integer userStatus;

    @ApiModelProperty(value = "企业认证状态0 未认证 1 认证审核中 2 认证不通过 3 认证通过", dataType = "Integer", required = false)
    private Integer enterpriseAuthStatus;
    @ApiModelProperty(value = "实名认证状态0 未认证 1 认证审核中 2 认证不通过 3 认证通过", dataType = "Integer", required = false)
    private Integer personAuthStatus;
    @ApiModelProperty(value = "认证状态0 未认证 1 认证审核中 2 认证不通过 3 认证通过", dataType = "Integer", required = false)
    private Integer authStatus;

    @NotNull(message = "protal不能为空")
    @ApiModelProperty(value = "平台信息(0统一用户管理 1核能商城 2硬核桃 3chatbot 5csp)", dataType = "Integer", required = true)
    private Integer protal;

    @ApiModelProperty(value = "模糊关键词", dataType = "String", required = false)
    private String keyWord;

    @ApiModelProperty(value = "违规次数0次传0  1-3次传1 3-10次传2  10次以上传3", dataType = "Integer", required = false)
    private List<Integer> unruleNums;

    @ApiModelProperty(value = "账户资质", dataType = "Integer", required = false)
    private List<Integer> userCertificate;

    @ApiModelProperty(value = "是否用于导出Excel")
    private Boolean isNotExport = IS_NOT_EXPORT;

    @ApiModelProperty(value = "排序字段(CREATETIME创建时间，UNRULE违规次数)")
    private String sortFile;

    @ApiModelProperty(value = "排序类型(ASC升序,DESC降序)")
    private String sortType;

    @ApiModelProperty(value = "csp状态，0-禁用，1-激活")
    private Integer cspActive;

    @ApiModelProperty(value = "用户id列表")
    private List<String> userIds;

    @ApiModelProperty(value = "扩展商城权限(0禁用,1启用)")
    private Integer tempStorePerm;

    @ApiModelProperty(value = "csp套餐客户数量状态(0正常,1异常)")
    private Integer cspMealStatus;
}

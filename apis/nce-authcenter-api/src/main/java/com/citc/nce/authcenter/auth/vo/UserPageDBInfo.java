package com.citc.nce.authcenter.auth.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class UserPageDBInfo extends PageParam {
    /**
     * 账户名 OR userId(账户id）
     */
    private String keyWord;

    /**
     * 违规次数
     * <p>
     * 全部 -1
     * 0次  0
     * 1-3次 1
     * 3-10次  2
     * 10次以上 3
     */
    private List<Integer> unruleNums;

    /**
     * 是否超过十次以上
     */
    private Integer tenFlag;

    @ApiModelProperty(value = "用户状态(0初始化 默认未开启,1启用,2禁用)", dataType = "Integer", required = false)
    private Integer userStatus;

    @ApiModelProperty(value = "资质code", dataType = "Integer", required = false)
    private List<Integer> userCertificate;

    @ApiModelProperty(value = "是否用于导出Excel")
    private Boolean isNotExport;

    @ApiModelProperty(value = "认证状态0 未认证 1 认证审核中 2 认证不通过 3 认证通过", dataType = "Integer", required = false)
    private Integer authStatus;

    @ApiModelProperty(value = "排序字段")
    private String sortFile;

    @ApiModelProperty(value = "排序类型(ASC升序,DESC降序)")
    private String sortType;

    @ApiModelProperty(value = "用户id列表")
    private List<String> userIds;
}

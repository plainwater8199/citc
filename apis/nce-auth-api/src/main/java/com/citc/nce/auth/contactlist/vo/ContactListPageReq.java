package com.citc.nce.auth.contactlist.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 16:23
 * @Version: 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class ContactListPageReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 联系人组id
     */
    @ApiModelProperty("联系人组id")
    @NotNull(message = "联系人组id不能为空")
    private Long groupId;

    /**
     * 姓名
     */
    @ApiModelProperty("姓名")
    private String personName;

    /**
     * 手机号
     */
    @ApiModelProperty("手机号")
    private String phoneNum;

    @ApiModelProperty("分页插件")
    private PageParam pageParam;

    @ApiModelProperty("最后修改时间")
    private Date updateTime;

    @ApiModelProperty("用户Id")
    private String userId;
}

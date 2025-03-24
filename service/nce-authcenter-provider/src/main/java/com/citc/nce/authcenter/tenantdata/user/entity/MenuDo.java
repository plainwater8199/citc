package com.citc.nce.authcenter.tenantdata.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.menu.entity
 * @Author: litao
 * @CreateTime: 2023-02-16  14:35
 
 * @Version: 1.0
 */
@Data
@TableName("chatbot_manage_menu")
public class MenuDo extends BaseDo<MenuDo> implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("chatbotId")
    private String chatbotId; //TODO fq

    @ApiModelProperty("chatbot_account_id")
    private String chatbotAccountId;

    @ApiModelProperty("账户管理Id")
    private String accountManagementId; //TODO fq


    @ApiModelProperty("菜单状态")
    private Integer menuStatus;

    @ApiModelProperty("菜单编码")
    private String menuCode;

    @ApiModelProperty("提交时间")
    private Date submitTime;

    @ApiModelProperty("审核结果")
    private String result;

    @ApiModelProperty("版本号")
    private Integer version;

    private String submitUser;

    private String creatorOld;

    private String updaterOld;
}

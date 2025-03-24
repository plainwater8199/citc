package com.citc.nce.dto;

//import com.sun.istack.internal.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: GroupDto
 */
@Data
public class PageReq {

    @ApiModelProperty(value = "分组分类查询的id",example = "1")
    private Long id;

    @ApiModelProperty(value = "当前页",example = "1")
    private Integer pageNo;

    @ApiModelProperty(value = "每页展示条数",example = "5")
    private Integer pageSize;

    @ApiModelProperty(value = "账户名",example = "联通")
    private String chatbotName;

    @ApiModelProperty(value = "文件名",example = "a.mp4")
    private String fileName;

    @ApiModelProperty(value = "审核状态",example = "0")
    private Integer status;

    /**
     * 素材主键id
     */
    private List<Long> materialIds;

    /**
     * 查询类型
     */
    private String tabs;

    @ApiModelProperty(value = "运营商列表",example = "0")
    private List<String> operators;
    @ApiModelProperty(value = "场景关联账号集合",example = "2222")
    private List<String> chatbotAccounts;
}

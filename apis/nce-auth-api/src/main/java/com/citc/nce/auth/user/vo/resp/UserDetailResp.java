package com.citc.nce.auth.user.vo.resp;

import com.citc.nce.common.core.pojo.BaseUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/6/21 17:09
 * @Version: 1.0
 * @Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailResp extends BaseUser {

    @ApiModelProperty(value = "id", dataType = "String")
    private Long id;
    @ApiModelProperty(value = "token", dataType = "String")
    private String token;

}

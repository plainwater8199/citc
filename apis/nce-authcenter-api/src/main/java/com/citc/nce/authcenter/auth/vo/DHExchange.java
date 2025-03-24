package com.citc.nce.authcenter.auth.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

/**
 * @author bydud
 * @since 2024/7/16 10:30
 */
@Data
@Accessors(chain = true)
public class DHExchange {
    @NotEmpty
    private String publicKey;
    //随机字符串
    private String time;
}

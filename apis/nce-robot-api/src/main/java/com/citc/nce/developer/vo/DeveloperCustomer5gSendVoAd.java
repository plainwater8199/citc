package com.citc.nce.developer.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 *
 * @author bydud
 * @since 2024/3/26
 */
@Data
public class DeveloperCustomer5gSendVoAd extends DeveloperCustomer5gSendVo {
    @NotBlank
    private String token;
}

package com.citc.nce.auth.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @authoer:ldy
 * @createDate:2022/7/17 22:05
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class UserAuthDto {
    private String userId;
    private Integer personAuthStatus;
    private Integer enterpriseAuthStatus;
    private Integer authStatus;
}

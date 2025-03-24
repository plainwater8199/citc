package com.citc.nce.authcenter.permission.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiancheng
 */
@Data
@Accessors(chain = true)
public class GetUrlPermissionReq {
    private String uri;
}

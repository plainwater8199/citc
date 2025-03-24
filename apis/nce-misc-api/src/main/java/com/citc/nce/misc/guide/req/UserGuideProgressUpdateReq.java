package com.citc.nce.misc.guide.req;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author jcrenc
 * @since 2024/11/6 17:10
 */
@Data
public class UserGuideProgressUpdateReq {

    @NotNull
    private String guideName;

    private int currentStep;
}

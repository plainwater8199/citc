package com.citc.nce.im.broadcast.exceptions;

import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.ErrorCode;

/**
 * @author jcrenc
 * @since 2024/4/15 15:48
 */
public class GroupPlanStoppedException extends BizException {
    private static final long serialVersionUID = -4333747798115522048L;

    public GroupPlanStoppedException(ErrorCode code) {
        super(code);
    }

    public GroupPlanStoppedException(String msg) {
        super(msg);
    }
}

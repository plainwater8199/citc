package com.citc.nce.im.broadcast.exceptions;

import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.ErrorCode;
import com.citc.nce.msgenum.SendStatus;
import lombok.Getter;

/**
 * @author jcrenc
 * @since 2024/4/13 11:20
 */
@Getter
public class GroupPlanExecuteException extends BizException {
    private static final long serialVersionUID = 732479697634450687L;
    private final SendStatus state;

    public GroupPlanExecuteException(String msg) {
        super(msg);
        state = SendStatus.SEND_FAIL;
    }

    public GroupPlanExecuteException(SendStatus state, String msg) {
        super(msg);
        this.state = state;
    }

    public GroupPlanExecuteException(ErrorCode code) {
        super(code);
        state = SendStatus.SEND_FAIL;
    }
}

package com.citc.nce.im.tempStore.service.impl.useTemplate;/*
 *
 * @author bydud
 * @since 2024/2/21
 */

import java.util.List;

public class SaveResException extends RuntimeException {
    private List<Long> duplicate;

    public SaveResException(List<Long> duplicate) {
        this.duplicate = duplicate;
    }

    public SaveResException(String message, List<Long> duplicate) {
        super(message);
        this.duplicate = duplicate;
    }

    public SaveResException(String message, Throwable cause, List<Long> duplicate) {
        super(message, cause);
        this.duplicate = duplicate;
    }

    public SaveResException(Throwable cause, List<Long> duplicate) {
        super(cause);
        this.duplicate = duplicate;
    }

    public SaveResException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<Long> duplicate) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.duplicate = duplicate;
    }

    public List<Long> getDuplicate() {
        return duplicate;
    }
}

package com.citc.nce.im.broadcast.utils;

import java.time.format.DateTimeFormatter;

/**
 * @author jcrenc
 * @since 2024/3/26 15:52
 */
public final class BroadcastConstants {
    private BroadcastConstants() {
    }

    public final static String START = "start";
    public final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public final static String ANY_BUTTON = "any";
    //5G消息发送批次数量
    public final static int _5G_BATCH_SIZE = 100;
    public final static int MEDIA_BATCH_SIZE = 500;

    public final static int _5G_SENT_CODE_SUCCESS = 0;
    public final static int _5G_SEND_CODE_FAILED = 1;

}

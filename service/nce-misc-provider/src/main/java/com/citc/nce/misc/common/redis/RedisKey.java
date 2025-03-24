package com.citc.nce.misc.common.redis;

import java.util.concurrent.TimeUnit;

/**
 * @authoer:ldy
 * @createDate:2022/7/2 2:04
 * @description:
 */
public enum RedisKey {

    CAPTCHA("CAPTCHA:%s", TimeUnit.MINUTES.toSeconds(5)),
    CAPTCHAEMAIL("CAPTCHAEMAIL:%s", TimeUnit.MINUTES.toSeconds(30)),
    CAPTCHASMS("CAPTCHASMS:%s", TimeUnit.MINUTES.toSeconds(5)),
    CAPTCHAONEMINITE("CAPTCHA:%s", TimeUnit.MINUTES.toSeconds(1));


    /**
     * key 的格式，
     */
    private String keyFormat;
    /**
     * 过期时间（单位s）
     */
    private long expireSecs;
    /**
     * 描述
     */
    private String desc;

    RedisKey(String keyFormat) {
        //key需要加上模块的前缀, 默认30天过期
        this(keyFormat, TimeUnit.DAYS.toSeconds(30));
    }

    RedisKey(String keyFormat, long expireSecs) {
        this.keyFormat = "MISC" + ":" + keyFormat;
        this.expireSecs = expireSecs;
    }

    public String build(Object... args) {
        if (args == null || args.length <= 0) {
            return keyFormat;
        }
        return String.format(keyFormat, args);
    }

    public long getExpireSecs() {
        return this.expireSecs;
    }

}

package com.citc.nce.auth.readingLetter.template.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author zjy
 */
@Getter
@RequiredArgsConstructor
public enum ReadingLetterTemplateType {
    //1图文单卡 2视频单卡 3图片视频单卡 4视频图片单卡 5长交本国文卡片 6图片轮酒卡片 7国文多卡 8行程卡片 9一般通知卡片
    // 10增强通知卡片 11新闻卡片 12单商品卡片 13字商品卡片 14单卡学卡片 15字卡学卡片 16红包卡片 17短剧视频 18短剧图片 19海报模板
    GRAPH_CARD(1,"图文单卡"),
    VIDEO_SINGLE_CARD(2,"视频单卡"),
    PICTURE_VIDEO_SINGLE_CARD(3,"图片视频单卡"),
    VIDEO_PICTURE_SINGLE_CARD(4,"视频图片单卡"),
    LONG_TEXT_GRAPHIC_CARD(5,"长文本图文卡片"),
    PICTURE_CAROUSEL_CARD(6,"图片轮播卡片"),
    TOOGRAPH(7,"图文多卡"),
    ITINERARY_CARD(8,"行程卡片"),
    GENERAL_NOTICE_CARD(9,"一般通知卡片"),
    ENHANCED_NOTIFICATION_CARD(10,"增强通知卡片"),
    NEWS_CARD(11,"新闻卡片"),
    SINGLE_COMMODITY_CARD(12,"单商品卡片"),
    MULTI_COMMODITY_CARD(13,"多商品卡片"),
    SINGLE_CARD_COUPON_CARD(14,"单卡券卡片"),
    MULTI_CARD_VOUCHER_CARD(15,"多卡券卡片"),
    RED_ENVELOPE_CARD(16,"红包卡片"),
    VIDEO_CARDS_FOR_SKITS(17,"短剧视频卡片"),
    SKIT_PICTURE_CARD(18,"短剧图片卡片"),
    POSTER_TEMPLATE_CARD(19,"海报模板卡片"),
    ;

    @EnumValue
    private final Integer code;

    private final String name;

    /*
    通过code获取name
     */
    public static String getName(Integer code) {
        for (ReadingLetterTemplateType type : ReadingLetterTemplateType.values()) {
            if (type.getCode().equals(code)) {
                return type.getName();
            }
        }
        return null;
    }

     /*
    通过code获取枚举
     */
    public static ReadingLetterTemplateType getEnum(Integer code) {
        for (ReadingLetterTemplateType type : ReadingLetterTemplateType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

}

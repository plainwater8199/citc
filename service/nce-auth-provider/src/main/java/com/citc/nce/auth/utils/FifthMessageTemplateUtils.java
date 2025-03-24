package com.citc.nce.auth.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author jcrenc
 * @since 2024/12/19 15:24
 */

public final class FifthMessageTemplateUtils {
    private FifthMessageTemplateUtils() {
    }

    /**
     * 查找5G消息模板里的素材，并返回素材id列表
     *
     * @param templateContent 模板内容
     * @param messageType     模板消息类型
     * @return 素材id列表
     */
    public static List<String> find5gTemplateMaterialFileUuid(String templateContent, int messageType) {
        JSONObject templateJson = JSONObject.parseObject(templateContent);
        if(templateJson == null)
            return Collections.emptyList();
        List<String> fileUrlIds = new ArrayList<>();
        switch (messageType) {
            //图片
            case 2:
                fileUrlIds.add(templateJson.getString("pictureUrlId"));
                break;
            //视频
            case 3:
                fileUrlIds.add(templateJson.getString("videoUrlId"));
                break;
            //音频
            case 4:
                fileUrlIds.add(templateJson.getString("audioUrlId"));
                break;
            //文件
            case 5:
                fileUrlIds.add(templateJson.getString("fileUrlId"));
                break;
            //单卡
            case 6:
                fileUrlIds.addAll(getTemplateFileIds(templateJson));
                break;
            //多卡
            case 7:
                JSONArray cardList = templateJson.getJSONArray("cardList");
                for (int i = 0; i < cardList.size(); i++) {
                    fileUrlIds.addAll(getTemplateFileIds(cardList.getJSONObject(i)));
                }
                break;
        }
        return fileUrlIds;
    }

    private static List<String> getTemplateFileIds(JSONObject templateJson) {
        List<String> fileIds = new ArrayList<>();
        if (StringUtils.isNotEmpty(templateJson.getString("pictureUrlId"))) {
            fileIds.add(templateJson.getString("pictureUrlId"));
        }
        if (StringUtils.isNotEmpty(templateJson.getString("videoUrlId"))) {
            fileIds.add(templateJson.getString("videoUrlId"));
        }
        if (StringUtils.isNotEmpty(templateJson.getString("audioUrlId"))) {
            fileIds.add(templateJson.getString("audioUrlId"));
        }
        return fileIds;
    }
}

package com.citc.nce.common.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/5/24 15:19
 * @Version: 1.0
 * @Description:
 */
public class JsonUtils {

    /**
     * 对象转Json格式字符串
     *
     * @param obj 对象
     * @return Json格式字符串
     */
    public static <T> String obj2String(T obj) {
        if (obj == null) {
            return null;
        }
        return obj instanceof String ? (String) obj : JSON.toJSONString(obj);
    }

    /**
     * 对象转Json格式字符串(格式化的Json字符串)
     *
     * @param obj 对象
     * @return 美化的Json格式字符串
     */
    public static <T> String obj2StringPretty(T obj) {
        if (obj == null) {
            return null;
        }
        return obj instanceof String ? (String) obj : JSON.toJSONString(obj, SerializerFeature.PrettyFormat);

    }

    /**
     * 字符串转换为自定义对象
     *
     * @param str   要转换的字符串
     * @param clazz 自定义对象的class对象
     * @return 自定义对象
     */
    public static <T> T string2Obj(String str, Class<T> clazz) {
        if (StrUtil.isEmpty(str) || clazz == null) {
            return null;
        }
        return clazz.equals(String.class) ? (T) str : JSON.parseObject(str, clazz);
    }


}

package com.citc.nce.common.util;

/**
 * 比较2个json是否相同
 * @author yy
 * @date 2024-03-13 14:50:15
 */
import cn.hutool.core.util.StrUtil;
import com.google.gson.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class JsonSameUtil {
    private static final Gson gson = new Gson();

    private static final JsonParser parser = new JsonParser();

    public static boolean same(String a, String b,String ignore) {
        if (null == a) {
            return  null== b;
        }else if(null== b)
        {
            return  false;
        }
        if (a.equals(b)) {
            return true;
        }
        JsonElement aElement = parser.parse(a);
        JsonElement bElement = parser.parse(b);
        if (gson.toJson(aElement).equals(gson.toJson(bElement))) {
            return true;
        }
        return same(aElement, bElement, ignore);
    }

    private static boolean same(JsonElement a, JsonElement b,String ignore) {
        if (a.isJsonObject() && b.isJsonObject()) {
            return same((JsonObject) a, (JsonObject) b,ignore);
        } else if (a.isJsonArray() && b.isJsonArray()) {
            return same((JsonArray) a, (JsonArray) b,ignore);
        } else if (a.isJsonPrimitive() && b.isJsonPrimitive()) {
            return same((JsonPrimitive) a, (JsonPrimitive) b);
        } else if (a.isJsonNull() && b.isJsonNull()) {
            return same((JsonNull) a, (JsonNull) b);
        } else {
            return false;
        }
    }

    private static boolean same(JsonObject a, JsonObject b,String ignore) {
        Set<String> aSet = a.keySet();
        Set<String> bSet = b.keySet();
        if (!aSet.equals(bSet)) {
            return false;
        }
        for (String aKey : aSet) {
            if(StrUtil.isNotEmpty(ignore)&&ignore.contains(aKey))
            continue;
            if (!same(a.get(aKey), b.get(aKey),ignore)) {
                return false;
            }
        }
        return true;
    }

    private static boolean same(JsonArray a, JsonArray b,String ignore) {
        if (a.size() != b.size()) {
            return false;
        }
        List<JsonElement> aList = toSortedList(a);
        List<JsonElement> bList = toSortedList(b);
        for (int i = 0; i < aList.size(); i++) {
            if (!same(aList.get(i), bList.get(i),ignore)) {
                return false;
            }
        }
        return true;
    }

    private static boolean same(JsonPrimitive a, JsonPrimitive b) {
        return a.equals(b);
    }

    private static boolean same(JsonNull a, JsonNull b) {
        return true;
    }

    private static List<JsonElement> toSortedList(JsonArray a) {
        List<JsonElement> aList = new ArrayList<>();
        a.forEach(aList::add);
        aList.sort(Comparator.comparing(gson::toJson));
        return aList;
    }
}

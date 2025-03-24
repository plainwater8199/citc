package com.citc.nce.common.constants;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.Getter;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 运营商枚举类
 * 0：缺省(硬核桃)，1：联通，2：移动，3：电信
 *
 * @author yy
 * @date 2024-03-17 10:56:13
 */

public enum CarrierEnum {
    Walnut("walnut", "硬核桃", 0),
    Unicom("unicom", "联通", 1),
    CMCC("cmcc", "移动", 2),
    Telecom("telecom", "电信", 3);
    @Getter
    String tag;
    @Getter
    String name;
    @Getter
    int value;

    CarrierEnum(String tag, String name, int value) {
        this.tag = tag;
        this.name = name;
        this.value = value;
    }
    private static final Map<Integer, CarrierEnum>  carrierEnumMap;

    static {
        carrierEnumMap = new ConcurrentHashMap<>();
        EnumSet.allOf(CarrierEnum.class).forEach(obj -> carrierEnumMap.put(obj.getValue(), obj));
    }

    public static CarrierEnum getCarrierEnum(Integer code) {
        return carrierEnumMap.getOrDefault(code, null);
    }
    public static CarrierEnum getCarrierEnum(String name) {
     List<CarrierEnum> carrierEnums=  carrierEnumMap.values().stream().filter(carrierEnum -> carrierEnum.getName().equals(name)||carrierEnum.getTag().equals(name)).collect(Collectors.toList());
        return carrierEnums.get(0);
    }
    public static boolean existForName(String name) {
        if(StrUtil.isEmpty(name))return false;
        List<CarrierEnum> carrierEnums=  carrierEnumMap.values().stream().filter(carrierEnum -> carrierEnum.getName().equals(name)).collect(Collectors.toList());
        return carrierEnums.stream().filter(item->item.getName().equals(name)).count()>0;
    }

}

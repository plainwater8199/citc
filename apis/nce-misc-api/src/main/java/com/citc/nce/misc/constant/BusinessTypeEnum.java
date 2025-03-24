package com.citc.nce.misc.constant;

public enum BusinessTypeEnum {
    YHGL_TY(0,"用户管理(统一用户运营管理)"),
    YHGL_HN(1,"用户管理(核能商城管理平台)"),
    XQYXGD(2,"需求意向工单"),
    HZYXGD(3,"合作意向工单"),
    ZZSQGD(4,"资质申请工单"),
    JJFAGL(5,"解决方案管理"),
    JJFAJBGL(6,"解决方案举报管理"),
    APIGL(7,"API管理"),
    DDGL(8,"订单管理"),
    NLTGSGL(9,"能力提供商管理"),
    JJFASGL(10,"解决方案商管理"),
    FWWJGL(11,"法务文件管理"),
    ZNXGL(12,"站内信管理"),
    SCGC(13,"素材广场");




    private Integer code;

    private String name;

    BusinessTypeEnum(Integer code, String name){
        this.code = code;
        this.name = name;
    }

    public Integer getCode(){
        return this.code;
    }

    public String getName(){
        return this.name;
    }

    public static String getName(int index) {
        for (BusinessTypeEnum s : BusinessTypeEnum
                .values()) {
            if (s.getCode() == index) {
                return s.getName();
            }
        }
        return "";
    }
}

package com.citc.nce.misc.constant;

public enum ProcessingContentEnum {
    BJYHXX(0,"编辑用户信息"),
    SMRZSHTG(1,"实名认证审核通过"),
    SMRZSHWTG(2,"实名认证审核未通过"),
    QYRZSHTG(3,"企业认证审核通过"),
    QYRZSHWTG(4,"企业认证审核未通过"),
    JYYH(5,"禁用平台"),
    QYYH(6,"启用平台"),
    QYJJFASZZ(7,"启用解决方案商资质"),
    JYJJFASZZ(8,"禁用解决方案商资质"),
    QYNLTGSZZ(9,"启用能力提供商资质"),
    JYNLTGSZZ(10,"禁用能力提供商资质"),
    JSGD(11,"接收工单"),
    CLGD(12,"处理工单"),
    TYSQ(13,"同意申请"),
    BHSQ(14,"驳回申请"),
    SHTG(15,"审核通过"),
    SHBTG(16,"审核不通过"),
    SC(17,"删除"),
    JBSSYSC(18,"举报属实已删除"),
    JBBSS(19,"举报不属实"),
    CJAPI(20,"创建API"),
    BJAPI(21,"编辑API"),
    SJ(22,"上架"),
    XJ(23,"下架"),
    JY(24,"禁用"),
    QXJY(25,"取消禁用"),
    SCAPI(26,"删除API"),
    CJDD(27,"创建订单"),
    GBDD(28,"关闭订单"),
    GXWD(29,"更新文档"),
    CJXX(30,"创建消息"),
    BJXX(31,"编辑消息"),
    SCXX(31,"编辑消息"),

    BGTD(32,"更换通道");

    private Integer code;

    private String name;

    ProcessingContentEnum(Integer code, String name){
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
        for (ProcessingContentEnum s : ProcessingContentEnum
                .values()) {
            if (s.getCode() == index) {
                return s.getName();
            }
        }
        return "";
    }
}

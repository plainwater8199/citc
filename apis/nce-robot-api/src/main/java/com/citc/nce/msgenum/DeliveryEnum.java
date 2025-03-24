package com.citc.nce.msgenum;

/**
 * 网关消息状态（）：
 * gatewaysent：消息网关
 * 已发送消息；
 * sent：消息已发送；
 * failed：消息发送失败；
 * delivered：消息已送
 * 达；
 * displayed：消息已阅
 * 读；
 * deliveredToNetwork：
 * 已转短信发送；
 * revokeOk：消息撤回成
 * 功;
 * revokeFail：消息撤回失
 * 败
 */
public enum DeliveryEnum {

    UN_KNOW(0,"未知",-1),
    DELIVERED(1,"消息已送达",3),
    FAILED(2,"发送失败",2),
    REVOKE_SUCCESS(3,"消息撤回成功",6),
    DELIVERED_TO_NETWORK(4,"已转短信发送",5),
    REVOKE_FAIL(5,"消息撤回失败",7),
    DISPLAYED(6,"消息已阅",4);


    private final int code;
    private final String desc;
    private final int order; //

    DeliveryEnum(int code, String desc,int order) {
        this.code = code;
        this.desc = desc;
        this.order = order;
    }
 
    /**
     * 自己定义一个静态方法,通过code返回枚举常量对象
     * @param code
     * @return
     */
    public static DeliveryEnum getValue(int code){
 
        for (DeliveryEnum color: values()) {
            if(color.getCode() == code){
                return  color;
            }
        }
        return null;
 
    }
 
 
    public int getCode() {
        return code;
    }

 
    public String getDesc() {
        return desc;
    }

    public int getOrder() {
        return order;
    }
}
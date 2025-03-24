package com.citc.nce.msgenum;

public enum RequestEnum {
    UN_KNOW(0,"未知"),
    SUCCESS(1,"发送成功"),
    FAILED(2,"发送失败"),
    REVOKE_SUCCESS(3,"消息撤回成功"),
    FALLBACK_SMS(4, "回落短信"),
    REVOKE_FAIL(5,"消息撤回失败"),
    DISPLAYED(6,"消息已阅");


    //    可以看出这在枚举类型里定义变量和方法和在普通类里面定义方法和变量没有什么区别。唯一要注意的只是变量和方法定义必须放在所有枚举值定义的后面，否则编译器会给出一个错误。
    private int code;
    private String desc;

    RequestEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 自己定义一个静态方法,通过code返回枚举常量对象
     * @param code
     * @return
     */
    public static RequestEnum getValue(int code){

        for (RequestEnum color: values()) {
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

}

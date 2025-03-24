package com.citc.nce.msgenum;
 
public enum SendStatus {

    TO_BE_SEND(0,"待启动"),
    SENDING(1,"执行中"),
    SEND_SUCCESS(2,"执行完毕"),
    SEND_STOP(3,"已暂停"),
    SEND_FAIL(4,"执行失败"),
    SEND_CLOSE(5,"已关闭"),
    EXPIRED(6,"已过期"),
    NO_STATUS(7,"无状态"),
    MATERIAL_REVIEW(8,"素材审核中");


    private int code;
    private String desc;
 
    SendStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public static SendStatus getValue(int code){
 
        for (SendStatus color: values()) {
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
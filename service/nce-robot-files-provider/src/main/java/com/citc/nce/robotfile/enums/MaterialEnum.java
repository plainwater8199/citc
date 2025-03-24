package com.citc.nce.robotfile.enums;

public enum MaterialEnum {
    PICTURE(1,"图片"),
    VIDEO(2,"视频"),
    AUDIO(3,"音频"),
    FILE(4,"文件");


    private int code;
    private String desc;

    MaterialEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 自己定义一个静态方法,通过code返回枚举常量对象
     * @param code
     * @return
     */
    public static MaterialEnum getValue(int code){

        for (MaterialEnum color: values()) {
            if(color.getCode() == code){
                return  color;
            }
        }
        return null;

    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

package com.citc.nce.auth.messagetemplate.vo.shortcutbutton;

import lombok.Data;

import java.util.List;

@Data
public class ModuleInformation {
    private String name;
    private String imgSrc;
    private String type;
    private String pictureUrlId;
    private String size;
    private TitleInfo title;
    private String width;
    private String height;
    private String position;
    private String style;
    private String styleInfo;
    private String layout;
    private String longitudinal;
    private DescriptionInfo description;
    private List<ShortCutButtonInfo> buttonList;
    private List<CardListInfo> cardList;

}

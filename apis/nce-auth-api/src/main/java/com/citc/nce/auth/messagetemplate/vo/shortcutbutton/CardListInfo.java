package com.citc.nce.auth.messagetemplate.vo.shortcutbutton;

import lombok.Data;

import java.util.List;

@Data
public class CardListInfo {
    private String id;
    private TitleInfo title;
    private String style;
    private DescriptionInfo description;
    private String imgSrc;
    private String width;
    private String height;
    private String position;
    private String styleInformation;
    private String styleStatus;
    private String styleInfo;
    private List<ShortCutButtonInfo> buttonList;
}

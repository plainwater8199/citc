package com.citc.nce.auth.readingLetter.shortUrl.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zjy
 */
@Data
public class ReadingLetterAvailableShortUrlsReq {
    @ApiModelProperty("模板名或短链")
    private String  nameOrUrl;
}

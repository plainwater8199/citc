package com.citc.nce.auth.readingLetter.shortUrl.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zjy
 */
@Data
public class ReadingLetterShortUrlReApplyReq extends ReadingLetterShortUrlAddReq{
    @ApiModelProperty("id")
    private Long id;
}

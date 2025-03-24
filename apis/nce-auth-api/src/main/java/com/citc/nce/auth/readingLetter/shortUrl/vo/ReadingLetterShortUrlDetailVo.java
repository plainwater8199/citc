package com.citc.nce.auth.readingLetter.shortUrl.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author zjy
 */
@Data
public class ReadingLetterShortUrlDetailVo extends ReadingLetterShortUrlAddReq{
    @ApiModelProperty("Id")
    private Long id;

}

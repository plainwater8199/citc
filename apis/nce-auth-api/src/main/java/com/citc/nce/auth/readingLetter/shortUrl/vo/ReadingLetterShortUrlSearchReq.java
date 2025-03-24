package com.citc.nce.auth.readingLetter.shortUrl.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zjy
 */
@Data
public class ReadingLetterShortUrlSearchReq {
    @ApiModelProperty("id")
    private Long id;
}

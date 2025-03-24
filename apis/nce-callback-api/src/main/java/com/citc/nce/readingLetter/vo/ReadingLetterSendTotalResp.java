package com.citc.nce.readingLetter.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author zjy
 */
@Data
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
public class ReadingLetterSendTotalResp {

    @ApiModelProperty("5G阅信数据")
    private Map<Integer, Integer> fifthReadingLetter;

    @ApiModelProperty("阅信+数据")
    private Map<Integer, Integer> readingLetterPlus;
}

package com.citc.nce.readingLetter.req;

import com.citc.nce.dataStatistics.vo.msg.MsgSendByChannelItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author zjy
 */
@Data
@ApiModel
public class ReadingLetterMsgSendTotalResp {
    @ApiModelProperty("5G阅信解析数量")
    private int fifthReadingLetterParseNum;

    @ApiModelProperty("阅信+解析数量")
    private int readingLetterPlusParseNum;
}

package com.citc.nce.dataStatistics.vo.resp;

import lombok.Data;

@Data
public class OperatorResp {

    private String sendTimeDay;

    private String sendTimeHour;

    private String sendTimeWeek;

    private Long mobileSendAmount = 0L;

    private Long unicomSendAmount = 0L ;

    private Long telecomSendAmount = 0L;

    private Long walnutSendAmount = 0L;

    private Long sendAmount = 0L;

    private Long sendAmountQuantityDifference = 0L;

    private Long mobileQuantityDifference = 0L;

    private Long unicomQuantityDifference = 0L;

    private Long telecomQuantityDifference = 0L;

    private Long walnutQuantityDifference = 0L;

    private Long yimeiQuantityDifference = 0L;

    private Long shortMsgSendAmount = 0L;

    private Long yimeiSendAmount = 0L;

    private String mobilePercentage = "0%";

    private String unicomPercentage = "0%";

    private String telecomPercentage = "0%";

    private String walnutPercentage = "0%";

    private String yimeiPercentage = "0%";

    private String sendAmountPercentage = "0%";

    private Integer activeChatBotNum = 0;

    private Integer activeChatBotDifference = 0;

    private String activeChatBotPercentage = "0%";

    private Integer activeAccountNum = 0;

    private Integer activeAccountDifference = 0;

    private String activeAccountPercentage = "0%";

    private String operators;

    private String activeUserNum = "0";

    private Long activeShortMsgUserNum = 0L;

    private String operator;

}

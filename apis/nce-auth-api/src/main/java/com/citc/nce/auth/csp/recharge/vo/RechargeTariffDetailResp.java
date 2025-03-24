package com.citc.nce.auth.csp.recharge.vo;

import com.citc.nce.auth.csp.recharge.Const.TariffTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@EqualsAndHashCode(callSuper = true)
@Data
public class RechargeTariffDetailResp extends RechargeTariffAdd implements Serializable {
    @ApiModelProperty(name = "主键", notes = "")
    private Long id;

    public Integer getPriceOfTariffType(Integer tariffType) {
        TariffTypeEnum tariffTypeEnum=TariffTypeEnum.getByCode(tariffType);
        switch (tariffTypeEnum)
        {
            case TEXT_MESSAGE:
                return this.getTextMsgPrice();
            case SMS:
                return  this.getSmsPrice();
            case VIDEO_SMS:
                return this.getVideoSmsPrice();
            case FALLBACK_SMS:
                return this.getFallbackSmsPrice();
            case SESSION_MESSAGE:
                return this.getSessionMsgPrice();
            case RICH_MEDIA_MESSAGE:
                return this.getRichMsgPrice();
            case READING_LETTER_PLUS_PARSE:
                return this.getYxPlusAnalysisPrice();
            case FIVE_G_READING_LETTER_PARSE:
                return this.getYxAnalysisPrice();
            default:
                throw new BizException(tariffType+"非法");
        }
    }
}

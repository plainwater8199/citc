package com.citc.nce.auth.utils;

import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import com.citc.nce.auth.prepayment.service.IPrepaymentService;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.SpringUtils;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import org.apache.commons.lang.RandomStringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum.*;

/**
 * @author jcrenc
 * @since 2024/3/6 10:32
 */
public class MsgPaymentUtils {

    public static final Map<MsgTypeEnum, String> PREPAYMENT_MSG_IMPLEMENT_MAP;

    static {
        Map<MsgTypeEnum, String> map = new HashMap<>(MsgTypeEnum.values().length);
        map.put(MsgTypeEnum.M5G_MSG, "fifthPrepaymentServiceImpl");
        map.put(MsgTypeEnum.MEDIA_MSG, "videoSmsPrepaymentServiceImpl");
        map.put(MsgTypeEnum.SHORT_MSG, "smsPrepaymentServiceImpl");
        PREPAYMENT_MSG_IMPLEMENT_MAP = Collections.unmodifiableMap(map);
    }

    private static final int PRICE_SCALE = 5;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.DOWN;

    //一个方案应该包含的运营商类型枚举集合
    public final static List<CSPOperatorCodeEnum> MSG_PAYMENT_OPERATORS = Arrays.asList(CMCC, CUNC, CT);

    //生成订单号用的df
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    /**
     * 格式化消息单价
     */
    public static BigDecimal formatPrice(BigDecimal price) {
        if(price == null){
            return BigDecimal.ZERO;
        }
        return price.setScale(PRICE_SCALE, ROUNDING_MODE).stripTrailingZeros();
    }

    public static BigDecimal formatOrderAmount(String amount){
        BigDecimal decimal = new BigDecimal(amount);
        return decimal.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    public static BigDecimal formatOrderAmount(BigDecimal amount){
        return amount.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
    }


    public static String generateOrderId() {
        return FORMATTER.format(LocalDateTime.now()) + RandomStringUtils.randomNumeric(3);
    }

    /**
     * 检查方案配置的运营商是否合法
     *
     * @param configuredOperators 方案里配置的运营商类别集合
     * @throws BizException 当缺少必要的运营商配置
     */
    public static void checkConfiguredOperators(Set<CSPOperatorCodeEnum> configuredOperators) {
        if (configuredOperators.size() != MSG_PAYMENT_OPERATORS.size())
            throw new BizException("配置运营商的数量不正确");
        Set<CSPOperatorCodeEnum> missingOperators = MSG_PAYMENT_OPERATORS.stream()
                .filter(operator -> !configuredOperators.contains(operator))
                .collect(Collectors.toSet());
        if (!missingOperators.isEmpty()) {
            String missingOperatorNames = missingOperators.stream()
                    .map(CSPOperatorCodeEnum::getName)
                    .collect(Collectors.joining(", "));
            throw new BizException(String.format("缺少%s的配置", missingOperatorNames));
        }
    }

    /**
     * 根据消息类型获取预付费订单实现
     */
    public static IPrepaymentService getPrepaymentServiceImpl(MsgTypeEnum msgTypeEnum) {
        if (!PREPAYMENT_MSG_IMPLEMENT_MAP.containsKey(msgTypeEnum))
            throw new BizException("no implementation");
        return SpringUtils.getBean(PREPAYMENT_MSG_IMPLEMENT_MAP.get(msgTypeEnum), IPrepaymentService.class);
    }
}

package com.citc.nce.auth.utils;

import com.citc.nce.robot.vo.directcustomer.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class RobotUtils {




    /**
     * 将字符串中系统变量、自定义变量、提问回复装载到List<Parameter>
     *
     * @param str            要翻译的字符串
     * @return 翻译后的字符串
     */
    public static String setVariableParam(String str, List<Parameter> params) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        str = getSystemVariableParam(str, params);
        str = getCustomVariableParam(str, params);
        str = getQuestionReplyParam(str, params);
        return str;
    }

    /**
     * 翻译系统变量
     * {{sys-time}}    系统时间
     * {{sys-phone}}   手机号
     * {{sys-process}} 触发流程上行
     *
     * @param str   需要翻译的字符串
     * @return 翻译后的字符串
     */
    public static String getSystemVariableParam(String str, List<Parameter> parameters) {
        if (StringUtils.isEmpty(str))
            return str;
        //系统时间
        if (str.contains("{{sys-time}}")) {
            //str = str.replace("{{sys-time}}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            Parameter timeParam = new Parameter("STRING", "sys-time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            parameters.add(timeParam);
          }

        //系统时间
        if (str.contains("{{sys-phone}}")) {
            //str = str.replace("{{sys-phone}}", StringUtils.isNotEmpty(phone) ? phone : "");
            Parameter phoneParam = new Parameter("STRING", "sys-phone", null);
            parameters.add(phoneParam);
          }
        if (str.contains("{{sys-process}}")) {
            Parameter processParam = new Parameter("STRING", "sys-process", null);
            parameters.add(processParam);
        }
        return str;
    }


    /**
     * 翻译系统变量
     *
     * @param str            需要翻译的字符串
     * @return 翻译后的字符串
     */
    public static String getCustomVariableParam(String str, List<Parameter> parameters) {
        if (StringUtils.isEmpty(str))
            return str;
        List<String> variableExpressions = findVariableExpression("\\{\\{custom-[^\\}]+\\}\\}", str);
        for (String variableExpression : variableExpressions) {
            log.info("variableExpression:{} ", variableExpression);
            //{{custome-64&美景}}这个查找不到 {{custome-美景}}这个查找得到 ,需要 替换为新key去查找数据
            int and = variableExpression.indexOf("&");
            int strigula = variableExpression.indexOf("-");
            if (and == -1 || strigula == -1) {
                log.info("没有找到&或-");
                continue;
            }
            if (variableExpression.contains("&") && variableExpression.contains("-")) {
                String[] split = variableExpression.split("&");
                String key = split[0].replace("{{custom-", "");
                Parameter customParam = new Parameter("STRING", key, null);
                parameters.add(customParam);
            }
        }
        log.info("parameters:{} ", parameters);
        return str;
    }

    /**
     * 翻译问题回复
     *
     * @param str
     * @return
     */
    public static String getQuestionReplyParam(String str, List<Parameter> parameters) {
        if (StringUtils.isEmpty(str))
            return str;
        List<String> variableExpressions = findVariableExpression("\\{\\{question-\\([^\\}]+\\)\\}\\}", str); //  {{question-([^}]
        for (String expression : variableExpressions) {
            if (expression.contains("question-(")) {
                int startIdx = expression.indexOf("(");
                int endIdx = expression.indexOf(")");
                if (startIdx != -1 && endIdx != -1 && startIdx < endIdx) {
                    String nodeId = expression.substring(startIdx + 1, endIdx);
                    Parameter questionParam = new Parameter("STRING", nodeId,null);
                    parameters.add(questionParam);
                  }
            }
        }
        return str;
    }


    /**
     * 将地理坐标翻译成实际位置
     *
     * @param str 需要翻译的字符串
     * @return 翻译后的字符串
     */
    private static String transGeoToAddress(String str) {
        if (str.startsWith("geo:") && str.contains("u=") && str.contains("crs=") && str.contains("rcs-l=")) {
            String[] split = str.split(";");
            StringBuilder addr = new StringBuilder("经度为");
            for (String s : split) {
                if (s.contains("geo:")) {
                    String geo = s.replace("geo:", "");
                    String[] split1 = geo.split(",");
                    addr.append(split1[1]).append(",维度为").append(split1[0]);
                }
                if (s.contains("rcs-l=")) {
                    String rcs = s.replace("rcs-l=", "");
                    try {
                        String decode = URLDecoder.decode(rcs, "utf-8");
                        addr.append(",详细地址为:").append(decode);
                    } catch (UnsupportedEncodingException e) {
                        log.error("地址转换失败", e);
                    }
                }
            }
            str = addr.toString();
        }
        return str;
    }

    public static List<String> findVariableExpression(String pattern, String str) {
        List<String> result = new ArrayList<>();
//        String pattern = "\\{\\{[^\\}]+\\}\\}";
        // 编译正则表达式
        Pattern regex = Pattern.compile(pattern);

        // 创建Matcher对象
        Matcher matcher = regex.matcher(str);

        // 循环查找匹配的子串
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result;
    }
}
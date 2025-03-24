package com.citc.nce.im.mall.snapshot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 用于提取流程图中的指令，变量中的指令
 * 2024/2/19
 **/
@Slf4j
public class RobotInputUtil {

    public final static String type_custom = "custom";
    public final static String type_sys = "sys";
    public final static String key_id = "id";
    public final static String key_variable = "variable";
    public static Pattern pattern_custom = Pattern.compile("\\{\\{(.*?)}}", Pattern.CASE_INSENSITIVE);

    public static List<Variable> getOrderRespVariables(String str) {
        List<Variable> variables = new LinkedList<>();
        if (!StringUtils.hasLength(str)) return variables;
        JSONArray jsonArray = JSON.parseArray(str);
        if (CollectionUtils.isEmpty(jsonArray)) return variables;
        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject) object;
            String id = jsonObject.getString(key_id);
            if (StringUtils.hasLength(id)) {
                String name = jsonObject.getString(key_variable);
                Variable variable = new Variable(id, name, type_custom);
                variables.add(variable);
            }
        }
        return variables;
    }


    @Data
    @AllArgsConstructor
    public static class Variable {
        private String id;
        private String variableName;
        private String type;

        public String toDisplay() {
            return "{{" + type + "-" + variableName + "}}";
        }

        public String toCommonValue() {
            return "{{" + type + "-" + id + "&" + variableName + "}}";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Variable variable = (Variable) o;
            return Objects.equals(id, variable.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    /**
     * 自定义变量json展示数据和值得数据
     *
     * @param variables 变量s
     */
    public static List<String> variablesToDisplay(List<RobotInputUtil.Variable> variables) {
        if (Objects.isNull(variables)) return null;
        StringBuilder sbName = new StringBuilder();
        StringBuilder sbValue = new StringBuilder();
        for (Variable variable : variables) {
            sbName.append(variable.toDisplay());
            sbValue.append(variable.toCommonValue());
        }
        return Arrays.asList(sbName.toString(), sbValue.toString());
    }

    public static void main(String[] args) {
        String str = "[{\\\"name\\\":\\\"\\\",\\\"value\\\":\\\"city\\\",\\\"variable\\\":\\\"city\\\"},{\\\"name\\\":\\\"\\\",\\\"value\\\":\\\"weather[0].date\\\",\\\"variable\\\":\\\"date\\\"},{\\\"name\\\":\\\"\\\",\\\"value\\\":\\\"weather[0].weather\\\",\\\"variable\\\":\\\"weather\\\"},{\\\"name\\\":\\\"\\\",\\\"value\\\":\\\"weather[0].temp\\\",\\\"variable\\\":\\\"temp\\\"},{\\\"name\\\":\\\"\\\",\\\"value\\\":\\\"weather[0].w\\\",\\\"variable\\\":\\\"w\\\"},{\\\"name\\\":\\\"\\\",\\\"value\\\":\\\"weather[0].wind\\\",\\\"variable\\\":\\\"wind\\\"}]";
        List<Variable> variables = getVariables(str);
        System.out.println("variables = " + variables);

    }

    public static List<Variable> getVariables(String str) {
        List<Variable> variables = new LinkedList<>();
        if (!StringUtils.hasLength(str)) return variables;
        Matcher matcher = pattern_custom.matcher(str);
        while (matcher.find()) {
            String group = matcher.group(1);
            if (StringUtils.hasLength(group)) {
                String[] split = group.split("&");
                if (split.length == 2) {
                    int index = split[0].indexOf("-");
                    String id = split[0].substring(index + 1);
                    String name = split[1];
                    String type = split[0].substring(0, index);
                    Variable variable = new Variable(id, name, type);
                    variables.add(variable);
                }
            }
        }
        return variables.stream().distinct().collect(Collectors.toList());
    }

}

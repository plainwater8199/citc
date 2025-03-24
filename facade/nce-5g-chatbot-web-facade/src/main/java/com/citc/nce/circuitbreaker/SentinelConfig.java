package com.citc.nce.circuitbreaker;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/18 11:11
 * @Version: 1.0
 * @Description: 通过初始化方法加载熔断默认配置
 */
@Component
public class SentinelConfig {
    @Resource
    ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
//        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
//        List<DegradeRule> rules = new ArrayList<>();
//        Map<RequestMappingInfo, HandlerMethod> handlerMethods = mapping.getHandlerMethods();
//        for (Map.Entry<RequestMappingInfo, HandlerMethod> requestMappingInfoHandlerMethodEntry : handlerMethods.entrySet()) {
//            RequestMappingInfo key = requestMappingInfoHandlerMethodEntry.getKey();
//            PatternsRequestCondition patternsCondition = key.getPatternsCondition();
//            Set<String> directPaths = patternsCondition.getDirectPaths();
//            for (String directPath : directPaths) {
//                DegradeRule rule = new DegradeRule();
//                rule.setResource(directPath);
//                rule.setGrade(RuleConstant.DEGRADE_GRADE_RT);
//                rule.setCount(4000);
//                rule.setMinRequestAmount(1);
//                rule.setStatIntervalMs(1);
//                rule.setTimeWindow(5);
//                rules.add(rule);
//            }
//        }
//        DegradeRuleManager.loadRules(rules);
    }
}

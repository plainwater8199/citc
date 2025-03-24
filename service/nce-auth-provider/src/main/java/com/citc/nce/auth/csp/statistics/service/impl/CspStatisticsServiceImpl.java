package com.citc.nce.auth.csp.statistics.service.impl;

import com.alibaba.nacos.api.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.citc.nce.auth.accountmanagement.dao.AccountManagementDao;
import com.citc.nce.auth.accountmanagement.entity.AccountManagementDo;
import com.citc.nce.auth.adminUser.service.AdminUserService;
import com.citc.nce.auth.csp.chatbot.dao.ChatbotManageDao;
import com.citc.nce.auth.csp.chatbot.entity.ChatbotManageDo;
import com.citc.nce.auth.constant.csp.common.CSPContractStatusEnum;
import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import com.citc.nce.auth.csp.contract.dao.ContractManageDao;
import com.citc.nce.auth.csp.contract.entity.ContractManageDo;
import com.citc.nce.auth.csp.csp.dao.CspDao;
import com.citc.nce.auth.csp.csp.entity.CspDo;
import com.citc.nce.auth.csp.customer.vo.CustomerProvinceResp;
import com.citc.nce.auth.csp.statistics.dao.CspStatisticsMapper;
import com.citc.nce.auth.csp.statistics.dto.CspGetTotalChatbot;
import com.citc.nce.auth.csp.statistics.service.CspStatisticsService;
import com.citc.nce.auth.csp.statistics.vo.*;
import com.citc.nce.auth.csp.statistics.vo.req.QueryUserStatisticByOperatorReq;
import com.citc.nce.auth.csp.statistics.vo.resp.GetCustomerIndustryStatisticForCMCCResp;
import com.citc.nce.auth.csp.statistics.vo.resp.QueryUserStatisticByOperatorResp;
import com.citc.nce.common.util.SessionContextUtil;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>csp-统一运营管理平台</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 14:51
 */
@Service
@Slf4j
public class CspStatisticsServiceImpl implements CspStatisticsService {

    @Autowired
    private CspStatisticsMapper mapper;

    @Autowired
    private ContractManageDao contractManageDao;

    @Autowired
    private ChatbotManageDao chatbotManageDao;

    @Resource
    private AdminUserService adminUserService;
    @Autowired
    private CspDao cspDao;
    @Resource
    private AccountManagementDao accountManagementDao;

    private static final String START = "start";
    private static final String END = "end";


    @Override
    public CspStatisticsTotalCspResp getTotalCsp() {
        CspStatisticsTotalCspResp resp = new CspStatisticsTotalCspResp();
        int total = getCspAmountByTime(null,null);//mapper.getTotalCsp();
        int yesterday = getCspAmountByTime(getTime(START, getYesterday(new Date())),getTime(END, getYesterday(new Date())));mapper.getYesterdayCsp();
        int today = getCspAmountByTime(getTime(START,new Date()),getTime(END,new Date()));//mapper.getTodayCsp();
        BigDecimal percent;
        int differ = today - yesterday;

        if (yesterday > 0) {
            percent = BigDecimal.valueOf(differ).divide(BigDecimal.valueOf(yesterday), 4, BigDecimal.ROUND_HALF_UP);
        } else {
            percent = BigDecimal.valueOf(differ).divide(BigDecimal.valueOf(1), 4, BigDecimal.ROUND_HALF_UP);
        }
        resp.setCpsCountDifferencesPercent(percent.multiply(BigDecimal.valueOf(100)));
        resp.setTotalCspCount(BigDecimal.valueOf(total));
        resp.setCpsCountDifferences(BigDecimal.valueOf(today));
        return resp;
    }

    private int getCspAmountByTime(Date startTime, Date endTime) {
        LambdaQueryWrapper<CspDo> cspDoQueryWrapper = new LambdaQueryWrapper<>();
        if(startTime != null){
            cspDoQueryWrapper.ge(CspDo::getCreateTime,startTime);
        }
        if(endTime != null){
            cspDoQueryWrapper.le(CspDo::getCreateTime,startTime);
        }
        cspDoQueryWrapper.eq(CspDo::getDeleted,0);
        Long account = cspDao.selectCount(cspDoQueryWrapper);
        return (account != null) ? Integer.parseInt(String.valueOf(account)) : 0;
    }

    private Date getYesterday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE,-1);
        return cal.getTime();
    }

    private Date getTime(String type, Date date) {
        if(type.equals(START)){
            return obtainDate(obtainDateStr(date,"yyyy-MM-dd")+" 00:00:00");
        }else{
            return obtainDate(obtainDateStr(date,"yyyy-MM-dd")+" 23:59:59");
        }
    }

    private Date obtainDate(String timeStr) {
        if(!Strings.isNullOrEmpty(timeStr)){
            DateFormat dateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                return dateFormat.parse(timeStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String obtainDateStr(Date date, String format) {
        if(date != null){
            DateFormat dateFormat =new SimpleDateFormat(format);
            return dateFormat.format(date);
        }
        return null;
    }

    @Override
    public CspStatisticsChatbotResp getTotalChatbot() {
        CspStatisticsChatbotResp resp = new CspStatisticsChatbotResp();
        List<CspGetTotalChatbot> chatbotList = mapper.getTotalChatbot();
        long total = 0L;
        if (CollectionUtils.isNotEmpty(chatbotList)) {
            for (CspGetTotalChatbot chatbot :
                    chatbotList) {
                if (0 == CSPOperatorCodeEnum.CUNC.getCode().compareTo(chatbot.getAccountTypeCode())) {
                    resp.setCuncSendCount(chatbot.getNum());
                    total += chatbot.getNum();
                    continue;
                }
                if (0 == CSPOperatorCodeEnum.CMCC.getCode().compareTo(chatbot.getAccountTypeCode())) {
                    resp.setCmccCount(chatbot.getNum());
                    total += chatbot.getNum();
                    continue;
                }
                if (0 == CSPOperatorCodeEnum.CT.getCode().compareTo(chatbot.getAccountTypeCode())) {
                    resp.setCtSendCount(chatbot.getNum());
                    total += chatbot.getNum();
                }
            }
        }
        resp.setTotalCount(total);
        return resp;
    }

    @Override
    public List<CspStatisticsIndustryTypeResp> getContractIndustryType() {
        List<CspStatisticsIndustryTypeResp> respList = new ArrayList<>();
        respList = contractManageDao.getContractIndustryType();
        if (CollectionUtils.isNotEmpty(respList)) {
            // 取前10的行业量与占比，其他行业数据汇总到【其他（other）】中
            for (int i = 0; i < respList.size(); i++) {
                if (i > 8) {
                    respList.get(i).setChatbotIndustryType("other");
                }
            }
        }
        return respList;
    }


    @Override
    public List<CustomerProvinceResp> getCspProvince() {
        return mapper.getCspProvince();
    }

    @Override
    public GetChatbotIndustryStatisticForCMCCResp getChatbotIndustryStatisticForCMCC() {
        GetChatbotIndustryStatisticForCMCCResp resp = new GetChatbotIndustryStatisticForCMCCResp();

        String userId = SessionContextUtil.getUser().getUserId();
        boolean isAdmin = adminUserService.checkIsAdmin(userId);

        List<IndustryStatisticInfo> industryStatisticForCMCCs = new ArrayList<>();
//        //1、查询所有的信息
        Map<String,Long> industryCountMap = new HashMap<>();
        QueryWrapper<ChatbotManageDo> chatbotManageDoQueryWrapper = new QueryWrapper<>();
        chatbotManageDoQueryWrapper.select("chatbot_industry_type AS industryType,count(id) AS sum ");
        chatbotManageDoQueryWrapper.eq("deleted",0);
        chatbotManageDoQueryWrapper.eq("operator_code",2);//查询移动
        if(!isAdmin){//是否为管理员用户
            chatbotManageDoQueryWrapper.eq("creator",userId);
        }
        chatbotManageDoQueryWrapper.orderByDesc("sum");
        chatbotManageDoQueryWrapper.groupBy("industryType");
        Map<String,String> industryNameMap = getIndustryNameMap();
        List<Map<String, Object>> maps = chatbotManageDao.selectMaps(chatbotManageDoQueryWrapper);

        if(CollectionUtils.isNotEmpty(maps)){
            boolean isHaveOther = false;
            int limit;
            for(int i = 0; i<maps.size(); i++){
                String type = maps.get(i).get("industryType")+"";
                if(industryNameMap.containsKey(type)){
                    Long sum = Long.valueOf(maps.get(i).get("sum")+"");
                    isHaveOther = isHaveOther || "13".equals(type);
                    limit = isHaveOther ? 10 : 9;
                    if(industryCountMap.size()<limit){
                        industryCountMap.put(type,sum);
                    }else{
                        isHaveOther = true;
                        industryCountMap.put("13",industryCountMap.getOrDefault("13",0L)+sum);
                    }
                }
            }
        }

        List<Map.Entry<String, Long>> list = new ArrayList<>(industryCountMap.entrySet());
        Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        IndustryStatisticInfo industryStatisticInfo;
        for (Map.Entry<String, Long> entry : list) {
            industryStatisticInfo = new IndustryStatisticInfo();
            industryStatisticInfo.setIndustryType(entry.getKey());
            industryStatisticInfo.setIndustryName(industryNameMap.get(entry.getKey()));
            industryStatisticInfo.setQuantity(entry.getValue());
            industryStatisticForCMCCs.add(industryStatisticInfo);

        }
        resp.setIndustryStatisticForCMCCs(industryStatisticForCMCCs);
        return resp;
    }

    @Override
    public QueryUserStatisticByOperatorResp queryUserStatisticByOperator(QueryUserStatisticByOperatorReq req) {
        QueryUserStatisticByOperatorResp resp = new QueryUserStatisticByOperatorResp();
        Date startTime = StringUtils.isEmpty(req.getStartTime()) ? null : getTime(START,obtainDate(req.getStartTime()));
        Date endTime = StringUtils.isEmpty(req.getEndTime()) ? null : getTime(END,obtainDate(req.getEndTime()));

        QueryWrapper<AccountManagementDo> accountManagementDoQueryWrapper = new QueryWrapper<>();
        accountManagementDoQueryWrapper.select("account_type as operator,count(id) as sum ");
        accountManagementDoQueryWrapper.eq("deleted",0);
        if (startTime != null) {//开始时间
            accountManagementDoQueryWrapper.ge("create_time", startTime);
        }
        if (endTime != null) {//结束时间
            accountManagementDoQueryWrapper.le("create_time", endTime);
        }
        accountManagementDoQueryWrapper.groupBy("operator");
        List<Map<String, Object>> maps = accountManagementDao.selectMaps(accountManagementDoQueryWrapper);
        Integer operatorType;
        List<UserStatisticInfo> newUserStatisticInfoList = new ArrayList<>();
        UserStatisticInfo userStatisticInfo;
        for(Map<String, Object> item : maps) {
            if (item != null) {
                userStatisticInfo = new UserStatisticInfo();
                operatorType = getOperatorType(item.get("operator") + "");
                if(operatorType != null){
                    userStatisticInfo.setOperatorType(operatorType);
                    userStatisticInfo.setSum(Long.valueOf(item.get("sum")+""));
                    newUserStatisticInfoList.add(userStatisticInfo);
                }
            }
        }
        resp.setNewUserStatisticInfoList(newUserStatisticInfoList);
        return resp;
    }

    @Override
    public GetCustomerIndustryStatisticForCMCCResp getCustomerIndustryStatisticForCMCC() {
        GetCustomerIndustryStatisticForCMCCResp resp = new GetCustomerIndustryStatisticForCMCCResp();
        String userId = SessionContextUtil.getUser().getUserId();
        boolean isAdmin = adminUserService.checkIsAdmin(userId);

        List<IndustryStatisticInfo> industryStatisticForCMCCs = new ArrayList<>();
//        //1、查询所有的信息
        QueryWrapper<ContractManageDo> contractManageDoQueryWrapper = new QueryWrapper<>();
        contractManageDoQueryWrapper.select("industry_type_str AS industryType,count(id) AS sum ");
        contractManageDoQueryWrapper.eq("deleted",0);
        contractManageDoQueryWrapper.eq("contract_status", CSPContractStatusEnum.STATUS_30_ONLINE.getCode());//状态正常
        contractManageDoQueryWrapper.eq("operator_code",2);//查询移动
        if(!isAdmin){//是否为管理员用户
            contractManageDoQueryWrapper.eq("creator",userId);
        }
        contractManageDoQueryWrapper.orderByDesc("sum");
        contractManageDoQueryWrapper.groupBy("industryType");

        List<Map<String, Object>> maps = contractManageDao.selectMaps(contractManageDoQueryWrapper);

        if(CollectionUtils.isNotEmpty(maps)){
            IndustryStatisticInfo industryStatisticInfo;
            for(int i = 0; i<maps.size(); i++){
                String type = maps.get(i).get("industryType")+"";
                if(!"null".equals(type)){
                    industryStatisticInfo = new IndustryStatisticInfo();
                    industryStatisticInfo.setIndustryName(type);
                    industryStatisticInfo.setQuantity( Long.valueOf(maps.get(i).get("sum")+""));
                    industryStatisticForCMCCs.add(industryStatisticInfo);
                }
            }
        }
        resp.setIndustryStatisticForCMCCs(industryStatisticForCMCCs);
        return resp;
    }

    private Integer getOperatorType(String operator) {
        if(!Strings.isNullOrEmpty(operator)){
            switch (operator) {
                case "联通":
                    return 1;
                case "移动":
                    return 2;
                case "电信":
                    return 3;
                default:
                    return 0;
            }
        }
        return null;
    }

    private static List<Map<String, Object>> getMaps() {
        List<Map<String, Object>> maps = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("industryType",1);
        map1.put("sum",190);
        maps.add(map1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("industryType",2);
        map2.put("sum",98);
        maps.add(map2);

        Map<String, Object> map3 = new HashMap<>();
        map3.put("industryType",3);
        map3.put("sum",87);
        maps.add(map3);


        Map<String, Object> map4= new HashMap<>();
        map4.put("industryType",4);
        map4.put("sum",77);
        maps.add(map4);


        Map<String, Object> map5 = new HashMap<>();
        map5.put("industryType",5);
        map5.put("sum",63);
        maps.add(map5);


        Map<String, Object> map6 = new HashMap<>();
        map6.put("industryType",6);
        map6.put("sum",12);
        maps.add(map6);



        Map<String, Object> map7 = new HashMap<>();
        map7.put("industryType",7);
        map7.put("sum",8);
        maps.add(map7);


        Map<String, Object> map11 = new HashMap<>();
        map11.put("industryType",11);
        map11.put("sum",4);
        maps.add(map11);

        return maps;
    }

    //行业类型 1-党政军,2-民生，3-金融，4-物流，5-游戏，6-电商，7-微商（个人），8-沿街商铺（中小），9-企业（大型）,10-教育培训,11-房地产,12-医疗器械、药店,13-其他
    private Map<String, String> getIndustryNameMap() {
        Map<String,String> industryNameMap = new HashMap<>();
        industryNameMap.put("1","党政军");
        industryNameMap.put("2","民生");
        industryNameMap.put("3","金融");
        industryNameMap.put("4","物流");
        industryNameMap.put("5","游戏");
        industryNameMap.put("6","电商");
        industryNameMap.put("7","微商（个人）");
        industryNameMap.put("8","沿街商铺（中小）");
        industryNameMap.put("9","企业（大型）");
        industryNameMap.put("10","教育培训");
        industryNameMap.put("11","房地产");
        industryNameMap.put("12","医疗器械、药店");
        industryNameMap.put("13","其他");
        return industryNameMap;

    }


    public static void main(String[] args) {

        Map<String,Long> industryCountMap = new HashMap<>();
        List<Map<String, Object>> maps = getMaps();

        if(CollectionUtils.isNotEmpty(maps)){
            boolean isHaveOther = false;
            int limit;
            for(int i = 0; i<maps.size(); i++){
                String type = maps.get(i).get("industryType")+"";
                Long sum = Long.valueOf(maps.get(i).get("sum")+"");
                isHaveOther = isHaveOther || "11".equals(type);
                limit = isHaveOther ? 5 : 4;
                if(industryCountMap.size()<limit){
                    industryCountMap.put(type,sum);
                }else{
                    isHaveOther = true;
                    industryCountMap.put("11",industryCountMap.getOrDefault("11",0L)+sum);
                }
            }
        }

        List<Map.Entry<String, Long>> list = new ArrayList<>(industryCountMap.entrySet());
        Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        for (Map.Entry<String, Long> entry : list) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }


    }
}




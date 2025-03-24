package com.citc.nce.authcenter.tenantdata.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citc.nce.authcenter.tenantdata.service.TenantDataSynService;
import com.citc.nce.authcenter.tenantdata.user.dao.*;
import com.citc.nce.authcenter.tenantdata.user.entity.*;
import com.citc.nce.authcenter.tenantdata.vo.DataSynReq;
import com.citc.nce.authcenter.user.dao.UserDao;
import com.citc.nce.authcenter.user.entity.UserDo;
import com.citc.nce.authcenter.userDataSyn.vo.TenantUserUpdateInfo;
import com.citc.nce.common.thread.ThreadTaskUtils;
import com.citc.nce.fileApi.TenantApi;
import com.citc.nce.tenant.CreateTenantTableApi;
import com.citc.nce.tenant.vo.req.CreateTableReq;
import com.citc.nce.tenant.vo.req.StatisticSyncReq;
import com.citc.nce.vo.tenant.req.TenantUpdateInfoReq;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class TenantDataSynServiceImpl implements TenantDataSynService {

    @Resource
    private CreateTenantTableApi createTenantTableApi;
    @Resource
    private Csp1Dao csp1Dao;
    @Resource
    private CspCustomer1Dao cspCustomer1Dao;
    @Resource
    private CustomerManage1Dao customerManage1Dao;
    @Resource
    private AccountManagement1Dao accountManagement1Dao;
    @Resource
    private CspCustomerChatbotAccount1Dao cspCustomerChatbotAccount1Dao;
    @Resource
    private UserDao userDao;
    @Resource
    private ContractManage1Dao contractManage1Dao;
    @Resource
    private CardStyle1Dao cardStyle1Dao;
    @Resource
    private CspOperatorAccountManage1Dao cspOperatorAccountManage1Dao;
    @Resource
    private Agent1Dao agent1Dao;
    @Resource
    private UserCertificateOptions1Dao userCertificateOptions1Dao;
    @Resource
    private UserEnterpriseIdentification1Dao userEnterpriseIdentification1Dao;
    @Resource
    private Menu1Dao menu1Dao;
    @Resource
    private MenuChild1Dao menuChild1Dao;
    @Resource
    private MenuParent1Dao menuParent1Dao;
    @Resource
    private ContactBackList1Dao contactBackList1Dao;
    @Resource
    private ContactGroup1Dao contactGroup1Dao;
    @Resource
    private ContactList1Dao contactList1Dao;
    @Resource
    private CspSmsAccount1Dao cspSmsAccount1Dao;
    @Resource
    private CspVideoSmsSignature1Dao cspVideoSmsSignature1Dao;
    @Resource
    private CspSmsSignature1Dao cspSmsSignature1Dao;
    @Resource
    private SmsTemplate1Dao smsTemplate1Dao;
    @Resource
    private SmsTemplateAudit1Dao smsTemplateAudit1Dao;
    @Resource
    private CspVideoSmsAccount1Dao cspVideoSmsAccount1Dao;
    @Resource
    private MediaSmsTemplateAudit1Dao mediaSmsTemplateAudit1Dao;
    @Resource
    private MediaSmsTemplate1Dao mediaSmsTemplate1Dao;
    @Resource
    private MediaSmsTemplateContent1Dao mediaSmsTemplateContent1Dao;
    @Resource
    private FormManagement1Dao formManagement1Dao;
    @Resource
    private MessageTemplate1Dao messageTemplate1Dao;
    @Resource
    private ShareLink1Dao shareLink1Dao;
    @Resource
    private UserLoginRecord1Dao userLoginRecord1Dao;
    @Resource
    private CspCustomerLoginRecord1Dao cspCustomerLoginRecord1Dao;
    @Resource
    private UserPersonIdentification1Dao userPersonIdentification1Dao;
    @Resource
    private UserPlatformPermissions1Dao userPlatformPermissions1Dao;
    @Resource
    private AccountRelationship1Dao accountRelationship1Dao;

    @Resource
    private CspVideoSmsSignature1Dao smsSignature1Dao;
    @Resource
    private TenantApi tenantApi;


    @Resource
    private com.citc.nce.authcenter.csp.multitenant.service.CspCreateTableService cspCreateTableService;




    private final List<String> errorList =  new ArrayList<>();




    @Override
    public void dropTable(DataSynReq req) {
        List<CspDo> cspDos = csp1Dao.selectList();
        Set<String> cspIdSet = new HashSet<>();
        cspDos.forEach(i-> cspIdSet.add(i.getCspId()));
        if(!cspIdSet.isEmpty()){
            cspCreateTableService.dropTable(cspIdSet);
        }

    }




    private final Map<Integer, Map<Long, String>> templateSignMap = new HashMap<>();

    private final Map<Integer,Map<String,Long>> templateMap = new HashMap<>();

    private final Map<Integer, Map<Long, String>> templateByIdMap = new HashMap<>();

    @Override
    public Object statisticBaseDataSyn(DataSynReq req) {
        //更新统计基础表
        //查询所有的cspId
        Set<String> cspIdAllSet = queryAllCspIdSet();
        //查询所有的customerIdMap//Map<userId,customerId>
        Map<String,String> idMap = queryCustomerIdMap();

        getTemplateSignMap();

        Map<String,Map<Integer,String>> chatbotMap = getChatbotMap();

        StatisticSyncReq statisticSyncReq = new StatisticSyncReq();
        statisticSyncReq.setIdMap(idMap);
        statisticSyncReq.setCspIdSet(cspIdAllSet);
        statisticSyncReq.setTemplateSignMap(templateSignMap);
        statisticSyncReq.setTemplateMap(templateMap);
        statisticSyncReq.setTemplateByIdMap(templateByIdMap);
        statisticSyncReq.setChatbotMap(chatbotMap);
        createTenantTableApi.statisticSync(statisticSyncReq);

        return null;
    }

    private Map<String, Map<Integer, String>> getChatbotMap() {
        Map<String,Integer> operatorCode = new HashMap<>();//0：缺省(硬核桃)，1：联通，2：移动，3：电信
        operatorCode.put("联通",1);
        operatorCode.put("移动",2);
        operatorCode.put("电信",3);
        operatorCode.put("硬核桃",0);
        List<CspCustomerChatbotAccountDo> cspCustomerChatbotAccountDos = cspCustomerChatbotAccount1Dao.selectList();
        Map<String, Map<Integer, String>> chatbotMap = new HashMap<>();
        for(CspCustomerChatbotAccountDo item : cspCustomerChatbotAccountDos){
            String customerId = item.getCustomerId();
            String accountType = item.getAccountType();
            String chatbotAccountId = item.getChatbotAccountId();
            if(chatbotMap.containsKey(customerId)){
                Map<Integer, String> chatbotTypeMap = chatbotMap.get(customerId);
                chatbotTypeMap.put(operatorCode.get(accountType),chatbotAccountId);
            }else{
                Map<Integer, String> chatbotTypeMap = new HashMap<>();
                chatbotTypeMap.put(operatorCode.get(accountType),chatbotAccountId);
                chatbotMap.put(customerId,chatbotTypeMap);
            }
        }
        return chatbotMap;
    }

    @Override
    public Object statisticDataSyn(DataSynReq req) {
        //查询所有的cspId
        Set<String> cspIdAllSet = queryAllCspIdSet();

        //更新统计
        if(!CollectionUtils.isEmpty(cspIdAllSet)){
            createTenantTableApi.updateStatistic(cspIdAllSet);
        }
        return null;
    }

    @Override
    public void menuForChatbot() {
        LambdaQueryWrapper<CspCustomerChatbotAccountDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CspCustomerChatbotAccountDo::getDeleted,0);
        List<CspCustomerChatbotAccountDo> cspCustomerChatbotAccountDos = cspCustomerChatbotAccount1Dao.selectList(queryWrapper);
        Map<String,String> chatbotMap = new HashMap<>();
        cspCustomerChatbotAccountDos.forEach(i->chatbotMap.put(i.getChatbotAccount(),i.getChatbotAccountId()));

        List<MenuDo> menuDos = menu1Dao.selectList();
        List<MenuDo> update = new ArrayList<>();
        for(MenuDo item : menuDos){
            if(chatbotMap.containsKey(item.getChatbotId())){
                item.setChatbotAccountId(chatbotMap.get(item.getChatbotId()));
                update.add(item);
            }
        }
        if(!CollectionUtils.isEmpty(update)){
            menu1Dao.updateBatch(update);
        }

        Map<Long,String> chatbotId = new HashMap<>();
        for(CspCustomerChatbotAccountDo item : cspCustomerChatbotAccountDos){
            if(item.getAccountManagementId() != null){
                chatbotId.put(item.getAccountManagementId(),item.getChatbotAccountId());
            }
        }
        TenantUpdateInfoReq req = new TenantUpdateInfoReq();
        req.setChatbotId(chatbotId);
        tenantApi.updateChatbot(req);



    }

//    @Override
//    public Object signSyn(DataSynReq req) {
//        //查询所有的cspId
//        Set<String> cspIdAllSet = queryAllCspIdSet();
//
//
//        getTemplateSignMap();
//
//        MsgRecordDataSynReq signReq = new MsgRecordDataSynReq();
//        signReq.setCspIdSet(cspIdAllSet);
//        signReq.setTemplateSignMap(templateSignMap);
//        createTenantTableApi.msgRecordDataSyn(signReq);
//
//        return null;
//    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object dataSyn(DataSynReq req) {

        //1、获取csp
        String cspUserId ;
        String cspId ;
        String customerUserId ;
        String customerId ;
        //获取所有的未同步的数据--只获取account_relationship表中的数据
        List<AccountRelationshipDo> accountRelationshipDos = getHistoryRelation();
        if(!CollectionUtils.isEmpty(accountRelationshipDos)){


            List<TenantUserUpdateInfo> tenantUserUpdateInfos = new ArrayList<>();

            for(AccountRelationshipDo item : accountRelationshipDos){
                cspUserId = item.getCspUserId();
                customerUserId = item.getUserId();
                //检查csp是否已经如库，如果已经进入csp库则获取最新的cspId，如果没有则保存进csp库，并且获取cspId
                cspId = checkCspIsSave(cspUserId);
                //处理用户
                if(!Strings.isNullOrEmpty(customerUserId) && !("user_data_handle".equals(customerUserId) || "system".equals(customerUserId))){
                    //检查是否分表，如果没有分表旧进行分表操作
                    cspCreateTableService.createCspTable(cspId);
                    //获取用户的详细信息
                    UserDo userDo = getCustomerInfo(customerUserId);
                    if(userDo != null){
                        customerId = (Strings.isNullOrEmpty(item.getCustomerId())) ? cspId + randomID(5):item.getCustomerId();
                        //更新customer的相关表
                        customerTableUpdate(cspUserId,cspId,userDo,customerId,tenantUserUpdateInfos);
                        //更新account_relation_ship表的映射信息customer_id和userId
                        item.setCustomerId(customerId);
                        item.setCspId(cspId);
                        accountRelationship1Dao.updateById(item);
                    }
                }else{
                    item.setCspId(cspId);
                    accountRelationship1Dao.updateById(item);
                }
            }

            //同步robot库中的数据
            CreateTableReq robotReq = new CreateTableReq();
            robotReq.setTenantUserUpdateInfos(tenantUserUpdateInfos);
            ThreadTaskUtils.execute(() -> {
                try {
                    createTenantTableApi.updateDate(robotReq);
                }catch (Exception e){
                    log.info(e.getMessage());
                }
            });
            //同步fileManage库中的数据
            TenantUpdateInfoReq fileReq = new TenantUpdateInfoReq();
            fileReq.setTenantUserUpdateInfos(tenantUserUpdateInfos);
            ThreadTaskUtils.execute(() -> {
                try {
                    tenantApi.updateData(fileReq);
                }catch (Exception e){
                    log.info(e.getMessage());
                }
            });


            log.info("----------------------------------------数据同步结束----------------------------------");

        }
        return null;
    }

    private Map<String, String> queryCustomerIdMap() {
        Map<String, String> customerIdMap = new HashMap<>();
        //查询历史的
        LambdaQueryWrapper<AccountRelationshipDo> accountRelationshipDoQueryWrapper = new LambdaQueryWrapper<>();
        accountRelationshipDoQueryWrapper.isNotNull(AccountRelationshipDo::getCustomerId);
        accountRelationshipDoQueryWrapper.ne(AccountRelationshipDo::getCustomerId,"");
        List<AccountRelationshipDo> accountRelationshipDo = accountRelationship1Dao.selectList(accountRelationshipDoQueryWrapper);
        accountRelationshipDo.forEach(i->customerIdMap.put(i.getUserId(),i.getCustomerId()));
        //查询最新的
        List<CspCustomerDo> cspCustomerDos = cspCustomer1Dao.selectList();
        for(CspCustomerDo item : cspCustomerDos){
            if(!customerIdMap.containsValue(item.getCustomerId())){
                customerIdMap.put(item.getCustomerId(),item.getCustomerId());
            }
        }
        return customerIdMap;
    }

    /**
     * 查询所有的cspId；
     * @return 所有的cspID
     */
    private Set<String> queryAllCspIdSet() {
        Set<String> cspIdSet = new HashSet<>();
        LambdaQueryWrapper<CspDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CspDo::getIsSplite,1);
        List<CspDo> cspDos = csp1Dao.selectList(queryWrapper);
        cspDos.forEach(i->cspIdSet.add(i.getCspId()));
        return cspIdSet;
    }

    private void getTemplateSignMap() {

        List<CspAccountSignatureDo> cspAccountSignatureDos = smsSignature1Dao.selectList();
        Map<Long,String> videoSignMap = new HashMap<>();
        cspAccountSignatureDos.forEach(i->videoSignMap.put(i.getId(),i.getSignature()));

        List<CspSmsAccountSignatureDo> cspSmsAccountSignatureDos = cspSmsSignature1Dao.selectList();
        Map<Long,String> smsSignMap = new HashMap<>();
        cspSmsAccountSignatureDos.forEach(i->smsSignMap.put(i.getId(),i.getSignature()));

        LambdaQueryWrapper<CspVideoSmsTemplateDo> cspVideoSmsTemplateDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cspVideoSmsTemplateDoLambdaQueryWrapper.isNotNull(CspVideoSmsTemplateDo::getSignatureId);

        Map<Long,String> videoTemplateSignMap = new HashMap<>();
        Map<Long,String> videoTemplateByIdMap = new HashMap<>();
        Map<String,Long> videoTemplateMap = new HashMap<>();
        List<CspVideoSmsTemplateDo> cspVideoSmsTemplateDos = mediaSmsTemplate1Dao.selectList(cspVideoSmsTemplateDoLambdaQueryWrapper);
        for(CspVideoSmsTemplateDo item : cspVideoSmsTemplateDos){
            videoTemplateMap.put(item.getTemplateName(),item.getId());
            videoTemplateByIdMap.put(item.getId(),item.getTemplateName());
            if(videoSignMap.containsKey(item.getSignatureId())){
                videoTemplateSignMap.put(item.getId(),videoSignMap.get(item.getSignatureId()));
            }
        }
        templateSignMap.put(2,videoTemplateSignMap);
        templateMap.put(2,videoTemplateMap);
        templateByIdMap.put(2,videoTemplateByIdMap);

        Map<Long,String> smsTemplateSignMap = new HashMap<>();
        Map<String,Long> smsTemplateMap = new HashMap<>();
        Map<Long,String> smsTemplateByIdMap = new HashMap<>();
        LambdaQueryWrapper<SmsTemplateDo> smsTemplateDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        smsTemplateDoLambdaQueryWrapper.isNotNull(SmsTemplateDo::getSignatureId);
        List<SmsTemplateDo> cspSmsTemplateDos = smsTemplate1Dao.selectList(smsTemplateDoLambdaQueryWrapper);
        for(SmsTemplateDo item : cspSmsTemplateDos){
            smsTemplateMap.put(item.getTemplateName(),item.getId());
            smsTemplateByIdMap.put(item.getId(),item.getTemplateName());
            if(smsSignMap.containsKey(item.getSignatureId())){
                smsTemplateSignMap.put(item.getId(),smsSignMap.get(item.getSignatureId()));
            }
        }
        templateSignMap.put(3,smsTemplateSignMap);
        templateMap.put(3,smsTemplateMap);
        templateByIdMap.put(3,smsTemplateByIdMap);
    }

    private UserDo getCustomerInfo(String customerUserId) {
        LambdaQueryWrapper<UserDo> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(UserDo::getUserId,customerUserId);
        return  userDao.selectOne(userQueryWrapper);
    }

    private String checkCspIsSave(String cspUserId) {
        String cspId;
        LambdaQueryWrapper<CspDo> cspQueryWrapper = new LambdaQueryWrapper<>();
        cspQueryWrapper.eq(CspDo::getUserId,cspUserId);
        List<CspDo> cspDos = csp1Dao.selectList(cspQueryWrapper);
        if(!CollectionUtils.isEmpty(cspDos)){
            cspId = cspDos.get(0).getCspId();
        }else{
            cspId = cspUserId.length() == 10 ? cspUserId : randomID(10);
            cspTableUpdate(cspUserId,cspId);//更新csp的相关表
        }
        return cspId;
    }

    private List<AccountRelationshipDo> getHistoryRelation() {
        LambdaQueryWrapper<AccountRelationshipDo> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.in(AccountRelationshipDo::getCustomerId,Arrays.asList("401559523298368","401559523231774"));
        return accountRelationship1Dao.selectList(queryWrapper);
    }


    private void customerTableUpdate(String cspUserId, String cspId, UserDo userDo, String customerId,List<TenantUserUpdateInfo> tenantUserUpdateInfos) {
        TenantUserUpdateInfo tenantUserUpdateInfo = new TenantUserUpdateInfo();

        Map<Long,String> accountManagementIdMap = new HashMap<>();
        Map<String,String> chatbotAccountMap = new HashMap<>();
        Map<Long,String> customerEnterpriseIdMap = new HashMap<>();

        String customerUserId = userDo.getUserId();
        LambdaQueryWrapper<CspCustomerDo> cspCustomerDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cspCustomerDoLambdaQueryWrapper.eq(CspCustomerDo::getCustomerId,customerId);
        cspCustomerDoLambdaQueryWrapper.eq(CspCustomerDo::getCspId,cspId);
        List<CspCustomerDo> cspCustomerDos = cspCustomer1Dao.selectList(cspCustomerDoLambdaQueryWrapper);
        if(CollectionUtils.isEmpty(cspCustomerDos)){
            //2、csp_customer   user + CustomerManageDo
            CspCustomerDo cspCustomerDo = new CspCustomerDo();
            LambdaQueryWrapper<CustomerManageDo> chatbotCspCustomerQueryWrapper = new LambdaQueryWrapper<>();
            chatbotCspCustomerQueryWrapper.eq(CustomerManageDo::getUserId,customerUserId);
            CustomerManageDo chatbotCspCustomer = customerManage1Dao.selectOne(chatbotCspCustomerQueryWrapper);
            chatbotCspCustomer = chatbotCspCustomer == null ? new CustomerManageDo() : chatbotCspCustomer;

            cspCustomerDo.setCustomerId(customerId);
            cspCustomerDo.setCspId(cspId);
            cspCustomerDo.setName(userDo.getName());
            cspCustomerDo.setUserImgUuid(userDo.getUserImgUuid());
            cspCustomerDo.setPhone(userDo.getPhone());
            cspCustomerDo.setMail(userDo.getMail());
            cspCustomerDo.setEmailActivated(userDo.getEmailActivated());
            cspCustomerDo.setPersonAuthStatus(userDo.getPersonAuthStatus());
            cspCustomerDo.setEnterpriseAuthStatus(userDo.getEnterpriseAuthStatus());
            cspCustomerDo.setAuthStatus(userDo.getAuthStatus());
            cspCustomerDo.setProvince(chatbotCspCustomer.getProvince() == null ? "":chatbotCspCustomer.getProvince());
            cspCustomerDo.setProvinceCode(chatbotCspCustomer.getProvinceCode()== null ? "":chatbotCspCustomer.getProvinceCode());
            cspCustomerDo.setCity(chatbotCspCustomer.getCity()== null ? "":chatbotCspCustomer.getCity());
            cspCustomerDo.setCityCode(chatbotCspCustomer.getCityCode()== null ? "":chatbotCspCustomer.getCityCode());
            cspCustomerDo.setArea(chatbotCspCustomer.getArea()== null ? "":chatbotCspCustomer.getArea());
            cspCustomerDo.setAreaCode(chatbotCspCustomer.getAreaCode()== null ? "":chatbotCspCustomer.getAreaCode());
            cspCustomerDo.setCustomerActive(chatbotCspCustomer.getCspActive() == null ? 0 :chatbotCspCustomer.getCspActive());
            cspCustomerDo.setPermissions(chatbotCspCustomer.getPermissions() == null ? "1" : chatbotCspCustomer.getPermissions());
            cspCustomerDo.setCreator(customerId);
            cspCustomerDo.setCreateTime(userDo.getCreateTime());
            cspCustomerDo.setIsBinding(chatbotCspCustomer.getIsBinding() == null ? 0 : chatbotCspCustomer.getIsBinding());
            cspCustomerDo.setIsBindingChatbot(chatbotCspCustomer.getIsBindingChatbot() == null ? 0 : chatbotCspCustomer.getIsBindingChatbot());
            cspCustomerDo.setDeleted(0);
            cspCustomerDo.setUpdater(userDo.getUpdater());
            cspCustomerDo.setDeletedTime(userDo.getDeletedTime());

            cspCustomer1Dao.insert(cspCustomerDo);
        }





        //csp_customer_chatbot_account   AccountManagementDo
        LambdaQueryWrapper<CspCustomerChatbotAccountDo> cspCustomerChatbotAccountDoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cspCustomerChatbotAccountDoLambdaQueryWrapper.eq(CspCustomerChatbotAccountDo::getCustomerId,customerId);
        cspCustomerChatbotAccountDoLambdaQueryWrapper.eq(CspCustomerChatbotAccountDo::getCspId,cspId);
        List<CspCustomerChatbotAccountDo> cspCustomerChatbotAccountDos = cspCustomerChatbotAccount1Dao.selectList(cspCustomerChatbotAccountDoLambdaQueryWrapper);
        if(CollectionUtils.isEmpty(cspCustomerChatbotAccountDos)){
            LambdaQueryWrapper<AccountManagementDo> accountManagementQueryWrapper = new LambdaQueryWrapper<>();
            accountManagementQueryWrapper.eq(AccountManagementDo::getCspUserId,cspUserId);
            accountManagementQueryWrapper.eq(AccountManagementDo::getCreator,customerUserId);
            List<AccountManagementDo> accountManagementDos = accountManagement1Dao.selectList(accountManagementQueryWrapper);
            for(AccountManagementDo accountManagementDo : accountManagementDos){
                CspCustomerChatbotAccountDo cspCustomerChatbotAccountDo = new CspCustomerChatbotAccountDo();
                cspCustomerChatbotAccountDo.setCspId(cspId);
                cspCustomerChatbotAccountDo.setCustomerId(customerId);
                cspCustomerChatbotAccountDo.setChatbotAccountId("CBA"+randomID(10));//系统内部使用
                cspCustomerChatbotAccountDo.setAccountManagementId(accountManagementDo.getId());
                cspCustomerChatbotAccountDo.setChatbotAccount(accountManagementDo.getAccountId());
                cspCustomerChatbotAccountDo.setAccountType(accountManagementDo.getAccountType());
                cspCustomerChatbotAccountDo.setAccountName(accountManagementDo.getAccountName());
                cspCustomerChatbotAccountDo.setAppId(accountManagementDo.getAppId());
                cspCustomerChatbotAccountDo.setAppKey(accountManagementDo.getAppKey());
                cspCustomerChatbotAccountDo.setToken(accountManagementDo.getToken());
                cspCustomerChatbotAccountDo.setMessageAddress(accountManagementDo.getMessageAddress());
                cspCustomerChatbotAccountDo.setFileAddress(accountManagementDo.getFileAddress());
                cspCustomerChatbotAccountDo.setChatbotStatus(accountManagementDo.getChatbotStatus());
                cspCustomerChatbotAccountDo.setAccountTypeCode(accountManagementDo.getAccountTypeCode());
                cspCustomerChatbotAccountDo.setIsAddOther(0);
                cspCustomerChatbotAccountDo.setCreateTime(accountManagementDo.getCreateTime());
                cspCustomerChatbotAccountDo.setUpdateTime(accountManagementDo.getUpdateTime());
                cspCustomerChatbotAccountDo.setDeleted(accountManagementDo.getDeleted());
                cspCustomerChatbotAccountDo.setDeleteTime(accountManagementDo.getDeleteTime());
                cspCustomerChatbotAccountDo.setCreator(cspId);
                cspCustomerChatbotAccountDo.setUpdater(cspId);
                cspCustomerChatbotAccountDo.setDeleted(accountManagementDo.getDeleted());
                cspCustomerChatbotAccountDo.setDeleteTime(accountManagementDo.getDeleteTime());
                cspCustomerChatbotAccount1Dao.insert(cspCustomerChatbotAccountDo);
                String chatbotAccountId = cspCustomerChatbotAccountDo.getChatbotAccountId();
                String chatbotAccount = cspCustomerChatbotAccountDo.getChatbotAccount();//robot_account:account,csp_customer_chatbot_account:chatbot_account
                accountManagementIdMap.put(accountManagementDo.getId(),chatbotAccountId);
                chatbotAccountMap.put(chatbotAccount,chatbotAccountId);

            }
        }else{
            LambdaQueryWrapper<AccountManagementDo> accountManagementQueryWrapper = new LambdaQueryWrapper<>();
            accountManagementQueryWrapper.eq(AccountManagementDo::getCspUserId,cspUserId);
            accountManagementQueryWrapper.eq(AccountManagementDo::getCreator,customerUserId);
            List<AccountManagementDo> accountManagementDos = accountManagement1Dao.selectList(accountManagementQueryWrapper);
            Map<String,Long> accountTempMap = new HashMap<>();
            accountManagementDos.forEach(i->accountTempMap.put(i.getAccountId(),i.getId()));
            for(CspCustomerChatbotAccountDo item : cspCustomerChatbotAccountDos){
                String chatbotAccountId = item.getChatbotAccountId();
                String chatbotAccount = item.getChatbotAccount();//robot_account:account,csp_customer_chatbot_account:chatbot_account
                chatbotAccountMap.put(chatbotAccount,chatbotAccountId);
                if(accountTempMap.containsKey(chatbotAccount)){
                    accountManagementIdMap.put(accountTempMap.get(chatbotAccount),chatbotAccountId);
                }
            }
        }



        //card_style
        LambdaQueryWrapper<CardStyleDo> cardStyleDoQueryWrapper = new LambdaQueryWrapper<>();
        cardStyleDoQueryWrapper.eq(CardStyleDo::getCreator,customerUserId).or().eq(CardStyleDo::getCreatorOld,customerUserId);
        List<CardStyleDo> cardStyleDoOld = cardStyle1Dao.selectList(cardStyleDoQueryWrapper);
        List<CardStyleDo> cardStyleDos = new ArrayList<>();
        for(CardStyleDo item : cardStyleDoOld){
            item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
            item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
            item.setCreator(customerId);
            item.setUpdater(customerId);
            cardStyleDos.add(item);
        }
        if(!CollectionUtils.isEmpty(cardStyleDos)){
            cardStyle1Dao.updateBatch(cardStyleDos);
        }

        //contact_blacklist -- customer_id
        LambdaQueryWrapper<ContactBackListDo> contactBackListDoQueryWrapper = new LambdaQueryWrapper<>();
        contactBackListDoQueryWrapper.eq(ContactBackListDo::getCreator,customerUserId).or().eq(ContactBackListDo::getCreatorOld,customerUserId);
        List<ContactBackListDo> contactBackListDos = contactBackList1Dao.selectList(contactBackListDoQueryWrapper);
        List<ContactBackListDo> contactBackListDos1 = new ArrayList<>();
        for(ContactBackListDo item : contactBackListDos){
            item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
            item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
            item.setCreator(customerId);
            item.setUpdater(customerId);
            contactBackListDos1.add(item);
        }
        if(!CollectionUtils.isEmpty(contactBackListDos1)){
            contactBackList1Dao.updateBatch(contactBackListDos1);
        }

        //contact_group -- customer_id
        LambdaQueryWrapper<ContactGroupDo> contactGroupDoQueryWrapper = new LambdaQueryWrapper<>();
        contactGroupDoQueryWrapper.eq(ContactGroupDo::getCreator,customerUserId).or().eq(ContactGroupDo::getCreatorOld,customerUserId);//
        List<ContactGroupDo> contactGroupDos = contactGroup1Dao.selectList(contactGroupDoQueryWrapper);
        List<ContactGroupDo> contactGroupDos1 = new ArrayList<>();
        for(ContactGroupDo item : contactGroupDos){
            item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
            item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
            item.setCreator(customerId);
            item.setUpdater(customerId);
            contactGroupDos1.add(item);
        }
        if(!CollectionUtils.isEmpty(contactGroupDos1)){
            contactGroup1Dao.updateBatch(contactGroupDos1);
        }

        //contact_list -- csp_id
        LambdaQueryWrapper<ContactListDo> contactListDoQueryWrapper = new LambdaQueryWrapper<>();
        contactListDoQueryWrapper.eq(ContactListDo::getCreator,customerUserId).or().eq(ContactListDo::getCreatorOld,customerUserId);//
        List<ContactListDo> contactListDos = contactList1Dao.selectList(contactListDoQueryWrapper);
        List<ContactListDo> contactListDos1 = new ArrayList<>();
        for(ContactListDo item : contactListDos){
            item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
            item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
            item.setCreator(customerId);
            item.setUpdater(customerId);
            contactListDos1.add(item);
        }
        if(!CollectionUtils.isEmpty(contactListDos1)){
            contactList1Dao.updateBatch(contactListDos1);
        }


        //user_certificate_options
        LambdaQueryWrapper<UserCertificateOptionsDo> userCertificateOptionsDoQueryWrapper = new LambdaQueryWrapper<>();
        userCertificateOptionsDoQueryWrapper.eq(UserCertificateOptionsDo::getUserId,customerUserId);
        List<UserCertificateOptionsDo> userCertificateOptionsDos = userCertificateOptions1Dao.selectList(userCertificateOptionsDoQueryWrapper);
        List<UserCertificateOptionsDo> userCertificateOptionsDos1 = new ArrayList<>();
        for(UserCertificateOptionsDo item : userCertificateOptionsDos){
            item.setCreator(customerId);
            item.setUserId(customerId);
            userCertificateOptionsDos1.add(item);
        }
        if(!CollectionUtils.isEmpty(userCertificateOptionsDos1)){
            userCertificateOptions1Dao.updateBatch(userCertificateOptionsDos1);
        }


        //user_enterprise_identification
        LambdaQueryWrapper<UserEnterpriseIdentificationDo> userEnterpriseIdentificationDoQueryWrapper = new LambdaQueryWrapper<>();
        userEnterpriseIdentificationDoQueryWrapper.eq(UserEnterpriseIdentificationDo::getUserId,customerUserId).or().eq(UserEnterpriseIdentificationDo::getUserId,customerId);
        List<UserEnterpriseIdentificationDo> userEnterpriseIdentificationDos = userEnterpriseIdentification1Dao.selectList(userEnterpriseIdentificationDoQueryWrapper);
        if(!userEnterpriseIdentificationDos.isEmpty()){
            UserEnterpriseIdentificationDo userEnterpriseIdentificationDo = userEnterpriseIdentificationDos.get(0);
            userEnterpriseIdentificationDo.setCreator(customerId);
            userEnterpriseIdentificationDo.setUserId(customerId);
            Long enterpriseIdentificationId = userEnterpriseIdentificationDo.getId();
            customerEnterpriseIdMap.put(enterpriseIdentificationId,customerId);
            userEnterpriseIdentification1Dao.updateById(userEnterpriseIdentificationDo);


            //chatbot_contract_manage
            LambdaQueryWrapper<ContractManageDo> contractManageDoQueryWrapper = new LambdaQueryWrapper<>();
            contractManageDoQueryWrapper.eq(ContractManageDo::getEnterpriseIdentificationId,enterpriseIdentificationId);
            contractManageDoQueryWrapper.and(wq->wq.eq(ContractManageDo::getCreator,cspUserId).or().eq(ContractManageDo::getCreatorOld,cspUserId));
            List<ContractManageDo> contractManageDos = contractManage1Dao.selectList(contractManageDoQueryWrapper);
            List<ContractManageDo> contractManageDos1 = new ArrayList<>();
            for(ContractManageDo item : contractManageDos){
                item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
                item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
                item.setCreator(cspId);
                item.setUpdater(cspId);
                item.setCustomerId(customerId);
                contractManageDos1.add(item);
            }
            if(!CollectionUtils.isEmpty(contractManageDos1)){
                contractManage1Dao.updateBatch(contractManageDos1);
            }


            //csp_video_sms_account -- cspId
            LambdaQueryWrapper<CspVideoSmsAccountDo> cspVideoSmsAccountDoQueryWrapper = new LambdaQueryWrapper<>();
//            cspVideoSmsAccountDoQueryWrapper.eq(CspVideoSmsAccountDo::getEnterpriseId,enterpriseIdentificationId);
            cspVideoSmsAccountDoQueryWrapper.and(wq->wq.eq(CspVideoSmsAccountDo::getCreator,cspUserId).or().eq(CspVideoSmsAccountDo::getCreatorOld,cspUserId));
            List<CspVideoSmsAccountDo> cspVideoSmsAccountDos = cspVideoSmsAccount1Dao.selectList(cspVideoSmsAccountDoQueryWrapper);
            List<CspVideoSmsAccountDo> cspVideoSmsAccountDos1 = new ArrayList<>();
            for(CspVideoSmsAccountDo item : cspVideoSmsAccountDos){
                item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
                item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
                item.setCspId(cspId);
                item.setCreator(cspId);
                item.setUpdater(cspId);
                item.setCustomerId(customerId);
                cspVideoSmsAccountDos1.add(item);
            }
            if(!CollectionUtils.isEmpty(cspVideoSmsAccountDos1)){
                cspVideoSmsAccount1Dao.updateBatch(cspVideoSmsAccountDos1);
            }


            //csp_sms_account -- customerId
            LambdaQueryWrapper<CspSmsAccountDo> cspSmsAccountDoQueryWrapper = new LambdaQueryWrapper<>();
            cspSmsAccountDoQueryWrapper.eq(CspSmsAccountDo::getEnterpriseId,enterpriseIdentificationId);
            cspSmsAccountDoQueryWrapper.and(wq->wq.eq(CspSmsAccountDo::getCreator,cspUserId).or().eq(CspSmsAccountDo::getCreatorOld,cspUserId));
            List<CspSmsAccountDo> cspSmsAccountDos = cspSmsAccount1Dao.selectList(cspSmsAccountDoQueryWrapper);
            List<CspSmsAccountDo> cspSmsAccountDos1 = new ArrayList<>();
            for(CspSmsAccountDo item : cspSmsAccountDos){
                item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
                item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
                item.setCspId(cspId);
                item.setCreator(cspId);
                item.setUpdater(cspId);
                item.setCustomerId(customerId);
                cspSmsAccountDos1.add(item);
            }
            if(!CollectionUtils.isEmpty(cspSmsAccountDos1)){
                cspSmsAccount1Dao.updateBatch(cspSmsAccountDos1);
            }
        }


        //chatbot_manage_menu
        LambdaQueryWrapper<MenuDo> menuDoQueryWrapper = new LambdaQueryWrapper<>();
        menuDoQueryWrapper.eq(MenuDo::getCreator,customerUserId).or().eq(MenuDo::getCreatorOld,customerUserId);//csp的customerId；
        List<MenuDo> menuDoDos = menu1Dao.selectList(menuDoQueryWrapper);
        List<MenuDo> menuDos = new ArrayList<>();
        for(MenuDo item : menuDoDos){
            item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
            item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
            item.setChatbotAccountId(chatbotAccountMap.get(item.getChatbotId()));
            item.setCreator(customerId);
            item.setUpdater(customerId);
            menuDos.add(item);
        }
        if(!CollectionUtils.isEmpty(menuDos)){
            menu1Dao.updateBatch(menuDos);
        }

        //chatbot_manage_menu_child
        LambdaQueryWrapper<MenuChildDo> menuChildDoQueryWrapper = new LambdaQueryWrapper<>();
        menuChildDoQueryWrapper.eq(MenuChildDo::getCreator,customerUserId);//csp的customerId；
        List<MenuChildDo> menuChildDoDos = menuChild1Dao.selectList(menuChildDoQueryWrapper);
        List<MenuChildDo> menuChildDos = new ArrayList<>();
        for(MenuChildDo item : menuChildDoDos){
            item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
            item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
            item.setCreator(customerId);
            item.setUpdater(customerId);
            menuChildDos.add(item);
        }
        if(!CollectionUtils.isEmpty(menuChildDos)){
            menuChild1Dao.updateBatch(menuChildDos);
        }



        //chatbot_manage_menu_parent
        LambdaQueryWrapper<MenuParentDo> menuParentDoQueryWrapper = new LambdaQueryWrapper<>();
        menuParentDoQueryWrapper.eq(MenuParentDo::getCreator,customerUserId);//csp的customerId；
        List<MenuParentDo> menuParentDos = menuParent1Dao.selectList(menuParentDoQueryWrapper);
        List<MenuParentDo> menuParentDos1 = new ArrayList<>();
        for(MenuParentDo item : menuParentDos){
            item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
            item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
            item.setCreator(customerId);
            item.setUpdater(customerId);
            menuParentDos1.add(item);
        }
        if(!CollectionUtils.isEmpty(menuParentDos1)){
            menuParent1Dao.updateBatch(menuParentDos1);
        }



        //csp_account_signature -- cspId
        LambdaQueryWrapper<CspAccountSignatureDo> cspAccountSignatureDoQueryWrapper = new LambdaQueryWrapper<>();
        cspAccountSignatureDoQueryWrapper.eq(CspAccountSignatureDo::getCreator,cspUserId).or().eq(CspAccountSignatureDo::getCreatorOld,cspUserId);//customer的userId
        List<CspAccountSignatureDo> cspAccountSignatureDos = cspVideoSmsSignature1Dao.selectList(cspAccountSignatureDoQueryWrapper);
        List<CspAccountSignatureDo> cspAccountSignatureDos1 = new ArrayList<>();
        for(CspAccountSignatureDo item : cspAccountSignatureDos){
            item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
            item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
            item.setCreator(cspId);
            item.setUpdater(cspId);
            cspAccountSignatureDos1.add(item);
            cspVideoSmsSignature1Dao.updateById(item);
        }
//        if(!CollectionUtils.isEmpty(cspAccountSignatureDos1)){
//            cspVideoSmsSignature1Dao.updateBatch(cspAccountSignatureDos1);
//        }



        //csp_sms_account_signature -- cspId
        LambdaQueryWrapper<CspSmsAccountSignatureDo> cspSmsAccountSignatureDoQueryWrapper = new LambdaQueryWrapper<>();
        cspSmsAccountSignatureDoQueryWrapper.eq(CspSmsAccountSignatureDo::getCreator,cspUserId).or().eq(CspSmsAccountSignatureDo::getCreatorOld,cspUserId);//customer的userId
        List<CspSmsAccountSignatureDo> cspSmsAccountSignatureDos = cspSmsSignature1Dao.selectList(cspSmsAccountSignatureDoQueryWrapper);
        List<CspSmsAccountSignatureDo> cspSmsAccountSignatureDos1 = new ArrayList<>();
        for(CspSmsAccountSignatureDo item : cspSmsAccountSignatureDos){
            item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
            item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
            item.setCreator(cspId);
            item.setUpdater(cspId);
            cspSmsAccountSignatureDos1.add(item);
        }
        if(!CollectionUtils.isEmpty(cspSmsAccountSignatureDos1)){
            cspSmsSignature1Dao.updateBatch(cspSmsAccountSignatureDos1);
        }


        //csp_sms_template -- customerId
        LambdaQueryWrapper<SmsTemplateDo> smsTemplateDoQueryWrapper = new LambdaQueryWrapper<>();
        smsTemplateDoQueryWrapper.eq(SmsTemplateDo::getCreator,customerUserId).or().eq(SmsTemplateDo::getCreatorOld,customerUserId);//customer的userId
        List<SmsTemplateDo> smsTemplateDos = smsTemplate1Dao.selectList(smsTemplateDoQueryWrapper);
        for(SmsTemplateDo item : smsTemplateDos){
            item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
            item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
            item.setCreator(customerId);
            item.setUpdater(customerId);
            item.setCustomerId(customerId);
            smsTemplate1Dao.updateById(item);
        }

        //csp_sms_template_audit -- customerId
        LambdaQueryWrapper<SmsTemplateAuditDo> smsTemplateAuditDoQueryWrapper = new LambdaQueryWrapper<>();
        smsTemplateAuditDoQueryWrapper.eq(SmsTemplateAuditDo::getCreator,customerUserId).or().eq(SmsTemplateAuditDo::getCreatorOld,customerUserId);//customer的userId
        List<SmsTemplateAuditDo> smsTemplateAuditDos = smsTemplateAudit1Dao.selectList(smsTemplateAuditDoQueryWrapper);
        for(SmsTemplateAuditDo item : smsTemplateAuditDos){
            item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
            item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
            item.setCreator(customerId);
            item.setUpdater(customerId);
            smsTemplateAudit1Dao.updateById(item);
        }



        //csp_video_sms_template -- customerId
        LambdaQueryWrapper<CspVideoSmsTemplateDo> cspVideoSmsTemplateDoQueryWrapper = new LambdaQueryWrapper<>();
        cspVideoSmsTemplateDoQueryWrapper.eq(CspVideoSmsTemplateDo::getCreator,customerUserId).or().eq(CspVideoSmsTemplateDo::getCreatorOld,customerUserId);//customer的userId
        List<CspVideoSmsTemplateDo> cspVideoSmsTemplateDos = mediaSmsTemplate1Dao.selectList(cspVideoSmsTemplateDoQueryWrapper);
        for(CspVideoSmsTemplateDo item : cspVideoSmsTemplateDos){
            item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
            item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
            item.setCreator(customerId);
            item.setUpdater(customerId);
            item.setCustomerId(customerId);
            mediaSmsTemplate1Dao.updateById(item);
        }


        //csp_video_sms_template_audit -- customerId
        LambdaQueryWrapper<CspVideoSmsTemplateAuditDo> cspVideoSmsTemplateAuditDoQueryWrapper = new LambdaQueryWrapper<>();
        cspVideoSmsTemplateAuditDoQueryWrapper.eq(CspVideoSmsTemplateAuditDo::getCreator,customerUserId).or().eq(CspVideoSmsTemplateAuditDo::getCreatorOld,customerUserId);//customer的userId
        List<CspVideoSmsTemplateAuditDo> cspVideoSmsTemplateAuditDos = mediaSmsTemplateAudit1Dao.selectList(cspVideoSmsTemplateAuditDoQueryWrapper);
        for(CspVideoSmsTemplateAuditDo item : cspVideoSmsTemplateAuditDos){
            item.setCreatorOld(customerUserId);
            item.setUpdaterOld(customerUserId);
            item.setCreator(customerId);
            item.setUpdater(customerId);
            mediaSmsTemplateAudit1Dao.updateById(item);
        }


        //csp_video_sms_template_content -- customerId
        LambdaQueryWrapper<CspVideoSmsTemplateContentDo> cspVideoSmsTemplateContentDoQueryWrapper = new LambdaQueryWrapper<>();
        cspVideoSmsTemplateContentDoQueryWrapper.eq(CspVideoSmsTemplateContentDo::getCreator,customerUserId).or().eq(CspVideoSmsTemplateContentDo::getCreatorOld,customerUserId);//customer的userId
        List<CspVideoSmsTemplateContentDo> cspVideoSmsTemplateContentDos = mediaSmsTemplateContent1Dao.selectList(cspVideoSmsTemplateContentDoQueryWrapper);
        for(CspVideoSmsTemplateContentDo item : cspVideoSmsTemplateContentDos){
            item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
            item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
            item.setCreator(customerId);
            item.setUpdater(customerId);
            mediaSmsTemplateContent1Dao.updateById(item);
        }



        //form_management -- customerId
        LambdaQueryWrapper<FormManagementDo> formManagementDoQueryWrapper = new LambdaQueryWrapper<>();
        formManagementDoQueryWrapper.eq(FormManagementDo::getCreator,customerUserId).or().eq(FormManagementDo::getCreatorOld,customerUserId);//customer的userId
        List<FormManagementDo> formManagementDos = formManagement1Dao.selectList(formManagementDoQueryWrapper);
        List<FormManagementDo> formManagementDos1 = new ArrayList<>();
        for(FormManagementDo item : formManagementDos){
            item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
            item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
            item.setCreator(customerId);
            item.setUpdater(customerId);
            formManagementDos1.add(item);
        }
        if(!CollectionUtils.isEmpty(formManagementDos1)){
            formManagement1Dao.updateBatch(formManagementDos1);
        }


        //message_template -- customerId
        LambdaQueryWrapper<MessageTemplateDo> messageTemplateDoQueryWrapper = new LambdaQueryWrapper<>();
        messageTemplateDoQueryWrapper.eq(MessageTemplateDo::getCreator,customerUserId).or().eq(MessageTemplateDo::getCreatorOld,customerUserId);//customer的userId
        List<MessageTemplateDo> messageTemplateDos = messageTemplate1Dao.selectList(messageTemplateDoQueryWrapper);
        List<MessageTemplateDo> messageTemplateDos1 = new ArrayList<>();
        for(MessageTemplateDo item : messageTemplateDos){
            item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
            item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
            item.setCreator(customerId);
            item.setUpdater(customerId);
            messageTemplateDos1.add(item);
        }
        if(!CollectionUtils.isEmpty(messageTemplateDos1)){
            messageTemplate1Dao.updateBatch(messageTemplateDos1);
        }



        //share_link -- customerId
        LambdaQueryWrapper<ShareLinkDo> shareLinkDoQueryWrapper = new LambdaQueryWrapper<>();
        shareLinkDoQueryWrapper.eq(ShareLinkDo::getCreator,customerUserId).or().eq(ShareLinkDo::getCreatorOld,customerUserId);//customer的userId
        List<ShareLinkDo> shareLinkDos = shareLink1Dao.selectList(shareLinkDoQueryWrapper);
        List<ShareLinkDo> shareLinkDos1 = new ArrayList<>();
        for(ShareLinkDo item : shareLinkDos){
            item.setId(null);
            item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
            item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
            item.setCreator(customerId);
            item.setUpdater(customerId);
            item.setChatbotAccountId(accountManagementIdMap.get(item.getAccountId())+"");
            shareLinkDos1.add(item);
        }
        if(!CollectionUtils.isEmpty(shareLinkDos1)){
            shareLink1Dao.updateBatch(shareLinkDos1);
        }



        //csp_customer_login_record
        LambdaQueryWrapper<UserLoginRecordDo> userLoginRecordQueryWrapper = new LambdaQueryWrapper<>();
        userLoginRecordQueryWrapper.eq(UserLoginRecordDo::getUserId,customerUserId);//customer的userId
        List<UserLoginRecordDo> userLoginRecordDos = userLoginRecord1Dao.selectList(userLoginRecordQueryWrapper);
        List<CspCustomerLoginRecordDo> cspCustomerLoginRecordDos = new ArrayList<>();
        CspCustomerLoginRecordDo cspCustomerLoginRecordDo;

        for(UserLoginRecordDo item : userLoginRecordDos){
            cspCustomerLoginRecordDo = new CspCustomerLoginRecordDo();
            cspCustomerLoginRecordDo.setCustomerId(customerId);
            cspCustomerLoginRecordDo.setCreateTime(item.getCreateTime());
            cspCustomerLoginRecordDo.setUpdateTime(item.getUpdateTime());
            cspCustomerLoginRecordDo.setCreator(customerId);
            cspCustomerLoginRecordDo.setUpdater(item.getUpdater());
            cspCustomerLoginRecordDos.add(cspCustomerLoginRecordDo);
        }
        if(!CollectionUtils.isEmpty(cspCustomerLoginRecordDos)){
            cspCustomerLoginRecord1Dao.insertBatch(cspCustomerLoginRecordDos);
        }

        //user_person_identification -- customerId
        LambdaQueryWrapper<UserPersonIdentificationDo> userPersonIdentificationDoQueryWrapper = new LambdaQueryWrapper<>();
        userPersonIdentificationDoQueryWrapper.eq(UserPersonIdentificationDo::getUserId,customerUserId);//customer的userId
        List<UserPersonIdentificationDo> userPersonIdentificationDos = userPersonIdentification1Dao.selectList(userPersonIdentificationDoQueryWrapper);
        List<UserPersonIdentificationDo> userPersonIdentificationDos1 = new ArrayList<>();
        for(UserPersonIdentificationDo item : userPersonIdentificationDos){
            item.setCreator(customerId);
            item.setUserId(customerId);
            userPersonIdentificationDos1.add(item);
        }
        if(!CollectionUtils.isEmpty(userPersonIdentificationDos1)){
            userPersonIdentification1Dao.updateBatch(userPersonIdentificationDos1);
        }




        //user_platform_permissions -- customerId
        LambdaQueryWrapper<UserPlatformPermissionsDo> userPlatformPermissionsDoQueryWrapper = new LambdaQueryWrapper<>();
        userPlatformPermissionsDoQueryWrapper.eq(UserPlatformPermissionsDo::getUserId,customerUserId);//customer的userId
        List<UserPlatformPermissionsDo> userPlatformPermissionsDos = userPlatformPermissions1Dao.selectList(userPlatformPermissionsDoQueryWrapper);
        List<UserPlatformPermissionsDo> userPlatformPermissionsDos1 = new ArrayList<>();
        for(UserPlatformPermissionsDo item : userPlatformPermissionsDos){
            item.setCreator(customerId);
            item.setUserId(customerId);
            userPlatformPermissionsDos1.add(item);
        }
        if(!CollectionUtils.isEmpty(userPlatformPermissionsDos1)){
            try{
                userPlatformPermissions1Dao.updateBatch(userPlatformPermissionsDos1);
            }catch (Exception e){
                log.info("异常");
            }

        }


        tenantUserUpdateInfo.setCspId(cspId);
        tenantUserUpdateInfo.setCustomerId(customerId);
        tenantUserUpdateInfo.setCspUserId(cspUserId);
        tenantUserUpdateInfo.setCustomerUserId(customerUserId);
        tenantUserUpdateInfo.setAccountManagementIdMap(accountManagementIdMap);
        tenantUserUpdateInfo.setCustomerEnterpriseIdMap(customerEnterpriseIdMap);
        tenantUserUpdateInfo.setChatbotAccountIdMap(chatbotAccountMap);
        tenantUserUpdateInfos.add(tenantUserUpdateInfo);


    }

    /**
     * 更新csp相关表
     * @param cspUserId csp原userId
     * @param cspId cspId
     */
    private void cspTableUpdate(String cspUserId, String cspId) {
        Map<Long,String> cspAccountManageMap = new HashMap<>();
        CspDo cspDo = new CspDo();
        cspDo.setCspId(cspId);
        cspDo.setUserId(cspUserId);
        cspDo.setCspActive(1);
        cspDo.setCreator("system");
        cspDo.setCreateTime(new Date());
        cspDo.setUpdater("system");
        cspDo.setUpdateTime(new Date());
        csp1Dao.insert(cspDo);



        //csp_operator_account_manage cspUserId
        LambdaQueryWrapper<CspOperatorAccountManageDo> cspOperatorAccountManageDoQueryWrapper = new LambdaQueryWrapper<>();
        cspOperatorAccountManageDoQueryWrapper.eq(CspOperatorAccountManageDo::getUserId,cspUserId);
        List<CspOperatorAccountManageDo> cspOperatorAccountManageDos = cspOperatorAccountManage1Dao.selectList(cspOperatorAccountManageDoQueryWrapper);
        List<CspOperatorAccountManageDo> cspOperatorAccountManageDos1 = new ArrayList<>();
        for(CspOperatorAccountManageDo item : cspOperatorAccountManageDos){
            item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
            item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
            item.setCreator(cspId);
            item.setUpdater(cspId);
            item.setCspId(cspId);
            item.setOperatorAccountId("OPA"+randomID(10));//内部自用
            cspAccountManageMap.put(item.getId(),item.getOperatorAccountId());
            cspOperatorAccountManageDos1.add(item);

        }
        if(!CollectionUtils.isEmpty(cspOperatorAccountManageDos1)){
            cspOperatorAccountManage1Dao.updateBatch(cspOperatorAccountManageDos1);
        }

        //chatbot_csp_agent_info
        LambdaQueryWrapper<AgentDo> agentDoQueryWrapper = new LambdaQueryWrapper<>();
        agentDoQueryWrapper.eq(AgentDo::getCreator,cspUserId);
        List<AgentDo> agentDos = agent1Dao.selectList(agentDoQueryWrapper);
        List<AgentDo> agentDos1 = new ArrayList<>();
        for(AgentDo item : agentDos){
            item.setCreatorOld(Strings.isNullOrEmpty(item.getCreatorOld()) ? item.getCreator() : item.getCreatorOld());
            item.setUpdaterOld(Strings.isNullOrEmpty(item.getUpdaterOld()) ? item.getUpdater() : item.getUpdaterOld());
            item.setCreator(cspId);
            item.setUpdater(cspId);
            item.setOperatorAccountId(cspAccountManageMap.get(item.getCspAccountManageId()));//内部自用
            agentDos1.add(item);
        }
        if(!CollectionUtils.isEmpty(agentDos1)){
            agent1Dao.updateBatch(agentDos1);
        }

    }




    public static String randomID(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(RandomUtil.randomInt(10));
        }
        return sb.toString();

    }


}

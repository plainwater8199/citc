package com.citc.nce.auth.csp.menu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.dao.AccountManagementDao;
import com.citc.nce.auth.accountmanagement.entity.AccountManagementDo;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.configure.FormUrlConfigure;
import com.citc.nce.auth.constant.csp.common.CSPChatbotStatusEnum;
import com.citc.nce.auth.csp.menu.dao.MenuChildDao;
import com.citc.nce.auth.csp.menu.dao.MenuDao;
import com.citc.nce.auth.csp.menu.dao.MenuParentDao;
import com.citc.nce.auth.csp.menu.entity.MenuChildDo;
import com.citc.nce.auth.csp.menu.entity.MenuDo;
import com.citc.nce.auth.csp.menu.entity.MenuParentDo;
import com.citc.nce.auth.csp.menu.service.MenuService;
import com.citc.nce.auth.csp.menu.vo.*;
import com.citc.nce.auth.mobile.req.MenuRequest;
import com.citc.nce.auth.mobile.resp.SyncResult;
import com.citc.nce.auth.mobile.service.ChatBotService;
import com.citc.nce.auth.utils.SimpleUuidUtil;
import com.citc.nce.authcenter.csp.CspApi;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.common.constants.Constants;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.misc.constant.NumCode;
import com.citc.nce.robot.enums.ButtonType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.menu.service.impl
 * @Author: litao
 * @CreateTime: 2023-02-16  14:41
 * @Version: 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(FormUrlConfigure.class)
public class MenuServiceImpl implements MenuService {
    @Resource
    private MenuDao menuDao;
    @Resource
    private MenuParentDao menuParentDao;
    @Resource
    private MenuChildDao menuChildDao;

    @Resource
    ChatBotService chatBotService;

    @Resource
    AccountManagementApi accountManagementApi;

    @Resource
    PlatformService platformService;

    private final FormUrlConfigure formUrlConfigure;

    public static final String SUCCESS_CODE = "00000";

    @Autowired
    private CspCustomerApi cspCustomerApi;

    @Autowired
    private AccountManagementDao accountManagementDao;

    @Autowired
    private CspApi cspApi;

    @Override
    public MenuChildResp queryButtonByUUID(String uuid) {
        LambdaQueryWrapper<MenuChildDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(MenuChildDo::getButtonContent, uuid);
        queryWrapper.orderByDesc(MenuChildDo::getCreateTime).last("limit 1");
        MenuChildDo menuChildDo = menuChildDao.selectOne(queryWrapper);
        if (ObjectUtils.isNotEmpty(menuChildDo)) {
            MenuChildResp resp = new MenuChildResp();
            BeanUtils.copyProperties(menuChildDo, resp);
            return resp;
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(MenuSaveReq req) {
        BaseUser baseUser = SessionContextUtil.getUser();
        String chatbotAccountId = req.getChatbotAccountId();
        AccountManagementResp management = accountManagementApi.getAccountManagementById(chatbotAccountId);

        if (management == null)
            throw new BizException(500, "查询不到机器人信息");
        log.info("management:{}", JsonUtils.obj2String(management));
        Integer version = getChatbotMenuVersion(req);
        //同步至网关
        MenuReq menuReq = new MenuReq();
        List<Object> menu = new ArrayList<>();
        for (MenuParentReq menuParentReq : req.getMenuParentReqList()) {
            List<MenuChildReq> menuChildReqList = menuParentReq.getMenuChildReqList();
            String menuContent = menuParentReq.getMenuContent();
            if (1 == menuParentReq.getMenuType()) {
                //功能选项
                Item item = getItem(menuChildReqList);
                menu.add(item);
            } else {
                //子父菜单
                SubMenu subMenu = getSubMenu(menuChildReqList, menuContent);
                menu.add(subMenu);
            }
        }
        menuReq.setMenu(menu);
        if (StringUtils.isNotEmpty(management.getAccountType()) && !management.getAccountType().equals("移动")) {
            if (CSPChatbotStatusEnum.isOfflineOrCancelable(management.getChatbotStatus())) {
                throw new BizException("机器人已注销或下线");
            }
            MessageData messageData = platformService.updateMenu(menuReq, management);
            if (messageData.getCode() != 0) {
                throw new BizException(820103018, "菜单送审失败," + messageData.getMessage());
            }
        }
        AccountManagementDo accountManagementDo = accountManagementDao.selectOne(Wrappers.<AccountManagementDo>lambdaQuery()
                .eq(AccountManagementDo::getChatbotAccountId, chatbotAccountId)
                .eq(AccountManagementDo::getDeleted, 0));
        if (accountManagementDo == null)
            throw new BizException(500, "查询不到机器人信息");
        List<UserInfoVo> userInfoVos = baseUser.getIsCustomer()
                ? cspCustomerApi.getByCustomerIds(Collections.singletonList(baseUser.getUserId()))
                : cspApi.getByIdList(Collections.singleton(baseUser.getUserId()));
        if (CollectionUtils.isEmpty(userInfoVos))
            throw new BizException(500, "查询不到用户信息");
        UserInfoVo submitUser = userInfoVos.get(0);
        //构建菜单信息
        MenuRequest menuRequest = buildCMCCMenu(req);
        buildParam(menuRequest);
        //1.新增主表（记录表）数据
        MenuDo menuDo = new MenuDo();
        menuDo.setMenuStatus(NumCode.ZERO.getCode());
        menuDo.setSubmitTime(new Date());
        menuDo.setVersion(version);
        menuDo.setChatbotId(accountManagementDo.getChatbotAccount());
        menuDo.setChatbotAccountId(accountManagementDo.getChatbotAccountId());
        menuDo.setResult("待审核");
        menuDo.setSubmitUser(submitUser.getName());
        //移动流水号
        menuDo.setMessageId(menuRequest.getMessageId());
        menuDao.insert(menuDo);

        //2.新增父菜单
        List<MenuChildDo> menuChildDoList = new ArrayList<>();
        req.getMenuParentReqList().forEach(item -> {
            MenuParentDo menuParentDo = new MenuParentDo();
            menuParentDo.setMenuId(menuDo.getId());
            menuParentDo.setMenuCode(item.getMenuCode());
            menuParentDo.setMenuType(item.getMenuType());
            menuParentDo.setMenuContent(item.getMenuContent());
            menuParentDo.setSort(item.getSort());
            menuParentDao.insert(menuParentDo);

            //3.新增子菜单
            item.getMenuChildReqList().forEach(childReq -> {
                MenuChildDo menuChildDo = new MenuChildDo();
                menuChildDo.setParentId(menuParentDo.getId());
                menuChildDo.setMenuCode(childReq.getMenuCode());
                menuChildDo.setButtonId(childReq.getButtonId());
                menuChildDo.setButtonType(childReq.getButtonType());
                menuChildDo.setButtonDesc(childReq.getButtonDesc());
                menuChildDo.setButtonContent(childReq.getButtonContent());
                menuChildDo.setSort(childReq.getSort());
                menuChildDo.setOptionList(JSON.toJSONString(childReq.getOption()));
                menuChildDoList.add(menuChildDo);
            });
        });
        menuChildDao.insertBatch(menuChildDoList);
        if (StringUtils.isNotEmpty(management.getAccountType()) && management.getAccountType().equals("移动")) {
            log.info("开始同步移动菜单");
            //同步移动
            menuRequest.setChatbotId(accountManagementDo.getChatbotAccount() + "@botplatform.rcs.chinamobile.com");
            menuRequest.setCreator(submitUser.getCspId());
            SyncResult syncResult = chatBotService.syncMenu(menuRequest);
            if (!StringUtils.equals(syncResult.getResultCode(), SUCCESS_CODE)) {
                throw new BizException(820103011, syncResult.getResultDesc());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSupplierChatbotMenu(MenuSaveReq req) {
        BaseUser baseUser = SessionContextUtil.getUser();
        String chatbotAccountId = req.getChatbotAccountId();
        AccountManagementDo accountManagementDo = accountManagementDao.selectOne(Wrappers.<AccountManagementDo>lambdaQuery()
                .eq(AccountManagementDo::getChatbotAccountId, chatbotAccountId)
                .eq(AccountManagementDo::getDeleted, 0));
        if (accountManagementDo == null) {
            throw new BizException(500, "查询不到机器人信息");
        }
        Integer version = getChatbotMenuVersion(req);
        //1.新增主表（记录表）数据
        MenuDo menuDo = new MenuDo();
        menuDo.setMenuStatus(NumCode.ONE.getCode());
        menuDo.setSubmitTime(new Date());
        menuDo.setVersion(version);
        menuDo.setChatbotId(accountManagementDo.getChatbotAccount());
        menuDo.setChatbotAccountId(chatbotAccountId);
        menuDo.setSubmitUser(baseUser.getUserName());
        menuDao.insert(menuDo);

        //2.新增父菜单
        List<MenuChildDo> menuChildDoList = new ArrayList<>();
        req.getMenuParentReqList().forEach(item -> {
            MenuParentDo menuParentDo = new MenuParentDo();
            menuParentDo.setMenuId(menuDo.getId());
            menuParentDo.setMenuCode(item.getMenuCode());
            menuParentDo.setMenuType(item.getMenuType());
            menuParentDo.setMenuContent(item.getMenuContent());
            menuParentDo.setSort(item.getSort());
            menuParentDao.insert(menuParentDo);

            //3.新增子菜单
            item.getMenuChildReqList().forEach(childReq -> {
                MenuChildDo menuChildDo = new MenuChildDo();
                menuChildDo.setParentId(menuParentDo.getId());
                menuChildDo.setMenuCode(childReq.getMenuCode());
                menuChildDo.setButtonId(childReq.getButtonId());
                if (Constants.SUPPLIER_TAG_FONTDO.equals(accountManagementDo.getSupplierTag()) && (String.valueOf(ButtonType.CAMERA.getCode()).equals(childReq.getButtonType()) || String.valueOf(ButtonType.CONTACT.getCode()).equals(childReq.getButtonType()))) {
                    throw new BizException("当前账号不支持拍摄或调起联系人操作");
                }
                menuChildDo.setButtonType(childReq.getButtonType());
                menuChildDo.setButtonDesc(childReq.getButtonDesc());
                menuChildDo.setButtonContent(childReq.getButtonContent());
                menuChildDo.setSort(childReq.getSort());
                menuChildDoList.add(menuChildDo);
            });
        });
        menuChildDao.insertBatch(menuChildDoList);
    }

    private void buildParam(MenuRequest menuRequest) {
        menuRequest.setMessageId(SimpleUuidUtil.generateShortUuid());
        menuRequest.setETag("1");
        menuRequest.setOpType(1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String format = sdf.format(new Date());
        menuRequest.setOpTime(format);
    }

    private MenuRequest buildCMCCMenu(MenuSaveReq req) {
        SecMenu menuResult = new SecMenu();
        List<Object> menu = new ArrayList<>();
        for (MenuParentReq menuParentReq : req.getMenuParentReqList()) {
            List<MenuChildReq> menuChildReqList = menuParentReq.getMenuChildReqList();
            String menuContent = menuParentReq.getMenuContent();
            if (1 == menuParentReq.getMenuType()) {
                //功能选项
                for (MenuChildReq menuChildReq : menuChildReqList) {
                    int buttonType = Integer.parseInt(menuChildReq.getButtonType());
                    String type = getButtonType(buttonType);
                    String buttonContent = menuChildReq.getButtonContent();
                    JSONObject jsonObject = JSONObject.parseObject(buttonContent);
                    String buttonText = jsonObject.getJSONObject("buttonDetail").getJSONObject("input").getString("value");
                    String buttonUUID = jsonObject.getString("uuid");
                    if (buttonType == 1) {
                        //回复按钮菜单
                        ReplyMenu replyMenu = new ReplyMenu();
                        MenuBase menuBase = new MenuBase();
                        menuBase.setDisplayText(buttonText);
                        Map<String, String> map = new HashMap<>();
                        map.put("data", buttonUUID);
                        menuBase.setPostback(map);
                        replyMenu.setReply(menuBase);
                        menu.add(replyMenu);
                    } else {
                        JSONObject object = new JSONObject();
                        JSONObject param = new JSONObject();
                        object.put("action", param);
                        //displayText
                        param.put("displayText", buttonText);
                        //postback
                        JSONObject data = new JSONObject();
                        param.put("postback", data);
                        data.put("data", buttonUUID);
                        JSONObject typeObject = new JSONObject();
                        buildType(type, typeObject, jsonObject);
                        if (buttonType == 5) {
                            typeObject.put("requestLocationPush", new HashMap<>());
                            param.put("mapAction", typeObject);
                        } else if (buttonType == 3) {
                            param.put("urlAction", typeObject);
                        } else {
                            param.put(type, typeObject);
                        }
                        menu.add(object);
                    }
                }
            } else {
                //子父菜单
                JSONObject result = new JSONObject();
                SecMenu secMenu = new SecMenu();
                List<Object> list = new ArrayList<>();
                for (MenuChildReq menuChildReq : menuChildReqList) {
                    int buttonType = Integer.parseInt(menuChildReq.getButtonType());
                    String type = getButtonType(buttonType);
                    String buttonContent = menuChildReq.getButtonContent();
                    JSONObject jsonObject = JSONObject.parseObject(buttonContent);
                    String buttonText = jsonObject.getJSONObject("buttonDetail").getJSONObject("input").getString("value");
                    String buttonUUID = jsonObject.getString("uuid");
                    if (buttonType == 1) {
                        //回复按钮菜单
                        ReplyMenu replyMenu = new ReplyMenu();
                        MenuBase menuBase = new MenuBase();
                        menuBase.setDisplayText(buttonText);
                        Map<String, String> map = new HashMap<>();
                        map.put("data", buttonUUID);
                        menuBase.setPostback(map);
                        replyMenu.setReply(menuBase);
                        list.add(replyMenu);
                    } else {
                        JSONObject object = new JSONObject();
                        JSONObject param = new JSONObject();
                        object.put("action", param);
                        //displayText
                        param.put("displayText", buttonText);
                        //postback
                        JSONObject data = new JSONObject();
                        param.put("postback", data);
                        data.put("data", buttonUUID);
                        JSONObject typeObject = new JSONObject();
                        buildType(type, typeObject, jsonObject);
                        //共享自己位置
                        if (buttonType == 5) {
                            typeObject.put("requestLocationPush", new HashMap<>());
                            param.put("mapAction", typeObject);
                        } else if (buttonType == 3) {
                            param.put("urlAction", typeObject);
                        } else if (buttonType == 7) {
                            param.put("dialerAction", typeObject);
                        } else {
                            param.put(type, typeObject);
                        }
                        list.add(object);
                    }
                }
                secMenu.setDisplayText(menuContent);
                secMenu.setEntries(list);
                result.put("menu", secMenu);
                menu.add(result);
            }
        }
        menuResult.setEntries(menu);
        JSONObject finalResult = new JSONObject();
        finalResult.put("menu", menuResult);
        MenuRequest menuRequest = new MenuRequest();
        String string = finalResult.toJSONString();
        log.info("构建移动菜单数据: {}", string);
        String encode = Base64.encode(string);
        menuRequest.setMenu(encode);
        return menuRequest;
    }

    private void buildType(String type, JSONObject typeObject, JSONObject buttonObject) {
        Map<String, String> actionParams = new HashMap<>();
        JSONObject buttonDetail = buttonObject.getJSONObject("buttonDetail");
        switch (type) {
            case "mapAction":
                // 地址定位
                actionParams.put("latitude", buttonDetail.getString("localLatitude"));
                actionParams.put("longitude", buttonDetail.getString("localLongitude"));
                actionParams.put("label", buttonDetail.getJSONObject("localLocation").getString("name"));
                /**
                 * 固定菜单提交问题
                 * #6815
                 */
                JSONObject location = new JSONObject();
                location.put("location", actionParams);
                typeObject.put("showLocation", location);
                break;
            case "urlAction":
                //跳转链接
                Long formId = buttonDetail.getLong("formId");
                if (ObjectUtil.isEmpty(formId)) {
                    //为空就按照linkUrl
                    actionParams.put("url", buttonDetail.getString("linkUrl"));
                } else {
                    //构建表单url
                    actionParams.put("url", formUrlConfigure.getShareUrl() + formId);

                }
                actionParams.put("application", "webview");
                actionParams.put("viewMode", "full");
                typeObject.put("openUrl", actionParams);
                break;
            case "dialerAction":
                //打电话
                actionParams.put("phoneNumber", buttonDetail.getString("phoneNum"));
                typeObject.put("dialPhoneNumber", actionParams);
                break;
            case "composeTextAction":
                //调起指定联系人
                actionParams.put("phoneNumber", buttonDetail.getString("targetPhoneNum"));
                actionParams.put("text", buttonDetail.getJSONObject("previewInput").getString("name"));
                typeObject.put("composeTextMessage", actionParams);
                break;
            case "composeVideoAction":
                //拍摄
                actionParams.put("phoneNumber", buttonDetail.getString("targetPhoneNumForPhoto"));
                typeObject.put("dialVideoCall", actionParams);
                break;
            //打开APP
            case "openApp":
                actionParams.put("url", buttonDetail.getString("linkUrl"));
                actionParams.put("application", "browser");
                typeObject.put("openUrl", actionParams);
                break;
            case "calendarAction":
                log.info("打开日历");
                actionParams.put("startTime", buildCalendar(buttonDetail.getString("calendarStartTime")));
                actionParams.put("endTime", buildCalendar(buttonDetail.getString("calendarEndTime")));
                actionParams.put("title", buttonDetail.getJSONObject("calendarTitle").getString("name"));
                actionParams.put("description", buttonDetail.getJSONObject("calendarTitle").getString("name"));
                typeObject.put("createCalendarEvent", actionParams);
                break;
            //共享设备信息
            case "deviceAction":
                typeObject.put("requestDeviceSpecifics", actionParams);
                break;
        }
    }


    private SubMenu getSubMenu(List<MenuChildReq> menuChildReqList, String menuContent) {
        SubMenu subMenu = new SubMenu();
        List<Suggestions> list = new ArrayList<>();
        for (MenuChildReq menuChildReq : menuChildReqList) {
            int buttonType = Integer.parseInt(menuChildReq.getButtonType());
            String buttonContent = menuChildReq.getButtonContent();
            JSONObject jsonObject = JSONObject.parseObject(buttonContent);
            Suggestions suggestions = buildButton(jsonObject, buttonType);
            list.add(suggestions);
        }
        subMenu.setDisplayText(menuContent);
        subMenu.setSubMenu(list);
        return subMenu;
    }

    private Item getItem(List<MenuChildReq> menuChildReqList) {
        Item item = new Item();
        for (MenuChildReq menuChildReq : menuChildReqList) {
            int buttonType = Integer.parseInt(menuChildReq.getButtonType());
            String buttonContent = menuChildReq.getButtonContent();
            JSONObject jsonObject = JSONObject.parseObject(buttonContent);
            Suggestions suggestions = buildButton(jsonObject, buttonType);
            item.setItem(suggestions);
        }
        return item;
    }

    /**
     * 构建按钮信息
     *
     * @param buttonContent 按钮数组
     * @return 构建参数
     */
    private Suggestions buildButton(JSONObject buttonContent, Integer buttonType) {
        Suggestions suggestion = new Suggestions();
        //按钮类型
        String type = getButtonType(buttonType);
        //按钮的内容
        Map<String, String> actionParams = getActionParams(type, buttonContent);
        String buttonText = buttonContent.getJSONObject("buttonDetail").getJSONObject("input").getString("value");
        String buttonUUID = buttonContent.getString("uuid");
        suggestion.setType(type);
        //打开app设置为urlAction
        if (StringUtils.equals("openApp", type)) {
            suggestion.setType("urlAction");
        }
        suggestion.setPostbackData(buttonUUID);
        suggestion.setDisplayText(buttonText);
        suggestion.setActionParams(actionParams);
        return suggestion;
    }


    /**
     * 获取按钮的信息
     *
     * @param buttonType 按钮的类型
     * @return 按钮信息map
     */
    private Map<String, String> getActionParams(String buttonType, JSONObject button) {
        Map<String, String> actionParams = new HashMap<>();
        JSONObject buttonDetail = button.getJSONObject("buttonDetail");
        //满足下面条件的不加参数 回复按钮、发送地址
        //根据不同按钮构建不通参数
        switch (buttonType) {
            case "mapAction":
                // 地址定位
                actionParams.put("latitude", buttonDetail.getString("localLatitude"));
                actionParams.put("longitude", buttonDetail.getString("localLongitude"));
                actionParams.put("label", buttonDetail.getJSONObject("localLocation").getString("name"));
                break;
            case "urlAction":
                //跳转链接
                Long formId = buttonDetail.getLong("formId");
                if (ObjectUtil.isEmpty(formId)) {
                    //为空就按照linkUrl
                    actionParams.put("url", buttonDetail.getString("linkUrl"));
                } else {
                    //构建表单url
                    actionParams.put("url", formUrlConfigure.getShareUrl() + formId);
                }
                if (buttonDetail.getInteger("localOpenMethod") == 1) {
                    actionParams.put("application", "webview");
                } else {
                    actionParams.put("application", "browser");
                }
                actionParams.put("viewMode", "full");
                break;
            case "dialerAction":
                //打电话
                actionParams.put("phoneNumber", buttonDetail.getString("phoneNum"));
                break;
            case "composeTextAction":
                //调起指定联系人
                actionParams.put("phoneNumber", buttonDetail.getString("targetPhoneNum"));
                actionParams.put("text", buttonDetail.getJSONObject("previewInput").getString("name"));
                break;
            case "composeVideoAction":
                //拍摄
                actionParams.put("phoneNumber", buttonDetail.getString("targetPhoneNumForPhoto"));
                break;
            //打开APP
            case "openApp":
                actionParams.put("url", buttonDetail.getString("linkUrl"));
                actionParams.put("application", "browser");
                actionParams.put("viewMode", "full");
                break;
            case "calendarAction":
                log.info("打开日历");
                actionParams.put("startTime", buildCalendar(buttonDetail.getString("calendarStartTime")));
                actionParams.put("endTime", buildCalendar(buttonDetail.getString("calendarEndTime")));
                actionParams.put("title", buttonDetail.getJSONObject("calendarTitle").getString("name"));
                actionParams.put("description", buttonDetail.getJSONObject("calendarTitle").getString("name"));
                break;
            default:
                return null;
        }
        return actionParams;
    }

    private String buildCalendar(String date) {
        date = date.replace(" ", "T");
        StringBuffer sb = new StringBuffer();
        sb.append(date);
        sb.append("Z");
        return sb.toString();
    }

    private Integer getChatbotMenuVersion(MenuSaveReq req) {
        LambdaQueryWrapper<MenuDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MenuDo::getChatbotAccountId, req.getChatbotAccountId());
        queryWrapper.orderByDesc(MenuDo::getVersion);
        List<MenuDo> menuDoList = menuDao.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(menuDoList) && ObjectUtil.isNotEmpty(req.getVersion()) &&
                !req.getVersion().equals(menuDoList.get(0).getVersion())) {
            throw new BizException(820103019, "当前版本非最新版，请刷新后操作");
        }
        int version = 1;
        if (CollectionUtil.isNotEmpty(menuDoList)) {
            version = menuDoList.get(0).getVersion() + 1;
        }
        return version;
    }

    private String getButtonType(Integer type) {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "reply");
        map.put(2, "urlAction");
        map.put(3, "openApp");
        map.put(4, "dialerAction");
        map.put(5, "ownMapAction");
        map.put(6, "mapAction");
        map.put(7, "composeVideoAction");
        map.put(8, "composeTextAction");
        map.put(9, "calendarAction");
        map.put(10, "deviceAction");
        return map.get(type);
    }


    @Override
    public MenuResp queryByChatbotId(ChatbotIdReq req) {
        MenuResp menuResp = new MenuResp();
        List<MenuRecordResp> menuRecordRespList = new ArrayList<>();
        //1.查询主表
        List<MenuDo> menuDoList = menuDao.selectList(MenuDo::getChatbotAccountId, req.getChatbotId());
        List<MenuDo> versionList = menuDoList.stream().sorted(Comparator.comparing(MenuDo::getVersion, Comparator.reverseOrder())).collect(Collectors.toList());
        List<Integer> status = menuDoList.stream().map(MenuDo::getMenuStatus).collect(Collectors.toList());
        //如果数据库有审核通过的才展示还原按钮
        menuResp.setIsShowReduction(CollectionUtil.isNotEmpty(status) && status.contains(1));
        if (CollectionUtil.isNotEmpty(versionList)) {
            menuResp.setVersion(versionList.get(0).getVersion());
        }
        if (CollectionUtil.isEmpty(menuDoList)) {
            menuResp.setMenuStatus(-1);
            return menuResp;
        }
        //2.查询父菜单
        List<Long> menuIdList = new ArrayList<>();
        menuDoList.forEach(item -> {
            menuIdList.add(item.getId());
        });
        QueryWrapper<MenuParentDo> parentDoQueryWrapper = new QueryWrapper<>();
        parentDoQueryWrapper.in("menu_id", menuIdList);
        List<MenuParentDo> menuParentDoList = menuParentDao.selectList(parentDoQueryWrapper);
        //3.查询子菜单
        List<Long> parentIdList = new ArrayList<>();
        menuParentDoList.forEach(item -> {
            parentIdList.add(item.getId());
        });
        QueryWrapper<MenuChildDo> menuChildDoQueryWrapper = new QueryWrapper<>();
        menuChildDoQueryWrapper.in("parent_id", parentIdList);
        List<MenuChildDo> menuChildDoList = menuChildDao.selectList(menuChildDoQueryWrapper);

        menuDoList.forEach(item -> {
            MenuRecordResp resp = new MenuRecordResp();
            BeanUtil.copyProperties(item, resp);
            resp.setSubmitUser(item.getSubmitUser());
            menuRecordRespList.add(resp);
        });

        for (MenuRecordResp resp : menuRecordRespList) {
            List<MenuParentResp> parentRespList = new ArrayList<>();
            for (MenuParentDo menuParentDo : menuParentDoList) {
                if (resp.getId().equals(menuParentDo.getMenuId())) {
                    MenuParentResp parentResp = new MenuParentResp();
                    BeanUtil.copyProperties(menuParentDo, parentResp);

                    List<MenuChildResp> menuChildRespList = new ArrayList<>();
                    for (MenuChildDo childDo : menuChildDoList) {
                        if (menuParentDo.getId().equals(childDo.getParentId())) {
                            MenuChildResp childResp = new MenuChildResp();
                            BeanUtil.copyProperties(childDo, childResp);
                            childResp.setOption(JSON.parseArray(childDo.getOptionList(), Integer.class));
                            menuChildRespList.add(childResp);
                        }
                    }
                    parentResp.setMenuChildRespList(menuChildRespList);
                    parentRespList.add(parentResp);
                }
            }
            resp.setMenuParentRespList(parentRespList);
        }

        List<MenuRecordResp> sortList = menuRecordRespList.stream().sorted(Comparator.comparing(MenuRecordResp::getVersion, Comparator.reverseOrder())).collect(Collectors.toList());
        menuResp.setMenuParentRespList(sortList.get(0).getMenuParentRespList());
        //展示可用的菜单
        if (ObjectUtil.isNotEmpty(req.getUseable()) && 1 == req.getUseable()) {
            List<MenuRecordResp> versions = sortList.stream().filter(m -> m.getMenuStatus() == 1).sorted(Comparator.comparing(MenuRecordResp::getVersion, Comparator.reverseOrder())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(versions)) {
                menuResp.setMenuParentRespList(versions.get(0).getMenuParentRespList());
            } else {
                menuResp.setMenuParentRespList(new ArrayList<>());
            }
        }
        List<MenuRecordResp> sortTimeList = menuRecordRespList.stream().sorted(Comparator.comparing(MenuRecordResp::getSubmitTime, Comparator.reverseOrder())).collect(Collectors.toList());
        menuResp.setMenuRecordRespList(sortTimeList);
        menuResp.setMenuStatus(sortList.get(0).getMenuStatus());
        return menuResp;
    }

    @Override
    public void reduction(ChatbotIdReq req) {
        LambdaQueryWrapper<MenuDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MenuDo::getChatbotAccountId, req.getChatbotId());
        queryWrapper.eq(MenuDo::getMenuStatus, NumCode.ONE.getCode());
        queryWrapper.orderByDesc(MenuDo::getVersion);
        List<MenuDo> latestAvailableMenu = menuDao.selectList(queryWrapper);
        if (CollectionUtil.isEmpty(latestAvailableMenu))
            throw new BizException("无过审的固定菜单记录");
        MenuDo menuDo = latestAvailableMenu.get(0);
        Integer version = menuDao.selectMenuVersion(req.getChatbotId());

        MenuDo updateDo = new MenuDo();
        BeanUtil.copyProperties(menuDo, updateDo);
        updateDo.setVersion(version + 1);
        menuDao.updateById(updateDo);
    }

    @Override
    public List<MenuParentResp> queryMenuContentById(IdReq req) {
        List<MenuParentResp> menuParentRespList = new ArrayList<>();
        QueryWrapper<MenuParentDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("menu_id", req.getId());
        List<MenuParentDo> menuParentDoList = menuParentDao.selectList(queryWrapper);

        List<Long> parentIdList = new ArrayList<>();
        menuParentDoList.forEach(item -> {
            MenuParentResp menuParentResp = new MenuParentResp();
            BeanUtil.copyProperties(item, menuParentResp);
            menuParentRespList.add(menuParentResp);
            parentIdList.add(item.getId());
        });

        QueryWrapper<MenuChildDo> menuChildDoQueryWrapper = new QueryWrapper<>();
        menuChildDoQueryWrapper.in("parent_id", parentIdList);
        List<MenuChildDo> menuChildDoList = menuChildDao.selectList(menuChildDoQueryWrapper);
        List<MenuChildResp> menuChildRespList = new ArrayList<>();
        menuChildDoList.forEach(item -> {
            MenuChildResp resp = new MenuChildResp();
            BeanUtil.copyProperties(item, resp);
            menuChildRespList.add(resp);
        });

        menuParentRespList.forEach(item -> {
            List<MenuChildResp> childRespList = new ArrayList<>();
            for (MenuChildResp childResp : menuChildRespList) {
                if (item.getId().equals(childResp.getParentId())) {
                    childRespList.add(childResp);
                }
            }
            item.setMenuChildRespList(childRespList);
        });

        return menuParentRespList;
    }
}

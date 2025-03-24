package com.citc.nce.im.materialSquare.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.cardstyle.CardStyleApi;
import com.citc.nce.auth.cardstyle.vo.CardStyleReq;
import com.citc.nce.auth.cardstyle.vo.CardStyleResp;
import com.citc.nce.auth.formmanagement.FormManagementApi;
import com.citc.nce.auth.formmanagement.tempStore.Csp4CustomerFrom;
import com.citc.nce.auth.messagetemplate.MessageTemplateApi;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplatePageReq;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateReq;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateResp;
import com.citc.nce.authcenter.csp.CspApi;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.CustomerDetailResp;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.ErrorCode;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.vo.tempStore.Csp4CustomerAudio;
import com.citc.nce.common.vo.tempStore.Csp4CustomerImg;
import com.citc.nce.common.vo.tempStore.Csp4CustomerVideo;
import com.citc.nce.customcommand.CustomCommandApi;
import com.citc.nce.customcommand.vo.CustomCommandDetailVo;
import com.citc.nce.common.xss.core.XssCleanIgnore;
import com.citc.nce.filecenter.TempStoreMaterialApi;
import com.citc.nce.filecenter.tempStore.*;
import com.citc.nce.h5.H5Api;
import com.citc.nce.h5.vo.H5Info;
import com.citc.nce.im.mall.snapshot.RobotInputUtil;
import com.citc.nce.im.mall.snapshot.model.TsCardStyleContent;
import com.citc.nce.im.mall.snapshot.service.MallSnapshotService;
import com.citc.nce.im.materialSquare.entity.MsSummarySnapshot;
import com.citc.nce.im.materialSquare.mapper.OrderMapper;
import com.citc.nce.im.materialSquare.service.IMsSummaryService;
import com.citc.nce.im.materialSquare.service.IOrderService;
import com.citc.nce.im.tempStore.service.impl.useTemplate.ProcessJsonUtil;
import com.citc.nce.im.tempStore.utils.PageSupport;
import com.citc.nce.materialSquare.MsManageActivityContentApi;
import com.citc.nce.materialSquare.vo.activity.MsPrice;
import com.citc.nce.materialSquare.vo.activity.MsPriceParam;
import com.citc.nce.modulemanagement.ModuleManagementApi;
import com.citc.nce.modulemanagement.vo.ModuleManagementItem;
import com.citc.nce.robot.*;
import com.citc.nce.robot.api.mall.common.*;
import com.citc.nce.robot.api.mall.constant.MallError;
import com.citc.nce.robot.api.mall.constant.MallTemplateTypeEnum;
import com.citc.nce.robot.api.materialSquare.MsSummaryApi;
import com.citc.nce.robot.api.materialSquare.emums.MsAuditStatus;
import com.citc.nce.robot.api.materialSquare.emums.MsPayType;
import com.citc.nce.robot.api.materialSquare.emums.MsType;
import com.citc.nce.robot.api.materialSquare.entity.MsSummary;
import com.citc.nce.robot.api.materialSquare.vo.cus.MsCustomerBuyVo;
import com.citc.nce.robot.api.materialSquare.vo.summary.MsSummaryDetailVO;
import com.citc.nce.robot.api.materialSquare.vo.summary.req.OperationOrderPageReq;
import com.citc.nce.robot.api.materialSquare.vo.summary.req.OperationOrderRemarkReq;
import com.citc.nce.robot.api.tempStore.bean.csp.*;
import com.citc.nce.robot.api.tempStore.bean.manage.ManageOrderPage;
import com.citc.nce.robot.api.tempStore.bean.manage.OrderManagePageQuery;
import com.citc.nce.robot.api.tempStore.domain.Order;
import com.citc.nce.robot.api.tempStore.enums.PayStatus;
import com.citc.nce.robot.vo.*;
import com.citc.nce.tempStore.UseTempStoreApi;
import com.citc.nce.tempStore.vo.UseTempVariableOrder;
import io.seata.core.context.RootContext;
import io.seata.core.exception.TransactionException;
import io.seata.core.model.GlobalStatus;
import io.seata.tm.api.GlobalTransactionContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.shardingsphere.transaction.annotation.ShardingSphereTransactionType;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 模板商城 订单表 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2023-11-17 02:11:15
 */
@Service
@AllArgsConstructor
@Slf4j
public class MsOrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {
    //<当前线程id，保存文件filecenter 线程id>
    private final ConcurrentHashMap<Long, RollbackInfo> rollbackMap = new ConcurrentHashMap<>();

    private final CspCustomerApi cspCustomerApi;
    private final CspApi cspApi;
    private final MallSnapshotService mallSnapshotService;
    private final MessageTemplateApi messageTemplateApi;
    private final RobotSceneNodeApi robotSceneNodeApi;
    private final RobotSceneMaterialApi robotSceneMaterialApi;
    private final RobotProcessSettingNodeApi robotProcessSettingNodeApi;
    private final RobotProcessTriggerNodeApi robotProcessTriggerNodeApi;
    private final RobotProcessTreeApi robotProcessTreeApi;
//    private final FormManagementApi formManagementApi;
    private final TempStoreMaterialApi tempStoreMaterialApi;
    private final UseTempStoreApi useTempStoreApi;
    private final IMsSummaryService summaryService;
    private final CardStyleApi cardStyleApi;
    private final MsManageActivityContentApi activityContentApi;
    private final CustomCommandApi commandApi;
    private final ModuleManagementApi moduleManagementApi;
    private final H5Api h5Api;


    @Override
    public void cspPage(String cspId, Page<Order> page, OrderPageQuery query) {
        LambdaUpdateWrapper<Order> wrapper = new LambdaUpdateWrapper<Order>()
                .eq(Order::getCspId, cspId)
                .eq(Objects.nonNull(query.getTempType()), Order::getTempType, query.getTempType())
                .eq(Objects.nonNull(query.getPayStatus()), Order::getPayStatus, query.getPayStatus())
                .like(StringUtils.hasLength(query.getGoodsName()), Order::getGoodsName, query.getGoodsName())
                .orderByDesc(Order::getCreateTime)
                .orderByDesc(Order::getOrderId);
        page(page, wrapper);
    }


    @Override
    public void cancelMyOrder(Long orderId) {
        Order o = getById(orderId);
        BaseUser user = SessionContextUtil.getUser();
        if (!o.getCreator().equals(user.getUserId())) {
            throw new BizException(500, "只能取消自己的订单");
        }
        //对于状态为待支付的订单，右侧显示取消订单。确认后取消成功，弹窗如下
        if (PayStatus.wait.equals(o.getPayStatus())) {
            updateById(new Order()
                    .setOrderId(orderId)
                    .setPayStatus(PayStatus.cancel));
        } else {
            throw new BizException(500, "订单不是待支付状态，不能取消订单");
        }
    }


    @Override
    public void cancelOrder(Long orderId) {
        Order o = getById(orderId);
        if (MsSummaryApi.cspMsTypeList.contains(o.getTempType())) {
            //为csp作品
            SessionContextUtil.sameCsp(o.getCspId());
        }
        //对于状态为待支付的订单，右侧显示取消订单。确认后取消成功，弹窗如下
        if (PayStatus.wait.equals(o.getPayStatus())) {
            updateById(new Order()
                    .setOrderId(orderId)
                    .setPayStatus(PayStatus.cancel));
        } else {
            throw new BizException(500, "订单不是待支付状态，不能取消订单");
        }
    }

    @Override
    public void payComplete(Long orderId) {
        Order o = getById(orderId);
        if (MsSummaryApi.cspMsTypeList.contains(o.getTempType())) {
            //为csp作品
            SessionContextUtil.sameCsp(o.getCspId());
        }
        if (PayStatus.wait.equals(o.getPayStatus())) {
            Order order = new Order()
                    .setOrderId(orderId)
                    .setPayStatus(PayStatus.complete)
                    .setPayTime(new Date());
            updateById(order);
            //如果是组件，则后台自动调用使用
            if (o.getTempType() == MsType.SYSTEM_MODULE) {
                OrderUseTemplate useReq = new OrderUseTemplate();
                useReq.setOrderId(orderId);
                MsOrderServiceImpl proxy = (MsOrderServiceImpl) AopContext.currentProxy();
                proxy.useTemplate(useReq);
            }
        } else {
            throw new BizException(500, "订单不是待支付状态，不能完成支付单");
        }
    }

    @Override
    public void putRemake(CspOrderRemake remake) {
        Order o = getById(remake.getOrderId());
        if (Objects.isNull(o)) {
            throw new BizException(500, "订单不存在");
        }
        if (MsSummaryApi.cspMsTypeList.contains(o.getTempType())) {
            //为csp作品
            SessionContextUtil.sameCsp(o.getCspId());
        }
        updateById(new Order()
                .setOrderId(remake.getOrderId())
                .setRemake(remake.getRemake()));
    }

    @Override
    public void clientPage(String userId, Page<Order> page, OrderPageQuery query) {
        LambdaUpdateWrapper<Order> wrapper = new LambdaUpdateWrapper<Order>()
                .eq(Order::getCreator, userId)
                .eq(Objects.nonNull(query.getTempType()), Order::getTempType, query.getTempType())
                .eq(Objects.nonNull(query.getPayStatus()), Order::getPayStatus, query.getPayStatus())
                .like(StringUtils.hasLength(query.getGoodsName()), Order::getGoodsName, query.getGoodsName())
                .orderByDesc(Order::getCreateTime)
                .orderByDesc(Order::getOrderId);
        page(page, wrapper);
    }

    @Override
    public List<ManageOrderPage> managePage(Page<Order> page, OrderManagePageQuery query) {
        //远程查询搜索字段
        final Map<String, UserInfoVo> cspMap = new HashMap<>();
        final Map<String, UserInfoVo> customerMap = new HashMap<>();
        List<MsType> msTypeList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(query.getTempTypes())) {
            msTypeList.addAll(query.getTempTypes());
        }
        //cps搜索字段
        if (StringUtils.hasLength(query.getGoodsQuery())) {
            if ("硬核桃WalnutHardcore".contains(query.getGoodsQuery())) {
                msTypeList.add(MsType.CUSTOM_ORDER);
                msTypeList.add(MsType.SYSTEM_MODULE);
            } else {
                cspMap.putAll(cspApi.queryByNameOrPhone(query.getGoodsQuery()).stream()
                        .collect(Collectors.toMap(UserInfoVo::getCspId, Function.identity(), (s1, s2) -> s1)));
                //cps搜索字段没查询到用户直接返回
                if (CollectionUtils.isEmpty(cspMap)) {
                    return Collections.emptyList();
                }
            }
        }
        //客户端搜索字段
        if (StringUtils.hasLength(query.getCustomerQuery())) {
            customerMap.putAll(cspCustomerApi.queryByNameOrPhone(query.getCustomerQuery()).stream()
                    .collect(Collectors.toMap(UserInfoVo::getCustomerId, Function.identity(), (s1, s2) -> s1)));
            //客户端搜索字段没查询到用户直接返回
            if (CollectionUtils.isEmpty(customerMap)) {
                return Collections.emptyList();
            }
        }
        //查询订单表
        LambdaUpdateWrapper<Order> wrapper = new LambdaUpdateWrapper<Order>()
                .eq(Objects.nonNull(query.getPayStatus()), Order::getPayStatus, query.getPayStatus())
                .in(!CollectionUtils.isEmpty(msTypeList), Order::getTempType, msTypeList)
                .in(!CollectionUtils.isEmpty(customerMap.keySet()), Order::getCreator, customerMap.keySet())
                .in(!CollectionUtils.isEmpty(cspMap.keySet()), Order::getCspId, cspMap.keySet())
                .orderByDesc(Order::getCreateTime)
                .orderByDesc(Order::getOrderId);
        page(page, wrapper);
        //查询数据后添加customer 和csp账号
        remoteFill(page.getRecords(), cspMap, customerMap);
        //封装数据
        return page.getRecords().stream().map(s -> {
            ManageOrderPage m = new ManageOrderPage();
            BeanUtils.copyProperties(s, m);
            UserInfoVo csp = cspMap.get(s.getCspId());
            if (Objects.nonNull(csp)) {
                m.setCspName(csp.getName());
                m.setCspPhone(csp.getPhone());
            } else {
                m.setCspName("硬核桃WalnutHardcore");
                m.setCspPhone("— —");
            }
            UserInfoVo customer = customerMap.get(s.getCreator());
            if (Objects.nonNull(customer)) {
                m.setCreatorName(customer.getName());
                m.setCreatorPhone(customer.getPhone());
            }
            return m;
        }).collect(Collectors.toList());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void customerBuy(MsCustomerBuyVo customerBuy) {
        Long mssId = customerBuy.getMssId();
        MsSummary goods = summaryService.getById(mssId);
        if (Objects.isNull(goods) || !MsAuditStatus.ACTIVE_ON.equals(goods.getAuditStatus())) {
            throw new BizException("素材已下架");
        }
        Order one = this.lambdaQuery().eq(Order::getGoodsId, customerBuy.getMssId())
                .eq(Order::getCreator, SessionContextUtil.getUserId())
                .ne(Order::getPayStatus, PayStatus.cancel).one();
        if (Objects.nonNull(one)) {
            if (PayStatus.wait.equals(one.getPayStatus())) {
                throw new BizException("您有未支付的订单，请在“订购记录”中查看");
            }
            if (PayStatus.complete.equals(one.getPayStatus())) {
                throw new BizException("已购买，无法重复购买");
            }
        }
        try {
            Order order = new Order();
            order.setOrderNum(getOrderNum(goods.getMsType()));
            //保存cspID查询时候可以少查询一步
            order.setCspId(goods.getCspId());
            order.setTempType(goods.getMsType());
            order.setGoodsId(goods.getMssId());//购买素材使用素材表主键id
            order.setGoodsName(goods.getName());
            order.setGoodsDesc(goods.getMsDesc());
            order.setOrderMoney(formatPrice(BigDecimal.ZERO));
            order.setOriginalPrice(formatPrice(BigDecimal.ZERO));
            //处理收费的
            if (MsPayType.PAID.equals(goods.getPayType())) {
                order.setOriginalPrice(formatPrice(goods.getOriginalPrice()));
                //成交价格查询
                MsPrice price = getPrice(customerBuy, goods);
                order.setOrderMoney(formatPrice(price.getPrice()));
                //购买时参加活动的快照
                if (Objects.nonNull(price.getActivityContent())) {
                    order.setMsActivityContentId(price.getActivityContent().getMsActivityContentId());
                }
            }
            //根据订单价格设置下单后状态
            if (Objects.isNull(order.getOrderMoney()) || 0L >= Double.parseDouble(order.getOrderMoney())) {
                //不需要付款直接购入
                order.setPayStatus(PayStatus.complete);
                Date time = new Date();
                order.setPayTime(time);
                order.setUpdateTime(time);

                //如果是组件，直接使用模版，更改模版状态；
                if (MsType.SYSTEM_MODULE.equals(goods.getMsType())) {
                    activateModule(goods.getMssId(),order.getCreator());
                }
            } else {
                order.setPayStatus(PayStatus.wait);
            }
            //处理素材 快照
            MsType msType = goods.getMsType();
            //如果是H5，备份数据
            if (MsType.H5_FORM.equals(msType)) {
                h5Api.saveForCustomer(Long.parseLong(goods.getMsId()), SessionContextUtil.getUserId(),goods.getMssId());
            }
            MsSummarySnapshot snapshot = summaryService.getSnapshot(mssId);
            if (Objects.nonNull(snapshot)) {
                order.setGoodsSnapshot(JSON.toJSONString(snapshot));
                //nr_msg 新增卡片样式订单快照（之前这样设计的这里不重构）
                String tsSnapshotUuid = snapshot.getTsSnapshotUuid();
                if (StringUtils.hasLength(tsSnapshotUuid)) {
                    order.setCardStyleContent(mallSnapshotService.getCardStyleContent(tsSnapshotUuid));
                }
                save(order);
            } else {
                throw new BizException("作品内容丢失请联系作品提供方");
            }
        } catch (BizException bizException) {
            throw bizException;
        } catch (Exception sqlException) {
            log.error("下单失败", sqlException);
            throw new BizException("下单失败，当前下单人数过多，请稍后重试");
        }
    }

    private String formatPrice(BigDecimal price) {
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        if (Objects.isNull(price)) {
            //价格是空的返回null
            return null;
        }
        return decimalFormat.format(price);
    }

    private MsPrice getPrice(MsCustomerBuyVo customerBuy, MsSummary summary) {
        MsPrice price = activityContentApi.getPrice(summary.getMssId(), customerBuy.getMsActivityContentId(), customerBuy.getPType());
        if (!customerBuy.getPrice().setScale(2, RoundingMode.HALF_UP).equals(price.getPrice().setScale(2,
                RoundingMode.HALF_UP))) {
            throw new BizException("确认活动价格失败,请刷新页面获取最新价格后重试");
        }
        return price;
    }

    @Override
    public String getOrderSnapshot(Long orderId) {
        return getBaseMapper().getOrderSnapshot(orderId);
    }


    @Override
//    @ShardingSphereTransactionType(value = TransactionType.BASE)
    @Transactional(rollbackFor = Exception.class)
    @XssCleanIgnore
    public OrderUseTemplateResp useTemplate(OrderUseTemplate req) {
        if (Arrays.asList(1, 0).contains(req.getType()) && (null == req.getChatbotAccount() || 0 == req.getChatbotAccount().length)) {
            //只有机器人和5G 关联5G消息账号
            throw new BizException(MallError.CHATBOT_ACCOUNT_ERROR);
        }
        Order order = getById(req.getOrderId());

        if (!PayStatus.complete.equals(order.getPayStatus())) {
            throw new BizException("此订单未支付");
        }

        MsSummarySnapshot snapshot = getOrderMsSummarySnapshot(req.getOrderId());
        MsSummary summary = snapshot.getSummary();
        switch (summary.getMsType()) {
            case ROBOT:
                return robot(req, JSON.parseObject(snapshot.getContent().getMsJson(), MallCommonContent.class));
            case NR_SSG:
                return fiveMsg(req, JSON.parseObject(snapshot.getContent().getMsJson(), MallCommonContent.class));
            case H5_FORM:
                return h5Form(order,req.getName());
            case CUSTOM_ORDER:
                return customOrder(req, JSON.parseObject(snapshot.getContent().getMsJson(), CustomCommandDetailVo.class));
            case SYSTEM_MODULE:
                return systemModule(JSON.parseObject(snapshot.getContent().getMsJson(), ModuleManagementItem.class));
            default:
                throw new RuntimeException("不支持的素材类型,暂未实现");
        }
    }

    private OrderUseTemplateResp h5Form(Order order,String name) {
        //如果是H5表单，则将改表单信息保存在用户的列表中
        Long mssId = order.getGoodsId();
//        MsSummary detail = summaryService.getById(mssId);
        h5Api.importH5ForCustomer(mssId, SessionContextUtil.getUserId(),name);
        return null;
    }

    private OrderUseTemplateResp systemModule(ModuleManagementItem moduleManagementItem) {
        moduleManagementApi.useModuleManagement(moduleManagementItem);
        return new OrderUseTemplateResp();
    }

    private OrderUseTemplateResp customOrder(OrderUseTemplate req, CustomCommandDetailVo detailVo) {
        detailVo.setName(req.getName());
        commandApi.useCommand(detailVo);
        return new OrderUseTemplateResp();
    }


    private MsSummarySnapshot getOrderMsSummarySnapshot(Long orderId) {
        String orderSnapshot = getOrderSnapshot(orderId);
        if (StringUtils.hasLength(orderSnapshot)) {
            MsSummarySnapshot content = JSON.parseObject(orderSnapshot, MsSummarySnapshot.class);
            if (Objects.nonNull(content)) {
                return content;
            }
        }
        throw new BizException(MallError.SNAPSHOT_NOT_EXIST);
    }

    private OrderUseTemplateResp fiveMsg(OrderUseTemplate req, MallCommonContent mallCommonContent) {
        MessageTemplateReq templateSave = new MessageTemplateReq();
        String userId = SessionContextUtil.getUser().getUserId();

        FiveG fiveG = mallCommonContent.getFiveG();
        ProcessJsonUtil processJsonUtil = null;
        //表单新插入
        if (!CollectionUtils.isEmpty(fiveG.getForm())) {
            processJsonUtil = getProcessJsonUtil();
            processJsonUtil.putForm(insertFrom(fiveG.getForm()));
        }
        //素材新插入
        if (Objects.nonNull(fiveG.getMaterial())) {
            processJsonUtil = Objects.isNull(processJsonUtil) ? getProcessJsonUtil() : processJsonUtil;
            saveMaterial(req.getChatbotAccount(), userId, processJsonUtil,
                    Collections.singletonList(fiveG.getMaterial()));
        }
        //改变json数据
        if (Objects.nonNull(processJsonUtil)) {
            fiveG.setModuleInformation(processJsonUtil.changeFiveG(fiveG.getModuleInformation()));
            fiveG.setShortcutButton(processJsonUtil.changeButtonList(fiveG.getShortcutButton()));

            String cardStyleSnapshot = getOrderCardStyleSnapshot(req.getOrderId());
            if (StrUtil.isNotBlank(cardStyleSnapshot)) {
                //存在卡片样式
                TsCardStyleContent cardStyle = JSON.parseObject(cardStyleSnapshot, TsCardStyleContent.class);
                fiveG.setModuleInformation(changeTsCardStyle(cardStyle, fiveG.getModuleInformation()));
                templateSave.setStyleInformation(getStyleInformation(fiveG.getModuleInformation()));
            }
        }

        // 现在的模板类型只支持普通模板
        BeanUtil.copyProperties(fiveG, templateSave);
        templateSave.setTemplateType(1);
        templateSave.setTemplateName(req.getName());
        templateSave.setTemplateSource(1);
        templateSave.setImportFlag(1);
        messageTemplateApi.saveMessageTemplate(templateSave);
        return new OrderUseTemplateResp();
    }

    /**
     * 提取 moduleInformation 里面的css样式JSON （目前看来保存时候里面外面是一样的）
     *
     * @param moduleInformation json
     * @return styleInformation json
     */
    private String getStyleInformation(String moduleInformation) {
        JSONObject jsonObject = JSON.parseObject(moduleInformation);
        JSONObject styleInformation = jsonObject.getJSONObject("styleInformation");
        if (Objects.isNull(styleInformation)) return null;
        return styleInformation.toJSONString();
    }

    /**
     * 处理5g消息卡片样式数据替换
     *
     * @param cardStyle         卡片样式
     * @param moduleInformation json数据
     */
    private String changeTsCardStyle(TsCardStyleContent cardStyle, String moduleInformation) {
        if (Objects.isNull(cardStyle) || StrUtil.isBlankIfStr(moduleInformation)) return moduleInformation;
        String str = moduleInformation;
        if (StrUtil.isNotBlank(cardStyle.getReqUrlId())) {
            //卡片样式中存在图片
            String oldFileUUid = getFileUUid(cardStyle.getReqUrlId());
            String fileUUid = tempStoreMaterialApi.saveTempStoreCardStyleImg(oldFileUUid);
            str = str.replaceAll(oldFileUUid, fileUUid);
        }
        CardStyleResp cardStyleSh = cardStyle.getCardStyle();
        CardStyleReq req = new CardStyleReq();
        BeanUtil.copyProperties(cardStyleSh, req);
        //随机生成的名称
        req.setStyleName(String.valueOf(System.currentTimeMillis()));
        Long newStyleId = cardStyleApi.saveCardStyle(req);
        str = str.replaceAll(cardStyle.getOldCardStyleId().toString(), newStyleId.toString());
        return str;
    }

    private String getFileUUid(String reqUrlId) {
        return reqUrlId.split("req=")[1];
    }

    private String getOrderCardStyleSnapshot(Long orderId) {
        return getBaseMapper().getCardStyleContent(orderId);
    }

    private void putRollbackMap(SaveMaterialResult result) {
        if (CollectionUtils.isEmpty(result.getAudioMap()) && CollectionUtils.isEmpty(result.getImgMap()) && CollectionUtils.isEmpty(result.getVideoMap())) {
            log.info("没有需要回退素材记录");
            return;
        }
        long threadId = Thread.currentThread().getId();
        RollbackInfo rollbackInfo = new RollbackInfo().setOrderTheadId(threadId).setFileTheadId(result.getThreadId());
        rollbackInfo.setXid(RootContext.getXID());
        rollbackMap.put(threadId, rollbackInfo);
    }

    private ProcessJsonUtil getProcessJsonUtil() {
        return new ProcessJsonUtil(null, null);
    }


    // 机器人模板
    private OrderUseTemplateResp robot(OrderUseTemplate req, MallCommonContent mallCommonContent) {
        Date currentDate = new Date();
        String userId = SessionContextUtil.getUser().getUserId();
        Long tsOrderId = req.getOrderId();
        List<Robot> robotList = mallCommonContent.getRobot();

        // 流程
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(robotList)) {
            //需要修改的变量
            Map<Long, EditName> variableNeedEdit = CollectionUtils.isEmpty(req.getVariableEditList())
                    ? new HashMap<>()
                    : req.getVariableEditList().stream().collect(Collectors.toMap(EditName::getId,
                    Function.identity()));
            //需要修改的指令
            Map<Long, EditName> orderNeedEdit = CollectionUtils.isEmpty(req.getOrderEditList())
                    ? new HashMap<>()
                    : req.getOrderEditList().stream().collect(Collectors.toMap(EditName::getId, Function.identity()));

            //需要保存的变量
            Map<Long, RobotVariableReq> variableMap = getVariableMap4Robot(variableNeedEdit, robotList, tsOrderId,
                    userId, currentDate);
            //需要保存的指令
            Map<Long, RobotOrderReq> orderMap = getOrderMap4Robot(orderNeedEdit, robotList, tsOrderId, userId,
                    currentDate);

            //远程保存变量、指令
            UseTempVariableOrder data = useTempStoreApi.saveVariableOrder(
                    new UseTempVariableOrder(variableNeedEdit, variableMap, orderMap));
            variableMap = data.getVariableMap();
            orderMap = data.getOrderMap();
            //变量、指令保存是否失败 (保存指令和变量前不插入素材，不影响回退)
            if (!CollectionUtils.isEmpty(data.getVariableDuplicate()) || !CollectionUtils.isEmpty(data.getOrderDuplicate())) {
                OrderUseTemplateResp resp = new OrderUseTemplateResp();
                resp.setVariableEditList(getVariableEditName(data.getVariableDuplicate(), variableMap));
                resp.setOrderEditList(getOrderEditName(data.getOrderDuplicate(), orderMap));
                return resp;
            }

            ProcessJsonUtil processUtil = new ProcessJsonUtil(orderMap, variableMap);

            try {
                //保存所以文件素材
                List<Material> materialList = robotList.stream().map(Robot::getMaterial).collect(Collectors.toList());
                saveMaterial(req.getChatbotAccount(), userId, processUtil, materialList);
            } catch (Exception e) {
                log.error("素材导入失败", e);
                throw new BizException("素材导入失败");
            }


            // 场景
            Long sceneId;
            try {
                sceneId = saveScene(req);
            } catch (BizException e) {
                log.error("导入场景失败", e);
                e.setMsg("导入场景失败" + e.getMessage());
                throw e;
            } catch (Exception e) {
                log.error("场景导入失败", e);
                throw new BizException(new ErrorCode(820702003, "场景名称重复，请重新填写"));
            }


            for (Robot robot : robotList) {
                //流程导入
                Long processId;
                try {
                    processId = saveProcessSetting(sceneId, robot);
                } catch (BizException e) {
                    log.error("导入场景失败", e);
                    e.setMsg("导入场景失败" + e.getMessage());
                    throw e;
                } catch (Exception e) {
                    log.error("保存场景失败", e);
                    throw new BizException("保存场景失败");
                }
                // 触发器导入
                try {
                    saveProcessTrigger(req, userId, sceneId, robot, processId);
                } catch (BizException e) {
                    log.error("导入触发器失败", e);
                    e.setMsg("导入触发器失败" + e.getMessage());
                    throw e;
                } catch (Exception e) {
                    log.error("导入触发器失败", e);
                    throw new BizException(MallError.TRIGGER_ERROR);
                }
                // 流程图导入
                try {
                    if (Objects.isNull(robot.getProcess())) {
                        //流程设计图是空的
                        continue;
                    }
                    saveProcessTree(sceneId, robot, processId, orderNeedEdit, variableNeedEdit, processUtil);
                } catch (BizException e) {
                    log.error("流程图导入失败", e);
                    e.setMsg("流程图导入失败" + e.getMessage());
                    throw e;
                } catch (Exception e) {
                    log.error("流程图导入失败 Exception", e);
                    throw new BizException(MallError.PROCESS_TREE_ERROR);
                }
            }
        }
        return new OrderUseTemplateResp();
    }

    /**
     * 素材到 资源管理中
     */
    private void saveMaterial(String[] chatbotAccount, String userId, ProcessJsonUtil processUtil,
                              List<Material> materialList) {
        List<ResourcesAudio> audioList = new ArrayList<>();
        List<ResourcesImg> pictureList = new ArrayList<>();
        List<ResourcesVideo> videoList = new ArrayList<>();
        //提取新素材
        for (Material material : materialList) {
            if (Objects.isNull(material)) continue;
            List<com.citc.nce.robot.api.tempStore.domain.ResourcesAudio> audios = material.getAudioList();
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(audios)) {
                audioList.addAll(BeanUtil.copyToList(audios, ResourcesAudio.class));
            }
            List<com.citc.nce.robot.api.tempStore.domain.ResourcesImg> imgList = material.getPictureList();
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(imgList)) {
                pictureList.addAll(BeanUtil.copyToList(imgList, ResourcesImg.class));
            }
            List<com.citc.nce.robot.api.tempStore.domain.ResourcesVideo> videos = material.getVideoList();
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(videos)) {
                videoList.addAll(BeanUtil.copyToList(videos, ResourcesVideo.class));
            }
        }
        //没有读取到素材
        if (audioList.isEmpty() && pictureList.isEmpty() && videoList.isEmpty()) return;

        //远程保存素材
        SaveMaterialReq saveMaterialReq = new SaveMaterialReq().setChatbotAccount(chatbotAccount).setUserId(userId);
        saveMaterialReq.setAudioList(audioList);
        saveMaterialReq.setPictureList(pictureList);
        saveMaterialReq.setVideoList(videoList);
        SaveMaterialResult result = tempStoreMaterialApi.saveTempStoreMaterial(saveMaterialReq);
        if (!CollectionUtils.isEmpty(result.getAudioMap())) {
            processUtil.putAudio(result.getAudioMap());
        }
        if (!CollectionUtils.isEmpty(result.getImgMap())) {
            processUtil.putImg(result.getImgMap());
        }
        if (!CollectionUtils.isEmpty(result.getVideoMap())) {
            processUtil.putVideo(result.getVideoMap());
        }
        //异步记录线程id
        putRollbackMap(result);
    }

    private List<EditName> getVariableEditName(List<Long> variableDuplicate, Map<Long, RobotVariableReq> variableMap) {
        List<EditName> list = new LinkedList<>();
        variableDuplicate.stream().distinct().forEach(s -> {
            RobotVariableReq variableReq = variableMap.get(s);
            list.add(new EditName().setId(s).setNewName(variableReq.getVariableName()));
        });
        return list;
    }

    private List<EditName> getOrderEditName(List<Long> orderDuplicate, Map<Long, RobotOrderReq> orderMap) {
        List<EditName> list = new LinkedList<>();
        orderDuplicate.stream().distinct().forEach(s -> {
            RobotOrderReq variableReq = orderMap.get(s);
            list.add(new EditName().setId(s).setNewName(variableReq.getOrderName()));
        });
        return list;
    }


    private Map<Long, RobotVariableReq> getVariableMap4Robot(Map<Long, EditName> variableNeedEdit,
                                                             List<Robot> robotList,
                                                             Long tsOrderId, String userId, Date currentDate) {
        Map<Long, RobotVariableReq> map = new HashMap<>();
        if (CollectionUtils.isEmpty(robotList)) return map;
        for (Robot robot : robotList) {
            List<RobotVariable> variables = robot.getVariables();
            if (CollectionUtils.isEmpty(variables)) continue;
            map.putAll(variables.stream().filter(s -> RobotInputUtil.type_custom.equalsIgnoreCase(s.getType())).map(s -> {
                RobotVariableReq entity = new RobotVariableReq();
                BeanUtils.copyProperties(s, entity);
                entity.setTsOrderId(tsOrderId);
                Long oldId = Long.valueOf(s.getId());
                entity.setOldId(oldId);
                entity.setId(null);
                entity.setCreator(userId);
                entity.setCreateTime(currentDate);
                entity.setDeleted(0);
                //名称是否编辑
                EditName edit = variableNeedEdit.get(oldId);
                if (Objects.nonNull(edit)) {
                    entity.setVariableName(edit.getNewName());
                }
                return entity;
            }).collect(Collectors.toMap(RobotVariableReq::getOldId, Function.identity())));
        }
        return map;
    }

    private Map<Long, RobotOrderReq> getOrderMap4Robot(Map<Long, EditName> orderNeedEdit, List<Robot> robotList,
                                                       Long tsOrderId, String userId, Date currentDate) {
        Map<Long, RobotOrderReq> map = new HashMap<>();
        if (CollectionUtils.isEmpty(robotList)) return map;
        for (Robot robot : robotList) {
            List<RobotOrder> variables = robot.getOrders();
            if (CollectionUtils.isEmpty(variables)) continue;
            map.putAll(variables.stream().map(s -> {
                RobotOrderReq entity = new RobotOrderReq();
                BeanUtils.copyProperties(s, entity);
                Long oldId = s.getId();
                entity.setOldId(oldId);
                entity.setTsOrderId(tsOrderId);
                entity.setId(null);
                entity.setCreator(userId);
                entity.setCreateTime(currentDate);
                entity.setDeleted(0);
                //名称是否编辑
                EditName edit = orderNeedEdit.get(oldId);
                if (Objects.nonNull(edit)) {
                    entity.setOrderName(edit.getNewName());
                }
                return entity;
            }).collect(Collectors.toMap(RobotOrderReq::getOldId, Function.identity())));
        }
        return map;
    }


    private Map<Long, Csp4CustomerFrom> insertFrom(List<Form> formList) {
        if (CollectionUtils.isEmpty(formList)) return new HashMap<>();
        List<Long> h5Ids = new ArrayList<>();
        for (Form form : formList) {
            h5Ids.add(form.getId());
        }
        return h5Api.importH5ForRobotAndMSG(h5Ids);
    }


    @Override
    public String useTemplateNameCheck(OrderUseTemplateNameCheckReq req) {
        if (MallTemplateTypeEnum.MSG.getCode().equals(req.getType())) {
            MessageTemplatePageReq messageTemplatePageReq = new MessageTemplatePageReq();
            messageTemplatePageReq.setTemplateName(req.getName());
            MessageTemplateResp byName = messageTemplateApi.getByName(messageTemplatePageReq);
            if (ObjectUtils.isNotEmpty(byName) && ObjectUtils.isNotEmpty(byName.getId())) {
                return "1";
            }
        }
        if (MallTemplateTypeEnum.ROBOT.getCode().equals(req.getType())) {
            RobotSceneNodeReq sceneNodeReq = new RobotSceneNodeReq();
            sceneNodeReq.setSceneName(req.getName());
            RobotSceneNodeResp sceneNodeResp = robotSceneNodeApi.queryByName(sceneNodeReq);
            if (ObjectUtils.isNotEmpty(sceneNodeResp) && ObjectUtils.isNotEmpty(sceneNodeResp.getId())) {
                return "1";
            }
        }
        if (MallTemplateTypeEnum.SYSTEM_MODULE.getCode().equals(req.getType())) {
            Boolean queried = moduleManagementApi.queryUsedPermissionsByName(req.getName());
            if (ObjectUtils.isNotEmpty(queried) && Boolean.TRUE.equals(queried)) {
                return "1";
            }
        }
        return "0";
    }


    private Long saveScene(OrderUseTemplate req) {
        RobotSceneNodeReq robotSceneNodeReq = new RobotSceneNodeReq();
        robotSceneNodeReq.setSceneName(req.getName());
        robotSceneNodeReq.setSceneValue(req.getDesc());
        robotSceneNodeReq.setAccounts(String.join(";", req.getChatbotAccount()));
        return robotSceneNodeApi.saveRobotSceneNode(robotSceneNodeReq);
    }

    private Long saveProcessSetting(Long sceneId, Robot robot) {
        RobotProcessSettingNodeReq robotProcessSettingNodeReq = new RobotProcessSettingNodeReq();
        robotProcessSettingNodeReq.setSceneId(sceneId);
        robotProcessSettingNodeReq.setProcessName(robot.getProcessName());
        robotProcessSettingNodeReq.setProcessValue(robot.getProcessValue());
        return robotProcessSettingNodeApi.saveRobotProcessSetting(robotProcessSettingNodeReq);
    }

    private void saveProcessTrigger(OrderUseTemplate req, String userId, Long sceneId, Robot robot, Long processId) {
        if (Objects.nonNull(robot) && Objects.nonNull(robot.getTrigger())) {
            RobotProcessTriggerNodeReq triggerNodeReq = new RobotProcessTriggerNodeReq();
            triggerNodeReq.setSceneId(sceneId);
            triggerNodeReq.setProcessId(processId);
            triggerNodeReq.setCreate(userId);
            triggerNodeReq.setPrimaryCodeList(robot.getTrigger().getPrimaryCodeList());
            triggerNodeReq.setRegularCode(robot.getTrigger().getRegularCode());

            robotProcessTriggerNodeApi.saveRobotProcessTriggerNodeReq(triggerNodeReq);
        }
    }

    /**
     * 保存流程设计图
     *
     * @param robot            机器人配置
     * @param processId        流程Id
     * @param orderNeedEdit    页面传递需要编辑的指令
     * @param variableNeedEdit 页面传递需要编辑的变量
     */
    private void saveProcessTree(Long sceneId, Robot robot, Long processId,
                                 Map<Long, EditName> orderNeedEdit, Map<Long, EditName> variableNeedEdit,
                                 ProcessJsonUtil processUtil) {

        Map<Long, RobotOrderReq> newOrderMap = processUtil.getOrderDbMap();
        Map<Long, RobotVariableReq> variableMap = processUtil.getVariableDbMap();
        RobotProcessTreeReq treeReq = new RobotProcessTreeReq();
        treeReq.setProcessId(processId);

        processUtil.putForm(insertFrom(robot.getForm()));

        // 处理指令
        String processDes = robot.getProcess().getProcessDes();
        JSONObject jsonObject = JSON.parseObject(processDes);
        JSONArray array = jsonObject.getJSONArray("robotProcessNodeList");
        if (Objects.nonNull(array) && !array.isEmpty()) {
            for (Object object : array) { //每个节点
                JSONObject entity = (JSONObject) object;
                //orderList
                processUtil.changeOrderList(entity);
                //varsList
                processUtil.changeVarsList(entity);
                //buttonList
                processUtil.changeButtonList(entity);
                //contentBody
                processUtil.changeContentBody(entity);
                //conditionList
                processUtil.changeConditionList(entity);
                //verifyList
                processUtil.changeVerifyList(entity);
            }
            jsonObject.put("robotProcessNodeList", array);
        }
        jsonObject.put("sourceIds", processUtil.getCustomMaterialSourceIds());

        processDes = JsonUtils.obj2String(jsonObject);


        //替换变量名称和key
        if (!CollectionUtils.isEmpty(variableNeedEdit) && !CollectionUtils.isEmpty(variableMap)) {
            for (EditName edit : variableNeedEdit.values()) {
                String oldVar = "{{custom-" + edit.getOldName() + "}}";
                String newVar = "{{custom-" + edit.getNewName() + "}}";
                processDes = org.apache.commons.lang3.StringUtils.replace(processDes, oldVar, newVar);

                String oldVarAndId = "{{custom-" + edit.getId() + "&" + edit.getOldName() + "}}";
                String newVarAndId = "{{custom-" + variableMap.get(edit.getId()).getId() + "&" + edit.getNewName() +
                        "}}";
                processDes = org.apache.commons.lang3.StringUtils.replace(processDes, oldVarAndId, newVarAndId);
            }
        }

        // 替换指令名称和key
        for (Map.Entry<Long, EditName> longEditNameEntry : orderNeedEdit.entrySet()) {
            EditName editName = longEditNameEntry.getValue();
            String oldVar = "{{custom-" + editName.getOldName() + "}}";
            StringBuilder oldVarAndId = new StringBuilder();
            oldVarAndId.append("{{custom-").append(editName.getId()).append("&").append(editName.getOldName()).append("}}");
            String replace1 = org.apache.commons.lang3.StringUtils.replace(processDes, oldVar,
                    "{{custom-" + editName.getNewName() + "}}");
            StringBuilder newVarAndId = new StringBuilder();
            if (ObjectUtils.isNotEmpty(newOrderMap.get(editName.getId()))) {
                newVarAndId.append("{{custom-").append(newOrderMap.get(editName.getId()).getId()).append("&").append(editName.getNewName()).append("}}");
                replace1 = org.apache.commons.lang3.StringUtils.replace(replace1, oldVarAndId.toString(),
                        newVarAndId.toString());
            }
            processDes = replace1;
        }

        treeReq.setProcessDes(processDes);
        treeReq.setRobotShortcutButtons(robot.getProcess().getRobotShortcutButtons());
        robotProcessTreeApi.saveProcessDes(treeReq);
        sceneMaterialBinding(sceneId, processId, processUtil);
    }

    /**
     * 绑定素材关系
     *
     * @param sceneId     场景id
     * @param processId   流程id
     * @param processUtil 素材数据
     */
    private void sceneMaterialBinding(Long sceneId, Long processId, ProcessJsonUtil processUtil) {
        List<SceneMaterialReq> list = new ArrayList<>();
        Map<Long, Csp4CustomerImg> imgMap = processUtil.getImgMap();
        if (!CollectionUtils.isEmpty(imgMap)) {
            list.addAll(imgMap.values().stream().map(s -> new SceneMaterialReq().setMaterialId(s.getNewId()).setMaterialType(1).setProcessId(processId))
                    .collect(Collectors.toList()));
        }
        Map<Long, Csp4CustomerAudio> audioMap = processUtil.getAudioMap();
        if (!CollectionUtils.isEmpty(audioMap)) {
            list.addAll(audioMap.values().stream().map(s -> new SceneMaterialReq().setMaterialId(s.getNewId()).setMaterialType(3).setProcessId(processId))
                    .collect(Collectors.toList()));
        }
        Map<Long, Csp4CustomerVideo> videoMap = processUtil.getVideoMap();
        if (!CollectionUtils.isEmpty(videoMap)) {
            list.addAll(videoMap.values().stream().map(s -> new SceneMaterialReq().setMaterialId(s.getNewId()).setMaterialType(2).setProcessId(processId))
                    .collect(Collectors.toList()));
        }

        if (CollectionUtils.isEmpty(list)) return;
        RobotSceneMaterialReq req = new RobotSceneMaterialReq();
        req.setSceneId(sceneId);
        req.setProcessId(processId);
        req.setSceneMaterialReqList(list);
        robotSceneMaterialApi.saveSceneMaterial(req);
    }


    /**
     * 安装订单规则生成订单编号
     *
     * @return 订单编号
     */
    private String getOrderNum(MsType msType) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");
        return "MD0" + msType.getCode() + sdf.format(new Date()) + (int) (Math.random() * 1000);
    }


    //远程添加用户信息
    private void remoteFill(List<Order> records, Map<String, UserInfoVo> cspMap, Map<String, UserInfoVo> customerMap) {
        Set<String> cus = records.stream().map(Order::getCreator).collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(cus)) {
            customerMap.putAll(cspCustomerApi.getDetailByCustomerIds(cus).stream()
                    .map(s -> {
                        UserInfoVo vo = new UserInfoVo();
                        vo.setCspId(s.getCspId());
                        vo.setName(s.getName());
                        vo.setCustomerId(s.getCustomerId());
                        vo.setMail(s.getMail());
                        vo.setPhone(s.getPhone());
                        vo.setMail(s.getMail());
                        return vo;
                    })
                    .collect(Collectors.toMap(UserInfoVo::getCustomerId, Function.identity(), (s1, s2) -> s1)));
        }
        Set<String> csp = records.stream().map(Order::getCspId).collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(csp)) {
            cspMap.putAll(cspApi.getByIdList(csp).stream()
                    .collect(Collectors.toMap(UserInfoVo::getCspId, Function.identity(), (s1, s2) -> s1)));
        }
    }

    @Async
    public void commit(Long theadId) {
        RollbackInfo reviewVo = rollbackMap.remove(theadId);
        if (Objects.isNull(reviewVo)) return;
        log.info("rollbackMap {}", rollbackMap);
        String xid = reviewVo.getXid();
        try {
            GlobalStatus status = GlobalTransactionContext.reload(xid).getStatus();
            log.info("xid:" + xid + " status:" + status.getCode());
            while (status.getCode() < 9) {
                TimeUnit.SECONDS.sleep(10);
                status = GlobalTransactionContext.reload(xid).getStatus();
                log.info("等待全局事务结束中 xid:" + xid + " status:" + status.getCode());
            }
            if (GlobalStatus.Committed.equals(status) || GlobalStatus.Finished.equals(status)) {
                log.info("全局事务已提交，开始送审 xid:" + xid + " status:" + status.getCode());
                tempStoreMaterialApi.commit(reviewVo.getFileTheadId());
            } else {
                log.warn("全局事务未正常提交,请检查日志手动处理问题 xid:" + xid + " status:" + status.getCode());
            }
        } catch (TransactionException e) {
            log.error("获取事务状态错误,请检查日志手动处理问题 xid:" + xid, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    @Async
    public void revokeReview(Long id) {
        RollbackInfo reviewVo = rollbackMap.remove(id);
        if (Objects.isNull(reviewVo)) return;
        tempStoreMaterialApi.cancel(reviewVo.getFileTheadId());
    }

    @Override
    public MsSummaryDetailVO getSummaryById(Long orderId) {
        MsSummaryDetailVO result = new MsSummaryDetailVO();
        Order order = getById(orderId);
        if (Objects.isNull(order)) {
            return result;
        }
        String goodsSnapshot = getOrderSnapshot(orderId);
        if (!StringUtils.hasLength(goodsSnapshot)) {
            return result;
        }
        MsSummarySnapshot snapshot = JSON.parseObject(goodsSnapshot, MsSummarySnapshot.class);
        BeanUtils.copyProperties(snapshot.getSummary(), result);
        //获取企业名称
        List<UserInfoVo> userInfoVos = cspApi.getByIdList(Collections.singletonList(snapshot.getSummary().getCspId()));
        if (!CollectionUtil.isEmpty(userInfoVos)) {
            result.setEnterpriseName(userInfoVos.get(0).getEnterpriseName());
            result.setCreatorName(userInfoVos.get(0).getName());
            result.setEnterpriseAccountName(userInfoVos.get(0).getEnterpriseAccountName());
        }
        if (Objects.nonNull(snapshot.getIntroduce())) {
            result.setIntroduce(snapshot.getIntroduce().getIntroduce());
        }
        if (Objects.nonNull(snapshot.getContent())) {
            result.setTemplateJson(snapshot.getContent().getMsJson());
        }
        //获取表单封面
        if (MsType.H5_FORM.equals(order.getTempType()) && snapshot.getSummary() != null) {
            String msId = snapshot.getSummary().getMsId();
            if (StringUtils.hasLength(msId)) {
                H5Info detail = h5Api.getDetailForSummery(Long.parseLong(msId));
                if (detail != null) {
                    result.setFormCover(detail.getFormCover());
                }
            }
        }
        return result;
    }

    @Override
    public PageResult<ManageOrderPage> orderPage(OperationOrderPageReq req) {
        MsType tempType = req.getTempType();
        List<MsType> supportTypes = Arrays.asList(MsType.CUSTOM_ORDER, MsType.SYSTEM_MODULE);//管理平台的订单运营只支持自定义指令和组件
        if (tempType == null || supportTypes.contains(tempType)) {
            Page<Order> page = PageSupport.getPage(Order.class, req.getPageNo(), req.getPageSize());

            //查询订单表
            LambdaUpdateWrapper<Order> wrapper = new LambdaUpdateWrapper<Order>()
                    .eq(Objects.nonNull(req.getPayStatus()), Order::getPayStatus, req.getPayStatus())
                    .eq(Objects.nonNull(req.getTempType()), Order::getTempType, req.getTempType())
                    .like(Objects.nonNull(req.getGoodsName()), Order::getGoodsName, req.getGoodsName())
                    .in(Order::getTempType, supportTypes)
                    .orderByDesc(Order::getCreateTime);
            page(page, wrapper);

            List<Order> records = page.getRecords();
            Set<String> cspIds = new HashSet<>();
            Set<String> customerIds = new HashSet<>();
            for (Order order : records) {
                cspIds.add(order.getCspId());
                customerIds.add(order.getCreator());
            }
            //查询csp和customer信息
            List<UserInfoVo> byIdList = cspApi.getByIdList(cspIds);
            Map<String, UserInfoVo> cspMap = byIdList.stream().collect(Collectors.toMap(UserInfoVo::getCspId, Function.identity(), (s1, s2) -> s1));
            List<CustomerDetailResp> detailByCustomerIds = cspCustomerApi.getDetailByCustomerIds(customerIds);
            Map<String, CustomerDetailResp> customerMap = detailByCustomerIds.stream().collect(Collectors.toMap(CustomerDetailResp::getCustomerId, Function.identity(), (s1, s2) -> s1));


            //封装数据
            List<ManageOrderPage> manageOrderPages = page.getRecords().stream().map(s -> {
                ManageOrderPage m = new ManageOrderPage();
                BeanUtils.copyProperties(s, m);
                if (cspMap.containsKey(s.getCspId())) {
                    UserInfoVo userInfoVo = cspMap.get(s.getCspId());
                    m.setCspName(userInfoVo.getName());
                    m.setCspPhone(userInfoVo.getPhone());
                }
                if (customerMap.containsKey(s.getCreator())) {
                    CustomerDetailResp customer = customerMap.get(s.getCreator());
                    m.setCreatorName(customer.getName());
                    m.setCreatorPhone(customer.getPhone());
                }
                return m;
            }).collect(Collectors.toList());

            return new PageResult<>(manageOrderPages, page.getTotal());

        } else {
            throw new BizException("该订单类型不支持！");
        }
    }

    @Override
    public void orderIsPay(Long orderId) {
        Order order = checkOrder(orderId);
        if (order.getPayStatus().equals(PayStatus.wait)) {
            order.setPayStatus(PayStatus.complete);
            order.setPayTime(new Date());
            updateById(order);
            //如果是组件，直接使用模版，更改模版状态；
            if (MsType.SYSTEM_MODULE.equals(order.getTempType())) {
                activateModule(order.getGoodsId(),order.getCreator());
            }
        } else {
            throw new BizException("订单状态不正确，订单完成失败！");
        }
    }


    /**
     * 激活用户的组件使用权限
     * @param mssId 作品ID
     */
    private void activateModule(Long mssId,String customerId) {
        MsSummary summary = summaryService.getById(mssId);
        if(summary != null && summary.getMsType().equals(MsType.SYSTEM_MODULE)){
            ModuleManagementItem moduleManagementItem = moduleManagementApi.queryById(Long.parseLong(summary.getMsId()));
            if(moduleManagementItem != null){
                moduleManagementItem.setCustomerId(customerId);
                systemModule(moduleManagementItem);
            }
        }
    }

    @Override
    public void orderCancel(Long orderId) {
        Order order = checkOrder(orderId);
        if (order.getPayStatus().equals(PayStatus.wait)) {
            order.setPayStatus(PayStatus.cancel);
            updateById(order);
        } else {
            throw new BizException("订单状态不正确，取消订单失败！");
        }
    }

    @Override
    public void orderRemark(OperationOrderRemarkReq req) {
        Order order = checkOrder(req.getOrderId());
        order.setRemake(req.getRemake());
        updateById(order);
    }

    @Override
    public void checkModulePermission() {
        List<Order> list = lambdaQuery().eq(Order::getPayStatus, PayStatus.complete).eq(Order::getTempType, MsType.SYSTEM_MODULE).list();
        if (!CollectionUtils.isEmpty(list)) {
            for (Order order : list) {
                activateModule(order.getGoodsId(),order.getCreator());
            }
        }
    }

    private Order checkOrder(Long orderId) {
        Order order = getById(orderId);
        if (Objects.isNull(order)) {
            throw new BizException("订单不存在！");
        }
        return order;
    }

    @Data
    @Accessors(chain = true)
    private static class RollbackInfo {
        private long orderTheadId;
        private long fileTheadId;
        private String xid;
    }

}

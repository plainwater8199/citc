package com.citc.nce.materialSquare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.authcenter.csp.CspApi;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.materialSquare.PromotionType;
import com.citc.nce.materialSquare.dao.MsManageActivityLiMapper;
import com.citc.nce.materialSquare.entity.MsManageActivityContent;
import com.citc.nce.materialSquare.entity.MsManageActivityLi;
import com.citc.nce.materialSquare.service.IMsManageActivityContentService;
import com.citc.nce.materialSquare.service.IMsManageActivityLiService;
import com.citc.nce.materialSquare.vo.MsSummaryDiscountPrice;
import com.citc.nce.materialSquare.vo.activity.*;
import com.citc.nce.materialSquare.vo.activity.resp.MsManageActivityContentResp;
import com.citc.nce.robot.api.materialSquare.MsSummaryApi;
import com.citc.nce.robot.api.materialSquare.emums.MsSource;
import com.citc.nce.robot.api.materialSquare.emums.MsType;
import com.citc.nce.robot.api.materialSquare.entity.MsSummary;
import com.citc.nce.robot.api.materialSquare.vo.summary.SummaryInfo;
import com.citc.nce.util.PageSupport;
import com.citc.nce.authcenter.Utils.UserInfoUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 素材广场_后台管理_参与活动的素材 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2024-05-14 02:05:36
 */
@Service
public class MsManageActivityLiServiceImpl extends ServiceImpl<MsManageActivityLiMapper, MsManageActivityLi> implements IMsManageActivityLiService {

    @Resource
    private MsSummaryApi msSummaryApi;
    @Resource
    private IMsManageActivityContentService activityContentService;
    @Resource
    private UserInfoUtil userInfoUtil;
    @Resource
    private CspApi cspApi;

    @Override
    public PageResult<LiPageResult> liPage(LiPageQuery pageQuery) {
        MsManageActivityContent activityContent = activityContentService.getById(pageQuery.getMsActivityContentId());
        if (Objects.isNull(activityContent)) {
            throw new BizException("活动方案已删除");
        }
        Page<MsManageActivityLi> page = PageSupport.getPage(MsManageActivityLi.class, pageQuery);
        page = page(page, new LambdaQueryWrapper<MsManageActivityLi>()
                .eq(MsManageActivityLi::getMsActivityContentId, pageQuery.getMsActivityContentId())
                .orderByDesc(MsManageActivityLi::getCreateTime)
        );
        List<MsManageActivityLi> records = page.getRecords();
        LinkedList<LiPageResult> list = new LinkedList<>();
        if(!CollectionUtils.isEmpty(records)){
            Map<Long, MsSummary> summaryMap =
                    mapByIds(records.stream().map(MsManageActivityLi::getMssId).collect(Collectors.toList()));

            for (MsManageActivityLi li : records) {
                MsSummary summary = summaryMap.get(li.getMssId());
                if (summary == null) continue;
                LiPageResult result = new LiPageResult();
                result.setName(summary.getName());
                result.setMsActivityLiId(li.getMsActivityLiId());
                result.setIdNO(summary.getMsNum());
                result.setFrom(StringUtils.hasLength(summary.getCspId()) ? MsSource.CSP_PLATFORM.getAlias() :
                        MsSource.OPERATE_PLATFORM.getAlias());
                result.setCreator(Optional.ofNullable(summary.getCspId()).orElse(summary.getCreator()));
                result.setOriginalPrice(summary.getOriginalPrice());
                result.setDiscountPrice(getDiscountPrice(activityContent.getPromotionType(),
                        activityContent.getDiscountRate(), summary.getOriginalPrice()));
                list.add(result);
            }
        }

        //填充csp名称，企业账户名称
        userInfoUtil.fillEnterpriseName(list);
        userInfoUtil.fillMangeName(list);
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public List<MsSummaryDiscountPrice> getDiscountPrice(Long mssId) {
        MsSummary summary = msSummaryApi.getByMssId(mssId);
        if (Objects.isNull(summary)) {
            throw new BizException("作品已删除");
        }
        BigDecimal originalPrice = summary.getOriginalPrice();

        List<MsSummaryDiscountPrice> list = getBaseMapper().selectActivityLiDiscountPriceInfo(mssId);
        list.forEach(s -> s.setDiscountPrice(getDiscountPrice(s.getPromotionType(), s.getDiscountRate(),
                originalPrice)));
        return list;
    }

    @Override
    public MsSummaryDiscountPrice getDiscountPrice(Long mssId, Long msActivityContentId) {
        MsSummary summary = msSummaryApi.getByMssId(mssId);
        if (Objects.isNull(summary)) {
            throw new BizException("作品已删除");
        }
        MsManageActivityContent activityContent = activityContentService.getById(msActivityContentId);
        if (Objects.isNull(activityContent)) {
            throw new BizException("活动方案已删除");
        }

        BigDecimal originalPrice = summary.getOriginalPrice();
        BigDecimal price = getDiscountPrice(activityContent.getPromotionType(), activityContent.getDiscountRate(),
                originalPrice);
        return getMsSummaryDiscountPrice(mssId, originalPrice, price, activityContent);
    }

    @Override
    public MsSummaryDiscountPrice getEffectDiscountPrice(Long mssId, Long msActivityContentId) {
        MsSummary summary = msSummaryApi.getByMssId(mssId);
        if (Objects.isNull(summary)) {
            throw new BizException("作品已删除");
        }
        MsManageActivityContent activityContent = activityContentService.getById(msActivityContentId);
        if (Objects.isNull(activityContent)) {
            throw new BizException("活动方案已删除");
        }

        BigDecimal originalPrice = summary.getOriginalPrice();
        BigDecimal price = getEffectDiscountPrice(activityContent, originalPrice);
        return getMsSummaryDiscountPrice(mssId, originalPrice, price, activityContent);
    }


    @Override
    public MsSummaryDiscountPrice getLastEffectDiscountPrice(Long mssId) {
        MsSummary summary = msSummaryApi.getByMssId(mssId);
        if (Objects.isNull(summary)) {
            throw new BizException("作品已删除");
        }
        //作品原价
        BigDecimal originalPrice = summary.getOriginalPrice();
        //作品参加的活动
        List<MsSummaryDiscountPrice> list = getBaseMapper().selectActivityLiDiscountPriceInfo(mssId);
        if (CollectionUtils.isEmpty(list)) {
            //作品没有参加活动
            return useOriginalPrice(originalPrice);
        }
        //得到最近有效活动
        List<Long> contentIds =
                list.stream().map(MsSummaryDiscountPrice::getMsActivityContentId).collect(Collectors.toList());
        MsManageActivityContent activityContent = activityContentService.getLastEffectActivityContent(contentIds);
        //计算活动价格
        BigDecimal discountPrice = getDiscountPrice(activityContent.getPromotionType(),
                activityContent.getDiscountRate(), originalPrice);
        return getMsSummaryDiscountPrice(mssId, originalPrice, discountPrice, activityContent);
    }


    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void addLi(PutAndRemove putAndRemove) {
        Long msActivityContentId = putAndRemove.getMsActivityContentId();
        MsManageActivityContent activityContent = activityContentService.getById(msActivityContentId);
        if (activityContent != null) {
            if (activityContent.getEndTime().after(new Date())) {
                //删除列表
                if (!CollectionUtils.isEmpty(putAndRemove.getRemoveMssIds())) {
                    remove(new LambdaQueryWrapper<MsManageActivityLi>()
                            .eq(MsManageActivityLi::getMsActivityContentId, msActivityContentId)
                            .in(MsManageActivityLi::getMssId, putAndRemove.getRemoveMssIds()));
                }
                //新增列表
                if (!CollectionUtils.isEmpty(putAndRemove.getAddMssIds())) {
//                    boolean exists = lambdaQuery().eq(MsManageActivityLi::getMsActivityContentId,
//                                    msActivityContentId)
//                            .in(MsManageActivityLi::getMssId, putAndRemove.getAddMssIds())
//                            .exists();
//                    if(exists) {
//                        throw new BizException("选择的活动已经在作品列表中");
//                    }
                    List<MsManageActivityLi> existsList = lambdaQuery().eq(MsManageActivityLi::getMsActivityContentId,
                            msActivityContentId).list();

                    List<MsManageActivityLi> saveList =
                            putAndRemove.getAddMssIds().stream().map(s -> new MsManageActivityLi().setMssId(s).setMsActivityContentId(msActivityContentId)).collect(Collectors.toList());
                    if(!CollectionUtils.isEmpty(existsList)) {
                        List<Long> existingMssIds = existsList.stream()
                                .map(MsManageActivityLi::getMssId)
                                .collect(Collectors.toList());
                        saveList = saveList.stream()
                                .filter(item -> !existingMssIds.contains(item.getMssId()))
                                .collect(Collectors.toList());
                    }
                    if (CollectionUtils.isEmpty(saveList)) return;
                    saveBatch(saveList);
                }
            } else {
                throw new BizException("活动方案已过期");
            }
        } else {
            throw new BizException("活动方案已删除");
        }
    }


    /**
     * 一个作品有可能同时在多个生效的活动中（有效期重叠），此时按最新的活动中的价格进行显示。
     * @param mssId 作品ID
     * @return
     */
    @Override
    public MsManageActivityContent getActivityContentByMssId(Long mssId) {
        //获取活动方案
        List<MsManageActivityLi> activityLis = this.lambdaQuery().eq(MsManageActivityLi::getMssId, mssId).list();
        if (!CollectionUtils.isEmpty(activityLis)) {
          List<Long> msActivityContentIds = activityLis.stream().map(MsManageActivityLi::getMsActivityContentId).collect(Collectors.toList());
            Date date = new Date();
            //获取活动内容

            return activityContentService.lambdaQuery().in(MsManageActivityContent::getMsActivityContentId,
                                    msActivityContentIds)
                            .le(MsManageActivityContent::getStartTime, date)
                            .ge(MsManageActivityContent::getEndTime, date)
                            .orderByDesc(MsManageActivityContent::getCreateTime)
                            .last("limit 1")
                            .one();
        }
        return null;
    }

    @Override
    public List<SummaryInfoForActivityContent> getSummaryInfoForActivity(Long msActivityContentId) {
        List<SummaryInfoForActivityContent> summaryList = new ArrayList<>();
        List<MsManageActivityLi> list = lambdaQuery().eq(MsManageActivityLi::getMsActivityContentId,
                        msActivityContentId)
                .list();
        if(!CollectionUtils.isEmpty(list)){
            Map<Long, SummaryInfo> longSummaryInfoMap = msSummaryApi.queryOnLineSummaryInfo(1);
            for(MsManageActivityLi li : list){
                if(longSummaryInfoMap.containsKey(li.getMssId())){
                    SummaryInfo summaryInfo = longSummaryInfoMap.get(li.getMssId());
                    SummaryInfoForActivityContent summaryInfoForActivityLi = new SummaryInfoForActivityContent();
                    summaryInfoForActivityLi.setMssId(li.getMssId());
                    summaryInfoForActivityLi.setName(summaryInfo.getName());
                    summaryInfoForActivityLi.setMssNum(summaryInfo.getMsNum());
                    summaryList.add(summaryInfoForActivityLi);
                }
            }
        }
        return summaryList;
    }

    @Override
    public Map<Long, Integer> queryCountForContentId() {
        Map<Long, Integer> map = new HashMap<>();
        List<MsManageActivityLi> list = lambdaQuery().list();
        if(!CollectionUtils.isEmpty(list)){
            for(MsManageActivityLi li : list){
                if(map.containsKey(li.getMsActivityContentId())){
                    map.put(li.getMsActivityContentId(), map.get(li.getMsActivityContentId()) + 1);

                }else{
                    map.put(li.getMsActivityContentId(), 1);
                }
            }
        }
        return map;
    }

    @Override
    public List<LiDetailResult> listByLiId(MssForLiReq req) {
        MsManageActivityContent activityContent = activityContentService.getById(req.getMsActivityContentId());
        if (Objects.isNull(activityContent)) {
            throw new BizException("活动方案已删除");
        }
        List<MsManageActivityLi> records = this.lambdaQuery().eq(MsManageActivityLi::getMsActivityContentId, req.getMsActivityContentId())
                .orderByDesc(MsManageActivityLi::getCreateTime).list();
        LinkedList<LiDetailResult> list = new LinkedList<>();
        if(!CollectionUtils.isEmpty(records)){
            Map<Long, MsSummary> summaryMap =
                    mapByIds(records.stream().map(MsManageActivityLi::getMssId).collect(Collectors.toList()));
            if(!CollectionUtils.isEmpty(summaryMap)){
                List<String> cspIds = new ArrayList<>();
                summaryMap.values().forEach(s -> cspIds.add(s.getCspId()));
                Map<String, UserInfoVo> map = cspApi.getByIdList(cspIds).stream()
                        .collect(Collectors.toMap(UserInfoVo::getUserId, Function.identity(), (s1, s2) -> s2));
                for (MsManageActivityLi li : records) {
                    MsSummary summary = summaryMap.get(li.getMssId());
                    if (summary != null) {
                        LiDetailResult result = new LiDetailResult();
                        result.setName(summary.getName());
                        result.setMsNum(summary.getMsNum());
                        result.setMssId(summary.getMssId());
                        result.setName(summary.getName());
                        result.setMsDesc(summary.getMsDesc());
                        result.setMsType(summary.getMsType());
                        result.setOriginalPrice(summary.getOriginalPrice());
                        result.setDiscountPrice(getDiscountPrice(activityContent.getPromotionType(),activityContent.getDiscountRate(),summary.getOriginalPrice()));
                        result.setAuditStatus(summary.getAuditStatus());
                        result.setAuditFailResult(summary.getAuditFailResult());
                        result.setTemplateStatus(summary.getTemplateStatus());
                        result.setPayType(summary.getPayType());
                        result.setWorksLibraryStatus(summary.getWorksLibraryStatus());
                        result.setLikesCount(summary.getLikesCount());
                        result.setViewCount(summary.getViewCount());
                        result.setCoverType(summary.getCoverType());
                        result.setCoverFile(summary.getCoverFile());
                        result.setPublishTime(summary.getPublishTime());
                        result.setCreateTime(summary.getCreateTime());
                        result.setCreator(summary.getCspId());
                        result.setFrom(StringUtils.hasLength(summary.getCspId()) ? MsSource.CSP_PLATFORM.getAlias() :
                                MsSource.OPERATE_PLATFORM.getAlias());
                        if(MsType.CUSTOM_ORDER.equals(summary.getMsType()) || MsType.SYSTEM_MODULE.equals(summary.getMsType())){
                            result.setCreatorName("硬核桃WalnutHardcore");
                            result.setEnterpriseName("硬核桃WalnutHardcore");
                        }else{
                            result.setCreatorName(map.get(summary.getCspId()).getName());
                            result.setEnterpriseName(map.get(summary.getCspId()).getEnterpriseName());
                        }
                        list.add(result);
                    }
                }
                //排序方式，1：创建时间升序，2：创建时间降序，3：浏览量降序，4：点赞量降序
                if(req.getSortType() != null){
                    if(req.getSortType() == 2){
                        list.sort(Comparator.comparing(LiDetailResult::getCreateTime).reversed());
                    }else if(req.getSortType() == 3){
                        list.sort(Comparator.comparing(LiDetailResult::getViewCount).reversed());
                    }else if(req.getSortType() == 4){
                        list.sort(Comparator.comparing(LiDetailResult::getLikesCount).reversed());
                    }else{
                        list.sort(Comparator.comparing(LiDetailResult::getCreateTime));
                    }
                }else{
                    list.sort(Comparator.comparing(LiDetailResult::getCreateTime));
                }

            }
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSummaryForActivity(List<Long> mssIds) {
        if (!CollectionUtils.isEmpty(mssIds)) {
            List<MsManageActivityLi> result = lambdaQuery().in(MsManageActivityLi::getMssId, mssIds).list();
            if(!CollectionUtils.isEmpty(result)){
                removeBatchByIds(result);
            }
        }
    }

    @Override
    public MsManageActivityContent getActivityContentById(Long msActivityContentId) {
        MsManageActivityLi activityLi = this.getById(msActivityContentId);
        if (activityLi != null) {
            //获取活动内容
            return activityContentService.lambdaQuery().eq(MsManageActivityContent::getMsActivityContentId,
                            activityLi.getMsActivityContentId())
                    .one();
        }
        return null;
    }

    /**
     * 计算活动价格
     *
     * @param activityContent 活动方案
     * @param originalPrice   原价
     */

    private BigDecimal getEffectDiscountPrice(MsManageActivityContent activityContent, BigDecimal originalPrice) {
        //活动未开始使用原价
        long l = new Date().getTime();
        if (l > activityContent.getEndTime().getTime() || l < activityContent.getStartTime().getTime()) {
            return originalPrice;
        }
        return getDiscountPrice(activityContent.getPromotionType(), activityContent.getDiscountRate(), originalPrice);
    }

    /**
     * 计算折扣价格
     *
     * @param promotionType 折扣类型
     * @param discountRate  折扣率
     * @param originalPrice 原价
     */
    private BigDecimal getDiscountPrice(PromotionType promotionType, Double discountRate, BigDecimal originalPrice) {
        switch (promotionType) {
            case FREE:
                return new BigDecimal("0.00");
            case NONE:
                return originalPrice;
            case DISCOUNT:
                return multiplyBigDecimalAndDouble(originalPrice, discountRate);
            default:
                throw new BizException("活动方案配置错误");
        }
    }

    /**
     * 将 BigDecimal 和 Double 进行乘法运算
     *
     * @param bigDecimalValue {@code BigDecimal} 值
     * @param doubleValue     {@code Double} 值
     * @return 乘法结果 {@code BigDecimal}
     */
    public static BigDecimal multiplyBigDecimalAndDouble(BigDecimal bigDecimalValue, Double doubleValue) {
        // 将 Double 转换为 BigDecimal
        BigDecimal doubleAsBigDecimal = BigDecimal.valueOf(doubleValue);

        // 进行乘法运算并返回结果
        return bigDecimalValue.multiply(doubleAsBigDecimal).setScale(2, RoundingMode.HALF_UP);
    }

    private Map<Long, MsSummary> mapByIds(List<Long> mssIds) {
        return msSummaryApi.listByIds(mssIds).stream().collect(Collectors.toMap(MsSummary::getMssId,
                Function.identity()));
    }


    /**
     * 封装返回对象
     *
     * @param mssId           作品id
     * @param originalPrice   原价
     * @param discountPrice   折扣价
     * @param activityContent 活动方案
     * @return
     */
    private @NotNull MsSummaryDiscountPrice getMsSummaryDiscountPrice(Long mssId, BigDecimal
            originalPrice, BigDecimal discountPrice, MsManageActivityContent activityContent) {
        MsSummaryDiscountPrice entity = new MsSummaryDiscountPrice();
        entity.setMssId(mssId);
        entity.setOriginalPrice(originalPrice);
        entity.setDiscountPrice(discountPrice);
        entity.setMsActivityLiId(getLiId(mssId, activityContent.getMsActivityContentId()));
        entity.setMsActivityCreateTime(activityContent.getCreateTime());
        entity.setMsActivityCreator(activityContent.getCreator());
        entity.setMsActivityContentId(activityContent.getMsActivityContentId());
        entity.setPromotionType(activityContent.getPromotionType());
        entity.setDiscountRate(activityContent.getDiscountRate());
        entity.setActivityContent(activityContent);
        return entity;
    }

    private Long getLiId(Long mssId, Long msActivityContentId) {
        return this.lambdaQuery().eq(MsManageActivityLi::getMssId, mssId)
                .eq(MsManageActivityLi::getMsActivityContentId, msActivityContentId)
                .one().getMsActivityLiId();
    }

    /**
     * 根据原价封装对象
     *
     * @param originalPrice 原价
     * @return 折扣价格
     */
    private MsSummaryDiscountPrice useOriginalPrice(BigDecimal originalPrice) {
        MsSummaryDiscountPrice discountPrice = new MsSummaryDiscountPrice();
        discountPrice.setOriginalPrice(originalPrice);
        discountPrice.setDiscountPrice(originalPrice);
        discountPrice.setMsActivityContentId(-1L);
        return discountPrice;
    }
}

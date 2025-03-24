package com.citc.nce.materialSquare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.materialSquare.MsManageActivityLiApi;
import com.citc.nce.materialSquare.PromotionType;
import com.citc.nce.materialSquare.dao.MsManageActivityContentMapper;
import com.citc.nce.materialSquare.entity.MsManageActivityContent;
import com.citc.nce.materialSquare.service.IMsManageActivityContentService;
import com.citc.nce.materialSquare.vo.MsSummaryDiscountPrice;
import com.citc.nce.materialSquare.vo.activity.ContentAdd;
import com.citc.nce.materialSquare.vo.activity.ContentPageQuery;
import com.citc.nce.materialSquare.vo.activity.ContentUpdate;
import com.citc.nce.materialSquare.vo.activity.MsPrice;
import com.citc.nce.materialSquare.vo.activity.resp.MsManageActivityContentResp;
import com.citc.nce.robot.api.materialSquare.MsSummaryApi;
import com.citc.nce.robot.api.materialSquare.entity.MsSummary;
import com.citc.nce.robot.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 素材广场_后台管理_活动方案 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2024-05-14 02:05:31
 */
@Service
@RequiredArgsConstructor
public class MsManageActivityContentServiceImpl extends ServiceImpl<MsManageActivityContentMapper, MsManageActivityContent> implements IMsManageActivityContentService {

    private final MsSummaryApi msSummaryApi;

    private final MsManageActivityLiApi activityLiApi;

    @Override
    public void addContent(ContentAdd contentAdd) {
        checkName(null, contentAdd.getName());

        MsManageActivityContent content = new MsManageActivityContent();
        BeanUtils.copyProperties(contentAdd, content);
        save(content);
    }


    @Override
    public void updateContent(ContentUpdate contentUpdate) {
        checkName(contentUpdate.getMsActivityContentId(), contentUpdate.getName());
        MsManageActivityContent content = getById(contentUpdate.getMsActivityContentId());
        if (Objects.isNull(content)) {
            throw new BizException("活动方案已被删除，不能编辑");
        }
        BeanUtils.copyProperties(contentUpdate, content);
        content.setUpdater(null);
        content.setUpdateTime(null);
        updateById(content);
    }

    @Override
    public PageResult<MsManageActivityContentResp> pageContent(ContentPageQuery pageQuery,Map<Long, Integer> countMap) {
        IPage<MsManageActivityContent> iPage = new Page<>(pageQuery.getPageNo(), pageQuery.getPageSize());
        page(iPage, new LambdaQueryWrapper<MsManageActivityContent>()
                .like(StringUtils.hasLength(pageQuery.getName()), MsManageActivityContent::getName, pageQuery.getName()).orderByDesc(MsManageActivityContent::getCreateTime));
        List<MsManageActivityContent> records = iPage.getRecords();
        List<MsManageActivityContentResp> result = new ArrayList<>();
        if(!CollectionUtils.isEmpty(records)){
//            List<Long> contentIds = records.stream().map(MsManageActivityContent::getMsActivityContentId).collect(Collectors.toList());
            records.forEach(s -> {
                MsManageActivityContentResp contentResp = new MsManageActivityContentResp();
                BeanUtils.copyProperties(s, contentResp);
                contentResp.setMsCount(countMap.getOrDefault(s.getMsActivityContentId(), 0));
                contentResp.setCreateTime(DateUtil.dateToString(s.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));
                contentResp.setStartTime(DateUtil.dateToString(s.getStartTime(),"yyyy-MM-dd HH:mm:ss"));
                contentResp.setEndTime(DateUtil.dateToString(s.getEndTime(),"yyyy-MM-dd HH:mm:ss"));
                if(s.getStartTime().after(new Date())){
                    contentResp.setStatus("待生效");
                }else if(s.getEndTime().after(new Date())){
                    contentResp.setStatus("生效中");
                }else{
                    contentResp.setStatus("已过期");
                }
                result.add(contentResp);
                });
        }
        return new PageResult<>(result, iPage.getTotal());
    }

    @Override
    public MsManageActivityContent getLastEffectActivityContent(List<Long> contentIds) {
        List<MsManageActivityContent> contents = listByIds(contentIds);
        long current = System.currentTimeMillis();
        contents = contents.stream().filter(s -> s.getStartTime().getTime() < current && s.getEndTime().getTime() > current)
                .sorted(Comparator.comparing(MsManageActivityContent::getCreateTime).reversed()).collect(Collectors.toList());
        return contents.isEmpty() ? null : contents.get(0);
    }

    @Override
    public MsManageActivityContentResp queryContentById(Long msActivityContentId) {
        MsManageActivityContent content = getById(msActivityContentId);
        if(!Objects.isNull(content)){
            MsManageActivityContentResp resp = new MsManageActivityContentResp();
            BeanUtils.copyProperties(content, resp);
            resp.setCreateTime(DateUtil.dateToString(content.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));
            resp.setStartTime(DateUtil.dateToString(content.getStartTime(),"yyyy-MM-dd HH:mm:ss"));
            resp.setEndTime(DateUtil.dateToString(content.getEndTime(),"yyyy-MM-dd HH:mm:ss"));
            if(content.getStartTime().after(new Date())){
                resp.setStatus("待生效");
            }else if(content.getEndTime().after(new Date())){
                resp.setStatus("生效中");
            }else{
                resp.setStatus("已过期");
            }
            return resp;
        }else{
            throw new BizException("活动方案不存在");
        }
    }

    @Override
    public void refreshToMQForMsActivity(Long msActivityId) {
        //如果没有指定活动，则表示查询第二天待生效的活动方案
        //如果指定活动方案，则表示对指定活动方案进行处理
          // -- 新增：生效时间如果是在当天，则加入MQ
          // -- 更新：生效时间如果是在当天，则加入MQ
          // -- 删除：不做处理，消息在消费是会再次检查
        //1、查询第二天待生效的方案
    }

    @Override
    public List<MsManageActivityContentResp> activityContentlist() {
        List<MsManageActivityContent> records = this.list();
        List<MsManageActivityContentResp> result = new ArrayList<>();
        if(!CollectionUtils.isEmpty(records)){
            records.forEach(s -> {
                MsManageActivityContentResp contentResp = new MsManageActivityContentResp();
                BeanUtils.copyProperties(s, contentResp);
                contentResp.setCreateTime(DateUtil.dateToString(s.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));
                contentResp.setStartTime(DateUtil.dateToString(s.getStartTime(),"yyyy-MM-dd HH:mm:ss"));
                contentResp.setEndTime(DateUtil.dateToString(s.getEndTime(),"yyyy-MM-dd HH:mm:ss"));
                if(s.getStartTime().after(new Date())){
                    contentResp.setStatus("待生效");
                    result.add(contentResp);
                }else if(s.getEndTime().after(new Date())){
                    contentResp.setStatus("生效中");
                    result.add(contentResp);
                }
            });
        }
        return result;
    }

    @Override
    public MsPrice getPrice(Long mssId, Long msActivityContentId, String pType) {
        //1、查看作品是否存在
        MsPrice msPrice = new MsPrice();
        MsSummary summary = msSummaryApi.getByMssId(mssId);
        if(summary != null){
            MsManageActivityContent content;
            //2、如果活动不为空，并且价格展示为活动价格（discount），
            if((msActivityContentId!= null && msActivityContentId>0) && "discount".equals(pType)){
                //2.1、查看活动是否存在
                content = getValidActivityContent(msActivityContentId);
            }else{
                //查看作品是否在有效活动中
                content = activityLiApi.getActivityContentByMssId(mssId);
            }
            if(content != null){
                msPrice.setPrice(getDiscountPrice(summary.getOriginalPrice(), content.getPromotionType(), content.getDiscountRate()));
                msPrice.setActivityContent(content);
            }else{
                msPrice.setPrice(summary.getOriginalPrice());
            }
        }
        return msPrice;
    }

    /**
     * 获取有效的活动方案
     * @param msActivityContentId 活动方案id
     * @return 活动方案
     */
    private MsManageActivityContent getValidActivityContent(Long msActivityContentId) {
        MsManageActivityContent content = getById(msActivityContentId);
        if(content != null && content.getStartTime().before(new Date()) && content.getEndTime().after(new Date())) {
            return content;
        }else{
            return null;
        }
    }

    /**
     * 根据促销类型和折扣率计算折扣价格
     * @param originalPrice 原价
     * @param promotionType 促销类型
     * @param discountRate 折扣率
     * @return 折扣价格
     */
    private BigDecimal getDiscountPrice(BigDecimal originalPrice, PromotionType promotionType, Double discountRate) {
        if(PromotionType.FREE.equals(promotionType)){
            return new BigDecimal(0);
        }else if(PromotionType.DISCOUNT.equals(promotionType)){
            return multiplyBigDecimalAndDouble(originalPrice, discountRate);
        }else{
            return originalPrice;
        }
    }

    private BigDecimal multiplyBigDecimalAndDouble(BigDecimal originalPrice, Double discountRate) {
        // 将 Double 转换为 BigDecimal
        BigDecimal doubleAsBigDecimal = BigDecimal.valueOf(discountRate);
        // 进行乘法运算并返回结果
        return originalPrice.multiply(doubleAsBigDecimal).setScale(2, RoundingMode.HALF_UP);
    }

    //检查名称是否重复

    private void checkName(Long msActivityContentId, String name) {
        if (exists(new LambdaQueryWrapper<MsManageActivityContent>()
                .eq(MsManageActivityContent::getName, name)
                .ne(Objects.nonNull(msActivityContentId), MsManageActivityContent::getMsActivityContentId, msActivityContentId))) {
            throw new BizException("活动方案名称不能重复");
        }
    }
}

package com.citc.nce.auth.meal.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.meal.dao.CspMealMapper;
import com.citc.nce.auth.meal.domain.CspMeal;
import com.citc.nce.auth.meal.domain.CspMealContract;
import com.citc.nce.auth.meal.domain.CspMealContractAssociation;
import com.citc.nce.auth.meal.enums.CspMealStatus;
import com.citc.nce.auth.meal.enums.CspMealType;
import com.citc.nce.auth.meal.service.ICspMealContractAssociationService;
import com.citc.nce.auth.meal.service.ICspMealService;
import com.citc.nce.auth.meal.vo.contract.CspContractMeal;
import com.citc.nce.auth.meal.vo.meal.CspMealPageInfo;
import com.citc.nce.auth.meal.vo.meal.CspMealPageQuery;
import com.citc.nce.common.core.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * csp用户套餐表 服务实现类
 * </p>
 *
 * @author jcrenc
 * @since 2024-01-19 04:01:13
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CspMealServiceImpl extends ServiceImpl<CspMealMapper, CspMeal> implements ICspMealService {

    private final ICspMealContractAssociationService mealContractAssociation;

    /**
     * 添加套餐
     *
     * @param type           套餐类型
     * @param name           套餐名称，扩容套餐可为空
     * @param customerNumber 客户数
     * @param price          套餐价格
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addMeal(CspMealType type, String name, Integer customerNumber, BigDecimal price) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        CspMeal cspMeal = new CspMeal()
                .setMealId("CCP" + df.format(LocalDateTime.now()))
                .setType(type)
                .setName(name)
                .setCustomerNumber(customerNumber)
                .setPrice(price)
                .setStatus(CspMealStatus.ON_SHELVES);//默认上架
        try {
            this.save(cspMeal);
        } catch (Exception sql) {
            if (sql.getMessage().contains("Duplicate entry")) {
                throw new BizException("名称重复");
            } else {
                throw sql;
            }
        }

    }

    /**
     * 修改csp套餐上下架状态
     *
     * @param id     套餐ID
     * @param status 目标状态
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateState(Long id, CspMealStatus status) {
        boolean updated = this.lambdaUpdate()
                .eq(CspMeal::getId, id)
                .ne(CspMeal::getStatus, status)
                .set(CspMeal::getStatus, status)
                .update();
        if (!updated)
            throw new BizException("操作失败");
    }

    /**
     * 删除csp套餐，只能删除自己创建且已下架的套餐
     *
     * @param id 要删除的套餐ID
     */
    @Override
    public void deleteMeal(Long id) {
        CspMeal meal = getById(id);
        if (Objects.isNull(meal)) return;
        if (!CspMealStatus.OFF_SHELVES.equals(meal.getStatus())) {
            throw new BizException("只能删除下架状态得套餐");
        }
        removeById(id);
    }

    @Override
    public Page<CspMealPageInfo> queryPage(CspMealPageQuery query) {
        //查询套餐分页数据
        Page<CspMealPageInfo> page = new Page<>();
        page.setCurrent(query.getPageNo());
        page.setSize(query.getPageSize());
        getBaseMapper().queryPage(page, query);
        //查询使用量
        for (CspMealPageInfo meal : page.getRecords()) {
            Long orderCount = mealContractAssociation.countOrderNum(meal.getMealId());
            meal.setOrderCount(orderCount);
        }
        return page;
    }

    @Override
    public List<CspMealPageInfo> exportList(CspMealPageQuery query) {
        //查询套餐分页数据
        List<CspMealPageInfo> list = getBaseMapper().exportList(query);
        //查询使用量
        for (CspMealPageInfo meal : list) {
            Long orderCount = mealContractAssociation.countOrderNum(meal.getMealId());
            meal.setOrderCount(orderCount);
        }
        return list;
    }

    @Override
    @Transactional
    public void checkAndSave(final CspMealContract contract, List<String> mealList, boolean removeOld) {
        Long contractId = contract.getContractId();
        if (removeOld) {
            //删除老套餐数据
            mealContractAssociation.remove(new LambdaQueryWrapper<CspMealContractAssociation>()
                    .eq(CspMealContractAssociation::getContractId, contractId));
        } else {
            //让前端只传递新增得 后台不处理了，区分不了是新增还是原来的数据
        }

        if (CollectionUtils.isEmpty(mealList)) return;
        //检查套餐数据是否存在,状态是否正确
        List<CspMeal> cspMeals = listByMealId(mealList);
        if (cspMeals.size() < mealList.stream().distinct().count()) {
            throw new BizException("你选择得套餐不存在，请重新选择");
        }
        if (cspMeals.stream().anyMatch(s -> CspMealStatus.OFF_SHELVES.equals(s.getStatus()))) {
            throw new BizException("不能选择未上架的套餐");
        }

        final Date effectiveTime = removeOld ? contract.getEffectiveTime() : new Date();
        //如果可以删除老数据，者表示合同还没有生效。如果不能删除之前的数据则表示合同已经生效，新加的套餐也久立即生效
        mealContractAssociation.saveBatch(cspMeals.stream()
                .map(o -> new CspMealContractAssociation(contractId, o, effectiveTime))
                .collect(Collectors.toList()));

    }

    private List<CspMeal> listByMealId(List<String> mealList) {
        return lambdaQuery().in(CspMeal::getMealId, mealList).list();
    }

    @Override
    public Long countMealCount(Long contractId) {
        return mealContractAssociation.countMealCount(contractId);
    }

    @Override
    public List<CspContractMeal> listByContractId(Long contractId) {
        List<CspMealContractAssociation> list = mealContractAssociation.lambdaQuery()
                .select(CspMealContractAssociation::getId,
                        CspMealContractAssociation::getMealDetail,
                        CspMealContractAssociation::getMealId,
                        CspMealContractAssociation::getName,
                        CspMealContractAssociation::getType,
                        CspMealContractAssociation::getCustomerNumber,
                        CspMealContractAssociation::getEffectiveTime)
                .eq(CspMealContractAssociation::getContractId, contractId)
                .list();

        return list.stream().map(CspContractMeal::new)
                .sorted(Comparator.comparingInt(s -> s.getType().getCode()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void setMealEffectTime(Long contractId) {
        mealContractAssociation.setMealEffectTime(contractId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeByMealContractId(Long id) {
        mealContractAssociation.removeByMealContractId(id);
    }
}

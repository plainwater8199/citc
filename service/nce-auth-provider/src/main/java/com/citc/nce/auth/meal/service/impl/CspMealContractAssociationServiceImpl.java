package com.citc.nce.auth.meal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.meal.domain.CspMealContractAssociation;
import com.citc.nce.auth.meal.dao.CspMealContractAssociationMapper;
import com.citc.nce.auth.meal.service.ICspMealContractAssociationService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * csp套餐合同详 关联表 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2024-01-22 02:01:23
 */
@Service
public class CspMealContractAssociationServiceImpl extends ServiceImpl<CspMealContractAssociationMapper, CspMealContractAssociation>
        implements ICspMealContractAssociationService {
    @Override
    public Long countOrderNum(@NotEmpty String mealId) {
        return count(new LambdaQueryWrapper<CspMealContractAssociation>()
                .eq(CspMealContractAssociation::getMealId, mealId)
        );
    }

    @Override
    public Long countMealCount(Long contractId) {
        Long count = getBaseMapper().selectMealCount(contractId);
        if (count == null) return 0L;
        return count;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void setMealEffectTime(Long contractId) {
        List<CspMealContractAssociation> list = lambdaQuery()
                .eq(CspMealContractAssociation::getContractId, contractId)
                .list();
        if (CollectionUtils.isEmpty(list)) return;
        List<Long> associations = list.stream().map(CspMealContractAssociation::getId).collect(Collectors.toList());
        lambdaUpdate()
                .set(CspMealContractAssociation::getEffectiveTime, new Date())
                .in(CspMealContractAssociation::getId, associations)
                .update();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeByMealContractId(Long contractId) {
        remove(new LambdaQueryWrapper<CspMealContractAssociation>().eq(CspMealContractAssociation::getContractId, contractId));
    }
}

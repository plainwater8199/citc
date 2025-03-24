package com.citc.nce.auth.meal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.meal.dao.CspMealContractMapper;
import com.citc.nce.auth.meal.domain.CspMealContract;
import com.citc.nce.auth.meal.enums.CspMealContractStatus;
import com.citc.nce.auth.meal.enums.CspMealType;
import com.citc.nce.auth.meal.service.ICspMealContractService;
import com.citc.nce.auth.meal.service.ICspMealService;
import com.citc.nce.auth.meal.vo.MealCspHome;
import com.citc.nce.auth.meal.vo.contract.*;
import com.citc.nce.authcenter.auth.vo.UserInfo;
import com.citc.nce.authcenter.csp.CspApi;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.SessionContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * csp套餐合同表 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2024-01-22 02:01:23
 */
@Service
@Slf4j
public class CspMealContractServiceImpl extends ServiceImpl<CspMealContractMapper, CspMealContract> implements ICspMealContractService {
    @Autowired
    private CspApi cspApi;
    @Autowired
    private ICspMealService mealService;
    @Autowired
    private CspCustomerApi cspCustomerApi;

    @Override

    public void add(CspMealContractAddVo contractInfo) {
        //查询csp是否存在
        checkCsp(contractInfo.getCspId());
        //csp id 合同编号查重
        if (lambdaQuery().eq(CspMealContract::getContractNo, contractInfo.getContractNo()).exists()) {
            throw new BizException("合同编号不能重复");
        }
        //根据用户传递参数生成 CspMealContract
        CspMealContract contract = getCspMealContract(contractInfo);

        //验证合同时间轴
        checkTimeAxis(contract, -1L);

        List<String> mealList = getMealList(contractInfo);

        //事务保存
        ICspMealContractService thisBean = SpringUtil.getBean(ICspMealContractService.class);
        thisBean.save(contract, mealList);
        //刷新状态
        thisBean.refreshCspMealStatus(contract.getCspId());

    }

    @NotNull
    private static List<String> getMealList(CspMealContractAddVo contractInfo) {
        List<String> mealList = new ArrayList<>();
        if (Objects.nonNull(contractInfo.getBasicMeal())) {
            mealList.add(contractInfo.getBasicMeal());
        }
        if (!CollectionUtils.isEmpty(contractInfo.getExpansionMeal())) {
            mealList.addAll(contractInfo.getExpansionMeal());
        }
        return mealList;
    }

    @Transactional
    public void save(CspMealContract contract, List<String> mealList) {
        try {
            save(contract);
        } catch (Exception sql) {
            if (sql.getMessage().contains("Duplicate entry")) {
                throw new BizException("名称重复");
            } else {
                throw sql;
            }
        }

        //存储关联meal
        mealService.checkAndSave(contract, mealList, true);
    }

    @NotNull
    private static CspMealContract getCspMealContract(CspMealContractAddVo contractInfo) {
        //合同的生效时间为生效日期当天的0点0分0秒
        contractInfo.setEffectiveTime(DateUtil.beginOfDay(contractInfo.getEffectiveTime()));
        CspMealContract contract = new CspMealContract();
        BeanUtils.copyProperties(contractInfo, contract);
        //合同结束时间
        Date endaTime = getEndOfDay(DateUtil.offset(contractInfo.getEffectiveTime(), DateField.YEAR, contractInfo.getValidTime()).offset(DateField.DAY_OF_YEAR, -1));
        contract.setExpireTime(endaTime);

        //计算当前合同状态
        Date date = new Date();
        if (date.before(contract.getEffectiveTime())) {
            //new Date() < contractInfo.getEffectiveTime()
            contract.setStatus(CspMealContractStatus.PENDING);
        } else if (date.after(contract.getEffectiveTime()) && date.before(contract.getExpireTime())) {
            //new Date() > contractInfo.getEffectiveTime() && new Date()< contract.getExpireTime()
            contract.setStatus(CspMealContractStatus.EFFECTIVE);
        } else {
            contract.setStatus(CspMealContractStatus.INEFFECTIVE);
        }
        return contract;
    }

    private void checkCsp(String cspId) {
        List<UserInfoVo> infoVos = cspApi.getByIdList(Collections.singletonList(cspId));
        if (CollectionUtils.isEmpty(infoVos) || Objects.isNull(infoVos.get(0))) {
            throw new BizException("csp客户不存在");
        }
    }

    /**
     * 验证合同时间轴
     *
     * @param contract       合同信息
     * @param filterContract 不包括的合同
     */
    private void checkTimeAxis(CspMealContract contract, Long filterContract) {
        List<CspMealContract> list = lambdaQuery().eq(CspMealContract::getCspId, contract.getCspId()).list();
        //没有合同不要检查时间
        if (CollectionUtils.isEmpty(list)) return;
        List<CspMealContract> sorted = list.stream().filter(s -> !s.getContractId().equals(filterContract))
                .sorted((o1, o2) -> o1.getExpireTime().before(o2.getExpireTime()) ? 1 : -1).collect(Collectors.toList());
        for (CspMealContract v : sorted) {
            if (isOverLap(contract.getEffectiveTime(), contract.getExpireTime(), v.getEffectiveTime(), v.getExpireTime())) {
                throw new BizException("合同时间段不能重复,重复合同编号：" + v.getContractNo());
            }
        }
    }

    //时间一和时间二是否重叠
    public static boolean isOverLap(Date dateTime1Start, Date dateTime1End, Date dateTime2Start, Date dateTime2End) {
        return !(dateTime1End.before(dateTime2Start) || dateTime1Start.after(dateTime2End));
    }

    @Override
    public void edit(CspMealContractEditVo contractInfo) {
        //查询原始合同
        CspMealContract mealContract = getById(contractInfo.getContractId());
        if (Objects.isNull(mealContract)) {
            throw new BizException("合同不存在");
        }

        //查询csp是否存在
        checkCsp(contractInfo.getCspId());
        //csp id 合同编号查重（不包含当前合同）
        if (lambdaQuery().eq(CspMealContract::getContractNo, contractInfo.getContractNo())
                .ne(CspMealContract::getContractId, contractInfo.getContractId())
                .exists()) {
            throw new BizException("合同编号不能重复");
        }
        //合同套餐列表（所以套餐放到一起）
        List<String> mealList = getMealList(contractInfo);
        ICspMealContractService thisBean = SpringUtil.getBean(ICspMealContractService.class);

        CspMealContractStatus status = mealContract.getStatus();
        if (CspMealContractStatus.PENDING.equals(status)) {
            //待生效修改全部信息
            //根据用户传递参数生成 CspMealContract
            CspMealContract contract = getCspMealContract(contractInfo);
            //验证合同时间轴
            checkTimeAxis(contract, contractInfo.getContractId());
            //事务保存
            thisBean.update(contract, mealList);
        } else if (CspMealContractStatus.EFFECTIVE.equals(status)) {
            //生效中合同只能新增扩容套餐,删除基础套餐
            mealList.remove(contractInfo.getBasicMeal());
            mealService.checkAndSave(mealContract, mealList, false);
        } else {
            throw new BizException("已失效的合同更不能修改");
        }
        //修改后刷新csp合同套餐状态
        thisBean.refreshCspMealStatus(contractInfo.getCspId());
    }

    @Transactional
    public void update(CspMealContract contract, List<String> mealList) {
        updateById(contract);
        //存储关联meal
        mealService.checkAndSave(contract, mealList, true);
    }

    @Override
    @Transactional
    public void del(Long id) {
        CspMealContract mealContract = getById(id);
        if (Objects.isNull(mealContract)) return;
        if (CspMealContractStatus.EFFECTIVE.equals(mealContract.getStatus())) {
            throw new BizException("合同生效中，不能删除");
        }
        removeById(id);
        mealService.removeByMealContractId(id);
    }

    @Override
    public Page<CspMealContractPageInfo> queryPage(CspMealContractPageQuery query) {
        Page<CspMealContractPageInfo> page = new Page<>(query.getPageNo(), query.getPageSize());
        if (Objects.nonNull(query.getExpireStartTime())) {
            query.setExpireStartTime(DateUtil.beginOfDay(query.getExpireStartTime()));
        }
        if (Objects.nonNull(query.getExpireEndTime())) {
            query.setExpireEndTime(getEndOfDay(query.getExpireEndTime()));
        }
        if (Objects.nonNull(query.getEffectiveStartTime())) {
            query.setEffectiveStartTime(DateUtil.beginOfDay(query.getEffectiveStartTime()));
        }
        if (Objects.nonNull(query.getEffectiveEndTime())) {
            query.setEffectiveEndTime(getEndOfDay(query.getEffectiveEndTime()));
        }
        Page<CspMealContractPageInfo> infoPage = getBaseMapper().queryPage(page, query);
        infoPage.getRecords().forEach(s -> {
            if (Objects.isNull(s.getMealNum())) {
                s.setMealNum(0L);
            }
        });
        return infoPage;
    }

    @Override
    public CspMealContractInfo queryById(Long id) {
        CspMealContract contract = getById(id);
        if (Objects.isNull(contract)) {
            throw new BizException("合同不存在");
        }
        CspMealContractInfo info = new CspMealContractInfo();
        BeanUtils.copyProperties(contract, info);
        //查询csp相关信息
        CspMealCspInfo cspInfo = getBaseMapper().selectMealCspInfo(contract.getCspId());
        BeanUtils.copyProperties(cspInfo, info);
        //查询套餐数据
        List<CspContractMeal> mealList = mealService.listByContractId(id);
        if (CollectionUtil.isNotEmpty(mealList)) {
            info.setBasicMeal(mealList.get(0));
            if (mealList.size() > 1) {
                info.setExpansionMeal(CollectionUtil.sub(mealList, 1, mealList.size() + 1));
            }
            info.setMealNum(mealList.stream().mapToLong(CspContractMeal::getCustomerNumber).sum());
        }
        return info;
    }


    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void updateStatus(Long contractId, CspMealContractStatus status) {
        update(new LambdaUpdateWrapper<CspMealContract>()
                .set(CspMealContract::getStatus, status)
                .eq(CspMealContract::getContractId, contractId));
    }


    @Override
    public Map<String, Integer> manageHome() {
        Date startTime = DateUtil.beginOfDay(new Date());
        Date endTime = DateUtil.offset(startTime, DateField.DAY_OF_YEAR, 91);
        //temporary
        long count = count(new LambdaQueryWrapper<CspMealContract>()
                .eq(CspMealContract::getStatus, CspMealContractStatus.EFFECTIVE)
                .ge(CspMealContract::getExpireTime, startTime)
                .le(CspMealContract::getExpireTime, endTime)
        );
        //abnormalCount
        int abnormalCount = cspApi.listMealStatus(null, 1).stream()
                .filter(s -> Objects.nonNull(s.getCspId()))
                .mapToInt(UserInfo::getCspMealStatus).sum();
        HashMap<String, Integer> map = new HashMap<>();
        map.put("temporary", (int) count);
        map.put("abnormalCount", abnormalCount);
        return map;
    }

    @Override
    public CspMealContract getCspCurrentContract(String cspId) {
        return lambdaQuery()
                .eq(CspMealContract::getCspId, cspId)
                .eq(CspMealContract::getStatus, CspMealContractStatus.EFFECTIVE)
                .one();
    }

    @Override
    public Long countCurrentMealNumMunByCspId(String cspId) {
        CspMealContract contract = getCspCurrentContract(cspId);
        if (Objects.isNull(contract)) return 0L;
        return mealService.countMealCount(contract.getContractId());
    }

    @Override
    @Transactional
    public void effectContract(Long contractId) {
        ICspMealContractService thisBean = SpringUtil.getBean(ICspMealContractService.class);
        thisBean.updateStatus(contractId, CspMealContractStatus.EFFECTIVE);
        mealService.setMealEffectTime(contractId);
    }

    @Override
    @Async
    public void refreshCspMealStatus(String cspId) {
        cspApi.refreshCspMealStatus(cspId);
    }

    @Override
    public MealCspHome cspHome() {
        String cspId = SessionContextUtil.getLoginUser().getCspId();
        if (!StringUtils.hasLength(cspId)) {
            throw new BizException("当前账号不是csp账号");
        }
        MealCspHome cspHome = new MealCspHome();
        CspMealContract contract = getCspCurrentContract(cspId);
        if (Objects.nonNull(contract)) {
            BeanUtils.copyProperties(contract, cspHome);
            List<CspContractMeal> mealList = mealService.listByContractId(contract.getContractId());
            cspHome.setBasicMeal(mealList.stream().filter(s -> CspMealType.NORMAL.equals(s.getType())).mapToInt(CspContractMeal::getCustomerNumber).sum());
            cspHome.setExpansionMeal(mealList.stream().filter(s -> CspMealType.EXTRA.equals(s.getType())).mapToInt(CspContractMeal::getCustomerNumber).sum());
        }
        cspHome.setMealNum((long) (cspHome.getBasicMeal() + cspHome.getExpansionMeal()));
        cspHome.setCustomerNum(cspCustomerApi.countActiveCustomerByCspId(cspId));
        return cspHome;
    }

    @Override
    public CspMealContract isUserContract(String cspId, Long contractId) {
        CspMealContract contract = getById(contractId);
        if (Objects.isNull(contract)) {
            throw new BizException("合同不存在");
        }
        if (StringUtils.hasLength(cspId) && !contract.getCspId().equals(cspId)) {
            throw new BizException("你查询的合同是其他csp的");
        }
        return contract;
    }

    @Override
    public List<CspContractMeal> cspContractMealDetail(Long contractId) {
        return mealService.listByContractId(contractId);
    }

    public static Date getEndOfDay(Date date) {
        return com.citc.nce.common.util.DateUtil.getEndOfDay(date);
    }
}

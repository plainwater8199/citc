package com.citc.nce.materialSquare.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.materialSquare.entity.MsManageActivity;
import com.citc.nce.materialSquare.entity.MsManageSuggest;
import com.citc.nce.materialSquare.dao.MsManageSuggestMapper;
import com.citc.nce.materialSquare.service.IMsManageActivityService;
import com.citc.nce.materialSquare.service.IMsManageSuggestService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.materialSquare.vo.suggest.SuggestAdd;
import com.citc.nce.materialSquare.vo.suggest.SuggestChangeNum;
import com.citc.nce.materialSquare.vo.suggest.SuggestListOrderNum;
import com.citc.nce.materialSquare.vo.suggest.req.SuggestOrderReq;
import com.citc.nce.materialSquare.vo.suggest.resp.SuggestListResp;
import com.citc.nce.robot.api.materialSquare.MsSummaryApi;
import com.citc.nce.robot.api.materialSquare.emums.MsType;
import com.citc.nce.robot.api.materialSquare.vo.summary.SummaryInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 素材广场_后台管理_首页推荐	 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2024-05-15 10:05:47
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MsManageSuggestServiceImpl extends ServiceImpl<MsManageSuggestMapper, MsManageSuggest> implements IMsManageSuggestService {

    private final MsSummaryApi msSummaryApi;

    private final IMsManageActivityService msManageActivityService;

    @Override
    @Transactional
    public synchronized void putSuggest(SuggestAdd suggestAdd) {
        MsManageSuggest suggest = lambdaQuery().eq(MsManageSuggest::getMssId, suggestAdd.getMssId()).one();
        if (Objects.nonNull(suggest) && suggest.getOrderNum() > 0) {
            throw new BizException("推荐作品不能重复");
        }

        List<MsManageSuggest> orders = lambdaQuery().list();//获取排序方式 已置顶的排序方式为准（order_type  0-正序，1-乱序）
        List<MsManageSuggest> allList = lambdaQuery().gt(MsManageSuggest::getOrderNum, 0).list();
        MsManageSuggest manageSuggest = new MsManageSuggest();
        manageSuggest.setOrderNum(CollectionUtils.isEmpty(allList) ? 1 : allList.size()+1);
        manageSuggest.setMssId(suggestAdd.getMssId());
        manageSuggest.setMsIdField(suggestAdd.getSuggestType().getCode());
        manageSuggest.setOrderType(CollectionUtils.isEmpty(orders)? 0 : orders.get(0).getOrderType());
        save(manageSuggest);
    }

    @Override
    @Transactional
    public void changeOrderNum(SuggestOrderReq req) {
        Integer orderType = req.getOrderType();
        if(orderType == 0) {//如果是乱序才去更新排列顺序
            for (SuggestChangeNum orderNum : req.getChangeNums()) {
                update(new LambdaUpdateWrapper<MsManageSuggest>()
                        .set(MsManageSuggest::getOrderNum, orderNum.getOrderNum())
                        .eq(MsManageSuggest::getMsSuggestId, orderNum.getMsSuggestId()));
            }
        }
        //更改排序方式
        update(new LambdaUpdateWrapper<MsManageSuggest>()
                .set(MsManageSuggest::getOrderType, orderType)
                .isNotNull(MsManageSuggest::getOrderNum));

    }


    @Override
    @Transactional
    public SuggestListResp listOrderNum() {
        SuggestListResp resp = new SuggestListResp();


        resp.setOrderType(0);//默认为0
        List<MsManageSuggest> list = lambdaQuery().orderByAsc(MsManageSuggest::getOrderNum).list();
        if (!CollectionUtils.isEmpty(list)) {

            List<Long> removeIds = new ArrayList<>();

            List<MsManageSuggest> activeList = list.stream().filter(s -> Objects.equals(s.getMsIdField(), MsType.ACTIVITY.getCode())).collect(Collectors.toList());
            List<SuggestListOrderNum> suggestListOrderNums = new ArrayList<>();

            Map<Long, SummaryInfo> longSummaryInfoMap = msSummaryApi.queryOnLineSummaryInfo(1);
            for(MsManageSuggest s : list){
                if(longSummaryInfoMap.containsKey(s.getMssId())){//只返回已上架的推荐设置
                    resp.setOrderType(s.getOrderType());
                    SummaryInfo summaryInfo = longSummaryInfoMap.get(s.getMssId());
                    SuggestListOrderNum suggestListOrderNum = new SuggestListOrderNum();
                    suggestListOrderNum.setMsSuggestId(s.getMsSuggestId());
                    suggestListOrderNum.setOrderNum(s.getOrderNum());
                    suggestListOrderNum.setMssId(s.getMssId());
                    suggestListOrderNum.setMssName(summaryInfo.getName());
                    suggestListOrderNum.setMssCoverFile(summaryInfo.getCoverFile());
                    suggestListOrderNum.setSuggestType(MsType.fromCode(s.getMsIdField()));
                    suggestListOrderNum.setMsIdField(s.getMsIdField());
                    suggestListOrderNums.add(suggestListOrderNum);
                }else{
                    if(!Objects.equals(s.getMsIdField(), MsType.ACTIVITY.getCode())){
                        removeIds.add(s.getMsSuggestId());
                    }

                }
            }

            if(!CollectionUtils.isEmpty(activeList)){
                List<Long> activityIds = activeList.stream().map(MsManageSuggest::getMssId).collect(Collectors.toList());
                Map<Long, MsManageActivity> activityMap = msManageActivityService.queryInSuggestInfo(activityIds);
                if(!CollectionUtils.isEmpty(activityMap)){
                    for(MsManageSuggest s : activeList){
                        if(activityMap.containsKey(s.getMssId())){//只返回已上架的推荐设置
                            resp.setOrderType(s.getOrderType());
                            MsManageActivity activityInfo = activityMap.get(s.getMssId());
                            SuggestListOrderNum suggestListOrderNum = new SuggestListOrderNum();
                            suggestListOrderNum.setMsSuggestId(s.getMsSuggestId());
                            suggestListOrderNum.setOrderNum(s.getOrderNum());
                            suggestListOrderNum.setMssId(s.getMssId());
                            suggestListOrderNum.setMssName(activityInfo.getName());
                            suggestListOrderNum.setMssCoverFile(activityInfo.getCoverFileId() );
                            suggestListOrderNum.setMsIdField(s.getMsIdField());
                            suggestListOrderNum.setSuggestType(MsType.fromCode(s.getMsIdField()));
                            suggestListOrderNum.setH5Id(activityInfo.getH5Id());
                            suggestListOrderNums.add(suggestListOrderNum);
                        }else{
                            removeIds.add(s.getMsSuggestId());
                        }
                    }
                }
            }

            if(!CollectionUtils.isEmpty(removeIds)){
                this.removeByIds(removeIds);
            }

            if(resp.getOrderType() == 1){//乱序
                disorderResp(suggestListOrderNums);
            }else{
                suggestListOrderNums.sort(Comparator.comparing(SuggestListOrderNum::getOrderNum));
            }
            resp.setChangeNums(suggestListOrderNums);
        }


        return resp;
    }

    private void disorderResp(List<SuggestListOrderNum> suggestListOrderNums) {
        if (!CollectionUtils.isEmpty(suggestListOrderNums)) {
            List<SuggestListOrderNum> disorderList = new ArrayList<>();
            SuggestListOrderNum top = null;
            for (SuggestListOrderNum item : suggestListOrderNums) {
                if(item.getOrderNum() == 0){
                    top = item;
                }else{
                    disorderList.add(item);
                }
            }
            // 乱序
            Collections.shuffle(disorderList);
            //合并队列，并且将置顶推荐放到第一位
            suggestListOrderNums.clear();
            if(Objects.nonNull(top)){
                suggestListOrderNums.add(top);
            }
            suggestListOrderNums.addAll(disorderList);

        }

    }



    @Override
    public void setTop(SuggestAdd suggestAdd) {
        MsManageSuggest saveSuggest = new MsManageSuggest();
        saveSuggest.setOrderNum(0);
        saveSuggest.setMssId(suggestAdd.getMssId());
        saveSuggest.setMsIdField(suggestAdd.getSuggestType().getCode());
        //先清除置顶推荐
        cleanTop();
        //获取排序方式
        List<MsManageSuggest> order = lambdaQuery().gt(MsManageSuggest::getOrderNum, 0).list();//获取排序方式 已置顶的排序方式为准（order_type  0-正序，1-乱序）
        if(!CollectionUtils.isEmpty(order)){
            saveSuggest.setOrderType(order.get(0).getOrderType());
        }else{
            saveSuggest.setOrderType(0);
        }
        save(saveSuggest);
    }

    /**
     * 获取置顶推荐
     * @return 置顶推荐
     */
    private List<MsManageSuggest> getTop() {
        return lambdaQuery().eq(MsManageSuggest::getOrderNum, 0).list();
    }

    @Override
    public void cleanTop() {
        List<MsManageSuggest> suggest = getTop();
        if (!CollectionUtils.isEmpty(suggest)) {
            for (MsManageSuggest s : suggest) {
                removeById(s.getMsSuggestId());
            }
        }
    }

    @Override
    public List<MsManageSuggest> listByMsActivityIds(List<Long> activityIds) {
        return lambdaQuery().in(MsManageSuggest::getMssId, activityIds).eq(MsManageSuggest::getDeleted, 0).list();
    }

}

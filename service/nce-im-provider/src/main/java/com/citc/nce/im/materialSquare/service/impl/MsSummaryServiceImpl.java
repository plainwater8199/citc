package com.citc.nce.im.materialSquare.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.authcenter.Utils.UserInfoUtil;
import com.citc.nce.authcenter.auth.AdminAuthApi;
import com.citc.nce.authcenter.auth.vo.resp.AdminUserResp;
import com.citc.nce.authcenter.csp.CspApi;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.EsPageResult;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.xss.core.XssCleanIgnore;
import com.citc.nce.customcommand.CustomCommandApi;
import com.citc.nce.customcommand.vo.CustomCommandDetailVo;
import com.citc.nce.h5.H5Api;
import com.citc.nce.h5.vo.H5Info;
import com.citc.nce.im.elasticsearch.EsSummaryService;
import com.citc.nce.im.mall.template.service.MallTemplateService;
import com.citc.nce.im.materialSquare.entity.MsSummarySnapshot;
import com.citc.nce.im.materialSquare.mapper.MsSummaryMapper;
import com.citc.nce.im.materialSquare.mapper.OrderMapper;
import com.citc.nce.im.materialSquare.service.*;
import com.citc.nce.im.tempStore.utils.PageSupport;
import com.citc.nce.materialSquare.MsManageActivityContentApi;
import com.citc.nce.materialSquare.MsManageActivityLiApi;
import com.citc.nce.materialSquare.vo.activity.MsPrice;
import com.citc.nce.misc.constant.BusinessTypeEnum;
import com.citc.nce.misc.record.ProcessingRecordApi;
import com.citc.nce.misc.record.req.ProcessingRecordReq;
import com.citc.nce.modulemanagement.ModuleManagementApi;
import com.citc.nce.modulemanagement.vo.ModuleManagementItem;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.citc.nce.robot.api.mall.template.vo.resp.MallTemplateQueryDetailResp;
import com.citc.nce.robot.api.materialSquare.MsSummaryApi;
import com.citc.nce.robot.api.materialSquare.emums.*;
import com.citc.nce.robot.api.materialSquare.entity.*;
import com.citc.nce.robot.api.materialSquare.vo.summary.*;
import com.citc.nce.robot.api.tempStore.bean.manage.GoodsOperate;
import com.citc.nce.robot.api.tempStore.bean.manage.GoodsOperateBatch;
import com.citc.nce.robot.api.tempStore.domain.Order;
import com.citc.nce.robot.api.tempStore.enums.PayStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 素材广场，发布汇总 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2024-05-31 09:05:58
 */
@Service
@Slf4j
@AllArgsConstructor
public class MsSummaryServiceImpl extends ServiceImpl<MsSummaryMapper, MsSummary> implements IMsSummaryService {

    private final IMsSummaryIntroduceService introduceService;
    private final IMsSummaryContentService contentService;
    private final CspApi cspApi;
    @Resource
    private EsSummaryService elasticsearch;
    private final UserInfoUtil userInfoUtil;
    private final IMsLikeRecordService likeRecordService;
    private final IMsViewRecordService viewRecordService;
    private final OrderMapper orderMapper;
    private final MsManageActivityLiApi activityLiApi;
    private final AdminAuthApi authApi;
    private final ProcessingRecordApi processingRecordApi;
    private final CustomCommandApi customCommandApi;
    private final ModuleManagementApi moduleManagementApi;
    private final MallTemplateService mallTemplateService;
    private final H5Api h5Api;
    private final MsManageActivityContentApi msManageActivityContentApi;

    @Override
    public MsSummary getByMsTypeAndMsId(MsType msType, String msId) {
        Assert.hasLength(msId, "素材id不能为空");
        return lambdaQuery().eq(MsSummary::getMsId, msId)
                .eq(Objects.nonNull(msType), MsSummary::getMsType, msType)
                .one();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void notifyTemplateUpgrade(MsType msType, String msId) {
        MsSummary summary = getByMsTypeAndMsId(msType, msId);
        if (Objects.isNull(summary)) {
            log.info("素材未绑定 msType:{}  msId:{}", msType, msId);
            return;
        }
        //把模板状态设为已更新
        updateMsTemplateStatus(summary.getMssId(), MsTemplateStatus.NEED_UPDATE);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void notifyTemplateDelete(MsType msType, String msId) {
        MsSummary summary = getByMsTypeAndMsId(msType, msId);
        if (Objects.isNull(summary)) {
            log.info("素材未绑定 msType:{}  msId:{}", msType, msId);
            return;
        }
        //把模板状态设为已更新
        updateMsTemplateStatus(summary.getMssId(), MsTemplateStatus.DELETED);
    }

    private void updateMsTemplateStatus(Long mssId, MsTemplateStatus statusEnum) {
        lambdaUpdate().set(MsSummary::getTemplateStatus, statusEnum)
                .eq(MsSummary::getMssId, mssId)
                .update(new MsSummary());
    }

    @Override
    @Transactional
    public void activeOff(GoodsOperateBatch mssIds) {
        List<MsSummary> summaryList = listByIds(mssIds.getMssIdList());
        List<MsSummary> list =
                summaryList.stream().filter(s -> MsAuditStatus.ACTIVE_ON.equals(s.getAuditStatus())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(list)) {
            activeOff(summaryList, StringUtils.hasLength(mssIds.getRemark()) ? mssIds.getRemark() : "管理员手动下架");
        } else {
            throw new BizException("已选择的作品中无可下架的作品，请重新选择");
        }
    }


    /**
     * 解除作品和活动的关联关系
     *
     * @param summaryList 作品列表
     */
    private void deleteSummaryForActivity(List<MsSummary> summaryList) {
        List<Long> mssIds = summaryList.stream().map(MsSummary::getMssId).collect(Collectors.toList());
        activityLiApi.deleteSummaryForActivity(mssIds);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void activeOff(List<MsSummary> summaryList, String remark) {
        if (CollectionUtil.isEmpty(summaryList)) return;

        for (MsSummary summary : summaryList) {
            if (Objects.nonNull(summary.getWorksLibraryStatus())) {
                summary.setWorksLibraryStatus(MsWorksLibraryStatus.ACTIVE_OFF);
            }
            summary.setAuditStatus(MsAuditStatus.ACTIVE_OFF);
        }
        updateBatchById(summaryList);

        //解除作品和活动的关联关系
        deleteSummaryForActivity(summaryList);

        List<Long> mssIdsForRecord = summaryList.stream().map(MsSummary::getMssId).collect(Collectors.toList());
        saveProcessingRecord(mssIdsForRecord, "作品下架", remark);


        elasticsearch.removeByMssIds(summaryList.stream().map(MsSummary::getMssId).collect(Collectors.toList()));
    }


    @Override
    @Transactional
    public void activeByCspId(String cspId) {
        List<MsSummary> list = listByCspId(cspId);
        activeOff(list, "关闭CSP模版发布权限");
    }

    @Override
    @Transactional
    public void auditPass(GoodsOperate req) {
        MsSummary summary = getById(req.getMssId());
        if (Objects.isNull(summary)) {
            throw new BizException("作品不存在");
        }
        if (!MsAuditStatus.WAIT.equals(summary.getAuditStatus())) {
            throw new BizException("非待审核状态，不能进行审核");
        }
        if (MsTemplateStatus.NEED_UPDATE.equals(summary.getTemplateStatus())) {
            //模板状态为已更新，审核通过csp平台的作品，则模板状态变为默认
            summary.setTemplateStatus(MsTemplateStatus.DEFAULT);
        }
        //审核通过同步作品库状态
        summary.setWorksLibraryStatus(MsWorksLibraryStatus.ACTIVE_ON);
        summary.setAuditStatus(MsAuditStatus.ACTIVE_ON);
        summary.setLastPutawayTime(new Date());
        if (summary.getFirstPutawayTime() == null) {
            summary.setFirstPutawayTime(new Date());
        }
        updateById(summary);

        if(summary.getMsType().equals(MsType.H5_FORM)){//如果是H5作品则需要更新H5的最新内容
            h5Api.updateSummaryH5(summary.getMsId());
        }
        //保存es数据（审核通过时正式开卖需要在es中存储，用户查询）
        elasticsearch.saveOrUpdate(summary);

        //保存操作日志
        saveProcessingRecord(Collections.singletonList(summary.getMssId()), "作品审核通过", req.getRemark());

    }


    @Override
    @Transactional
    public void activeMangeOn(GoodsOperateBatch mssIds) {
        List<MsSummary> summaryList = listByIds(mssIds.getMssIdList());
        if (CollectionUtil.isEmpty(summaryList) && summaryList.size() != mssIds.getMssIdList().size()) {
            throw new BizException("需要上架的作品已被删除");
        }
        if (summaryList.stream().anyMatch(s -> !(MsType.CUSTOM_ORDER.equals(s.getMsType()) || MsType.SYSTEM_MODULE.equals(s.getMsType())))) {
            throw new BizException("管理员不能上架csp的作品");
        }
        List<MsSummary> summaryListForUpdate = new ArrayList<>();
        for (MsSummary summary : summaryList) {
            summary.setAuditStatus(MsAuditStatus.ACTIVE_ON);
            summary.setWorksLibraryStatus(MsWorksLibraryStatus.ACTIVE_ON);
            summary.setLastPutawayTime(new Date());
            if (summary.getFirstPutawayTime() == null) {
                summary.setFirstPutawayTime(new Date());
            }
            summaryListForUpdate.add(summary);
        }
//        summaryList.forEach(s -> s.setAuditStatus(MsAuditStatus.ACTIVE_ON).setWorksLibraryStatus(MsWorksLibraryStatus.ACTIVE_ON));
        if (!CollectionUtils.isEmpty(summaryListForUpdate)) {
            updateBatchById(summaryList);
        }
        //操作日志
        List<Long> mssIdsForRecord = summaryList.stream().map(MsSummary::getMssId).collect(Collectors.toList());
        saveProcessingRecord(mssIdsForRecord, "作品上架", mssIds.getRemark());
        for (MsSummary summary : summaryList) {
            //添加到es
            elasticsearch.saveOrUpdate(summary);
        }
    }

    @Override
    public void auditFail(GoodsOperate req) {
        MsSummary summary = getById(req.getMssId());
        if (Objects.isNull(summary)) {
            throw new BizException("作品不存在");
        }
        if (!MsAuditStatus.WAIT.equals(summary.getAuditStatus())) {
            throw new BizException("非待审核状态，不能进行审核");
        }
        summary.setAuditStatus(MsAuditStatus.FAILURE);
        summary.setAuditFailResult(req.getRemark());
        updateById(summary);
        //保存操作日志
        saveProcessingRecord(Collections.singletonList(summary.getMssId()), "作品审核不通过", req.getRemark());
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteList(GoodsOperateBatch batch) {
        List<MsSummary> summaryList = listByIds(batch.getMssIdList());
        List<MsSummary> list =
                summaryList.stream().filter(s -> (MsType.CUSTOM_ORDER.equals(s.getMsType()) || MsType.SYSTEM_MODULE.equals(s.getMsType())) && MsAuditStatus.ACTIVE_OFF.equals(s.getAuditStatus())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(list)) {
            List<Long> mssIds = list.stream().map(MsSummary::getMssId).collect(Collectors.toList());
            saveProcessingRecord(mssIds, "作品删除", batch.getRemark());
            removeBatchByIds(list);

            //更新指令与组件的管理关系
            updateWorkStatusForMS(list);
            //更新作品和活动的关系
            deleteSummaryForActivity(list);

        } else {
            throw new BizException("已选择的作品中无可删除的作品，请重新选择");
        }
    }

    private void updateWorkStatusForMS(List<MsSummary> list) {
        List<String> moduleIds = new ArrayList<>();
        List<Long> mssIDList = new ArrayList<>();
        List<String> templateIDList = new ArrayList<>();
        List<Long> h5OfSummeryIdList = new ArrayList<>();
        for (MsSummary item : list) {
            if (MsType.CUSTOM_ORDER.equals(item.getMsType())) {
                mssIDList.add(Long.parseLong(item.getMsId()));
            } else if (MsType.SYSTEM_MODULE.equals(item.getMsType())) {
                moduleIds.add(item.getMsId());
            }else if(MsType.NR_SSG.equals(item.getMsType()) || MsType.ROBOT.equals(item.getMsType())){
                templateIDList.add(item.getMsId());
            }else{
                h5OfSummeryIdList.add(Long.parseLong(item.getMsId()));
            }
        }
        if (!CollectionUtils.isEmpty(mssIDList)) {
            customCommandApi.deleteMssIDForIds(mssIDList);
        }
        if (!CollectionUtils.isEmpty(moduleIds)) {
            moduleManagementApi.deleteMssIDForIds(moduleIds);
        }
        if (!CollectionUtils.isEmpty(templateIDList)) {
            mallTemplateService.deleteMssIDForIds(templateIDList);
        }
        if (!CollectionUtils.isEmpty(h5OfSummeryIdList)) {
            h5Api.deleteMssIDForIds(h5OfSummeryIdList);
        }
    }

    private List<MsSummary> listByCspId(String cspId) {
        return lambdaQuery().eq(MsSummary::getCspId, cspId).list();
    }


    //1、存ms_summary 每次修改都需要存储
    //2、存ms_summary_content  申请上架时在存储
    //3、存ms_summary_introduce 每次修改都需要存储
    @Override
    @Transactional
    @XssCleanIgnore
    public void saveDraft(MsSummarySaveDart dart) {
        cspApi.checkUserPublishAuth(SessionContextUtil.getUserId());

        if (MsPayType.PAID.equals(dart.getPayType()) && !(dart.getOriginalPrice().compareTo(BigDecimal.ZERO) > 0)) {
            throw new BizException("价格最低为必须大于0.0");
        }
        MsSummary summary = new MsSummary();
        BeanUtils.copyProperties(dart, summary);
        summary.setMsNum(getMsNum(dart.getMsType()));
        summary.setContentVersion(1);
        summary.setIntroduceVersion(1);

        //校验作平名字 商品名称在同一个CSP用户下唯一，全平台可不唯一
        checkMs(summary,false);

        saveSummaryDrat(dart.getMsTag(), dart.getIntroduce(), summary,null);
    }

    private void checkMs(MsSummary summary,boolean isUpdate) {
        //校验作品是否同名
        MsType msType = summary.getMsType();
        if (StringUtils.hasLength(summary.getName())) {
            boolean msNameExists = lambdaQuery()
                    .eq(MsSummary::getName, summary.getName())
                    .in((MsType.CUSTOM_ORDER.equals(msType) || MsType.SYSTEM_MODULE.equals(msType)),MsSummary::getMsType, Arrays.asList(MsType.CUSTOM_ORDER, MsType.SYSTEM_MODULE))
                    .ne(summary.getMssId() != null, MsSummary::getMssId, summary.getMssId())
                    //如果是组件和指令，则为管理员创建，不考虑创建者
                    .eq(!(MsType.CUSTOM_ORDER.equals(msType) || MsType.SYSTEM_MODULE.equals(msType)), MsSummary::getCreator, SessionContextUtil.getUserId())
                    .exists();
            if(msNameExists){
                throw new BizException("作品名称已存在!");
            }
        }

        //校验素材模版是否已经被占用
        String msId = summary.getMsId();//素材ID
        if (MsType.CUSTOM_ORDER.equals(msType)) {//自定义指令
            CustomCommandDetailVo detail = customCommandApi.getById(msId);
            if (detail.getMssId() != null && !Objects.equals(detail.getMssId(), summary.getMssId())) {
                throw new BizException("该素材模版已被占用!");
            }
            if (!detail.getActive()) {
                throw new BizException("该素材模版当前不可使用!");
            }
        } else if (MsType.SYSTEM_MODULE.equals(msType)) {//组件
            ModuleManagementItem detail = moduleManagementApi.queryById(Long.parseLong(msId));
            if (detail.getMssId() != null && !Objects.equals(detail.getMssId(), summary.getMssId())) {
                throw new BizException("该素材模版已被占用!");
            }
        } else if (MsType.H5_FORM.equals(msType)) {
            H5Info detail;
            if(isUpdate){
                H5Info detailForSummery = h5Api.getDetailForSummery(Long.parseLong(msId));
                if(detailForSummery.getId() == null){
                    detail = h5Api.getDetail(Long.parseLong(msId));
                }else{
                    detail = h5Api.getDetail(detailForSummery.getId());
                }
            }else{
                detail = h5Api.getDetail(Long.parseLong(msId));
            }


            if (detail.getMssId() != null && !Objects.equals(detail.getMssId(), summary.getMssId())) {
                throw new BizException("该素材模版已被占用!");
            }
        } else {
            MallTemplateQueryDetailResp mallTemplateQueryDetailResp = mallTemplateService.queryDetail(msId);
            if (mallTemplateQueryDetailResp.getMssId() != null && !Objects.equals(mallTemplateQueryDetailResp.getMssId(), summary.getMssId())) {
                throw new BizException("该素材模版已被占用!");
            }
        }

    }

    private String getMsNum(MsType msType) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");
        return "MS0" + msType.getCode() + sdf.format(new Date()) + (int) (Math.random() * 1000);
    }


    @Override
    @Transactional
    public void updateDraft(MsSummaryUpdateDart dart) {
        cspApi.checkUserPublishAuth(SessionContextUtil.getUserId());
        MsSummary summary = getById(dart.getMssId());
        if (Objects.isNull(summary)) {
            throw new BizException("作品已删除");
        }
        if (!MsAuditStatus.ACTIVE_OFF.equals(summary.getAuditStatus()) && !MsAuditStatus.FAILURE.equals(summary.getAuditStatus())) {
            throw new BizException("作品状态不为已下架，无法编辑");
        }
        String oldMsId = summary.getMsId();
        BeanUtils.copyProperties(dart, summary);
        summary.setUpdater(SessionContextUtil.getUserId());
        summary.setUpdateTime(new Date());
        //校验作平名字 商品名称在同一个CSP用户下唯一，全平台可不唯一
        checkMs(summary,true);
        saveSummaryDrat(dart.getMsTag(), dart.getIntroduce(), summary,oldMsId);
    }


    @Transactional
    public void saveSummaryDrat(List<String> tags, String introduce, MsSummary summary,String oldMsId) {
        if (CollectionUtil.isNotEmpty(tags)) {
            summary.setMsTag(String.join(",", tags));
        } else {
            summary.setMsTag("");
        }

        String msId = summary.getMsId();
        //如果是H5表单、则创建一个H5模版、并且将新H5模版的ID赋值给summary
        if (MsType.H5_FORM.equals(summary.getMsType())) {
            summary.setMsId(h5Api.createSummeryH5(msId)+"");
        }
        if(Strings.isNotBlank(oldMsId) && !msId.equals(oldMsId)){
            summary.setTemplateStatus(MsTemplateStatus.DEFAULT);
        }
        saveOrUpdate(summary);
        summary.setContentVersion(contentService.saveContent(summary));
        summary.setIntroduceVersion(introduceService.saveIntroduce(summary.getMssId(), introduce));
        //如果保存是提交“申请上架”就直接上架并且更新上架时间
        Date now = new Date();
        if (MsAuditStatus.WAIT.equals(summary.getAuditStatus())) {
            summary.setPublishTime(now);
        }
        //管理员创建的直接初始化为下架状态
        if (MsType.SYSTEM_MODULE.equals(summary.getMsType()) || MsType.CUSTOM_ORDER.equals(summary.getMsType())) {
            summary.setAuditStatus(MsAuditStatus.ACTIVE_OFF);
            summary.setWorksLibraryStatus(MsWorksLibraryStatus.ACTIVE_OFF);
            summary.setPublishTime(now);
        }
        updateById(summary);

        //更改相关模版的接口
        updateWorkStatus(summary,msId);


        if (MsSummaryApi.manageMsTypeList.contains(summary.getMsType()) && MsAuditStatus.ACTIVE_ON.equals(summary.getAuditStatus())) {
            //为后台管理，直接上架的商品(自定义指令/组件类型)
            elasticsearch.removeByMssId(summary.getMssId());
            elasticsearch.saveOrUpdate(summary);
        }
    }

    private void updateWorkStatus(MsSummary summary,String msId) {
        MsType msType = summary.getMsType();
        Long mssId = summary.getMssId();//作品ID
        if (MsType.CUSTOM_ORDER.equals(msType)) {//自定义指令
            customCommandApi.updateMssID(msId, mssId);
        } else if (MsType.SYSTEM_MODULE.equals(msType)) {//组件
            moduleManagementApi.updateMssID(Long.parseLong(msId), mssId);
        } else if (MsType.H5_FORM.equals(msType)) {
            h5Api.updateMssID(msId, mssId);
        } else {
            mallTemplateService.updateMssID(msId, mssId);
        }
    }

    @Override
    @Transactional
    public void publishByMssId(Long mssId) {
        cspApi.checkUserPublishAuth(SessionContextUtil.getUserId());
        MsSummary summary = getById(mssId);
        if (Objects.isNull(summary)) {
            throw new BizException("作品已删除");
        }
        if (!MsAuditStatus.ACTIVE_OFF.equals(summary.getAuditStatus())) {
            throw new BizException("作品状态不为已下架，无法上架");
        }
        if(MsTemplateStatus.DELETED.equals(summary.getTemplateStatus())){
            throw new BizException("模版已经被删除，无法上架");
        }
        summary.setAuditStatus(MsAuditStatus.WAIT);
        summary.setContentVersion(contentService.saveContent(summary));
        summary.setPublishTime(new Date());
        updateById(summary);
    }


    @Override
    public MsSummarySnapshot getSnapshot(Long mssId) {
        MsSummary summary = getById(mssId);
        if (Objects.isNull(summary)) {
            throw new BizException("作品不存在");
        }
        MsSummaryContent content = contentService.getByVersion(mssId, summary.getContentVersion());
        MsSummaryIntroduce introduce = introduceService.getByVersion(mssId, summary.getIntroduceVersion());
        return new MsSummarySnapshot(summary, content, introduce);
    }

    @Override
    public List<MsPublisherVo> getPublishVo(MsPage msPage) {
        List<MsPublisherVo> List = getBaseMapper().getPublishVo(msPage);
        userInfoUtil.fillEnterpriseName(List);
        userInfoUtil.fillMangeName(List);
        return List;
    }

    @Override
    public PageResult<MsPageResult> pageQuery(MsPage msPage) {
        Page<MsPageResult> page = PageSupport.getPage(MsPageResult.class, msPage);

        //如果存在csp用户名手机查询 则远程查询用户
        if (StringUtils.hasLength(msPage.getCspQuery())) {
            List<String> cspIds = new ArrayList<>();
            List<UserInfoVo> userInfoVos = cspApi.queryByNameOrPhone(msPage.getCspQuery());
            if (!CollectionUtils.isEmpty(userInfoVos)) {
                Set<String> set = userInfoVos.stream().map(UserInfoVo::getCspId).collect(Collectors.toSet());
                cspIds.addAll(set);
            }
            msPage.setCspId(cspIds);
        }
        List<MsType> msTypes = msPage.getMsTypes();
        if (!CollectionUtils.isEmpty(msTypes)) {
            msPage.setMsTypesCode(msTypes.stream().map(MsType::getCode).collect(Collectors.toList()));
        }
        page = getBaseMapper().pageQuery(page, msPage);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public void delete(Long mssId) {
        MsSummary summary = getById(mssId);
        if (Objects.isNull(summary)) return;
        if (MsAuditStatus.ACTIVE_OFF.equals(summary.getAuditStatus()) || MsAuditStatus.FAILURE.equals(summary.getAuditStatus())) {
            removeById(mssId);

            //更新组件和活动的关系
            deleteSummaryForActivity(Collections.singletonList(summary));

            //更新作品与素材之间的关系
            updateWorkStatusForMS(Collections.singletonList(summary));


            saveProcessingRecord(Collections.singletonList(mssId), "作品删除", "CSP删除作品");
        } else {
            throw new BizException("当前状态不支持删除");
        }

    }

    @Override
    @Transactional
    public void activeOffByCsp(Long mssId) {
        MsSummary summary = getById(mssId);
        if (Objects.isNull(summary)) {
            throw new BizException("作品不存在");
        }
        if (!MsAuditStatus.ACTIVE_ON.equals(summary.getAuditStatus())) {
            throw new BizException("作品状态不为已上架，无法下架");
        }
        if (!SessionContextUtil.verifyCspLogin().equals(summary.getCspId())) {
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }
        activeOff(Collections.singletonList(summary), "csp手动下架");
    }

    @PostConstruct
    public void initESData() {
        List<MsSummary> list =
                lambdaQuery().eq(MsSummary::getWorksLibraryStatus, MsWorksLibraryStatus.ACTIVE_ON)
                        .eq(MsSummary::getAuditStatus, MsAuditStatus.ACTIVE_ON).list();
        if (CollectionUtil.isEmpty(list)) return;
        log.info("作品库初始化es数据");
        elasticsearch.removeAll();
        elasticsearch.saveOrUpdate(list);
        log.info("作品库初始化es数据完成");
    }

    @Override
    public void addLike(Long msId) {
        log.info("点赞作品id {}", msId);
        if (Objects.isNull(msId)) {
            throw new BizException(GlobalErrorCode.BAD_REQUEST.getMsg());
        }
        MsSummary summary = getOne(Wrappers.<MsSummary>lambdaQuery()
                .eq(MsSummary::getMssId, msId)
                .eq(MsSummary::getDeleted, 0));
        if (Objects.isNull(summary)) {
            throw new BizException(GlobalErrorCode.BAD_REQUEST.getMsg());
        }
        likeRecordService.addLikeRecord(msId);
        addLikeCount(summary);
    }

    @Override
    public void addView(Long msId) {
        log.info("点赞作品id {}", msId);
        if (Objects.isNull(msId)) {
            throw new BizException(GlobalErrorCode.BAD_REQUEST.getMsg());
        }
        MsSummary summary = getOne(Wrappers.<MsSummary>lambdaQuery()
                .eq(MsSummary::getMssId, msId)
                .eq(MsSummary::getDeleted, 0));
        if (Objects.isNull(summary)) {
            throw new BizException(GlobalErrorCode.BAD_REQUEST.getMsg());
        }
        viewRecordService.addViewRecord(msId);
        addViewCount(summary);
    }

    public void addViewCount(MsSummary summary) {
        log.info("浏览前 " + summary.getViewCount());
        BigDecimal viewCount = summary.getViewCount().add(new BigDecimal(1));
        log.info("浏览后 " + summary.getLikesCount());
        update(Wrappers.<MsSummary>lambdaUpdate()
                .set(MsSummary::getViewCount, viewCount)
                .eq(MsSummary::getMssId, summary.getMssId()));

        elasticsearch.updateView(summary.getMssId(), viewCount.longValue());
    }

    @Override
    public void cancelLike(Long msId) {
        log.info("取消作品id {}", msId);
        if (Objects.isNull(msId)) {
            throw new BizException(GlobalErrorCode.BAD_REQUEST.getMsg());
        }
        MsSummary summary = getOne(Wrappers.<MsSummary>lambdaQuery()
                .eq(MsSummary::getMssId, msId)
                .eq(MsSummary::getDeleted, 0));
        if (Objects.isNull(summary)) {
            throw new BizException(GlobalErrorCode.BAD_REQUEST.getMsg());
        }
        likeRecordService.cancelLike(msId);
        subLikeCount(summary);
    }

    @Override
    public List<MsSummaryLikeRecord> likeListByMsId(Long msId) {
        List<MsSummaryLikeRecord> list = likeRecordService.list(Wrappers.<MsSummaryLikeRecord>lambdaQuery()
                .eq(MsSummaryLikeRecord::getMssId, msId));
        return list;
    }

    @Override
    public List<MsSummaryViewRecord> viewListByMsId(Long msId) {
        return viewRecordService.list(Wrappers.<MsSummaryViewRecord>lambdaQuery()
                .eq(MsSummaryViewRecord::getMssId, msId));
    }

    @Override
    public MsSummaryDetailVO getDetail(Long mssId,boolean isBoss) {
        MsSummaryDetailVO result = new MsSummaryDetailVO();
        MsSummary summary = getById(mssId);
        if (Objects.isNull(summary)) {
            throw new BizException("作品不存在");
        }
        BeanUtils.copyProperties(summary, result);
        //获取企业名称
        List<UserInfoVo> userInfoVos = cspApi.getByIdList(Collections.singletonList(summary.getCspId()));
        if (!CollectionUtil.isEmpty(userInfoVos)) {
            result.setEnterpriseName(userInfoVos.get(0).getEnterpriseName());
            result.setCreatorName(userInfoVos.get(0).getName());
            result.setEnterpriseAccountName(userInfoVos.get(0).getEnterpriseAccountName());
        }
        //获取表单封面
        if (MsType.H5_FORM.equals(summary.getMsType())) {
            String msId = summary.getMsId();
            if (StringUtils.hasLength(msId)) {//如果是审核中状态，并且是管理平台查看则获取最新的H5信息
                H5Info detailForSummery = h5Api.getDetailForSummery(Long.parseLong(msId));
                H5Info detail = (isBoss && summary.getAuditStatus() == MsAuditStatus.WAIT) ? h5Api.getDetail(detailForSummery.getId()) : detailForSummery;
                if (detail != null) {
                    result.setFormCover(detail.getFormCover());
                }
            }
        }
        MsSummaryIntroduce introduce = introduceService.getLastIntroduce(mssId);
        MsSummaryContent content = contentService.getLastMsSummary(mssId);
        if (Objects.nonNull(introduce)) {
            result.setIntroduce(introduce.getIntroduce());
        }
        if (Objects.nonNull(content)) {
            result.setTemplateJson(content.getMsJson());
        }
        String userId = SessionContextUtil.getUserId();
        MsSummaryLikeRecord record = likeRecordService.lambdaQuery().eq(MsSummaryLikeRecord::getMssId, mssId)
                .eq(MsSummaryLikeRecord::getUserId, userId).one();
        if (Objects.nonNull(record)) {
            result.setIfLike(true);
        }
        //存在订单且状态不为 已取消
        long count = orderMapper.selectCount(new LambdaQueryWrapperX<Order>().eq(Order::getGoodsId, mssId)
                .ne(Order::getPayStatus, PayStatus.cancel));
        result.setPayCount(count);
        Order order = orderMapper.selectOne(new LambdaQueryWrapperX<Order>().eq(Order::getGoodsId, mssId)
                .eq(Order::getCreator, userId)
                .orderByDesc(Order::getCreateTime)
                .last("limit 1"));
        if (Objects.nonNull(order)) {
            result.setPayStatus(order.getPayStatus());
        }
        if(summary.getTemplateStatus() == MsTemplateStatus.DELETED){
            result.setMsId(null);
        }

        return result;
    }

    @Override
    public MsSummaryDetailVO getSummaryDetail(Long mssId, Long msActivityContentId, String pType) {
        MsSummaryDetailVO result = new MsSummaryDetailVO();
        MsSummary summary = getById(mssId);
        if (Objects.isNull(summary)) {
            throw new BizException("作品不存在");
        }
        BeanUtils.copyProperties(summary, result);
        MsSummaryIntroduce introduce = introduceService.getByVersion(mssId, summary.getIntroduceVersion());
        MsSummaryContent content = contentService.getByVersion(mssId, summary.getContentVersion());
        if (Objects.nonNull(introduce)) {
            result.setIntroduce(introduce.getIntroduce());
        }
        if (Objects.nonNull(content)) {
            result.setTemplateJson(content.getMsJson());
        }
        //获取表单封面
        if (MsType.H5_FORM.equals(summary.getMsType())) {
            String msId = summary.getMsId();
            if (StringUtils.hasLength(msId)) {
                H5Info detail = h5Api.getDetailForSummery(Long.parseLong(msId));
                if (detail != null) {
                    result.setFormCover(detail.getFormCover());
                }
            }
        }
        String userId = SessionContextUtil.getUserId();
        MsSummaryLikeRecord record = likeRecordService.lambdaQuery().eq(MsSummaryLikeRecord::getMssId, mssId)
                .eq(MsSummaryLikeRecord::getUserId, userId).one();
        if (Objects.nonNull(record)) {
            result.setIfLike(true);
        }
        //存在订单且状态不为 已取消
        long count = orderMapper.selectCount(new LambdaQueryWrapperX<Order>().eq(Order::getGoodsId, mssId)
                .ne(Order::getPayStatus, PayStatus.cancel));
        result.setPayCount(count);
        Order order = orderMapper.selectOne(new LambdaQueryWrapperX<Order>().eq(Order::getGoodsId, mssId)
                .eq(Order::getCreator, userId)
                .orderByDesc(Order::getCreateTime)
                .last("limit 1"));
        if (Objects.nonNull(order)) {
            result.setPayStatus(order.getPayStatus());
        }

        //获取活动价格
        MsPrice price = msManageActivityContentApi.getPrice(mssId, msActivityContentId, pType);
        //当活动内容不为空的时候才设置活动价格
        if (price.getActivityContent() != null) {
            result.setActivityPrice(price.getPrice());
        }
        result.setDiscountRate(price.getDiscountRate());

        //用户名称/企业名称
        if (StringUtils.hasLength(result.getCspId())) {
            fillCspName(result);
        } else {
            fillAdminName(result);
        }
        return result;
    }

//    /**
//     * 获取活动价格
//     * @param summary 作品信息
//     * @param mssId 作品ID
//     * @param msActivityContentId 当前活动ID
//     * @param pType 价格展示：
//     *              当前价格（original）: 最新的活动价格，和首页价格一样
//     *              活动价格（discount）: 如果通过活动页面进入作品，价格展示为该活动下该作品的价格
//     */
//    private void getActivityPrice(MsSummaryDetailVO result,MsSummary summary, Long mssId, Long msActivityContentId, String pType) {
//        //获取活动信息
//        MsManageActivityContent activityContent;
//        if((msActivityContentId != null && StringUtils.hasLength(pType)) && pType.equals("discount")){
//            activityContent = activityLiApi.getActivityContentById(msActivityContentId);
//        }else{
//            activityContent = activityLiApi.getActivityContentByMssId(mssId);
//        }
//        if (Objects.nonNull(activityContent)) {
//            result.setMsActivityContentId(activityContent.getMsActivityContentId());
//            result.setDiscountRate(activityContent.getDiscountRate());
//            if(PromotionType.FREE.equals(activityContent.getPromotionType())){
//                result.setActivityPrice(new BigDecimal(0));
//            }else if(PromotionType.DISCOUNT.equals(activityContent.getPromotionType())){
//                result.setActivityPrice(multiplyBigDecimalAndDouble(summary.getOriginalPrice(), activityContent.getDiscountRate()));
//            }else{
//                result.setActivityPrice(summary.getOriginalPrice());
//            }
//        }
//
//    }

//    /**
//     * 将 BigDecimal 和 Double 进行乘法运算
//     *
//     * @param bigDecimalValue {@code BigDecimal} 值
//     * @param doubleValue     {@code Double} 值
//     * @return 乘法结果 {@code BigDecimal}
//     */
//    public static BigDecimal multiplyBigDecimalAndDouble(BigDecimal bigDecimalValue, Double doubleValue) {
//        // 将 Double 转换为 BigDecimal
//        BigDecimal doubleAsBigDecimal = BigDecimal.valueOf(doubleValue);
//
//        // 进行乘法运算并返回结果
//        return bigDecimalValue.multiply(doubleAsBigDecimal).setScale(2, RoundingMode.HALF_UP);
//    }

    @Override
    public EsPageResult<MsEsPageResult> pageQueryEs(MsEsPageQuery msPage) {
        return elasticsearch.page(msPage);
    }

    @Override
    public Map<Long, SummaryInfo> queryOnLineSummaryInfo(Integer isOnline) {
        List<MsSummary> msSummaries = lambdaQuery().eq(isOnline == 1, MsSummary::getAuditStatus, MsAuditStatus.ACTIVE_ON)
                .orderByDesc(MsSummary::getCreateTime).list();
        return msSummaries.stream()
                .collect(Collectors.toMap(
                        MsSummary::getMssId,
                        msSummary -> {
                            SummaryInfo summaryInfo = new SummaryInfo();
                            summaryInfo.setName(msSummary.getName());
                            summaryInfo.setCoverFile(msSummary.getCoverFile());
                            summaryInfo.setMsNum(msSummary.getMsNum());
                            return summaryInfo;
                        }
                ));
    }

    private void fillAdminName(MsSummaryDetailVO summary) {
        List<String> userId = Arrays.asList(summary.getCreator());
        List<AdminUserResp> adminUserList = authApi.getAdminUserByUserId(userId);
        Map<String, AdminUserResp> map = adminUserList.stream()
                .collect(Collectors.toMap(AdminUserResp::getUserId, Function.identity(), (s1, s2) -> s2));
        if (CollectionUtils.isEmpty(adminUserList)) return;
        AdminUserResp userResp = map.get(summary.getCreator());
        if (Objects.isNull(userResp)) return;
        summary.setCreatorName(userResp.getAccountName());
        summary.setEnterpriseName(userResp.getFullName());

    }

    private void fillCspName(MsSummaryDetailVO summary) {
        List<String> cspIds = Arrays.asList(summary.getCspId());
        Map<String, UserInfoVo> map = cspApi.getByIdList(cspIds).stream()
                .collect(Collectors.toMap(UserInfoVo::getUserId, Function.identity(), (s1, s2) -> s2));
        UserInfoVo userInfoVo = map.get(summary.getCreator());
        if (Objects.isNull(userInfoVo)) return;
        summary.setEnterpriseName(userInfoVo.getEnterpriseName());
        summary.setCreatorName(userInfoVo.getName());
    }

    public void subLikeCount(MsSummary summary) {
        //点赞数增1
        log.info("点赞前" + summary.getLikesCount());
        BigDecimal likesCount = summary.getLikesCount().subtract(new BigDecimal(1));
        log.info("点赞后" + summary.getLikesCount());
        update(Wrappers.<MsSummary>lambdaUpdate()
                .set(MsSummary::getLikesCount, likesCount)
                .eq(MsSummary::getMssId, summary.getMssId()));

        elasticsearch.updateLike(summary.getMssId(), likesCount.longValue());
    }

    public void addLikeCount(MsSummary summary) {
        //点赞数增1
        log.info("点赞前" + summary.getLikesCount());
        BigDecimal likesCount = summary.getLikesCount().add(new BigDecimal(1));
        log.info("点赞后" + summary.getLikesCount());
        update(Wrappers.<MsSummary>lambdaUpdate()
                .set(MsSummary::getLikesCount, likesCount)
                .eq(MsSummary::getMssId, summary.getMssId()));

        //修改es数据
        elasticsearch.updateLike(summary.getMssId(), likesCount.longValue());
    }

    @Scheduled(cron = "0 0 05 * * ?")
    public void removeESData() {
        List<MsSummary> list =
                lambdaQuery().eq(MsSummary::getWorksLibraryStatus, MsWorksLibraryStatus.ACTIVE_OFF).list();
        if (CollectionUtil.isEmpty(list)) return;
        elasticsearch.removeByMssIds(list.stream().map(MsSummary::getMssId).collect(Collectors.toList()));
    }


    private void saveProcessingRecord(List<Long> mssIds, String content, String remark) {
        List<ProcessingRecordReq> reqList = new ArrayList<>();
        for (Long mssId : mssIds) {
            ProcessingRecordReq req = new ProcessingRecordReq();
            req.setProcessingUserId(SessionContextUtil.getUserId());
            req.setBusinessId(mssId + "");
            req.setBusinessType(BusinessTypeEnum.SCGC.getCode());
            req.setProcessingContent(content);
            req.setRemark(Strings.isBlank(remark) ? "无" : remark);
            reqList.add(req);
        }
        processingRecordApi.addBatchRecord(reqList);
    }
}

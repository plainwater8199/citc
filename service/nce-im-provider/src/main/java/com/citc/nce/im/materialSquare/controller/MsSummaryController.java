package com.citc.nce.im.materialSquare.controller;


import com.citc.nce.authcenter.Utils.UserInfoUtil;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.EsPageResult;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.xss.core.XssCleanIgnore;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.im.mall.template.service.MallTemplateService;
import com.citc.nce.im.materialSquare.service.IMsSummaryService;
import com.citc.nce.im.tempStore.service.IResourcesFormService;
import com.citc.nce.materialSquare.MsManageSuggestApi;
import com.citc.nce.materialSquare.vo.suggest.SuggestListOrderNum;
import com.citc.nce.materialSquare.vo.suggest.resp.SuggestListResp;
import com.citc.nce.robot.api.materialSquare.MsSummaryApi;
import com.citc.nce.robot.api.materialSquare.emums.MsAuditStatus;
import com.citc.nce.robot.api.materialSquare.emums.MsType;
import com.citc.nce.robot.api.materialSquare.entity.MsSummary;
import com.citc.nce.robot.api.materialSquare.entity.MsSummaryLikeRecord;
import com.citc.nce.robot.api.materialSquare.entity.MsSummaryViewRecord;
import com.citc.nce.robot.api.materialSquare.vo.summary.*;
import com.citc.nce.robot.api.materialSquare.vo.summary.req.H5TemplateListQueryReq;
import com.citc.nce.robot.api.materialSquare.vo.summary.req.TemplateListQueryReq;
import com.citc.nce.robot.api.tempStore.bean.manage.GoodsOperate;
import com.citc.nce.robot.api.tempStore.bean.manage.GoodsOperateBatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 素材广场，模板发布汇总 前端控制器
 * </p>
 *
 * @author bydud
 * @since 2024-05-31 09:05:58
 */
@Slf4j
@RestController
public class MsSummaryController implements MsSummaryApi {

    @Autowired
    private IMsSummaryService summaryService;
    @Autowired
    private UserInfoUtil userInfoUtil;
    @Autowired
    private MsManageSuggestApi msManageSuggestApi;
    @Autowired
    private MallTemplateService mallTemplateService;
    @Autowired
    private IResourcesFormService formService;
    @Autowired
    private FileApi fileApi;

    @Override
    public Map<Long, SummaryInfo> queryOnLineSummaryInfo(Integer isOnline) {
        return summaryService.queryOnLineSummaryInfo(isOnline);
    }

    @Override
    public void activeOff(GoodsOperateBatch mssIds) {
        summaryService.activeOff(mssIds);
    }

    @Override
    public void closeTempStorePermission(String cspId) {
        summaryService.activeByCspId(cspId);
    }

    @Override
    public void auditPass(GoodsOperate req) {
        summaryService.auditPass(req);
    }

    @Override
    public void auditFail(GoodsOperate req) {
        summaryService.auditFail(req);
    }

    @Override
    public void saveDraftManage(MsSummarySaveDart dart) {
        summaryService.saveDraft(dart);
    }

    @Override
    @XssCleanIgnore
    public void updateDraftManage(MsSummaryUpdateDart dart) {
        summaryService.updateDraft(dart);
    }

    @Override
    public void deleteList(GoodsOperateBatch batch) {
        summaryService.deleteList(batch);
    }

    @Override
    public MsSummaryDetailVO getDetailManage(Long mssId) {
        return summaryService.getDetail(mssId,true);
    }

    @Override
    public void saveDraft(MsSummarySaveDart dart) {
        dart.setCspId(SessionContextUtil.verifyCspLogin());
        summaryService.saveDraft(dart);
    }

    @Override
    public void updateDraft(MsSummaryUpdateDart dart) {
        dart.setCspId(SessionContextUtil.verifyCspLogin());
        summaryService.updateDraft(dart);
    }

    @Override
    public void publishByMssId(Long mssId) {
        summaryService.publishByMssId(mssId);
    }

    @Override
    public List<MsPublisherVo> getPublishVo(MsPage msPage) {
        return summaryService.getPublishVo(msPage);
    }

    @Override
    public PageResult<MsPageResult> pageQuery(MsPage msPage) {
        PageResult<MsPageResult> result = summaryService.pageQuery(msPage);
        List<MsPageResult> list = result.getList();
        if (!CollectionUtils.isEmpty(list)) {
            userInfoUtil.fillEnterpriseName(list);
            userInfoUtil.fillMangeName(list);
            result.setList(list);
        }
        return result;
    }

    @Override
    public EsPageResult<MsEsPageResult> pageQueryEs(MsEsPageQuery msPage) {
        EsPageResult<MsEsPageResult> result = EsPageResult.empty();
        List<MsEsPageResult> suggestionSummaries = null;
        //如果需要首页推荐，则查询出推荐的素材放入结果中,首页推荐的素材放在最前面
        if (msPage.getMsType() == null && msPage.isNeedSuggestions())
            suggestionSummaries = this.getSuggestionSummaries();
        try {
            msPage.setSuggestions(suggestionSummaries);
            result = summaryService.pageQueryEs(msPage);
        } catch (Throwable e) {
            log.error("es query error : {}", e.getMessage(), e);
        }
        if (suggestionSummaries != null) {
            result.getList().addAll(0, suggestionSummaries);
            result.setTotal(result.getTotal() + suggestionSummaries.size());
        }
        List<MsEsPageResult> list = result.getList();
        if (!CollectionUtils.isEmpty(list)) {
            userInfoUtil.fillEnterpriseName(list);
            userInfoUtil.fillMangeName(list);

            //获取图片的缩略图
            List<String> coverFiles = new ArrayList<>();
            for (MsEsPageResult esPageResult : list) {
                String coverFile = esPageResult.getCoverFile();
                if(StringUtils.hasLength(coverFile)){
                    if(coverFile.length()>50){//如果是用的base64图片，则直接使用
                        esPageResult.setAutoThumbnail(coverFile);
                    }else{
                        coverFiles.add(coverFile);
                    }
                }
            }
            Map<String, String> thumbnailMap = fileApi.getAutoThumbnail(coverFiles);
            for (MsEsPageResult esPageResult : list) {
                if(!StringUtils.hasLength(esPageResult.getAutoThumbnail())){
                    esPageResult.setAutoThumbnail(thumbnailMap.get(esPageResult.getCoverFile()));
                }
            }
            result.setList(list);
        }
        return result;
    }

    private List<MsEsPageResult> getSuggestionSummaries() {
        SuggestListResp suggestListResp = msManageSuggestApi.listOrderNum();
        if (suggestListResp != null && suggestListResp.getChangeNums() != null && !suggestListResp.getChangeNums().isEmpty()) {
            List<SuggestListOrderNum> changeNums = suggestListResp.getChangeNums();

            List<Long> suggestMsIds = changeNums.stream()
                    .map(SuggestListOrderNum::getMssId)
                    //对推荐作品去重
                    .distinct()
                    .collect(Collectors.toList());
            List<MsEsPageResult> suggestions = summaryService.listByIds(suggestMsIds).stream()
                    //listByIds查询结果跟参数ids顺序不一致，再排序一次使其对齐
                    .sorted(Comparator.comparingInt(summary -> suggestMsIds.indexOf(summary.getMssId())))
                    .map(summary -> {
                        MsEsPageResult esPageResult = new MsEsPageResult();
                        BeanUtils.copyProperties(summary, esPageResult);
                        esPageResult.setLikeNum(summary.getLikesCount().longValue());
                        esPageResult.setViewNum(summary.getViewCount().longValue());
                        return esPageResult;
                    })
                    .collect(Collectors.toList());

            List<MsEsPageResult> suggestionsAll = new ArrayList<>();
            Map<Long, MsEsPageResult> suggestionMap = suggestions.stream().collect(Collectors.toMap(MsEsPageResult::getMssId, i -> i));
            for (SuggestListOrderNum suggestListOrderNum : changeNums) {
                if (suggestionMap.containsKey(suggestListOrderNum.getMssId())) {
                    suggestionsAll.add(suggestionMap.get(suggestListOrderNum.getMssId()));
                } else {
                    if (MsType.ACTIVITY.equals(suggestListOrderNum.getSuggestType())) {
                        MsEsPageResult esPageResult = new MsEsPageResult();
                        esPageResult.setMssId(suggestListOrderNum.getMssId());
                        esPageResult.setMsType(MsType.ACTIVITY);
                        esPageResult.setH5Id(suggestListOrderNum.getH5Id());
                        esPageResult.setName(suggestListOrderNum.getMssName());
                        esPageResult.setCoverFile(suggestListOrderNum.getMssCoverFile());
                        suggestionsAll.add(esPageResult);
                    }
                }
            }
            return suggestionsAll;
        }
        return null;
    }

    @Override
    public void delete(Long mssId) {
        summaryService.delete(mssId);
    }

    @Override
    public MsSummaryDetailVO getDetail(Long mssId) {
        return summaryService.getDetail(mssId,false);
    }

    @Override
    public void activeOffByCsp(Long mssId) {
        summaryService.activeOffByCsp(mssId);
    }

    @Override
    public MsSummary getByMssId(Long mssId) {
        return summaryService.getById(mssId);
    }

    @Override
    public List<MsSummary> listByIds(Collection<Long> ids) {
        return summaryService.listByIds(ids);
    }

    @Override
    public void activeMangeOn(GoodsOperateBatch mssIds) {
        summaryService.activeMangeOn(mssIds);
    }

    @Override
    public void addLike(Long msId) {
        summaryService.addLike(msId);
    }

    @Override
    public void addView(Long msId) {
        summaryService.addView(msId);
    }

    @Override
    public void cancelLike(Long msId) {
        summaryService.cancelLike(msId);
    }

    @Override
    public List<MsSummaryLikeRecord> likeListByMsId(Long msId) {
        return summaryService.likeListByMsId(msId);
    }

    @Override
    public List<MsSummaryViewRecord> viewListByMsId(Long msId) {
        return summaryService.viewListByMsId(msId);
    }

    @Override
    public MsSummaryDetailVO getSummaryDetail(Long mssId, Long msActivityContentId, String pType) {
        MsSummaryDetailVO detail = summaryService.getSummaryDetail(mssId, msActivityContentId, pType);
        if (Objects.isNull(detail.getAuditStatus()) || !MsAuditStatus.ACTIVE_ON.equals(detail.getAuditStatus())) {
            throw new BizException("作品未上架，无法查看");
        }
        //增加浏览量
        summaryService.addView(mssId);
        return detail;
    }

    @Override
    public MsSummary getEsSummaryDetail(Long mssId) {
        return summaryService.lambdaQuery().eq(MsSummary::getMssId, mssId)
                .select(MsSummary::getLikesCount, MsSummary::getViewCount).one();
    }

    @Override
    public List<String> getTagList() {
        List<MsSummary> list =
                summaryService.lambdaQuery().eq(MsSummary::getAuditStatus, MsAuditStatus.ACTIVE_ON).list();
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        List<String> collect =
                list.stream().filter(i -> StringUtils.hasLength(i.getMsTag())).map(MsSummary::getMsTag).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)) {
            return Collections.emptyList();
        }
        return collect.stream().flatMap(s -> Arrays.stream(s.split(","))).distinct()
                .sorted((tag1, tag2) -> {
                    char firstChar1 = tag1.charAt(0);
                    char firstChar2 = tag2.charAt(0);
                    return Character.compare(firstChar1, firstChar2);
                }).collect(Collectors.toList());
    }

    @Override
    public List<TemplateInfo> templateListQuery(TemplateListQueryReq req) {
        return mallTemplateService.templateListQuery(req);
    }

    @Override
    public List<H5TemplateInfo> h5TemplateListQuery(H5TemplateListQueryReq req) {
        return formService.h5TemplateListQuery(req);
    }

    @Override
    public void notifyTemplateDelete(MsType msType, String msId) {
        summaryService.notifyTemplateDelete(msType, msId);
    }

    @Override
    public void notifyTemplateUpgrade(MsType msType, String msId) {
        summaryService.notifyTemplateUpgrade(msType, msId);
    }
}


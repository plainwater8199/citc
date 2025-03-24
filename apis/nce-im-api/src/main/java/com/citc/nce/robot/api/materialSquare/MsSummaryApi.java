package com.citc.nce.robot.api.materialSquare;

import com.citc.nce.common.core.pojo.EsPageResult;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.api.materialSquare.emums.MsType;
import com.citc.nce.robot.api.materialSquare.entity.MsSummary;
import com.citc.nce.robot.api.materialSquare.entity.MsSummaryLikeRecord;
import com.citc.nce.robot.api.materialSquare.entity.MsSummaryViewRecord;
import com.citc.nce.robot.api.materialSquare.vo.summary.*;
import com.citc.nce.robot.api.materialSquare.vo.summary.req.H5TemplateListQueryReq;
import com.citc.nce.robot.api.materialSquare.vo.summary.req.TemplateListQueryReq;
import com.citc.nce.robot.api.tempStore.bean.manage.GoodsOperate;
import com.citc.nce.robot.api.tempStore.bean.manage.GoodsOperateBatch;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 素材广场-素材api
 *
 * @author bydud
 * @since 9:56
 */

@FeignClient(value = "im-service", contextId = "gooDsApi", url = "${im:}")
public interface MsSummaryApi {
    List<MsType> cspMsTypeList = Arrays.asList(MsType.ROBOT, MsType.NR_SSG, MsType.H5_FORM);
    List<MsType> manageMsTypeList = Arrays.asList(MsType.CUSTOM_ORDER, MsType.SYSTEM_MODULE);

    @PostMapping("/ms/queryOnLineSummaryInfo/{isOnline}")
    @ApiOperation("查询所有上线的作品")
    Map<Long, SummaryInfo> queryOnLineSummaryInfo(@PathVariable("isOnline") Integer isOnline);

    //管理端
    @PostMapping("/ms/manage/activeOff")
    @ApiOperation("manage-下架商品")
    void activeOff(@RequestBody @Valid GoodsOperateBatch mssIds);

    @PostMapping("/ms/manage/closeTempStorePermission/{cspId}")
    @ApiOperation("manage-关闭模版发布")
    void closeTempStorePermission(@PathVariable("cspId") String cspId);

    @PostMapping("/ms/manage/auditPass")
    @ApiOperation("manage-审核通过")
    void auditPass(@RequestBody @Valid GoodsOperate req);

    @PostMapping("/ms/manage/auditFail")
    @ApiOperation("manage-审核不通过")
    void auditFail(@RequestBody @Valid GoodsOperate req);

    @PostMapping("/ms/manage/saveDraft")
    @ApiOperation(value = "manage-保存草稿", notes = "manage-保存草稿")
    void saveDraftManage(@RequestBody @Valid MsSummarySaveDart dart);

    @PostMapping("/ms/manage/updateDraft")
    @ApiOperation(value = "manage-更新草稿", notes = "manage-更新草稿")
    void updateDraftManage(@RequestBody @Valid MsSummaryUpdateDart dart);

    @PostMapping("/ms/summary/goods/manage/delete")
    @ApiOperation("manage-删除商品")
    void deleteList(@RequestBody @Valid GoodsOperateBatch batch);

    @PostMapping("/ms/manage/getDetailManage/{mssId}")
    @ApiOperation(value = "manage-查看作品详情", notes = "查看作品详情")
    MsSummaryDetailVO getDetailManage(@PathVariable("mssId") Long mssId);

    @PostMapping("/ms/manage/audit/activeOn")
    @ApiOperation(value = "manage-上架作品", notes = "manage-上架作品")
    void activeMangeOn(@RequestBody @Valid GoodsOperateBatch mssIds);

    //csp端

    @PostMapping("/ms/summary/saveDraft")
    @ApiOperation(value = "保存草稿", notes = "保存草稿")
    void saveDraft(@RequestBody @Valid MsSummarySaveDart dart);

    @PostMapping("/ms/summary/updateDraft")
    @ApiOperation(value = "更新草稿", notes = "更新草稿")
    void updateDraft(@RequestBody @Valid MsSummaryUpdateDart dart);

    @PostMapping("/ms/summary/publishByMssId/{mssId}")
    @ApiOperation(value = "发布/上架", notes = "发布")
    void publishByMssId(@PathVariable("mssId") Long mssId);

    @PostMapping("/ms/summary/getPublishVo")
    @ApiOperation(value = "csp分页查询")
    List<MsPublisherVo> getPublishVo(@RequestBody MsPage msPage);

    @PostMapping("/ms/summary/pageQuery")
    @ApiOperation(value = "base分页查询")
    PageResult<MsPageResult> pageQuery(@RequestBody MsPage msPage);

    @PostMapping("/ms/summary/pageQueryEs")
    @ApiOperation(value = "base分页查询--es查询")
    EsPageResult<MsEsPageResult> pageQueryEs(@RequestBody MsEsPageQuery msPage);

    @PostMapping("/ms/summary/delete/{mssId}")
    @ApiOperation(value = "删除", notes = "删除")
    void delete(@PathVariable("mssId") Long mssId);

    @PostMapping("/ms/summary/getDetail/{mssId}")
    @ApiOperation(value = "查看作品详情", notes = "查看作品详情")
    MsSummaryDetailVO getDetail(@PathVariable("mssId") Long mssId);

    @PostMapping("/ms/summary/activeOffCsp/{mssId}")
    @ApiOperation(value = "下架", notes = "下架")
    void activeOffByCsp(@PathVariable("mssId") Long mssId);

    @GetMapping("/ms/summary/getById/{mssId}")
    MsSummary getByMssId(@PathVariable("mssId") Long mssId);

    @PostMapping("/ms/summary/listByIds")
    List<MsSummary> listByIds(@RequestBody Collection<Long> mssIds);

    @PostMapping("/ms/summary/like/addLike/{msId}")
    @ApiOperation(value = "点赞", notes = "点赞")
    void addLike(@PathVariable("msId") Long msId);

    @PostMapping("/ms/summary/view/addView/{msId}")
    @ApiOperation(value = "增加浏览量", notes = "增加浏览量")
    void addView(@PathVariable("msId") Long msId);

    @PostMapping("/ms/summary/like/cancelLike/{msId}")
    @ApiOperation(value = "取消点赞", notes = "取消点赞")
    void cancelLike(@PathVariable("msId") Long msId);

    @GetMapping("/ms/summary/like/list/{msId}")
    @ApiOperation(value = "点赞列表", notes = "点赞列表")
    List<MsSummaryLikeRecord> likeListByMsId(@PathVariable("msId") Long msId);

    @GetMapping("/ms/summary/view/list/{msId}")
    @ApiOperation(value = "浏览记录列表", notes = "浏览记录列表")
    List<MsSummaryViewRecord> viewListByMsId(@PathVariable("msId") Long msId);

    /**
     * 获取作品详情
     *
     * @param mssId               作平ID
     * @param msActivityContentId 活动关联ID
     * @param pType               价格展示：
     *                            当前价格（original）: 最新的活动价格，和首页价格一样
     *                            活动价格（discount）: 如果通过活动页面进入作品，价格展示为该活动下该作品的价格
     * @return 作品详情
     */
    //客户端
    @GetMapping("/customer/getSummaryDetail/{mssId}")
    @ApiOperation("客户-查看作品详情")
    MsSummaryDetailVO getSummaryDetail(@PathVariable("mssId") Long mssId, @RequestParam(name = "msActivityContentId", required = false) Long msActivityContentId, @RequestParam(name = "pType", required = false) String pType);


    //es
    @GetMapping("/ms/es/getEsSummaryDetail/{mssId}")
    @ApiOperation("es-获取浏览/点赞量")
    MsSummary getEsSummaryDetail(@PathVariable("mssId") Long mssId);

    @PostMapping("/customer/getTagList")
    @ApiOperation(value = "客户-素材广场首页--标签查询")
    List<String> getTagList();


    @PostMapping("/ms/summary/template/list")
    @ApiOperation(value = "机器人、5G消息模版列表查询")
    List<TemplateInfo> templateListQuery(@RequestBody @Valid TemplateListQueryReq req);

    @PostMapping("/ms/summary/h5/template/list")
    @ApiOperation(value = "H5列表查询")
    List<H5TemplateInfo> h5TemplateListQuery(@RequestBody H5TemplateListQueryReq req);

    @PostMapping("/ms/summary/template/deleteNotify")
    @ApiOperation(value = "通知模板删除")
    void notifyTemplateDelete(@RequestParam("msType") MsType msType, @RequestParam("msId") String msId);

    @PostMapping("/ms/summary/template/upgradeNotify")
    @ApiOperation(value = "通知模板修改")
    void notifyTemplateUpgrade(@RequestParam("msType") MsType msType, @RequestParam("msId") String msId);

}

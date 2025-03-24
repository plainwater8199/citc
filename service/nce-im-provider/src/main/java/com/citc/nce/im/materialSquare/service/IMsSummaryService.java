package com.citc.nce.im.materialSquare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.common.core.pojo.EsPageResult;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.im.materialSquare.entity.MsSummarySnapshot;
import com.citc.nce.robot.api.materialSquare.emums.MsType;
import com.citc.nce.robot.api.materialSquare.entity.MsSummary;
import com.citc.nce.robot.api.materialSquare.entity.MsSummaryLikeRecord;
import com.citc.nce.robot.api.materialSquare.entity.MsSummaryViewRecord;
import com.citc.nce.robot.api.materialSquare.vo.summary.*;
import com.citc.nce.robot.api.tempStore.bean.manage.GoodsOperate;
import com.citc.nce.robot.api.tempStore.bean.manage.GoodsOperateBatch;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 素材广场，发布汇总 服务类
 * </p>
 *
 * @author bydud
 * @since 2024-05-31 09:05:58
 */
public interface IMsSummaryService extends IService<MsSummary> {

    /**
     * 根据素材类型和id查询素材
     *
     * @param msType 素材类型
     * @param msId   素材id
     * @return null MsSummary
     */
    MsSummary getByMsTypeAndMsId(MsType msType, @NotEmpty String msId);


    /**
     * 把模板状态设置为更新状态
     *
     * @param msType 素材类型
     * @param msId   素材id
     */
    void notifyTemplateUpgrade(MsType msType, String msId);

    /**
     * 模板删除通知素材管理
     *
     * @param msType 素材类型
     * @param msId   素材id
     */
    void notifyTemplateDelete(MsType msType, String msId);

    /**
     * 下架模板素材
     *
     * @param mssIds
     */
    void activeOff(GoodsOperateBatch mssIds);

    void activeByCspId(String cspId);

    /**
     * 管理员审核
     */
    void auditPass(GoodsOperate req);

    /**
     * 管理员上架（只支持 组件，自定义指令）
     *
     */
    void activeMangeOn(GoodsOperateBatch mssIds);

    void auditFail(GoodsOperate req);

    /**
     * 发布模板素材
     */
    void saveDraft(MsSummarySaveDart dart);

    void updateDraft(MsSummaryUpdateDart dart);

    void publishByMssId(Long mssId);

    /**
     * 查询素材发布时的所以数据
     *
     * @param mssId id
     */
    MsSummarySnapshot getSnapshot(Long mssId);


    /**
     * 根据分页参数查询发布者列表
     *
     * @param msPage 分页参数
     * @return 发布者id和名称
     */
    List<MsPublisherVo> getPublishVo(MsPage msPage);

    PageResult<MsPageResult> pageQuery(MsPage msPage);

    void delete(Long mssId);

    void activeOffByCsp(Long mssId);

    void deleteList(GoodsOperateBatch batch);

    void addLike(Long msId);

    void addView(Long msId);

    void cancelLike(Long msId);

    List<MsSummaryLikeRecord> likeListByMsId(Long msId);

    List<MsSummaryViewRecord> viewListByMsId(Long msId);

    /**
     * 获取详细信息
     *
     * @param mssId mss id
     */
    MsSummaryDetailVO getDetail(Long mssId,boolean isBoss);

    MsSummaryDetailVO getSummaryDetail(Long mssId, Long msActivityContentId, String pType);

    EsPageResult<MsEsPageResult> pageQueryEs(MsEsPageQuery msPage);

    Map<Long, SummaryInfo> queryOnLineSummaryInfo(Integer isOnline);
}

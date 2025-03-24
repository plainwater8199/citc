package com.citc.nce.materialSquare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.materialSquare.entity.MsManageActivityContent;
import com.citc.nce.materialSquare.entity.MsManageActivityLi;
import com.citc.nce.materialSquare.vo.MsSummaryDiscountPrice;
import com.citc.nce.materialSquare.vo.activity.*;
import com.citc.nce.materialSquare.vo.activity.resp.MsManageActivityContentResp;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 素材广场_后台管理_参与活动的素材 服务类
 * </p>
 *
 * @author bydud
 * @since 2024-05-14 02:05:36
 */
public interface IMsManageActivityLiService extends IService<MsManageActivityLi> {

    /**
     * 查询活动返回 对应的作品列表
     *
     * @param pageQuery 查询参数
     * @return 价格和销售方信息
     */
    PageResult<LiPageResult> liPage(LiPageQuery pageQuery);

    /**
     * 查询 作品全部活动价格（不考虑生效）
     *
     * @param mssId 作品id
     * @return 指方案生效时的应该是的价格
     */
    List<MsSummaryDiscountPrice> getDiscountPrice(Long mssId);


    /**
     * 查询 作品指定活动价格（不考虑生效）
     *
     * @param mssId 作品id
     * @return 指方案生效时的应该是的价格
     */
    MsSummaryDiscountPrice getDiscountPrice(Long mssId, Long msActivityContentId);

    /**
     * 查询 作品指定活动方案中的价格（没剩下返回当前价格）
     *
     * @param mssId 作品id
     * @return 指方案当前的价格
     */
    MsSummaryDiscountPrice getEffectDiscountPrice(Long mssId, Long msActivityContentId);


    /**
     * 查询 作品最新价格（当前价格）
     *
     * @param mssId 作品id
     * @return 甲方说的是，当前生效的活动价格，，，，存在多个活动都在生效中，使用最新创建的活动的价格
     */
    MsSummaryDiscountPrice getLastEffectDiscountPrice(Long mssId);


    void addLi(PutAndRemove putAndRemove);

    /**
     * 通过商品id获取活动
     *
     * @param mssId mss id
     * @return {@code MsManageActivity }
     */
    MsManageActivityContent getActivityContentByMssId(Long mssId);

    List<SummaryInfoForActivityContent> getSummaryInfoForActivity(Long msActivityContentId);

    Map<Long, Integer> queryCountForContentId();

    List<LiDetailResult> listByLiId(MssForLiReq req);

    void deleteSummaryForActivity(List<Long> mssIds);

    MsManageActivityContent getActivityContentById(Long msActivityContentId);
}

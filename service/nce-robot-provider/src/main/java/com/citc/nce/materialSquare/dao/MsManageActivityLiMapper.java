package com.citc.nce.materialSquare.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.materialSquare.entity.MsManageActivityLi;
import com.citc.nce.materialSquare.vo.MsSummaryDiscountPrice;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 素材广场_后台管理_参与活动的素材 Mapper 接口
 * </p>
 *
 * @author bydud
 * @since 2024-05-14 02:05:36
 */
public interface MsManageActivityLiMapper extends BaseMapper<MsManageActivityLi> {

    /**
     * 查询作品关联的活动id,创建时间，创建人，策略
     *
     * @param mssId 作品id
     */
    List<MsSummaryDiscountPrice> selectActivityLiDiscountPriceInfo(@Param("mssId") Long mssId);
}

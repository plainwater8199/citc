package com.citc.nce.im.materialSquare.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.robot.api.materialSquare.entity.MsSummary;
import com.citc.nce.robot.api.materialSquare.entity.MsSummaryLikeRecord;
import com.citc.nce.robot.api.materialSquare.vo.summary.MsPage;
import com.citc.nce.robot.api.materialSquare.vo.summary.MsPageResult;
import com.citc.nce.robot.api.materialSquare.vo.summary.MsPublisherVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 素材广场，发布汇总 Mapper 接口
 * </p>
 *
 * @author bydud
 * @since 2024-05-31 09:05:58
 */
@Mapper
public interface MsSummaryLikeRecordMapper extends BaseMapper<MsSummaryLikeRecord> {


}

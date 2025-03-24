package com.citc.nce.robotfile.mapper;

import com.citc.nce.filecenter.vo.ExamineOneReq;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import com.citc.nce.robotfile.entity.ExamineResultDo;
import com.citc.nce.vo.StatusTotal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年8月16日09:56:05
 * @Version: 1.0
 * @Description: ExamineResult
 */
@Mapper
public interface ExamineResultMapper extends BaseMapperX<ExamineResultDo> {
    List<ExamineOneReq> getOverdueIds(@Param(value = "date") Date date);

    void updateStatus(ExamineResultDo examineResultDo);

    Integer verification(@Param("fileIds") List<String> fileIds,@Param("operators") List<String> operators);

    List<StatusTotal> countPicture(@Param("operator") String operator, @Param("creator") String creator);

    List<StatusTotal> countVideo(@Param("operator") String operator, @Param("creator") String creator);

    List<StatusTotal> countAudio(@Param("operator") String operator, @Param("creator") String creator);

    List<StatusTotal> countFile(@Param("operator") String operator, @Param("creator") String creator);
}

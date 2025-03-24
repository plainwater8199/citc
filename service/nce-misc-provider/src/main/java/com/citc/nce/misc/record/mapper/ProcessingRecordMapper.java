package com.citc.nce.misc.record.mapper;

import com.citc.nce.misc.record.dto.ProcessingRecordDto;
import com.citc.nce.misc.record.entity.ProcessingRecordDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: litao
 * @Contact: taolifr
 * @Date: 2022/11/29 15:26
 * @Version: 1.0
 * @Description:
 */
@Mapper
public interface ProcessingRecordMapper extends BaseMapperX<ProcessingRecordDo> {
    List<ProcessingRecordDto> findProcessingRecordList(ProcessingRecordDto processingRecordDto);
}

package com.citc.nce.misc.dictionary.mapper;


import com.citc.nce.misc.dictionary.entity.DataDictionaryDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022/6/28 16:04
 * @Version: 1.0
 * @Description:
 */
@Mapper
public interface DataDictionaryMapper extends BaseMapperX<DataDictionaryDo> {
    void updateDeletedByDicTypeId(Integer deleted);
}

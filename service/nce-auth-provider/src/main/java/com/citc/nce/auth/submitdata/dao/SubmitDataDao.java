package com.citc.nce.auth.submitdata.dao;

import com.citc.nce.auth.sharelink.entity.ShareLinkDo;
import com.citc.nce.auth.submitdata.entity.SubmitDataDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: yangchuang
 * @Date: 2022/10/9 21:38
 * @Version: 1.0
 * @Description:
 */
public interface SubmitDataDao extends BaseMapperX<SubmitDataDo> {

    @Update("UPDATE submit_data SET delete_time=#{deleteTime},deleted=#{deleted} WHERE id=#{id}")
    int delSubmitDataById(HashMap<String, Object> map);

    @Select("SELECT form_id AS formId,COUNT(*) AS nums FROM submit_data WHERE  deleted=0 GROUP BY form_id")
    List<Map<String, Long>> selectCountByFormId();
}

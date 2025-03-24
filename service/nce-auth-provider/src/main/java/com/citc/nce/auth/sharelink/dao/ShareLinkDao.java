package com.citc.nce.auth.sharelink.dao;

import com.citc.nce.auth.sharelink.entity.ShareLinkDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Update;

import java.util.HashMap;

/**
 * @Author: yangchuang
 * @Date: 2022/10/9 21:38
 * @Version: 1.0
 * @Description:
 */
public interface ShareLinkDao extends BaseMapperX<ShareLinkDo> {

    @Update("UPDATE share_link SET delete_time=#{deleteTime},deleted=#{deleted} WHERE id=#{id}")
    int delShareLinkById(HashMap<String, Object> map);
}

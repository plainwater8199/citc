package com.citc.nce.robotfile.mapper;


import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import com.citc.nce.robotfile.entity.PictureDo;
import com.citc.nce.vo.GroupResp;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: PictureMapper
 */
@Repository
public interface PictureMapper extends BaseMapperX<PictureDo> {
    List<GroupResp> countByGroupId(String creator);

    void updateGroupId(Long id);
}

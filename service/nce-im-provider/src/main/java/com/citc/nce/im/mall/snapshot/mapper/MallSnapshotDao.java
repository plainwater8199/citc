package com.citc.nce.im.mall.snapshot.mapper;

import com.citc.nce.im.mall.snapshot.entity.MallSnapshotDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import com.citc.nce.im.mall.snapshot.entity.ThumbnailResp;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 18:12
 */
public interface MallSnapshotDao extends BaseMapperX<MallSnapshotDo> {
    @Select("SELECT card_style_content from mall_snapshot WHERE snapshot_uuid = #{snapshotUuid}")
    String selectCardStyleContent(@Param("snapshotUuid") String snapshotUuid);


    @Select({
            "<script>",
            "SELECT snapshot_uuid, ifnull(JSON_UNQUOTE(JSON_EXTRACT(snapshot_content, '$.fiveG.thumbnail')),'') AS " +
                    "thumbnail",
            "FROM mall_snapshot",
            "WHERE deleted = 0 ",
            "AND snapshot_uuid IN",
            "<foreach item='id' collection='snapshotUuids' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    List<ThumbnailResp> get5GThumbnailBySnapshotUuid(@Param("snapshotUuids") List<String> snapshotUuids);
}

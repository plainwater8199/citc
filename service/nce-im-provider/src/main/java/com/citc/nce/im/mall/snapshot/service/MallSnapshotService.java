package com.citc.nce.im.mall.snapshot.service;

import com.citc.nce.im.mall.snapshot.entity.MallSnapshotDo;
import com.citc.nce.robot.api.mall.common.MallCommonContent;
import com.citc.nce.robot.api.mall.snapshot.req.MallRobotOrderSaveOrUpdateReq;
import com.citc.nce.robot.api.mall.snapshot.resp.MallRobotOrderSaveOrUpdateResp;

import java.util.List;
import java.util.Map;

/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/21 17:00
 */
public interface MallSnapshotService {

    String dealWithContent(MallCommonContent content, Integer snapshotType);

    MallSnapshotDo queryDetail(String uuid);
    MallCommonContent queryContent(String snapshotUuid);

    MallRobotOrderSaveOrUpdateResp saveOrUpdateSnapshot(MallRobotOrderSaveOrUpdateReq req);

    MallSnapshotDo setCareStyle(String snapshotUuid);

    String getCardStyleContent(String snapshotUuid);

    /**
     * 根据快照uuid 获取5g消息的缩略图id
     *
     * @param strings 字符串
     * @return {@code Map<String, String> }
     */
    Map<String, String> get5GThumbnailBySnapshotUuid(List<String> strings);
}

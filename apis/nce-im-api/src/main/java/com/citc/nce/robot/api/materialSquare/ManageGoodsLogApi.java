package com.citc.nce.robot.api.materialSquare;

import com.citc.nce.robot.api.tempStore.domain.GoodsManageLog;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author bydud
 * @since 10:35
 */

@FeignClient(value = "im-service", contextId = "manageGoodsLogApi", url = "${im:}")
public interface ManageGoodsLogApi {
    @GetMapping("/tempStore/goods/manage/oplog/{goodsId}")
    public List<GoodsManageLog> listByGoodsId(@PathVariable("goodsId") Long goodsId);
}

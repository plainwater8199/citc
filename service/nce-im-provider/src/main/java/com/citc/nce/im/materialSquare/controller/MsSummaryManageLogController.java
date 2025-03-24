package com.citc.nce.im.materialSquare.controller;


import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citc.nce.authcenter.auth.AdminAuthApi;
import com.citc.nce.authcenter.auth.vo.resp.AdminUserResp;
import com.citc.nce.im.materialSquare.service.IGoodsManageLogService;
import com.citc.nce.robot.api.materialSquare.ManageGoodsLogApi;
import com.citc.nce.robot.api.tempStore.domain.GoodsManageLog;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品操作日志 前端控制器
 * </p>
 *
 * @author bydud
 * @since 2023-11-27 10:11:42
 */
@RestController
@Api(value = "后台商品操作日志")
@AllArgsConstructor
public class MsSummaryManageLogController implements ManageGoodsLogApi {
    private final IGoodsManageLogService logService;
    private final AdminAuthApi authApi;

    @Override
    public List<GoodsManageLog> listByGoodsId(Long goodsId) {
        List<GoodsManageLog> list = logService.list(new LambdaQueryWrapper<GoodsManageLog>()
                .eq(GoodsManageLog::getGoodsId, goodsId)
                .orderByDesc(GoodsManageLog::getCreateTime)
                .orderByDesc(GoodsManageLog::getLogId));
        fillCreatorName(list);
        return list;
    }

    private void fillCreatorName(List<GoodsManageLog> list) {
        if (CollectionUtils.isEmpty(list)) return;
        List<String> userId = list.stream().map(GoodsManageLog::getCreator).distinct().collect(Collectors.toList());
        List<AdminUserResp> userInfos = authApi.getAdminUserByUserId(userId);
        Map<String, String> map = CollectionUtils.isEmpty(userInfos) ? MapUtil.empty() : userInfos.stream()
                .collect(Collectors.toMap(AdminUserResp::getUserId, AdminUserResp::getAccountName));
        for (GoodsManageLog log : list) {
            log.setCreatorName(map.getOrDefault(log.getCreator(), ""));
        }
    }
}


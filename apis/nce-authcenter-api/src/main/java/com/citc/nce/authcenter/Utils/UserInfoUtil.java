package com.citc.nce.authcenter.Utils;

import com.citc.nce.authcenter.auth.AdminAuthApi;
import com.citc.nce.authcenter.auth.vo.resp.AdminUserResp;
import com.citc.nce.authcenter.csp.CspApi;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.common.bean.CspNameBase;
import lombok.AllArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author bydud
 * @since 2024/6/21 15:38
 */
@Component
@AllArgsConstructor
@Order()
public class UserInfoUtil {

    private final CspApi cspApi;
    private final AdminAuthApi adminAuthApi;

    /**
     * 填充csp（买商品）企业名称 和 用户名
     *
     * @param list 数据
     */
    public <T extends CspNameBase> void fillEnterpriseName(List<T> list) {
        if (CollectionUtils.isEmpty(list)) return;
        Set<String> set = list.stream().map(T::getCreator).collect(Collectors.toSet());
        List<String> cspIds = cspApi.obtainCspIds(set);
        if (CollectionUtils.isEmpty(cspIds)) return;
        Map<String, UserInfoVo> map = cspApi.getByIdList(cspIds).stream()
                .collect(Collectors.toMap(UserInfoVo::getUserId, Function.identity(), (s1, s2) -> s2));
        list.forEach(s -> {
            UserInfoVo userInfo = map.get(s.getCreator());
            if (Objects.nonNull(userInfo)) {
                s.setEnterpriseName(userInfo.getEnterpriseName());
                s.setCreatorName(userInfo.getName());
            }
        });
    }

    /**
     * 填充管理员名称
     *
     * @param list 数据
     */
    public <T extends CspNameBase> void fillMangeName(List<T> list) {
        if (CollectionUtils.isEmpty(list)) return;
        List<String> set = list.stream().map(T::getCreator).distinct().collect(Collectors.toList());
        List<AdminUserResp> adminUserList = adminAuthApi.getAdminUserByUserId(set);
        if (CollectionUtils.isEmpty(adminUserList)) return;
        Map<String, AdminUserResp> map = adminUserList.stream()
                .collect(Collectors.toMap(AdminUserResp::getUserId, Function.identity(), (s1, s2) -> s2));
        list.forEach(s -> {
            if (StringUtils.hasLength(s.getCreator())) {
                AdminUserResp admin = map.get(s.getCreator());
                if (Objects.nonNull(admin)) {
                    s.setCreatorName(admin.getAccountName());
                    s.setEnterpriseName(admin.getFullName());
                }
            }
        });
    }
}

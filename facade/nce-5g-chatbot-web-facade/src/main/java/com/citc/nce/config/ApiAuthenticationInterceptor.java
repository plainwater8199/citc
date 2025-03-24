package com.citc.nce.config;

import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.permission.ApiPermissionApi;
import com.citc.nce.authcenter.permission.enums.Permission;
import com.citc.nce.authcenter.permission.vo.GetUrlPermissionReq;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.RestResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.web.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * api鉴权拦截器,必须在token拦截器之后（需要用户信息进行鉴权）
 *
 * @author jiancheng
 */
@Slf4j
@Order //默认最后
public class ApiAuthenticationInterceptor implements HandlerInterceptor {

    @Resource
    private ApiPermissionApi apiPermissionApi;
    @Resource
    private CspCustomerApi cspCustomerApi;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpMethod method = HttpMethod.resolve(request.getMethod());
        if (method == HttpMethod.OPTIONS)
            return true;
        String requestURI = request.getRequestURI();
        BaseUser user = SessionContextUtil.getUser();
        List<Permission> urlPermission = apiPermissionApi.getUrlPermission(new GetUrlPermissionReq().setUri(requestURI));
        //如果接口没有配置权限，则直接放行
        if (CollectionUtils.isEmpty(urlPermission))
            return true;
        String userPermission = cspCustomerApi.getUserPermission(user.getUserId());
        if (userPermission == null) {
            ResponseUtils.sendJson(response, RestResult.error(500, "没有访问此接口权限"));
            return false;
        }
        List<Integer> userPermissions = Arrays.stream(userPermission.split(",")).map(Integer::new).collect(Collectors.toList());
        if (urlPermission.stream()
                .anyMatch(permission -> userPermissions.stream().noneMatch(up -> permission.getCode() == up))) {
            ResponseUtils.sendJson(response, RestResult.error(500, "没有访问此接口权限"));
            return false;
        }
        return true;
    }
}

package com.citc.nce.auth.dyz;

import com.citc.nce.auth.adminUser.vo.req.DyzUserStatusReq;
import com.citc.nce.auth.formmanagement.vo.*;
import com.citc.nce.common.core.pojo.PageParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 16:53
 * @Version: 1.0
 * @Description:卡片样式
 */

@FeignClient(value = "auth-service", contextId = "DyzApi", url = "${auth:}")
public interface DyzApi {
    /**
     * 修改多因子用户状态
     *
     * @param
     * @return
     */
    @PostMapping("/dyz/changeDyzUserStatus")
    String changeDyzUserStatus(@RequestBody @Valid DyzUserStatusReq statusReq);

}

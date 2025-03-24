package com.citc.nce.auth.helpcenter;

import com.citc.nce.auth.helpcenter.vo.ArticleVo;
import com.citc.nce.auth.helpcenter.vo.DirectoryVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author yy
 * @date 2024-05-06 09:52:18
 * @Description:帮助中心前端查看
 */
@FeignClient(value = "auth-service", contextId = "HelpCenterView", url = "${auth:}")

public interface HelpCenterViewApi {
   @GetMapping("/help/view/getDirectory")
   DirectoryVo getViewDirectory();

}

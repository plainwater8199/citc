package com.citc.nce.csp;

import com.citc.nce.authcenter.csp.PlatformDefinitionApi;
import com.citc.nce.authcenter.csp.vo.resp.PlatformDefinition;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.log.annotation.Log;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.security.annotation.HasCsp;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.filecenter.vo.DownloadReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jiancheng
 */
@Api(tags = "CspPlatformDefinition 平台定义")
@RestController
@RequestMapping("/csp/platformDefinition")
@Slf4j
public class CspPlatformDefinitionController {
    @Autowired
    private PlatformDefinitionApi definitionApi;
    @Autowired
    private FileApi fileApi;

    @GetMapping("/get")
    @ApiOperation("查询平台定义信息")
    public PlatformDefinition platformDefinition() {
        return definitionApi.platformDefinition(SessionContextUtil.verifyCspLogin());
    }

    @PostMapping("/update")
    @ApiOperation("修改平台定义信息")
    @HasCsp
    @Log(title = "修改平台定义信息")
    public void updatePlatformDefinition(@RequestBody @Valid PlatformDefinition platformDefinition) {
        platformDefinition.setCspId(SessionContextUtil.verifyCspLogin());
        definitionApi.updatePlatformDefinition(platformDefinition);
    }

    @GetMapping("/getBase64")
    @ApiOperation("查询平台定义信息")
    @SkipToken
    public PlatformDefinition platformDefinitionBase64(@RequestParam(value = "encode", required = false) String encode) {
        String cspId = decodeCspId(encode);
        PlatformDefinition platformDefinition = definitionApi.platformDefinition(cspId);
        if (StringUtils.isNotEmpty(platformDefinition.getLogo())) {
            platformDefinition.setLogo(getFileBase64(platformDefinition.getLogo()));
        }
        if (!CollectionUtils.isEmpty(platformDefinition.getCarouselChart())) {
            platformDefinition.setCarouselChart(platformDefinition.getCarouselChart().stream().map(this::getFileBase64).collect(Collectors.toList()));
        }
        return platformDefinition;
    }

    private String getFileBase64(String uuid) {
        ResponseEntity<byte[]> download = fileApi.download(new DownloadReq().setFileUUID(uuid));
        String data = Base64.getEncoder().encodeToString(download.getBody());
        List<String> file_type = download.getHeaders().get("Citc_file_type");
        if (!CollectionUtils.isEmpty(file_type)) {
            String type = file_type.get(0);
            if ("svg".equals(type)) type = type + "+xml";
            data = "data:image/" + type + ";base64," + data;
        }
        return data;
    }

    private static String decodeCspId(String encoded) {
        if (StringUtils.isNotEmpty(encoded)) {
            try {
                String cspId = new String(Base64.getDecoder().decode(URLDecoder.decode(encoded, "utf-8")));
                if (cspId.length() == 10) return cspId;
            } catch (UnsupportedEncodingException e) {
                log.info("cspId encoded error {}", encoded, e);
            }
        }
        throw new BizException(600, "登录链接不正确");
    }
}

package com.citc.nce.shortUrl;

import com.citc.nce.common.facadeserver.annotations.SkipToken;
import com.citc.nce.misc.shortUrl.ShortUrlApi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;

@Controller
@Api(value = "ShortUrlController",tags = "短链接")
@Slf4j
public class ShortUrlController {
    @Resource
    private ShortUrlApi shortUrlApi;

    @GetMapping("/p/{shortUrl}")
    @SkipToken
    @ApiOperation(value = "短链接重定向", notes = "短链接重定向")
    public String redirect(@PathVariable("shortUrl")String shortUrl) {
        log.info("shortUrl redirect is start, shortUrl : {}", shortUrl);
        String redirect = shortUrlApi.redirect(shortUrl);
        log.info("shortUrl redirect : {}", redirect);
        return redirect;

    }
}

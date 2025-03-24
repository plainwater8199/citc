package com.citc.nce.misc.shortUrl;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "misc-service", contextId = "ShortUrlApi", url = "${miscServer:}")
public interface ShortUrlApi {

    @PostMapping("/shortUrl/generateUrl")
    String generateUrlByIdAndType(@RequestParam("id")Long id, @RequestParam("type")String type);

    @GetMapping("/{shortUrl}")
    String redirect(@PathVariable("shortUrl")String shortUrl);

    @PostMapping("/shortUrl/getShortUrlByIdAndType")
    String getShortUrlByIdAndType(@RequestParam("id")Long id, @RequestParam("type")String type);
}

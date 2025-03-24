package com.citc.nce.misc.shortUrl;

import com.citc.nce.misc.shortUrl.service.ShortUrlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ShortUrlController implements ShortUrlApi {

    @Autowired
    private ShortUrlService shortUrlService;

    @Override
    public String generateUrlByIdAndType(Long id, String type) {
        return shortUrlService.generateUrlByIdAndType(id, type);
    }

    @Override
    public String redirect(String shortUrl) {
        return shortUrlService.redirect(shortUrl);
    }

    @Override
    public String getShortUrlByIdAndType(Long id, String type) {
        return shortUrlService.getShortUrlByIdAndType(id, type);
    }

}

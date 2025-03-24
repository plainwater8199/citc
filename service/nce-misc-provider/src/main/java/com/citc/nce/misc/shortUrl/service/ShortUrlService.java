package com.citc.nce.misc.shortUrl.service;


import com.citc.nce.misc.email.req.EmailSendReq;
import com.citc.nce.misc.shortUrl.entity.ShortUrlDo;


public interface ShortUrlService {

    String generateUrlByIdAndType(Long id, String type);

    ShortUrlDo getShortUrlDoByShortUrl(String shortUrl);

    String redirect(String shortUrl);

    String getShortUrlByIdAndType(Long id, String type);
}

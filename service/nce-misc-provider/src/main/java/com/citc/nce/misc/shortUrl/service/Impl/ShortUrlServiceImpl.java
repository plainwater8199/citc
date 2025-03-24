package com.citc.nce.misc.shortUrl.service.Impl;

import com.citc.nce.misc.shortUrl.configure.FormShareConfigure;
import com.citc.nce.misc.shortUrl.entity.ShortUrlDo;
import com.citc.nce.misc.shortUrl.mapper.ShortUrlMapper;
import com.citc.nce.misc.shortUrl.service.ShortUrlService;
import com.citc.nce.misc.utils.Base62Utils;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;


@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(FormShareConfigure.class)
public class ShortUrlServiceImpl implements ShortUrlService {

    @Autowired
    private ShortUrlMapper shortUrlMapper;

    private final FormShareConfigure formShareConfigure;

    private final String PREFIX = "5G";

    @Override
    public String generateUrlByIdAndType(Long id, String type) {
        long hash = Hashing.murmur3_32_fixed().hashString(id.toString() + type, StandardCharsets.UTF_8).padToLong();

        LambdaQueryWrapperX<ShortUrlDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(ShortUrlDo::getHash, hash)
                .eq(ShortUrlDo::getType, type);
        ShortUrlDo shortUrlDo = shortUrlMapper.selectOne(queryWrapperX);
        if (ObjectUtils.isNotEmpty(shortUrlDo)) {
            return shortUrlDo.getShortUrl();
        }

        String ShortUrl = regenerateOnHash(hash);
        shortUrlDo = new ShortUrlDo();
        shortUrlDo.setShortUrl(PREFIX + ShortUrl);
        shortUrlDo.setType(type);
        shortUrlDo.setHash(hash);
        shortUrlDo.setOriginId(id);
        // 暂时只有表单需要，所以写的固定值
        shortUrlDo.setOriginUrl(formShareConfigure.getShareUrl());
        shortUrlMapper.insert(shortUrlDo);
        return shortUrlDo.getShortUrl();
    }

    @Override
    public ShortUrlDo getShortUrlDoByShortUrl(String shortUrl) {
        LambdaQueryWrapperX<ShortUrlDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(ShortUrlDo::getShortUrl, shortUrl);
        ShortUrlDo shortUrlDo = shortUrlMapper.selectOne(queryWrapperX);
        return shortUrlDo;
    }

    @Override
    public String redirect(String shortUrl) {
        if (StringUtils.startsWith(shortUrl, PREFIX)) {
//            String replace = shortUrl.replaceFirst(PREFIX, "");
            ShortUrlDo shortUrlDo = getShortUrlDoByShortUrl(shortUrl);
            if (ObjectUtils.isNotEmpty(shortUrlDo)) {
                return "redirect:" + shortUrlDo.getOriginUrl() + shortUrlDo.getOriginId();
            }
        }
        return null;
    }

    @Override
    public String getShortUrlByIdAndType(Long id, String type) {
        LambdaQueryWrapperX<ShortUrlDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.eq(ShortUrlDo::getOriginId, id);
        queryWrapperX.eq(ShortUrlDo::getType, type);
        ShortUrlDo shortUrlDo = shortUrlMapper.selectOne(queryWrapperX);
        if (ObjectUtils.isNotEmpty(shortUrlDo)) {
            return shortUrlDo.getShortUrl();
        }
        return null;
    }

    private String regenerateOnHash(long hash) {
        String shortUrl = Base62Utils.encode2Base62String(Math.abs(hash));
        ShortUrlDo shortUrlDo = getShortUrlDoByShortUrl(shortUrl);
        if (ObjectUtils.isEmpty(shortUrlDo)) {
            return shortUrl;
        }
        return regenerateOnHash(hash + 1);
    }
}

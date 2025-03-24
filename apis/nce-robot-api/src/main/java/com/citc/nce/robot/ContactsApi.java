package com.citc.nce.robot;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(value = "rebot-service",contextId="ContactsApi", url = "${robot:}")
public interface ContactsApi {

    @PostMapping(path = "/im/message/checkContacts",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Boolean checkContacts(@RequestPart(value = "file") MultipartFile file);
}

package com.citc.nce.developer;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.citc.nce.authcenter.auth.vo.UserNameInfo;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

/**
 * @author ping chen
 */
@RestController
@Slf4j
@Api(tags = "开发者服务文档下载")
public class DeveloperController {

    @GetMapping("/developer/download")
    @ApiOperation("开发者文档下载")
    @SkipToken
    public void downloadFile(HttpServletResponse response) {
        String path = "file/开发者文档.doc";
        ClassPathResource classPathResource = new ClassPathResource(path);
        try (InputStream inputStream = classPathResource.getInputStream(); ServletOutputStream outputStream = response.getOutputStream()) {
            String fileName = "开发者文档.doc";
            String fileNameURL = URLEncoder.encode(fileName, "UTF-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileNameURL + ";" + "filename*=utf-8''" + fileNameURL);

            IOUtils.copy(inputStream, outputStream);
            response.flushBuffer();
        } catch (Exception e) {
            log.error("文件读取失败", e);
        }
    }

}

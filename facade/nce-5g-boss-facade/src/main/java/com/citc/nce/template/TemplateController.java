package com.citc.nce.template;

import com.citc.nce.auth.messagetemplate.MessageTemplateApi;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import com.citc.nce.robot.RobotProcessTreeApi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

@Api(tags = "后台管理-模板服务")
@RestController
@RequestMapping("/template")
@Slf4j
public class TemplateController {

    @Resource
    MessageTemplateApi messageTemplateApi;

    @Resource
    RobotProcessTreeApi robotProcessTreeApi;

    /**
     * 蜂动版本，模板审核初始化时使用
     */
    @GetMapping("/message/template/transfer")
    public void templateInitForNewAuditBranch() {
        messageTemplateApi.templateInitForNewAuditBranch();
    }

    @GetMapping("/process/tree/oldProcessInit")
   public void oldProcessToAuditInit() {
        robotProcessTreeApi.oldProcessToAuditInit();
    }

    @ApiOperation("下载模板")
    @GetMapping("/downloadApiParamTemplate/{name}")
    public void downloadTemplate2(HttpServletResponse response, HttpServletRequest request, @PathVariable("name") String name) throws IOException {
        String path = "templates/".concat(name).concat("模板.csv");
        ClassPathResource classPathResource = new ClassPathResource(path);
        InputStream inputStream = classPathResource.getInputStream();
        ServletOutputStream servletOutputStream = null;
        try {
            String fileName = name.concat("模板.csv");
            String fileNameURL = URLEncoder.encode(fileName, "UTF-8");
            response.setContentType("application/vnd.ms-excel");
            response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.addHeader("charset", "utf-8");
            response.addHeader("Pragma", "no-cache");
            response.setHeader("Content-disposition", "attachment;filename=" + fileNameURL + ";" + "filename*=utf-8''" + fileNameURL);
            servletOutputStream = response.getOutputStream();
            IOUtils.copy(inputStream, servletOutputStream);
            response.flushBuffer();
        } catch (Exception e) {
            log.error("下载模板文件错误", e);
            e.printStackTrace();
        } finally {
            try {
                if (servletOutputStream != null) {
                    servletOutputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

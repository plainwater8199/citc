package com.citc.nce.auth;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.citc.nce.authcenter.auth.vo.UserNameInfo;
import com.citc.nce.authcenter.identification.vo.resp.GetCertificateListResp;
import com.citc.nce.authcenter.identification.vo.resp.SendSystemMessageResp;
import com.citc.nce.authcenter.systemmsg.AdminSysMsgManageApi;
import com.citc.nce.authcenter.systemmsg.vo.SysMsgManageListInfo;
import com.citc.nce.authcenter.systemmsg.vo.req.*;
import com.citc.nce.authcenter.systemmsg.vo.resp.AddSysMsgManageResp;
import com.citc.nce.authcenter.systemmsg.vo.resp.ImportUserByCSVResp;
import com.citc.nce.authcenter.systemmsg.vo.resp.QuerySysMsgManageDetailResp;
import com.citc.nce.authcenter.systemmsg.vo.resp.UpdateSysMsgManageResp;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.xss.core.XssCleanIgnore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

@Api(tags = "管理平台--站内信模块")
@RestController
@RequestMapping("/admin/sysMsg")
public class SysMsgForYHTController {

    @Resource
    private AdminSysMsgManageApi adminSysMsgManageApi;

    /**
     * 管理平台--获取用户标签列表
     * @param req 请求消息
     * @return 响应消息
     */
    @ApiOperation("管理平台--获取用户标签列表")
    @PostMapping("/getCertificateList")
    GetCertificateListResp getCertificateList(@RequestBody GetCertificateListReq req){
        return adminSysMsgManageApi.getCertificateList(req);
    }

    /**
     * 管理平台--批量导入用户
     * @param file 导入文件
     * @return 响应消息
     */
    @ApiOperation("管理平台--公告csv批量导入用户")
    @PostMapping("/importUserByCSV")
    ImportUserByCSVResp importUserByCSV(@RequestParam(value = "file") MultipartFile file){
        return adminSysMsgManageApi. importUserByCSV(file);
    }



    /**
     * 管理平台--新增站内信
     * @param req 请求消息
     * @return 响应消息
     */
    @ApiOperation("管理平台--新增站内信")
    @PostMapping("/addSysMsgManage")
    @XssCleanIgnore
    AddSysMsgManageResp addSysMsgManage(@RequestBody @Valid AddSysMsgManageReq req){
        return adminSysMsgManageApi.addSysMsgManage(req);
    }

    /**
     * 管理平台--更改站内信
     * @param req 请求消息
     * @return 响应消息
     */
    @ApiOperation("管理平台--更改站内信")
    @PostMapping("/updateSysMsgManage")
    @XssCleanIgnore
    UpdateSysMsgManageResp updateSysMsgManage(@RequestBody @Valid UpdateSysMsgManageReq req){
        return adminSysMsgManageApi.updateSysMsgManage(req);
    }


    /**
     * 管理平台--查询站内信列表
     * @param req 请求消息
     * @return 响应消息
     */
    @ApiOperation("管理平台--查询站内信列表")
    @PostMapping("/querySysMsgManageList")
    PageResult<SysMsgManageListInfo> querySysMsgManageList(@RequestBody QuerySysMsgManageListReq req){
        return adminSysMsgManageApi.querySysMsgManageList(req);
    }


    /**
     * 管理平台--查询站内信详情
     * @param req 请求消息
     * @return 响应消息
     */
    @ApiOperation("管理平台--查询站内信详情")
    @PostMapping("/querySysMsgManageDetail")
    QuerySysMsgManageDetailResp querySysMsgManageDetail(@RequestBody @Valid QuerySysMsgManageDetailReq req){
        return adminSysMsgManageApi.querySysMsgManageDetail(req);
    }

    /**
     * 管理平台--查询站内信详情
     * @param req 请求消息
     * @return 响应消息
     */
    @ApiOperation("管理平台--删除站内信管理")
    @PostMapping("/deleteSysMsgManage")
    void deleteSysMsgManage(@RequestBody @Valid DeleteSysMsgManageReq req){
        adminSysMsgManageApi.deleteSysMsgManage(req);
    }


    @ApiOperation("管理平台--下载站内信用户导入模板")
    @GetMapping("/downloadTemplate/{name}")
    public void downloadTemplate(HttpServletResponse response, @PathVariable("name") String name) throws IOException {
        String path = "templates/".concat(name).concat("模板.xlsx");
        ClassPathResource classPathResource = new ClassPathResource(path);
        InputStream inputStream = classPathResource.getInputStream();
        ServletOutputStream servletOutputStream = null;
        try {
            String fileName = name.concat("模板.xlsx");
            String fileNameURL = URLEncoder.encode(fileName, "UTF-8");
            response.setContentType("application/vnd.ms-excel");
//            response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
//            response.addHeader("charset", "utf-8");
//            response.addHeader("Pragma", "no-cache");
            response.setHeader("Content-disposition", "attachment;filename=" + fileNameURL + ";" + "filename*=utf-8''" + fileNameURL);
            servletOutputStream = response.getOutputStream();
            EasyExcel.write(servletOutputStream, UserNameInfo.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("内信用户导入模板");
            IOUtils.copy(inputStream, servletOutputStream);
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (servletOutputStream != null) {
                    servletOutputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 管理平台--发送系统消息
     * @param req 请求消息
     * @return 响应消息
     */
    @ApiOperation("管理平台--发送系统消息")
    @PostMapping("/sendSysMsg")
    SendSystemMessageResp sendSysMsg(@RequestBody @Valid SendSystemMessageReq req){
        return adminSysMsgManageApi.sendSysMsg(req);
    }



}

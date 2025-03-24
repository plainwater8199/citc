package com.citc.nce.file;

import com.alibaba.excel.util.IoUtils;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.csp.mediasms.template.MediaSmsTemplateApi;
import com.citc.nce.auth.csp.mediasms.template.vo.MediaSmsTemplateVo;
import com.citc.nce.auth.formmanagement.FormManagementApi;
import com.citc.nce.auth.formmanagement.vo.FormManagementOneReq;
import com.citc.nce.auth.formmanagement.vo.FormManagementResp;
import com.citc.nce.authcenter.csp.CspApi;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import com.citc.nce.common.redis.config.RedisService;
import com.citc.nce.configure.BaiduSensitiveCheckConfigure;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.filecenter.vo.*;
import com.citc.nce.filter.BaiduService;
import com.google.common.base.Strings;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 文件服务管理层
 */
@Api(tags = "后台管理-文件服务")
@RestController
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(BaiduSensitiveCheckConfigure.class)
public class FileController {

    @Resource
    private FileApi fileApi;
    @Resource
    private AccountManagementApi accountManagementApi;
    @Resource
    private CspApi cspApi;
    @Resource
    private FormManagementApi formManagementApi;
    @Resource
    private MediaSmsTemplateApi mediaSmsTemplateApi;
    @Resource
    private BaiduService baiduService;

    private final BaiduSensitiveCheckConfigure baiduSensitiveCheckConfigure;

    @Resource
    private RedisService redisService;
    public final static String baiduDownloadFix = "BaiduService:allow:download:";

    @PostMapping("/file/upload")
    @ApiOperation("文件上传")
    public List<UploadResp> upload(UploadReq req) {
        String filename = req.getFile().getOriginalFilename();
        String format = null;
        if (filename != null) {
            format = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase(Locale.ROOT);
        }
        //如果视频是空的那么用之前送审时传minio的文件
        if (baiduSensitiveCheckConfigure.getIsExamine()) {
            byte[] bytes = null;
            try {
                bytes = IoUtils.toByteArray(req.getFile().getInputStream());
            } catch (IOException e) {
                throw new BizException(500, "文件传输异常");
            }

            if (getAudioFormats().contains(format)) {
                //音频审核
                JSONObject jsonObject = baiduService.audioCensor(bytes, format);
                exceptionOut(jsonObject);
            }
            if (getPictureFormats().contains(format)) {
                //图片审核
                JSONObject jsonObject = baiduService.imageCensor(bytes);
                exceptionOut(jsonObject);
            }
            //文本已经在AllFilter送过了 com.citc.nce.filter.AllFilter

            //视频送审,送审视频需要给百度下载地址需要先把文件存入minio
            if (getVideoFormats().contains(format)) {
                //先不送网关
                UploadReq videReq = new UploadReq();
                videReq.setFile(req.getFile());
                videReq.setThumbnail(req.getThumbnail());
                videReq.setList(null);//不送审网关
                videReq.setCreator(req.getCreator());
                videReq.setSceneId("codeincodeservice");
                UploadResp uploadResp = fileApi.uploadFile(videReq).get(0);
                String fileUUid = uploadResp.getUrlId();
                try {
                    //视频审核
                    redisService.setCacheObject(baiduDownloadFix + fileUUid, uploadResp, 10L, TimeUnit.MINUTES);
                    String videoUrl = baiduSensitiveCheckConfigure.getFileExamineUrl() + fileUUid;
                    JSONObject jsonObject = baiduService.videoCensor(videoUrl, filename);
                    exceptionOut(jsonObject);
                } finally {
                    //删除百度临时需要的文件
                    fileApi.deleteFile(new DeleteReq().setFileUrlIds(Collections.singletonList(fileUUid)));
                    redisService.deleteObject(baiduDownloadFix + fileUUid);
                }
            }
        }
        //list是空的就再送审一次
        return fileApi.uploadFile(req);
    }

    @PostMapping(value = "/file/baiduUpload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation("上传，走百度审核")
    public List<UploadResp> simpleUpload(UploadReq req) {
        req.setList(null);
        return upload(req);
    }


    @PostMapping("/file/upload/public")
    @ApiOperation("公开文件上传")
    @SkipToken
    public List<UploadResp> publicUpload(UploadReq req) {
        return fileApi.uploadFile(req);
    }


    @ApiOperation("文件下载")
    @PostMapping("/file/download")
    public ResponseEntity<byte[]> download(@RequestBody @Valid DownloadReq downloadReq) {
        return fileApi.download(downloadReq);
    }

    @ApiOperation("通过id文件下载")
    @GetMapping(value = "/file/download/id")
    public ResponseEntity<byte[]> download2(@RequestParam String req, HttpServletRequest request) {
        String range = request.getHeader("Range");
        if (!Strings.isNullOrEmpty(req)) {
            //1、获取视频流文件
            FileInfoReq fileInfoReq = new FileInfoReq();
            fileInfoReq.setUuid(req);
            FileInfo fileInfo = fileApi.getFileInfo(fileInfoReq);
            List<String> videoFormatList = Arrays.asList("mp4", "3gp", "webm");
            try {
                //视频文件处理
                if (videoFormatList.contains(fileInfo.getFileFormat().toLowerCase(Locale.ROOT)) && !Strings.isNullOrEmpty(range)) {
                    DownloadReq downloadReq = new DownloadReq();
                    downloadReq.setFileUUID(req);
                    downloadReq.setRange(range);
                    return fileApi.videoDownload(downloadReq);
                } else {//其它文件处理
                    DownloadReq downloadReq = new DownloadReq();
                    downloadReq.setFileUUID(req);
                    return fileApi.download(downloadReq);
                }
            }catch (Exception e) {
                throw new BizException("文件系统中未查询到相关文件");
            }
        } else {
            throw new BizException(GlobalErrorCode.BAD_REQUEST);
        }
    }

    @ApiOperation("根据场景ID下载图片")
    @SkipToken
    @GetMapping(value = "/file/download/byScenarioId")
    public ResponseEntity<byte[]> byScenarioId(@RequestParam("scenarioId") String scenarioId,@RequestParam("fileId") String fileId, HttpServletRequest request) {
        String range = request.getHeader("Range");
        if (!Strings.isNullOrEmpty(scenarioId) && !Strings.isNullOrEmpty(fileId)) {
            //1、获取视频流文件
            FileInfoReq fileInfoReq = new FileInfoReq();
            fileInfoReq.setUuid(fileId);
            fileInfoReq.setScenarioID(scenarioId);
            FileInfo fileInfo = fileApi.getFileInfoByScenarioId(fileInfoReq);
            List<String> videoFormatList = Arrays.asList("mp4", "3gp", "webm");
            //视频文件处理
            if (videoFormatList.contains(fileInfo.getFileFormat().toLowerCase(Locale.ROOT)) && !Strings.isNullOrEmpty(range)) {
                DownloadReq downloadReq = new DownloadReq();
                downloadReq.setFileUUID(fileId);
                downloadReq.setRange(range);
                return fileApi.videoDownload(downloadReq);
            } else {//其它文件处理
                DownloadReq downloadReq = new DownloadReq();
                downloadReq.setFileUUID(fileId);
                return fileApi.download(downloadReq);
            }
        } else {
            throw new BizException(GlobalErrorCode.BAD_REQUEST);
        }
    }



    @ApiOperation("百度平台专用下载地址")
    @GetMapping(value = "/file/baiduPlatform/download/id")
    @SkipToken
    public ResponseEntity<byte[]> download3(@RequestParam String fileUUid, HttpServletRequest request) {
        if (!redisService.hasKey(baiduDownloadFix + fileUUid)) {
            log.warn("百度平台专用下载地址,请求了不是百度专用文件地址fileUUid {}", fileUUid);
            throw new BizException(500, "文件不存在");
        }
        return download2(fileUUid, request);
    }

    @ApiOperation("查询文件信息")
    @PostMapping(value = "/file/getFileInfo")
    public FileInfo getFileInfo(@RequestBody @Valid FileInfoReq Req) {
        return fileApi.getFileInfo(Req);
    }


    private List<String> getVideoFormats() {
        List<String> list = new ArrayList<>();
        list.add("mp4");
        return list;
    }

    private List<String> getPictureFormats() {
        List<String> list = new ArrayList<>();
        list.add("png");
        list.add("jpeg");
        list.add("jpg");
        list.add("bmp");
        list.add("gif");
        list.add("tiff");
        return list;
    }


    private List<String> getAudioFormats() {
        List<String> list = new ArrayList<>();
        list.add("mp3");
        list.add("aac");
        list.add("amr");
        list.add("m4a");
        return list;
    }

    private void  exceptionOut(JSONObject jsonObject) {
        if (jsonObject.getInteger("conclusionType") != 1) {
            log.warn("百度送审失败: {}", jsonObject.getString("error_msg"));
            //不合规
            throw new BizException(81001248, "请求失败，包含敏感信息或不符合相关规定");
        }
    }


    @ApiOperation("通过uuid下载公开css文件")
    @GetMapping(value = "/file/download/css")
    @SkipToken
    public ResponseEntity<byte[]> downloadPublicCss(@RequestParam String uuid) {
        FileInfoReq fileInfoReq = new FileInfoReq();
        fileInfoReq.setUuid(uuid);
        FileInfo fileInfo = fileApi.getFileInfo(fileInfoReq);
        if (!Objects.equals("css", fileInfo.getFileFormat().toLowerCase())) {
            throw new BizException("资源不存在");
        }
        DownloadReq downloadReq = new DownloadReq();
        downloadReq.setFileUUID(uuid);
        return fileApi.download(downloadReq);
    }


    @ApiOperation("通过文件uuid和chatbotId下载文件")
    @GetMapping(value = "/file/download/chatbot")
    @SkipToken
    public ResponseEntity<byte[]> downloadPublicChatbotResource(@RequestParam String uuid,
                                                                @RequestParam(required = false) String chatbotId,
                                                                @RequestParam(required = false) Long formId,
                                                                @RequestParam(required = false) Long templateId,
                                                                @RequestParam(required = false, value = "customerId") String cid,
                                                                HttpServletRequest request) {
        log.info("请求免认证资源 uuid:{} chatbotId:{} formId:{} templateId:{} customerId:{}", uuid, chatbotId, formId, templateId, cid);
        FileInfoReq fileInfoReq = new FileInfoReq();
        fileInfoReq.setUuid(uuid);
        FileInfo fileInfo = fileApi.getFileInfo(fileInfoReq);
        String fileOwner = fileInfo.getCreator();
        String customerId = null;
        //尝试通过所有的非必填参数验证用户身份（是否可访问目标资源），如全部验证都失败则不允许下载
        if (StringUtils.isNotBlank(chatbotId)) {
            AccountManagementResp chatbotAccount = accountManagementApi.getAccountManagementByAccountId(chatbotId);
            if (chatbotAccount == null || chatbotAccount.getChatbotAccountId() == null)
                throw new BizException("机器人不存在");
            customerId = chatbotAccount.getCustomerId();
        } else if (formId != null) {
            FormManagementResp form = formManagementApi.getFormManagementById(new FormManagementOneReq().setId(formId));
            if (form != null) {
                customerId = form.getCreator();
                if (org.springframework.util.StringUtils.hasLength(fileOwner) && !fileOwner.equals(customerId)) {
                    try {
                        //是csp创建的表单文件
                        String cspId = cspApi.queryCspId(fileOwner);
                        //干扰判断拥有者
                        if (customerId.startsWith(cspId)) customerId = fileOwner;
                    } catch (Exception e) {
                        log.warn("表单创建者可能不是csp");
                    }
                }
            }
        } else if (templateId != null) {
            MediaSmsTemplateVo template = mediaSmsTemplateApi.getTemplateById(templateId);
            if (template != null) {
                customerId = template.getCustomerId();
            }
        } else if (cid != null) {
            customerId = cid;
        }
        //creator是admin的资源都是公开可访问的
        if (!Objects.equals(fileOwner, "admin") && !Objects.equals(fileOwner, customerId)) {
            throw new BizException("权限不足");
        }
        DownloadReq downloadReq = new DownloadReq();
        downloadReq.setFileUUID(uuid);
        String range = request.getHeader("Range");
        List<String> videoFormatList = Arrays.asList("mp4", "3gp", "webm");
        //视频文件处理
        if (videoFormatList.contains(fileInfo.getFileFormat().toLowerCase(Locale.ROOT)) && !Strings.isNullOrEmpty(range)) {
            downloadReq.setRange(range);
            return fileApi.videoDownload(downloadReq);
        } else {//其它文件处理
            return fileApi.download(downloadReq);
        }
    }


}

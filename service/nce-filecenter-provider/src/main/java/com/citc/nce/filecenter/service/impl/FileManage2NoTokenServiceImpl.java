package com.citc.nce.filecenter.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.filecenter.configure.AllowUploadFormatsConfigure;
import com.citc.nce.filecenter.entity.FileManage;
import com.citc.nce.filecenter.exp.FileExp;
import com.citc.nce.filecenter.mapper.FileManageMapper;
import com.citc.nce.filecenter.service.FileManage2NoTokenService;
import com.citc.nce.filecenter.service.FileManageService;
import com.citc.nce.filecenter.util.DurationUtil;
import com.citc.nce.filecenter.util.MinioInfo;
import com.citc.nce.filecenter.util.MinioUtils;
import com.citc.nce.filecenter.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaInfo;
import ws.schild.jave.MultimediaObject;
import javax.annotation.Resource;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(AllowUploadFormatsConfigure.class)
public class FileManage2NoTokenServiceImpl extends ServiceImpl<FileManageMapper, FileManage> implements FileManage2NoTokenService {

    @Resource
    MinioUtils minioUtil;
    @Resource
    FileManageService fileManageService;

    private final List<String> scenes = Arrays.asList("H5","richText");

    //无权访问的场景地址
    private final List<String> NO_AUTH_SCENES = Arrays.asList("AUTH","zhengjian5M29384","touxiang5fdasfsda","chatbotfujian5M","fengtonghetongwenjian10M438943",
            "yingyezhizhao5M1236","hetongfujianshaomiao10M435","qiyelogo5M43894","yidonghetongfujian10M3243","kaipiao10M","fengtonghetong10M438943");


    @Override
    @SneakyThrows
    public ResponseEntity<byte[]> downloadScene(String uuid,String scene) {
        if (uuid != null && scenes.contains(scene)) {
            List<FileManage> fileManages = this.lambdaQuery().eq(FileManage::getFileUuid, uuid).list();
            if(!fileManages.isEmpty()) {
                FileManage fileManage = fileManages.get(0);
                if(!NO_AUTH_SCENES.contains(fileManage.getScenarioID())){
                    String minioFileName = fileManage.getMinioFileName();
                    HttpHeaders headers = new HttpHeaders();
                    ResponseEntity<byte[]> entity;
                    InputStream in = minioUtil.getObject("robot", minioFileName);
                    byte[] buffer = new byte[1024];
                    int len ;
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    while ((len = in.read(buffer)) != -1) {
                        bos.write(buffer, 0, len);
                    }
                    bos.flush();
                    byte[] bytes = bos.toByteArray();
                    String fileName = fileManage.getFileName();
                    String fileNameURL = URLEncoder.encode(fileName, "UTF-8");
                    headers.add("Content-Type", "application/octet-stream");
                    headers.add("Content-Disposition", "attachment; filename=" + fileNameURL);
                    headers.add("Citc_file_type", fileManage.getFileFormat());
                    List<String> videoFormatList = getVideoFormatList();
                    if (videoFormatList.contains(fileManage.getFileFormat().toLowerCase(Locale.ROOT))) {
                        headers.add("Content-Type", "video/" + fileManage.getFileFormat().toLowerCase(Locale.ROOT));
                        headers.setContentLength(Math.toIntExact(bytes.length));
                        headers.add("Content-Range", "bytes 0-1/" + bytes.length);
                        headers.add("Accept-Ranges", "bytes");
                    }
                    entity = new ResponseEntity<>(bytes, headers, HttpStatus.OK);
                    return entity;
                }else{
                    throw new BizException("无权访问");
                }

            }else{
                throw new BizException("文件不存在");
            }
        }else{
            throw new BizException(GlobalErrorCode.BAD_REQUEST);
        }

    }

    @Override
    @Transactional
    public List<UploadResp> uploadFile2Scene(UploadReq uploadReq) {
        if(scenes.contains(uploadReq.getSceneId())){
            MinioInfo minioInfo = minioUtil.uploadFile(uploadReq.getFile(), "robot");
            String fileUrl = minioInfo.getFileUrl();
            String originalFilename = uploadReq.getFile().getOriginalFilename();
            String format = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            File tempFile = multipartFileToFile(uploadReq.getFile());
            String uuid = IdUtil.fastSimpleUUID();
            FileManage fileManage = new FileManage();
            String videoDuration = getVideoDuration(tempFile, format);
            String size = DurationUtil.longToString(tempFile.length());
            fileManage.setFileSize(size);
            fileManage.setFileDuration(videoDuration);
            fileManage.setFileUploadTime(new Date());
            fileManage.setFileUrl(fileUrl);
            fileManage.setFileUuid(uuid);
            fileManage.setFileName(originalFilename);
            fileManage.setCreator("admin");
            fileManage.setScenarioID(uploadReq.getSceneId());
            String minioFileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            fileManage.setFileFormat(format);
            fileManage.setMinioFileName(minioFileName);
            fileManage.setAutoThumbnail(minioInfo.getThumbnailBase64());
            saveOrUpdate(fileManage);
            List<UploadResp> result = new ArrayList<>();
            UploadResp resp = new UploadResp();
            resp.setUrlId(fileManage.getFileUuid());
            BeanUtils.copyProperties(fileManage, resp);
            result.add(resp);
            boolean delete = tempFile.delete();
            if (!delete) {
                throw new BizException(FileExp.FILE_DELETE_ERROR);
            }
            return result;
        }else{
            throw new BizException("越权请求");
        }
    }

    private List<String> getVideoFormatList() {
        List<String> videoFormats = new ArrayList<>();
        videoFormats.add("mp4");
        videoFormats.add("3gp");
        videoFormats.add("webm");
        return videoFormats;
    }

    private String getVideoDuration(File file, String format) {
        if (!file.exists()) {
            return null;
        }
        List<String> videoFormatList = getVideoFormatList();
        if (!videoFormatList.contains(format.toLowerCase(Locale.ROOT))) {
            return null;
        }
        MultimediaObject multimediaObject = new MultimediaObject(file);
        MultimediaInfo info = null;
        try {
            info = multimediaObject.getInfo();
        } catch (EncoderException e) {
            throw new BizException(FileExp.VIDEO_ANALYSIS_ERROR);
        }
        long playTime = info.getDuration();
        return DurationUtil.millisToStringShort(playTime);
    }

    public File multipartFileToFile(MultipartFile multiFile) {
        // 获取文件名
        String fileName = multiFile.getOriginalFilename();
        Assert.notNull(fileName,"filename must not be null");
        // 获取文件后缀
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        String uuid = IdUtil.fastUUID();
        try {
            File file = File.createTempFile(uuid, prefix);
            multiFile.transferTo(file);
            return file;
        } catch (Exception e) {
            log.error("临时文件生成失败", e);
            throw new BizException(FileExp.FILE_CREATE_ERROR);
        }
    }
}

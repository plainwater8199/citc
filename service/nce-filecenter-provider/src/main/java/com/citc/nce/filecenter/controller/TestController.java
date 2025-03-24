package com.citc.nce.filecenter.controller;


import cn.hutool.core.util.IdUtil;
import com.citc.nce.filecenter.entity.FileManage;
import com.citc.nce.filecenter.mapper.FileManageMapper;
import com.citc.nce.filecenter.util.MinioInfo;
import com.citc.nce.filecenter.util.MinioUtils;
import com.citc.nce.filecenter.vo.UploadReq;
import com.citc.nce.filecenter.vo.UploadResp;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

@RestController
@Slf4j
public class TestController {

    @Resource
    MinioUtils minioUtil;
    @Resource
    private FileManageMapper fileManageMapper;

    private final static String BUCKET_NAME = "robot";

    @PostMapping(value = "/file/test/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public List<UploadResp> uploadFile(UploadReq uploadReq) {

        //主文件
        long startTime = System.currentTimeMillis();
        MinioInfo minioInfo = minioUtil.uploadFile(uploadReq.getFile(), BUCKET_NAME);
        String fileUrl = minioInfo.getFileUrl();
        String objectName = uploadReq.getFile().getOriginalFilename();
//        try {
//            String uploadId = minioUtil.initiateMultipartUpload(objectName);
//
//            long partSize = 5 * 1024 * 1024; // 每个分片大小为 5MB
//            Part[] parts = minioUtil.uploadParts(uploadId, objectName, file, partSize);
//
//            minioUtil.completeMultipartUpload(uploadId, objectName, parts);
//
//            System.out.println("File uploaded successfully!");
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println( "File upload failed: " + e.getMessage());
//        }

//        String originalFilename = uploadReq.getFile().getOriginalFilename();
        String format = objectName.substring(objectName.lastIndexOf(".") + 1).toLowerCase(Locale.ROOT);

        String uuid = IdUtil.fastSimpleUUID();
        FileManage fileManage = new FileManage();
        fileManage.setFileUploadTime(new Date());
        fileManage.setFileUrl(fileUrl);
        fileManage.setFileUuid(uuid);
        fileManage.setFileName(objectName);
        fileManage.setScenarioID(uploadReq.getScenarioID());
        fileManage.setCreator("water");
        fileManage.setFileFormat(format);
        fileManage.setFileSize("water");
        fileManage.setMinioFileName(fileUrl.substring(fileUrl.lastIndexOf("/") + 1));
        fileManage.setAutoThumbnail(minioInfo.getThumbnailBase64());
        fileManageMapper.insert(fileManage);

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        System.out.println("Query Execution Time: " + executionTime + " ms");
        return null;
    }


    @PostMapping(value = "/file/test/autoThumbnail")
    public void autoThumbnail(@RequestParam("id") Long id) {
        List<FileManage> fileManageList = fileManageMapper.selectList(null);
        for (FileManage fileManage : fileManageList) {

            try (InputStream in = minioUtil.getObject("robot", fileManage.getMinioFileName())) {
                // 创建缩略图并转换为Base64
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Thumbnails.of(in)
                        .size(500, 500)
                        .outputFormat("jpg")
                        .toOutputStream(outputStream);
                byte[] thumbnailBytes = outputStream.toByteArray();
                String base64Thumbnail = Base64.encodeBase64String(thumbnailBytes);
                fileManage.setAutoThumbnail("data:image/jpeg;base64," + base64Thumbnail);
                fileManageMapper.updateById(fileManage);
                System.out.println("---------:" + fileManage.getId());
            } catch (Exception e) {
                System.out.println("生成缩略图异常！！！！！！");
            }
        }


    }
}

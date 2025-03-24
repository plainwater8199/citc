package com.citc.nce.filecenter.util;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.filecenter.config.CustomMinioClient;
import com.citc.nce.filecenter.entity.FileManage;
import com.citc.nce.filecenter.exp.FileExp;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

@Component
@Slf4j
@EnableConfigurationProperties(MinioProp.class)
public class MinioUtils {

    @Resource
    private MinioClient minioClient;

    private CustomMinioClient customMinioClient;

    @Resource
    private MinioProp minioProp;

    // spring自动注入会失败
    @PostConstruct
    public void init() {
        MinioAsyncClient minioClient = MinioAsyncClient.builder()
                .endpoint(minioProp.getEndpoint())
                .credentials(minioProp.getAccessKey(), minioProp.getSecretKey())
                .build();
        customMinioClient = new CustomMinioClient(minioClient);
    }

    /**
     * 创建bucket
     *
     * @param bucketName bucket名称
     */
    public void createBucket(String bucketName) throws Exception {
        if (!customMinioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build()).get()) {
            customMinioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    /**
     * 获取全部bucket
     */
    @SneakyThrows
    public List<Bucket> getAllBuckets() {
        return minioClient.listBuckets();
    }

    /**
     * 根据bucketName获取信息
     *
     * @param bucketName bucket名称
     */
    @SneakyThrows
    public Optional<Bucket> getBucket(String bucketName) {
        return minioClient.listBuckets().stream().filter(b -> b.name().equals(bucketName)).findFirst();
    }

    /**
     * 根据bucketName删除信息
     *
     * @param bucketName bucket名称
     */
    @SneakyThrows
    public void removeBucket(String bucketName) {
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
    }

    /**
     * 获取文件外链
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param expires    过期时间 <=7
     * @return url
     */
    @SneakyThrows
    public String getObjectURL(String bucketName, String objectName, Integer expires) {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(bucketName).object(objectName).expiry(expires).build());
    }

    /**
     * 获取文件外链
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return url
     */
    @SneakyThrows
    public String getObjectURL(String bucketName, String objectName) {
        return customMinioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(bucketName).object(objectName).method(Method.GET).build());
    }

    /**
     * 获取文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return 二进制流
     */
    @SneakyThrows
    public InputStream getObject(String bucketName, String objectName) {
        return customMinioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build()).get();
    }

    /**
     * 上传文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param stream     文件流
     * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#putObject
     */
    public void putObject(String bucketName, String objectName, InputStream stream) throws Exception {
        minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(stream, stream.available(), -1).contentType(objectName.substring(objectName.lastIndexOf("."))).build());
    }

    /**
     * 上传文件
     *
     * @param bucketName  bucket名称
     * @param objectName  文件名称
     * @param stream      文件流
     * @param size        大小
     * @param contextType 类型
     * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#putObject
     */
    public void putObject(String bucketName, String objectName, InputStream stream, long size, String contextType) throws Exception {
        minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(stream, size, -1).contentType(contextType).build());
    }

    /**
     * 获取文件信息
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#statObject
     */
    public StatObjectResponse getObjectInfo(String bucketName, String objectName) throws Exception {
        return minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }

    /**
     * 删除文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#removeObject
     */
    public void removeObject(String bucketName, String objectName) throws Exception {
        customMinioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }

    /**
     * 上传文件
     *
     * @param file       文件
     * @param bucketName 存储桶
     * @return
     */
    public MinioInfo uploadFile(MultipartFile file, String bucketName) {
        MinioInfo minioInfo = new MinioInfo();
        // 判断上传文件是否为空
        if (null == file || 0 == file.getSize()) {
            throw new BizException(FileExp.FILE_MISS_ERROR);
        }
        try (InputStream inputStream = file.getInputStream()) {
            String thumbnailBase64 = autoThumbnail(inputStream);
            minioInfo.setThumbnailBase64(thumbnailBase64);
            // 判断存储桶是否存在
            createBucket(bucketName);
            // 文件名
            String originalFilename = file.getOriginalFilename();
            String format = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 新的文件名 = 存储桶名称_时间戳.后缀名
            String fileName = IdUtil.fastSimpleUUID() + format;
            // 开始上传

            customMinioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(
                                    file.getInputStream(), file.getSize(), -1)
                            .contentType("application/octet-stream")
                            .build());

            String url = getObjectURL(bucketName, fileName);
            if(!StringUtils.isEmpty(url) && url.contains("?")){
                minioInfo.setFileUrl( url.substring(0, url.indexOf("?")));
            }else{
                minioInfo.setFileUrl(url);
            }
            return minioInfo;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BizException(FileExp.FILE_UPLOAD_ERROR);
        }
    }

    public String autoThumbnail(InputStream in) {
        try{
            // 创建缩略图并转换为Base64
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(in)
                    .size(2000, 2000)
                    .outputFormat("jpg")
                    .toOutputStream(outputStream);
            byte[] thumbnailBytes = outputStream.toByteArray();
            String base64Thumbnail = Base64.encodeBase64String(thumbnailBytes);
            return "data:image/jpeg;base64," + base64Thumbnail;
        }catch (Exception e){
            System.out.println("生成缩略图异常！！！！！！");
            e.getMessage();
        }
        return null;
    }
}



//package com.citc.nce.filecenter.util;
//
//import cn.hutool.core.util.IdUtil;
//import com.citc.nce.common.core.exception.BizException;
//import com.citc.nce.filecenter.exp.FileExp;
//import io.minio.MinioClient;
//import io.minio.ObjectStat;
//import io.minio.messages.Bucket;
//import lombok.Cleanup;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.io.IOUtils;
//import org.springframework.stereotype.Component;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.annotation.Resource;
//import java.io.InputStream;
//import java.util.List;
//import java.util.Optional;
//
//
//@Component
//@Slf4j
//public class MinioUtils {
//
//    @Resource
//    private MinioClient client;
//
//    /**
//     * 创建bucket
//     *
//     * @param bucketName bucket名称
//     */
//    public void createBucket(String bucketName) throws Exception {
//        if (!client.bucketExists(bucketName)) {
//            client.makeBucket(bucketName);
//        }
//    }
//
//    /**
//     * 获取全部bucket
//     */
//    @SneakyThrows
//    public List<Bucket> getAllBuckets() {
//        return client.listBuckets();
//    }
//
//    /**
//     * 根据bucketName获取信息
//     *
//     * @param bucketName bucket名称
//     */
//    @SneakyThrows
//    public Optional<Bucket> getBucket(String bucketName) {
//        return client.listBuckets().stream().filter(b -> b.name().equals(bucketName)).findFirst();
//    }
//
//    /**
//     * 根据bucketName删除信息
//     *
//     * @param bucketName bucket名称
//     */
//    @SneakyThrows
//    public void removeBucket(String bucketName) {
//        client.removeBucket(bucketName);
//    }
//
//    /**
//     * 获取文件外链
//     *
//     * @param bucketName bucket名称
//     * @param objectName 文件名称
//     * @param expires    过期时间 <=7
//     * @return url
//     */
//    @SneakyThrows
//    public String getObjectURL(String bucketName, String objectName, Integer expires) {
//        return client.presignedGetObject(bucketName, objectName, expires);
//    }
//
//    /**
//     * 获取文件
//     *
//     * @param bucketName bucket名称
//     * @param objectName 文件名称
//     * @return 二进制流
//     */
//    @SneakyThrows
//    public InputStream getObject(String bucketName, String objectName) {
//        return client.getObject(bucketName, objectName);
//    }
//
//    /**
//     * 上传文件
//     *
//     * @param bucketName bucket名称
//     * @param objectName 文件名称
//     * @param stream     文件流
//     * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#putObject
//     */
//    public void putObject(String bucketName, String objectName, InputStream stream) throws Exception {
//        client.putObject(bucketName, objectName, stream, stream.available(), "application/octet-stream");
//    }
//
//    /**
//     * 上传文件
//     *
//     * @param bucketName  bucket名称
//     * @param objectName  文件名称
//     * @param stream      文件流
//     * @param size        大小
//     * @param contextType 类型
//     * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#putObject
//     */
//    public void putObject(String bucketName, String objectName, InputStream stream, long size, String contextType) throws Exception {
//        client.putObject(bucketName, objectName, stream, size, contextType);
//    }
//
//    /**
//     * 获取文件信息
//     *
//     * @param bucketName bucket名称
//     * @param objectName 文件名称
//     * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#statObject
//     */
//    public ObjectStat getObjectInfo(String bucketName, String objectName) throws Exception {
//        return client.statObject(bucketName, objectName);
//    }
//
//    /**
//     * 删除文件
//     *
//     * @param bucketName bucket名称
//     * @param objectName 文件名称
//     * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#removeObject
//     */
//    public void removeObject(String bucketName, String objectName) throws Exception {
//        client.removeObject(bucketName, objectName);
//    }
//
//    /**
//     * 上传文件
//     *
//     * @param file       文件
//     * @param bucketName 存储桶
//     * @return
//     */
//    public String uploadFile(MultipartFile file, String bucketName) {
//        // 判断上传文件是否为空
//        if (null == file || 0 == file.getSize()) {
//            throw new BizException(FileExp.FILE_MISS_ERROR);
//        }
//        try (InputStream inputStream = file.getInputStream()) {
//            // 判断存储桶是否存在
//            createBucket(bucketName);
//            // 文件名
//            String originalFilename = file.getOriginalFilename();
//            String format = originalFilename.substring(originalFilename.lastIndexOf("."));
//            // 新的文件名 = 存储桶名称_时间戳.后缀名
//            String fileName = IdUtil.fastSimpleUUID() + format;
//            // 开始上传
//            client.putObject(bucketName, fileName, inputStream, "application/octet-stream");
//            return client.getObjectUrl(bucketName, fileName);
//        } catch (Exception e) {
//            log.error("文件上传失败", e);
//            throw new BizException(FileExp.FILE_UPLOAD_ERROR);
//        }
//    }
//
//}
//

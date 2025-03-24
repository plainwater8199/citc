package com.citc.nce.filecenter.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementTypeReq;
import com.citc.nce.auth.common.CSPChatbotSupplierTagEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.thread.ThreadTaskUtils;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.dto.*;
import com.citc.nce.fileApi.PictureApi;
import com.citc.nce.fileApi.PlatformApi;
import com.citc.nce.fileApi.VideoApi;
import com.citc.nce.filecenter.configure.AllowUploadFormatsConfigure;
import com.citc.nce.filecenter.configure.FileUploadConfig;
import com.citc.nce.filecenter.configure.FontdoUploadFileConfigure;
import com.citc.nce.filecenter.configure.OwnerUploadFileConfigure;
import com.citc.nce.filecenter.entity.AvailableQuantity;
import com.citc.nce.filecenter.entity.FileManage;
import com.citc.nce.filecenter.exp.FileExp;
import com.citc.nce.filecenter.mapper.FileManageMapper;
import com.citc.nce.filecenter.platform.vo.ReceiveData;
import com.citc.nce.filecenter.platform.vo.UpReceiveData;
import com.citc.nce.filecenter.service.FileManageService;
import com.citc.nce.filecenter.service.IAvailableQuantityService;
import com.citc.nce.filecenter.service.PlatformService;
import com.citc.nce.filecenter.util.*;
import com.citc.nce.filecenter.vo.*;
import com.citc.nce.vo.ExamResp;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaInfo;
import ws.schild.jave.MultimediaObject;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: wenliuch
 * @Date: 2022年8月18日15:11:54
 * @Version: 1.0
 * @Description: FileManageServiceImpl
 */
@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(AllowUploadFormatsConfigure.class)
public class FileManageServiceImpl extends ServiceImpl<FileManageMapper, FileManage> implements FileManageService {

    private final AllowUploadFormatsConfigure allowUploadFormatsConfigure;
    private final OwnerUploadFileConfigure ownerUploadFileConfigure;
    private final FontdoUploadFileConfigure fontdoUploadFileConfigure;
    @Resource
    MinioUtils minioUtil;

    @Resource
    FileManageMapper mapper;

    @Resource
    PlatformService platformService;

    @Resource
    RedisTemplate<String, String> redisTemplate;

    @Resource
    IAvailableQuantityService availableQuantityService;

    @Resource
    private FileUploadConfig fileUploadConfig;

    @Resource
    private AccountManagementApi accountManagementApi;

    @Resource
    PlatformApi platformApi;

    @Resource
    RedissonClient redissonClient;
    @Autowired
    private PictureApi pictureApi;
    @Autowired
    private VideoApi videoApi;


    @Override
    public List<UploadResp> simpleUploadFile(UploadReq uploadReq, String bucketName) {
        //检验文件的相关场景限制
        checkFile(uploadReq.getSceneId(),uploadReq.getFile());
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
        fileManage.setScenarioID(uploadReq.getSceneId());
        fileManage.setFileName(originalFilename);
        fileManage.setCreator("admin");
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
    }

    @Override
    public List<UploadResp> uploadFile(UploadReq uploadReq, String bucketName) {
        //检验文件的相关场景限制
        checkFile(uploadReq.getSceneId(),uploadReq.getFile());
        //主文件
        String originalFilename = uploadReq.getFile().getOriginalFilename();
        String format = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase(Locale.ROOT);
        List<String> formats = Arrays.asList(allowUploadFormatsConfigure.getAllowUploadFormats().split(","));
        if (!formats.contains(format)) {
            throw new BizException(FileExp.FORMAT_ERROR);
        }
        MinioInfo minioInfo = minioUtil.uploadFile(uploadReq.getFile(), "robot");
        String fileUrl = minioInfo.getFileUrl();
        String uuid = IdUtil.fastSimpleUUID();
        FileManage fileManage = new FileManage();
        fileManage.setFileUploadTime(new Date());
        fileManage.setFileUrl(fileUrl);
        fileManage.setFileUuid(uuid);
        fileManage.setFileName(originalFilename);
        fileManage.setScenarioID(uploadReq.getScenarioID());
        BaseUser user = SessionContextUtil.getUser();
        if (user != null) {
            fileManage.setCreator(user.getUserId());
        } else {
            fileManage.setCreator("admin");
        }
        String minioFileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        fileManage.setFileFormat(format);
        fileManage.setMinioFileName(minioFileName);
        File thumbnail = null;
        String thumbnailFileName = "";
        List<String> pictureFormats = Arrays.asList("PNG", "JPEG", "BMP", "JPG");
        boolean isVideoCover = false;
        if (ObjectUtil.isNotEmpty(uploadReq.getThumbnail())) {
            thumbnailFileName = minioUtil.uploadFile(uploadReq.getThumbnail(), bucketName).getFileUrl().substring(fileUrl.lastIndexOf("/") + 1);
            thumbnail = multipartFileToFile(uploadReq.getThumbnail());
            isVideoCover = true;
        }
        if (pictureFormats.contains(format.toUpperCase(Locale.ROOT))) {
            thumbnail = MyFileUtil.buildPictureThumbnail(uploadReq.getFile(), IdUtil.fastSimpleUUID());
            CommonsMultipartFile multipartFile = MyFileUtil.getMultipartFile(thumbnail);
            thumbnailFileName = minioUtil.uploadFile(multipartFile, bucketName).getFileUrl().substring(fileUrl.lastIndexOf("/") + 1);
        }
        if (StringUtils.isNotEmpty(thumbnailFileName)) {
            fileManage.setThumbnailFileName(thumbnailFileName);
        }
        // 最后再获取文件
        File tempFile = multipartFileToFile(uploadReq.getFile());
        String videoDuration = getVideoDuration(tempFile, format);
        String size = DurationUtil.longToString(tempFile.length());
        fileManage.setFileSize(size);
        fileManage.setFileLength(tempFile.length());
        fileManage.setFileDuration(videoDuration);
        fileManage.setAutoThumbnail(minioInfo.getThumbnailBase64());
        saveOrUpdate(fileManage);


        //送审
        List<UploadResp> result = new ArrayList<>();
        if (uploadReq.getList() != null) {
            //加上redis锁
            
            RLock lock = redissonClient.getLock("fileUploadLock");
            try {
                lock.lock();
                String listString = uploadReq.getList();
                List<String> list = JSONArray.parseArray(listString, String.class);
                for (String operator : list) {
                    AccountManagementTypeReq typeReq = new AccountManagementTypeReq();
                    typeReq.setAccountType(operator);
                    typeReq.setCreator(StringUtils.isNotEmpty(uploadReq.getCreator()) ? uploadReq.getCreator() : user.getUserId());

                    AccountManagementResp req = accountManagementApi.getAccountManagementByAccountType(typeReq);
                    UpReceiveData upReceiveData = new UpReceiveData();
                    // 添加蜂动渠道
                    if (Objects.equals(req.getSupplierTag(), CSPChatbotSupplierTagEnum.OWNER.getValue())) {
                        String token = getToken(req.getAppId(), req.getAppKey());
                        upReceiveData = platformService.upToConsumer(tempFile, token, thumbnail);
                    }
                    if (Objects.equals(req.getSupplierTag(), CSPChatbotSupplierTagEnum.FONTDO.getValue())) {
                        String openId = req.getAgentId();
                        String appId = req.getAppId();
                        String nonce = String.valueOf(DateUtil.currentSeconds());
                        String secret = req.getAppKey();
                        upReceiveData = platformService.upToSupplier(tempFile, false, openId, appId, nonce, secret);
                        // 蜂动通道视频上传封面，其他情况不上传
                        if (isVideoCover) {
                            UpReceiveData thumbnailReceiveData = platformService.upToSupplier(thumbnail, true, openId, appId, nonce, secret);
                            upReceiveData.getData().setThumbnailTid(thumbnailReceiveData.getData().getFileTid());
                        }
                    }
                    //保存送审记录
                    saveExam(uuid, req, upReceiveData);
                    //删除临时文件
                    UploadResp resp = new UploadResp();
                    resp.setUrlId(fileManage.getFileUuid());
                    BeanUtils.copyProperties(fileManage, resp);
                    resp.setFileTid(upReceiveData.getData().getFileTid());
                    resp.setThumbnailTid(upReceiveData.getData().getThumbnailTid());
                    resp.setChatbotName(req.getAccountName());
                    resp.setOperator(req.getAccountType());
                    resp.setAccountId(req.getChatbotAccountId());
                    updateQuantity(req, upReceiveData);
                    result.add(resp);
                }
            } finally {
                if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } else {
            UploadResp resp = new UploadResp();
            resp.setUrlId(fileManage.getFileUuid());
            BeanUtils.copyProperties(fileManage, resp);
            result.add(resp);
        }
        if (ObjectUtil.isNotEmpty(thumbnail)) {
            assert thumbnail != null;
            boolean deleteResult = thumbnail.delete();
            if (!deleteResult) {
                log.info("删除失败！");
            }
        }
        boolean delete = tempFile.delete();
        if (!delete) {
            throw new BizException(FileExp.FILE_DELETE_ERROR);
        }
        return result;
    }



    private void checkFile(String sceneId,MultipartFile file){
        if(!"codeincodeservice".equals(sceneId)){//如果是服务中的代码调用就不用做文件类型校验
            File tempFile = FileUtil.convertInputStreamToFile(file);
            if(tempFile != null){
                try{
                    Map<String, FileUploadConfig.FileLimitInfo> sceneMap = fileUploadConfig.getSceneMap();
                    long size = file.getSize();
                    if(sceneMap.containsKey(sceneId)){
                        FileUploadConfig.FileLimitInfo fileLimitInfo = sceneMap.get(sceneId);
                        Long maxSize = fileLimitInfo.getSize();
                        List<String> limitFormats = Strings.isNullOrEmpty(fileLimitInfo.getFormat()) ? new ArrayList<>(): Arrays.asList(fileLimitInfo.getFormat().split(","));
                        String format = FileUtil.getFileExtension(file.getOriginalFilename());
                        if(size <= maxSize*1024){//校验文件大小
                            if(limitFormats.contains(format)){
                                if(FileUtil.checkFormatIsNotPDF(tempFile)){
                                    if((fileLimitInfo.getDuration() != null && fileLimitInfo.getDuration() > 0)){
                                        long duration = FileUtil.getFileDuration(tempFile,format);
                                        if(duration > fileLimitInfo.getDuration()*1000){
                                            throw new BizException(FileExp.FILE_DURATION_ERROR);
                                        }
                                    }
                                }else{
                                    throw new BizException(FileExp.FILE_FORMAT_FALSIFY);
                                }
                            }else{
                                throw new BizException(FileExp.FORMAT_ERROR);
                            }
                        }else{
                            throw new BizException(FileExp.FILE_SIZE_ERROR);
                        }
                    }else{
                        throw new BizException(FileExp.FILE_SCENE_ERROR);
                    }
                }finally {
                    boolean delete = tempFile.delete();
                    if(!delete){
                        log.error(FileExp.FILE_DELETE_ERROR.getMsg());
                    }
                }
            }else{
                throw new BizException(FileExp.FILE_CREATE_ERROR);

            }
        }
    }


    private void updateQuantity(AccountManagementResp req, UpReceiveData upReceiveData) {
        //没有运营商不需要存储
        if (Objects.isNull(req.getAppId())) return;
        updateQuantityDb(req, upReceiveData);
    }

    /**
     * 真实修改数据库使用量
     *
     * @param req           机器人账号数据
     * @param upReceiveData 网关返回——机器人使用量数据
     */
    private void updateQuantityDb(AccountManagementResp req, UpReceiveData upReceiveData) {
        AvailableQuantity quantity = new AvailableQuantity();
        quantity.setAppId(req.getAppId());
        quantity.setChatbotName(req.getAccountName())
                .setOperator(req.getAccountType())
                .setFileCount(upReceiveData.getData().getFileCount())
                .setTotalCount(upReceiveData.getData().getTotalCount());
        QueryWrapper<AvailableQuantity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("operator", req.getAccountType());
        String creator = null;
        if (ObjectUtil.isEmpty(SessionContextUtil.getUser())) {
            creator = req.getCreator();
        } else {
            creator = SessionContextUtil.getUser().getUserId();
        }
        queryWrapper.eq("creator", creator);
        if (!availableQuantityService.exists(queryWrapper)) {
            availableQuantityService.save(quantity);
        } else {
            availableQuantityService.update(quantity, queryWrapper);
        }
    }


    public File multipartFileToFile(MultipartFile multiFile) {
        // 获取文件名
        String fileName = multiFile.getOriginalFilename();
        Assert.notNull(fileName,"filename is null");
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

    private Long getMediaDuration(File file) {
        if (!file.exists()) {
            return null;
        }
        MultimediaObject multimediaObject = new MultimediaObject(file);
        MultimediaInfo info = null;
        try {
            info = multimediaObject.getInfo();
        } catch (EncoderException e) {
            throw new BizException(FileExp.VIDEO_ANALYSIS_ERROR);
        }
        return info.getDuration();
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

    private List<String> getVideoFormatList() {
        List<String> videoFormats = new ArrayList<>();
        videoFormats.add("mp4");
        videoFormats.add("3gp");
        videoFormats.add("webm");
        return videoFormats;
    }

    @SneakyThrows
    public ResponseEntity<byte[]> downloadFile(String uuid) {
        if (uuid == null) {
            throw new BizException(GlobalErrorCode.BAD_REQUEST);
        }
        QueryWrapper<FileManage> wrapper = new QueryWrapper<>();
        wrapper.eq("file_uuid", uuid);
        FileManage fileManage = mapper.selectOne(wrapper);
        String minioFileName = fileManage.getMinioFileName();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<byte[]> entity = null;
        InputStream in = minioUtil.getObject("robot", minioFileName);

        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = in.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.flush();
        byte[] bytes = bos.toByteArray();
        String fileName = fileManage.getFileName();
        String fileNameURL = URLEncoder.encode(fileName, "UTF-8");
        Optional<MediaType> mediaType = MediaTypeFactory.getMediaType(fileName);
        headers.add("Content-Type", mediaType.orElse(MediaType.APPLICATION_OCTET_STREAM).toString());
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
    }

    @Override
    public void deleteFile(DeleteReq req) {
        List<String> fileUrlIds = req.getFileUrlIds();
        //删除网关数据
        List<ExamResp> list = platformApi.findExamByUuid(new VerificationReq().setFileIds(fileUrlIds));
        for (ExamResp examResp : list) {
            AccountManagementTypeReq typeReq = new AccountManagementTypeReq();
            typeReq.setAccountType(examResp.getOperator());
            typeReq.setCreator(SessionContextUtil.getUser().getUserId());
            AccountManagementResp accountType = accountManagementApi.getAccountManagementByAccountType(typeReq);
            if (Objects.equals(CSPChatbotSupplierTagEnum.OWNER.getValue(), accountType.getSupplierTag())) {
                String token = getToken(accountType.getAppId(), accountType.getAppKey());
                if (StringUtils.isNotEmpty(examResp.getThumbnailTid()) && !StringUtils.equals(examResp.getOperator(), "移动")) {
                    platformService.deletedFile(examResp.getThumbnailTid(), token);
                }
                platformService.deletedFile(examResp.getFileId(), token);
            }
            if (Objects.equals(CSPChatbotSupplierTagEnum.FONTDO.getValue(), accountType.getSupplierTag())) {
                String openId = accountType.getAgentId();
                String appId = accountType.getAppId();
                String nonce = String.valueOf(DateUtil.currentSeconds());
                String secret = accountType.getAppKey();
                if (StringUtils.isNotEmpty(examResp.getThumbnailTid())) {
                    platformService.deletedSupplierFile(examResp.getThumbnailTid(), true, openId, appId, nonce, secret);
                }
                platformService.deletedSupplierFile(examResp.getFileId(), false, openId, appId, nonce, secret);
            }
        }

        //删除minio
        fileUrlIds.forEach(uuid -> {
            FileManage fileManage = getByUUId(uuid);
            if (Objects.nonNull(fileManage)) {
                //删除数据库记录
                removeById(fileManage.getId());
                //多线程删除minio
                ThreadTaskUtils.execute(() -> {
                    try {
                        minioUtil.removeObject("robot", fileManage.getMinioFileName());
                        if (StringUtils.isNotEmpty(fileManage.getThumbnailFileName())) {
                            minioUtil.removeObject("robot", fileManage.getThumbnailFileName());
                        }
                    } catch (Exception e) {
                        throw new BizException(FileExp.FILE_DELETE_ERROR);
                    }
                });
            }
        });
    }

    private FileManage getByUUId(String fileUUid) {
        QueryWrapper<FileManage> wrapper = new QueryWrapper<>();
        wrapper.eq("file_uuid", fileUUid);
        return mapper.selectOne(wrapper);
    }

    @Override
    public List<UploadResp> examine(ExamineReq examineReq) {
        List<String> fileUUIDs = examineReq.getFileUUIDs();
        List<String> chatbotAccounts = examineReq.getChatbotAccounts();
        List<UploadResp> result = new ArrayList<>();
        fileUUIDs.forEach(uuid -> {
            AccountManagementTypeReq typeReq = new AccountManagementTypeReq();
            if (ObjectUtil.isNotEmpty(SessionContextUtil.getUser())) {
                typeReq.setCreator(SessionContextUtil.getUser().getUserId());
            } else if (StringUtils.isNotEmpty(examineReq.getCreator())) {
                typeReq.setCreator(examineReq.getCreator());
            } else {
                throw new BizException("送审用户未找到");
            }
            if (ObjectUtil.isNotEmpty(chatbotAccounts)) {
                chatbotAccounts.forEach(item -> {
                    typeReq.setChatbotAccount(item);
                    AccountManagementResp req = accountManagementApi.getAccountManagementByAccountType(typeReq);
                    if (req == null) {
                        throw new BizException("机器人账号未找到");
                    }
                    result.add(examineItem(uuid, req, examineReq.getMediaType()));
                });
            } else if (ObjectUtil.isNotEmpty(examineReq.getOperators())) {
                examineReq.getOperators().forEach(item -> {
                    typeReq.setAccountType(item);
                    AccountManagementResp req = accountManagementApi.getAccountManagementByAccountType(typeReq);
                    if (req == null) {
                        throw new BizException("机器人账号未找到");
                    }
                    result.add(examineItem(uuid, req, examineReq.getMediaType()));
                });
            }
        });
        String errorMessages = result.stream().filter(item -> item.getErrorMsg() != null && !item.getErrorMsg().isEmpty())
                .map(UploadResp::getErrorMsg)
                .collect(Collectors.joining(";"));
        if (StringUtils.isNotEmpty(errorMessages)) {
            throw new BizException(errorMessages);
        }
        return result;
    }

    /**
     * @param uuid              文件uuid
     * @param accountManagement 机器人信息
     * @param mediaType         媒体类型 1音频 2文件 3图片 4视频
     * @return
     */
    public UploadResp examineItem(String uuid, AccountManagementResp accountManagement, Integer mediaType) {
        QueryWrapper<FileManage> wrapper = new QueryWrapper<>();
        wrapper.eq("file_uuid", uuid);
        FileManage fileManage = mapper.selectOne(wrapper);
        File file = getFile(uuid);
        File thumbnail = null;

        UploadResp uploadResp = new UploadResp();
        // 判断送审的素材和通道不符合
        if (!checkItem(file, accountManagement, mediaType)) {
            uploadResp.setErrorMsg(fileManage.getFileName() + " 不符合 " + accountManagement.getAccountName() + " 通道要求，在该账号送审失败");
            return uploadResp;
        }

        // 添加蜂动支持
        UpReceiveData upReceiveData = new UpReceiveData();
        FileTidReq fileTidReq = new FileTidReq();
        fileTidReq.setFileUrlId(uuid);
        fileTidReq.setOperator(accountManagement.getAccountType());
        fileTidReq.setAppId(accountManagement.getAppId());
        FileAccept fileAccept = platformApi.getFileTid(fileTidReq);
        log.info("fileAccept is {}", fileAccept);
        if (Objects.equals(CSPChatbotSupplierTagEnum.OWNER.getValue(), accountManagement.getSupplierTag())) {
            String token = getToken(accountManagement.getAppId(), accountManagement.getAppKey());
            //删除平台测文件
            try {
                if (StringUtils.isNotEmpty(fileAccept.getFileId()) && StringUtils.isNotEmpty(fileAccept.getType())
                        && StringUtils.equals(fileAccept.getType(), "NotExpire")) {
                    if (StringUtils.isNotEmpty(fileAccept.getThumbnailTid()) && !StringUtils.equals(accountManagement.getAccountType(), "移动")) {
                        platformService.deletedFile(fileAccept.getThumbnailTid(), token);
                    }
                    platformService.deletedFile(fileAccept.getFileId(), token);
                }
            } catch (Exception e) {
                log.warn("删除运营商素材失败:uuid:{} reason:{}", uuid, e.getMessage());
            }
            thumbnail = getThumbNailByUUID(uuid);
            upReceiveData = platformService.upToConsumer(file, token, thumbnail);
        }
        if (Objects.equals(CSPChatbotSupplierTagEnum.FONTDO.getValue(), accountManagement.getSupplierTag())) {
            String openId = accountManagement.getAgentId();
            String appId = accountManagement.getAppId();
            String nonce = String.valueOf(DateUtil.currentSeconds());
            String secret = accountManagement.getAppKey();
            // 判断是否有审核素材
            if (StringUtils.isNotEmpty(fileAccept.getFileId())) {
                if (fileAccept.getUseable() == 2) { // 审核通过不进行处理
                    return uploadResp;
                }
                // 没有审核通过的先删除再进行上传操作
                if (StringUtils.isNotEmpty(fileAccept.getThumbnailTid())) { // 如果有缩略图，先删除缩略图
                    platformService.deletedSupplierFile(fileAccept.getThumbnailTid(), true, openId, appId, nonce, secret);
                }
                platformService.deletedSupplierFile(fileAccept.getFileId(), false, openId, appId, nonce, secret);
            }
            upReceiveData = platformService.upToSupplier(file, false, openId, appId, nonce, secret);
            thumbnail = getThumbNailByUUID(uuid);
            if (thumbnail != null) { // 如果有缩略图，上传缩略图
                UpReceiveData thumbnailReceiveData = platformService.upToSupplier(thumbnail, true, openId, appId, nonce, secret);
                upReceiveData.getData().setThumbnailTid(thumbnailReceiveData.getData().getFileTid());
            }
        }
        //删除临时文件
        if (ObjectUtil.isNotEmpty(thumbnail)) {
            assert thumbnail != null;
            boolean deleteResult = thumbnail.delete();
            if (!deleteResult) {
                log.info("删除失败！");
            }
        }

        //更新数据库
        saveExam(uuid, accountManagement, upReceiveData);
        silentlyUpdateThumbnailId(mediaType, uuid, upReceiveData.getData().getThumbnailTid());
        updateQuantity(accountManagement, upReceiveData);
        uploadResp = getUploadResp(upReceiveData, uuid);
        BeanUtils.copyProperties(accountManagement, uploadResp);
        return uploadResp;
    }

    /**
     * 静默更新资源缩略图ID。
     * 即使发生异常，也不会向上抛出，适合在不需要中断流程时使用。
     *
     * @param mediaType   媒体类型 1音频 2文件 3图片 4视频
     * @param fileUuid    文件uuid
     * @param thumbnailId 新的缩略图id
     */
    private void silentlyUpdateThumbnailId(Integer mediaType, String fileUuid, String thumbnailId) {
        try {
            if (mediaType == null || fileUuid == null || thumbnailId == null)
                return;
            switch (mediaType) {
                case 3: {
                    PictureThumbnailReq req = new PictureThumbnailReq();
                    req.setPictureUrlId(fileUuid);
                    req.setThumbnailId(thumbnailId);
                    pictureApi.updatePictureThumbnail(req);
                    break;
                }
                case 4: {
                    VideoThumbnailReq req = new VideoThumbnailReq();
                    req.setVideoUrlId(fileUuid);
                    req.setThumbnailId(thumbnailId);
                    videoApi.updateVideoThumbnail(req);
                    break;
                }
            }
        } catch (Throwable throwable) {
            log.warn("静默更新缩略图id失败:{}", throwable.getMessage());
        }
    }

    @Override
    public FileInfo getFileInfoByScenarioId(FileInfoReq fileInfoReq) {
        if (StringUtils.isNotEmpty(fileInfoReq.getScenarioID()) && StringUtils.isNotEmpty(fileInfoReq.getUuid())) {
            FileInfo fileInfo = new FileInfo();
            QueryWrapper<FileManage> wrapper = new QueryWrapper<>();
            wrapper.eq("file_uuid", fileInfoReq.getUuid());
            wrapper.eq("scenario_id", fileInfoReq.getScenarioID());
            List<FileManage> fileManages = mapper.selectList(wrapper);
            if (!CollectionUtils.isEmpty(fileManages)) {
                FileManage fileManage = fileManages.get(0);
                String fileFormat = fileManage.getFileFormat();
                String fileName = fileManage.getFileName();
                try {
                    fileInfo.setFileFormat(fileFormat);
                    fileInfo.setFileName(fileName);
                    fileInfo.setFileSize(DurationUtil.stringToLong(fileManage.getFileSize()));
                    fileInfo.setCreator(fileManage.getCreator());
                } catch (Exception e) {
                    throw new BizException(FileExp.EXAMINE_ERROR);
                }
                return fileInfo;
            } else {
                throw new BizException(FileExp.FILE_MISS_ERROR);
            }
        }
        return null;
    }

    @Override
    public void checkFile(UploadReq req) {
        checkFile(req.getSceneId(),req.getFile());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadForCaptcha(UploadForCaptchaImageReq req) {
        //检验文件的相关场景限制
        String format = checkImage(req.getFile());
        MinioInfo minioInfo = minioUtil.uploadFile(req.getFile(), "robot");
        String fileUrl = minioInfo.getFileUrl();
        String originalFilename = req.getFile().getOriginalFilename();
        String uuid = IdUtil.fastSimpleUUID();
        FileManage fileManage = new FileManage();
        fileManage.setFileSize(String.valueOf(req.getFile().getSize()));
        fileManage.setFileUploadTime(new Date());
        fileManage.setFileUrl(fileUrl);
        fileManage.setFileUuid(uuid);
        fileManage.setFileName(originalFilename);
        fileManage.setCreator("admin");
        String minioFileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        fileManage.setFileFormat(format);
        fileManage.setMinioFileName(minioFileName);
        fileManage.setAutoThumbnail(minioInfo.getThumbnailBase64());
        saveOrUpdate(fileManage);
        return uuid;
    }

    @Override
    public Map<String, String> getThumbnail(List<String> fileUuids) {
        Map<String, String> result = new HashMap<>();
        if(!CollectionUtils.isEmpty(fileUuids)){
            LambdaQueryWrapper<FileManage> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(FileManage::getFileUuid, fileUuids);
            List<FileManage> fileManages = mapper.selectList(wrapper);
            for (FileManage fileManage : fileManages) {
                result.put(fileManage.getFileUuid(), fileManage.getAutoThumbnail());
            }
        }
        return result;
    }

    private String checkImage(MultipartFile image) {
        File tempFile = FileUtil.convertInputStreamToFile(image);
        long size = image.getSize();
        List<String> limitFormats = Arrays.asList("jpg", "jpeg", "png", "gif");
        String format = FileUtil.getFileExtension(image.getOriginalFilename());
        if(size <= 5120*1024){//校验文件大小
            if(limitFormats.contains(format)){
                if(!FileUtil.checkFormat(tempFile,format)){
                    throw new BizException(FileExp.FILE_FORMAT_FALSIFY);
                }
            }else{
                throw new BizException(FileExp.FORMAT_ERROR);
            }
        }else{
            throw new BizException(FileExp.FILE_SIZE_ERROR);
        }
        return format;
    }


    private File getThumbNailByUUID(String uuid) {
        QueryWrapper<FileManage> wrapper = new QueryWrapper<>();
        wrapper.eq("file_uuid", uuid);
        FileManage fileManage = mapper.selectOne(wrapper);
        File thumbnail = null;
        if (StringUtils.isNotEmpty(fileManage.getThumbnailFileName())) {
            String thumbnailFileName = fileManage.getThumbnailFileName();
            try (InputStream inputStream = minioUtil.getObject("robot", thumbnailFileName)) {
                String format = thumbnailFileName.substring(thumbnailFileName.lastIndexOf(".") + 1);
                String name = thumbnailFileName.substring(0, thumbnailFileName.lastIndexOf("."));
                log.info("文件名：{}，文件格式：{}", name, format);
                thumbnail = MyFileUtil.createTmpFile(inputStream, name, format);
            } catch (IOException e) {
                log.error("文件处理错误", e);
            }
        }
        return thumbnail;
    }


    private void saveExam(String uuid, AccountManagementResp req, UpReceiveData upReceiveData) {
        UpFileReq upFileReq = new UpFileReq();
        upFileReq.setFileUrlId(uuid);
        upFileReq.setOperator(req.getAccountType());
        upFileReq.setAppId(req.getAppId());
        upFileReq.setSupplierTag(req.getSupplierTag());
        upFileReq.setChatBotId(req.getChatbotAccount());
        upFileReq.setChatbotAccountId(req.getChatbotAccountId());
        upFileReq.setChatbotName(req.getAccountName());
        upFileReq.setFileId(upReceiveData.getData().getFileTid());
        if (StringUtils.isNotEmpty(upReceiveData.getData().getThumbnailTid())) {
            upFileReq.setThumbnailTid(upReceiveData.getData().getThumbnailTid());
        }
        if (ObjectUtil.isEmpty(SessionContextUtil.getUser())) {
            upFileReq.setCreator(req.getCreator());
        } else {
            upFileReq.setCreator(SessionContextUtil.getUser().getUserId());
        }
        // 蜂动上传默认为5 审核中 其余上传默认为1 待审核
        upFileReq.setFileStatus(Objects.equals(CSPChatbotSupplierTagEnum.OWNER.getValue(), req.getSupplierTag()) ? 1 : 5);
        platformApi.saveExam(upFileReq);
    }

    public String getToken(String appId, String appKey) {
        log.info("appId is {}  appKey is {}", appId, appKey);
        String token = redisTemplate.opsForValue().get(appId);
        if (StringUtils.isEmpty(token)) {
            ReceiveData receiveData = platformService.getToken(appId, appKey);
            token = receiveData.getData().getToken();
            log.warn("使用新token：{}", token);
            redisTemplate.opsForValue().set(appId, token, 2, TimeUnit.HOURS);
        } else {
            token = redisTemplate.opsForValue().get(appId);
        }
        return token;
    }

    @Override
    public UploadResp examineOne(ExamineOneReq examineOneReq) {
        String operator = examineOneReq.getOperator();
        AccountManagementTypeReq typeReq = new AccountManagementTypeReq();
        typeReq.setAccountType(operator);
        typeReq.setCreator(examineOneReq.getCreator() != null ? examineOneReq.getCreator() : SessionContextUtil.getLoginUser().getUserId());
        AccountManagementResp resp = accountManagementApi.getAccountManagementByAccountType(typeReq);
        if (resp == null) {
            throw new BizException("找不到素材所属机器人账号");
        }
        String token = getToken(resp.getAppId(), resp.getAppKey());
        UpReceiveData upReceiveData = platformService.upToConsumer(getFile(examineOneReq.getFileUUID()), token, null);
        if (upReceiveData.getCode() != 200) {
            throw new BizException(FileExp.EXAMINE_ERROR);
        }
        UploadResp uploadResp = getUploadResp(upReceiveData, examineOneReq.getFileUUID());
        uploadResp.setOperator(operator);
        return uploadResp;
    }

    private File getFile(String uuid) {
        QueryWrapper<FileManage> wrapper = new QueryWrapper<>();
        wrapper.eq("file_uuid", uuid);
        FileManage fileManage = mapper.selectOne(wrapper);
        ResponseEntity<byte[]> responseEntity = downloadFile(uuid);
        byte[] bytes = responseEntity.getBody();
        File file = null;
        try {
            file = MyFileUtil.bytesToFile(bytes, fileManage.getFileFormat(), IdUtil.fastSimpleUUID());
        } catch (IOException e) {
            throw new BizException(FileExp.EXAMINE_ERROR);
        }
        return file;
    }

    /**
     * mediaType 1 音频 3 图片 4 视频
     */
    private Boolean checkItem(File file, AccountManagementResp resp, Integer mediaType) {
        //mediaType为空表示不是在资源上传页面送审，为续期操作，不检查规则
        if (ObjectUtil.isEmpty(mediaType))
            return true;
        // 获取文件后缀 以及文件大小
        String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        long size = file.length();
        long duration = 0L;
        if (mediaType == 1 || mediaType == 4) {
            duration = getMediaDuration(file);
        }
        // 判断通道是否和文件相配
        // 直连
        if (Objects.equals(CSPChatbotSupplierTagEnum.OWNER.getValue(), resp.getSupplierTag())) {
            // 图像
            if (mediaType == 3) {
                if (!checkFormat(ownerUploadFileConfigure.getImgFormat(), suffix)) {
                    return false;
                }
                if (!checkSize(ownerUploadFileConfigure.getImgSize(), size)) {
                    return false;
                }
            }
            // 音频
            if (mediaType == 1) {
                if (!checkFormat(ownerUploadFileConfigure.getAudioFormat(), suffix)) {
                    return false;
                }
                if (!checkSize(ownerUploadFileConfigure.getAudioDuration(), duration)) {
                    return false;
                }
            }
            // 视频
            if (mediaType == 4) {
                if (!checkFormat(ownerUploadFileConfigure.getVideoFormat(), suffix)) {
                    return false;
                }
                if (!checkSize(ownerUploadFileConfigure.getVideoSize(), size)) {
                    return false;
                }
            }
        }
        // 蜂动
        if (Objects.equals(CSPChatbotSupplierTagEnum.FONTDO.getValue(), resp.getSupplierTag())) {
            // 图像
            if (mediaType == 3) {
                if (!checkFormat(fontdoUploadFileConfigure.getImgFormat(), suffix)) {
                    return false;
                }
                if (!checkSize(fontdoUploadFileConfigure.getImgSize(), size)) {
                    return false;
                }
            }
            // 音频
            if (mediaType == 1) {
                if (!checkFormat(fontdoUploadFileConfigure.getAudioFormat(), suffix)) {
                    return false;
                }
                if (!checkSize(fontdoUploadFileConfigure.getAudioDuration(), duration)) {
                    return false;
                }
                if (!checkSize(fontdoUploadFileConfigure.getAudioSize(), size)) {
                    return false;
                }
            }
            // 视频
            if (mediaType == 4) {
                if (!checkFormat(fontdoUploadFileConfigure.getVideoFormat(), suffix)) {
                    return false;
                }
                if (!checkSize(fontdoUploadFileConfigure.getVideoDuration(), duration)) {
                    return false;
                }
                if (!checkSize(fontdoUploadFileConfigure.getVideoSize(), size)) {
                    return false;
                }
            }
        }
        return true;
    }

    private Boolean checkFormat(String formats, String suffix) {
        List<String> suffixFormats = Arrays.asList(formats.split(","));
        return suffixFormats.contains(suffix);
    }

    private Boolean checkSize(Long allowSize, Long size) {
        return allowSize >= size;
    }


    private UploadResp getUploadResp(UpReceiveData upReceiveData, String uuid) {
        if (upReceiveData.getCode() != 200) {
            throw new BizException(FileExp.EXAMINE_ERROR);
        }
        QueryWrapper<FileManage> wrapper = new QueryWrapper<>();
        wrapper.eq("file_uuid", uuid);
        FileManage fileManage = getOne(wrapper);
        UploadResp uploadResp = new UploadResp();
        uploadResp.setFileTid(upReceiveData.getData().getFileTid());
        uploadResp.setUrlId(uuid);
        BeanUtils.copyProperties(fileManage, uploadResp);
        return uploadResp;
    }

    @Override
    public UpReceiveData upToConsumer(UploadReq uploadReq) {
        String operator = uploadReq.getList();
        File tempFile = multipartFileToFile(uploadReq.getFile());
        AccountManagementTypeReq typeReq = new AccountManagementTypeReq();
        typeReq.setAccountType(operator);
        typeReq.setCreator(uploadReq.getCreator());
        AccountManagementResp req = accountManagementApi.getAccountManagementByAccountType(typeReq);
        String token = getToken(req.getAppId(), req.getAppKey());
        return platformService.upToConsumer(tempFile, token, null);
    }

    @Override
    public ResponseEntity<byte[]> downloadFile2(String uuid, HttpServletRequest request) {
        ResponseEntity<byte[]> entity = null;
        RandomAccessFile randomFile = null;
        if (!Strings.isNullOrEmpty(uuid)) {
            HttpStatus status;
            try {
                String range = request.getHeader("Range");
                log.error("=========Range============:" + range);
                //1、获取视频流文件
                FileInfo fileInfo = getFileInfo(uuid);
                randomFile = new RandomAccessFile(fileInfo.getFile(), "r");//只读模式
                long contentLength = randomFile.length();
                //2、获取访问的位置
                Integer[] intArr = obtainStart(range);
                int start = intArr[0];
                int requestSize = intArr[1];
                //3、组装头部信息
                HttpHeaders headers = obtainHttpHeaders(fileInfo);
                //第一次请求只返回content length来让客户端请求多次实际数据
                if (range == null) {
                    status = HttpStatus.OK;
                    headers.add("Content-length", contentLength + "");
                } else {
                    status = HttpStatus.PARTIAL_CONTENT;
                    //以后的多次以断点续传的方式来返回视频数据
                    Map<String, Long> indexMap = obtainIndexMap(range);
                    //更新头部信息
                    updateHttpHeasers(headers, indexMap, contentLength);
                }
                //获取字段流
                ByteArrayOutputStream bos = obtainOPS(randomFile, requestSize, start);
                byte[] bytes = bos.toByteArray();
                log.error("=========status============:" + status);
                entity = new ResponseEntity<>(bytes, headers, status);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(randomFile != null)
                    IOUtils.closeQuietly(randomFile);
            }
        } else {
            throw new BizException(GlobalErrorCode.BAD_REQUEST);
        }
        return entity;
    }

    @Override
    public FileInfo getFileInfo(String uuid) {
        FileInfo fileInfo = new FileInfo();
        QueryWrapper<FileManage> wrapper = new QueryWrapper<>();
        wrapper.eq("file_uuid", uuid);
        FileManage fileManage = mapper.selectOne(wrapper);
        if (ObjectUtil.isEmpty(fileManage)) {
            throw new BizException(FileExp.FILE_MISS_ERROR);
        }
        String fileFormat = fileManage.getFileFormat();
        String fileName = fileManage.getFileName();
        try {
            fileInfo.setFileFormat(fileFormat);
            fileInfo.setFileName(fileName);
            fileInfo.setFileSize(DurationUtil.stringToLong(fileManage.getFileSize()));
            fileInfo.setCreator(fileManage.getCreator());
        } catch (Exception e) {
            throw new BizException(FileExp.EXAMINE_ERROR);
        }
        return fileInfo;
    }

    @Override
    public ResponseEntity<byte[]> videoDownload(DownloadReq downloadReq) {
        ResponseEntity<byte[]> entity = null;
        HttpStatus status;
        RandomAccessFile randomFile = null;
        try {
            String range = downloadReq.getRange();
            QueryWrapper<FileManage> wrapper = new QueryWrapper<>();
            wrapper.eq("file_uuid", downloadReq.getFileUUID());
            FileManage fileManage = mapper.selectOne(wrapper);
            String minioFileName = fileManage.getMinioFileName();
            InputStream in = minioUtil.getObject("robot", minioFileName);
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileFormat(fileManage.getFileFormat());
            fileInfo.setFileName(fileManage.getFileName());
            //2、缓存为临时文件
            String directoryStr = "/tmp/tempFile";
            String stringBuilder = directoryStr + "/" + fileManage.getFileName() + "-tmp" + fileManage.getFileFormat();
            File directory = new File(directoryStr);
            if (!directory.exists()) {
                boolean result = directory.mkdir();
                if (!result) {
                    log.info("目录创建失败！");
                }
            }
            File file = new File(stringBuilder);
            if (!file.exists()) {
                boolean result = file.createNewFile();
                if (!result) {
                    log.info("文件创建失败！");
                }
                FileUtils.copyInputStreamToFile(in, file);
            }
            randomFile = new RandomAccessFile(file, "r");//只读模式
            long contentLength = randomFile.length();
            //3、获取访问的位置
            Integer[] intArr = FileUtil.obtainStart(range);
            int start = intArr[0];
            int requestSize = intArr[1];
            //4、组装头部信息
            HttpHeaders headers = FileUtil.obtainHttpHeaders(fileInfo);
            //第一次请求只返回content length来让客户端请求多次实际数据
            if (range == null) {
                status = HttpStatus.OK;
                headers.add("Content-length", contentLength + "");
            } else {
                status = HttpStatus.PARTIAL_CONTENT;
                //以后的多次以断点续传的方式来返回视频数据
                Map<String, Long> indexMap = FileUtil.obtainIndexMap(range);
                //更新头部信息
                FileUtil.updateHttpHeasers(headers, indexMap, contentLength);
            }
            //获取字段流
            ByteArrayOutputStream bos = FileUtil.obtainOPS(randomFile, requestSize, start);
            byte[] bytes = bos.toByteArray();
            log.error("=========status============:" + status);
            entity = new ResponseEntity<>(bytes, headers, status);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(randomFile != null)
                IOUtils.closeQuietly(randomFile);
        }
        return entity;
    }

    private ByteArrayOutputStream obtainOPS(RandomAccessFile randomFile, int requestSize, int start) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int needSize = requestSize;
        randomFile.seek(start);
        while (needSize > 0) {
            int len = randomFile.read(buffer);
            if (needSize < buffer.length) {
                bos.write(buffer, 0, needSize);
            } else {
                bos.write(buffer, 0, len);
                if (len < buffer.length) {
                    break;
                }
            }
            needSize -= buffer.length;
        }
        randomFile.close();
        bos.flush();
        return bos;
    }

    private void updateHttpHeasers(HttpHeaders headers, Map<String, Long> indexMap, Long contentLength) {
        long requestStart = indexMap.get("requestStart");
        long requestEnd = indexMap.get("requestEnd");
        long length;
        if (requestEnd > 0) {
            length = requestEnd - requestStart + 1;
            headers.add("Content-length", length + "");
            headers.add("Content-Range", "bytes " + requestStart + "-" + requestEnd + "/" + contentLength);
        } else {
            length = contentLength - requestStart;
            headers.add("Content-length", length + "");
            headers.add("Content-Range", "bytes " + requestStart + "-" + (contentLength - 1) + "/" + contentLength);
        }
    }

    private Map<String, Long> obtainIndexMap(String range) {
        Map<String, Long> indexMap = new HashMap<>();
        long requestStart = 0;
        long requestEnd = 0;
        String[] ranges = range.split("=");
        if (ranges.length > 1) {
            String[] rangeDatas = ranges[1].split("-");
            requestStart = Integer.parseInt(rangeDatas[0]);
            if (rangeDatas.length > 1) {
                requestEnd = Integer.parseInt(rangeDatas[1]);
            }
        }
        indexMap.put("requestStart", requestStart);
        indexMap.put("requestEnd", requestEnd);
        return indexMap;
    }

    private HttpHeaders obtainHttpHeaders(FileInfo fileInfo) throws UnsupportedEncodingException {
        HttpHeaders headers = new HttpHeaders();
        String fileName = fileInfo.getFileName();
        String fileFormat = fileInfo.getFileFormat();
        String fileNameURL = URLEncoder.encode(fileName, "UTF-8");
        headers.add("Content-disposition", "attachment;filename=" + fileNameURL);
        headers.add("Accept-Ranges", "bytes");
        headers.add("ETag", fileName);
        headers.add("Last-Modified", new Date().toString());
        List<String> videoFormatList = getVideoFormatList();
        if (videoFormatList.contains(fileFormat.toLowerCase(Locale.ROOT))) {
            headers.add("Content-Type", "video/" + fileFormat.toLowerCase(Locale.ROOT));
        }
        return headers;
    }

    private Integer[] obtainStart(String range) {
        int start = 0;
        int end = 0;
        int requestSize;
        if (range != null && range.startsWith("bytes=")) {
            String[] values = range.split("=")[1].split("-");
            start = Integer.parseInt(values[0]);
            if (values.length > 1) {
                end = Integer.parseInt(values[1]);
            }
        }
        if (end != 0 && end > start) {
            requestSize = end - start + 1;
        } else {
            requestSize = Integer.MAX_VALUE;
        }
        return new Integer[]{start, requestSize};
    }
}

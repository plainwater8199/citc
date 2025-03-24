package com.citc.nce.im.robot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.citc.nce.filecenter.vo.UploadResp;
import com.citc.nce.im.robot.dto.message.*;
import com.citc.nce.im.robot.dto.robot.RobotDto;
import com.citc.nce.im.robot.enums.MessageType;
import com.citc.nce.im.robot.service.SaveRecordService;
import com.citc.nce.im.service.PlatformService;
import com.citc.nce.im.session.entity.Gps;
import com.citc.nce.im.util.ChannelTypeUtil;
import com.citc.nce.msgenum.MsgActionEnum;
import com.citc.nce.robot.RobotAccountApi;
import com.citc.nce.robot.RobotRecordApi;
import com.citc.nce.robot.vo.RobotAccountPageReq;
import com.citc.nce.robot.vo.RobotAccountReq;
import com.citc.nce.robot.vo.RobotRecordResp;
import com.citc.nce.robot.vo.RobotRecordSaveReq;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/10/27 15:25
 */
@Slf4j
@Service
public class SaveRecordServiceImpl implements SaveRecordService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RobotRecordApi robotRecordApi;
    @Resource
    private RobotAccountApi robotAccountApi;

    private final List<String> pictureFormat = Arrays.asList("png", "jpg", "jpeg", "gif");
    private final List<String> audioFormat = Arrays.asList("mp3", "aac", "amr");
    private final List<String> videoFormat = Arrays.asList("mp4");

    @Resource
    PlatformService platformService;
    @Value("${account.callbackUrl}")
    private String callBack;

    @Override
    public RobotRecordResp saveRecord(MsgDto msgDto, RobotDto robotDto) {
        // 保存聊天记录使用newMsgDto
        MsgDto newMsgDto = BeanUtil.copyProperties(msgDto, MsgDto.class);
        // 文件处理 上传到文件服务器
        String fileId = "";
        String thumbnailId = "";
        int messageType = 0;
        log.info("########## SaveRecordServiceImpl saveRecord newMsgDto :{}", newMsgDto);
        if (newMsgDto.getMessageData().contains("fileSize") && newMsgDto.getMessageData().contains("fileContentType")) {
            String messageData = newMsgDto.getMessageData();
            JSONObject jsonObject = JSONObject.parseObject(messageData);
            log.info("上传文件服务器数据 {}", jsonObject);
            String fileContentType = jsonObject.getString("fileContentType");
            if (fileContentType.contains("image/")) {
                fileContentType = fileContentType.replace("image/", "");
                messageType = MessageType.PICTURE.getCode();
            } else if (fileContentType.contains("audio/")) {
                fileContentType = fileContentType.replace("audio/", "");
                messageType = MessageType.AUDIO.getCode();
            } else if (fileContentType.contains("video/")) {
                fileContentType = fileContentType.replace("video/", "");
                messageType = MessageType.VIDEO.getCode();
                //上传缩略图
                String thumbnailContentType = jsonObject.getString("thumbnailContentType");
                String replace = thumbnailContentType.replace("image/", "");
                List<String> fileID = platformService.downloadFromPlatform(jsonObject.getString("thumbnailID"), msgDto.getChatbotAccount(), replace);
                log.info("上传缩略图服务器后数据 {} ", fileID);
                if (CollectionUtils.isNotEmpty(fileID)) {
                    thumbnailId = fileID.get(0);

                }

            } else if (fileContentType.contains("thumbnail/")) {
                fileContentType = fileContentType.replace("thumbnail/", "");
            }
            List<UploadResp> uploadResps = platformService.downloadFromPlatformGetFileInfo(jsonObject.getString("fileID"), msgDto.getChatbotAccount(), fileContentType);
            if (CollectionUtils.isNotEmpty(uploadResps)) {
                List<String> fileID = uploadResps.stream().map(UploadResp::getUrlId).collect(Collectors.toList());
                fileId = fileID.get(0);
                log.info("上传文件服务器后数据 {} 文件id {}", fileID, fileId);
                log.info("上传文件服务器后数据  uploadResps{} ", uploadResps);
                // 消息内容替换成文件的地址
                msgDto.setMessageData(callBack + "/file/download/id?req=" + fileId);
                /**
                 * 手机终端发送文件时修改聊天记录，不直接保存链接，而是让它能够正常展示
                 */
                UploadResp uploadResp = uploadResps.get(0);
                JSONObject msg = new JSONObject();
                JSONObject messageDetail = new JSONObject();
                boolean isPicture = pictureFormat.stream().anyMatch(format -> org.apache.commons.lang.StringUtils.equals(format, uploadResp.getFileFormat().toLowerCase()));
                boolean isAudio = audioFormat.stream().anyMatch(format -> org.apache.commons.lang.StringUtils.equals(format, uploadResp.getFileFormat().toLowerCase()));
                boolean isVideo = videoFormat.stream().anyMatch(format -> org.apache.commons.lang.StringUtils.equals(format, uploadResp.getFileFormat().toLowerCase()));
                if (isVideo) {
                    messageDetail.put("videoUrlId", uploadResp.getUrlId());
                    messageDetail.put("mainCoverId", uploadResp.getUrlId());
                    msg.put("type", MessageType.VIDEO.getCode());
                    messageDetail.put("imgSrc", "/chatbotApi/file/download/id?req=" + uploadResp.getUrlId());

                }
                if (isAudio) {
                    messageDetail.put("audioUrlId", uploadResp.getUrlId());
                    messageDetail.put("duration", uploadResp.getFileDuration());
                    messageDetail.put("size", uploadResp.getFileSize());
                    messageDetail.put("type", uploadResp.getFileFormat());
                    msg.put("type", MessageType.AUDIO.getCode());
                    messageDetail.put("imgSrc", "data:image/png;base64,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");
                }
                if (isPicture) {
                    messageDetail.put("pictureUrlId", uploadResp.getUrlId());
                    msg.put("type", MessageType.PICTURE.getCode());
                    msgDto.setMessageType(MessageType.PICTURE.getCode());
                    messageDetail.put("imgSrc", "/chatbotApi/file/download/id?req=" + uploadResp.getUrlId());
                }
                messageDetail.put("name", uploadResp.getFileName());

                msg.put("messageDetail", messageDetail);
                JSONObject body = new JSONObject();
                body.put("msg", msg);
                JSONObject message = new JSONObject();
                message.put("account", msgDto.getChatbotAccount());
                message.put("body", body);
                newMsgDto.setMessageData(message.toString());
            }
        }

        /**
         * 非按钮时要保存聊天记录
         */
        if ((null != newMsgDto.getIsDeliveryMessage() && newMsgDto.getIsDeliveryMessage() != 1)
                || !MsgActionEnum.ACTION.getCode().equals(newMsgDto.getAction())) {
            return savaRecord(newMsgDto, fileId, thumbnailId, messageType, robotDto.getExpireTime());
        }
        return null;
    }

    private RobotRecordResp savaRecord(MsgDto newMsgDto, String fileId, String thumbnailId, int messageType, Long expireTime) {
        RobotRecordResp robotRecordDo = null;
        try {
            StringBuilder redisKey = new StringBuilder();
            redisKey.append(newMsgDto.getChatbotAccount());
            if (StringUtil.isNotEmpty(newMsgDto.getPhone())) {
                redisKey.append("@");
                redisKey.append(newMsgDto.getPhone());
            }

            redisKey.append(":SerialNum");
            if (!checkRecordList(newMsgDto.getPhone(), newMsgDto.getChatbotAccount(), newMsgDto.getCreate())) {
                // 新增用户数 活跃用户数
                saveRobotAccount(newMsgDto);
                Long serialNum = 1L;
                stringRedisTemplate.opsForValue().set(redisKey.toString(), String.valueOf(serialNum), expireTime, TimeUnit.MINUTES);
                RobotRecordSaveReq robotRecordReq = getRecordSaveReq(newMsgDto, fileId, thumbnailId, messageType, serialNum);
                robotRecordDo = robotRecordApi.saveRobotRecord(robotRecordReq);
            } else {
                String serialNumStr = stringRedisTemplate.opsForValue().get(redisKey.toString());
                Long serialNum = 1L;
                if (StringUtils.isNotEmpty(serialNumStr)) {
                    serialNum = Long.parseLong(serialNumStr) + 1;
                }
                RobotRecordSaveReq robotRecordReq = getRecordSaveReq(newMsgDto, fileId, thumbnailId, messageType, serialNum);
                log.info("----------robotRecordReq:{}",robotRecordReq);
                robotRecordDo = robotRecordApi.saveRobotRecord(robotRecordReq);
                stringRedisTemplate.opsForValue().set(redisKey.toString(), String.valueOf(serialNum), expireTime, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            log.error("保存聊天记录异常:{}", e.getMessage(), e);
        }
        return robotRecordDo;
    }

    private Boolean checkRecordList(String phone, String account, String create) {
        RobotAccountPageReq robotAccountPageReq = new RobotAccountPageReq();
        robotAccountPageReq.setMobileNum(phone);
        robotAccountPageReq.setAccount(account);
        robotAccountPageReq.setCreate(create);
        return robotAccountApi.getRobotAccountList(robotAccountPageReq).getList().size() > 0;
    }

    private void saveRobotAccount(MsgDto newMsgDto) {
        RobotAccountReq robotAccountReq = new RobotAccountReq();
        robotAccountReq.setAccount(newMsgDto.getChatbotAccount());
        robotAccountReq.setChannelType(ChannelTypeUtil.getChannelType(newMsgDto.getAccountType()));
        robotAccountReq.setMobileNum(newMsgDto.getPhone());
        robotAccountReq.setConversationId(newMsgDto.getPhone());
        robotAccountReq.setCreator(newMsgDto.getCustomerId());
        robotAccountReq.setUpdater(newMsgDto.getCustomerId());
        robotAccountReq.setChatbotAccountId(newMsgDto.getChatbotAccountId());
        robotAccountApi.saveRobotAccount(robotAccountReq);
    }

    private RobotRecordSaveReq getRecordSaveReq(MsgDto newMsgDto, String fileId, String thumbnailId, int messageType, Long serialNum) {
        RobotRecordSaveReq robotRecordReq = new RobotRecordSaveReq();
        robotRecordReq.setConversationId(newMsgDto.getPhone());
        robotRecordReq.setSerialNum(serialNum);
        robotRecordReq.setSendPerson("2");
        robotRecordReq.setChatbotAccountId(newMsgDto.getChatbotAccountId());
        robotRecordReq.setChatbotAccount(newMsgDto.getChatbotAccount());
        String message = changeMessageData(newMsgDto.getMessageData(), fileId, thumbnailId);
        robotRecordReq.setMessage(message);
        robotRecordReq.setMobileNum(newMsgDto.getPhone());
        robotRecordReq.setCreator(newMsgDto.getCustomerId());
        robotRecordReq.setUpdater(newMsgDto.getCustomerId());
        robotRecordReq.setChannelType(ChannelTypeUtil.getChannelType(newMsgDto.getAccountType()));
        robotRecordReq.setMessageType(messageType);
        return robotRecordReq;
    }

    private String changeMessageData(String messageData, String fileId, String thumbnailId) {
        if (messageData.contains("fileSize") && messageData.contains("fileContentType") && messageData.contains("fileID")) {
            JSONObject jsonObject = JSONObject.parseObject(messageData);
            String fileContentType = jsonObject.getString("fileContentType");
            log.error("聊天记录消息信息：", messageData);
            JSONObject newObject = new JSONObject();
            if (fileContentType.contains("image/")) {
                fileContentType = fileContentType.replace("image/", "");
                putMessageDetail(MessageType.PICTURE, fileId, fileId, jsonObject, fileContentType, newObject, null);
            } else if (fileContentType.contains("audio/")) {
                fileContentType = fileContentType.replace("audio/", "");
                putMessageDetail(MessageType.AUDIO, null, fileId, jsonObject, fileContentType, newObject, null);
            } else if (fileContentType.contains("video/")) {
                fileContentType = fileContentType.replace("video/", "");
                putMessageDetail(MessageType.VIDEO, thumbnailId, fileId, jsonObject, fileContentType, newObject, thumbnailId);
            }
            return newObject.toJSONString();
        }

        if (messageData.contains("geo:") && messageData.contains("u=") && messageData.contains("crs=") && messageData.contains("rcs-l=")) {
            try {
                JSONObject newObject = new JSONObject();
                newObject.put("type", MessageType.LOCATION.getCode());
                MessageDetail messageDetail = new MessageDetail();
                String[] split = messageData.split(";");
                for (int i = 0; i < split.length; i++) {
                    if (split[i].contains("geo:")) {
                        String replace = split[i].replace("geo:", "");
                        String[] split1 = replace.split(",");
                        Gps gps = bd09_To_Gcj02(Double.parseDouble(split1[0]), Double.parseDouble(split1[1]));
                        Location location = new Location();
                        location.setLongitude(String.valueOf(gps.getLon()));
                        location.setLatitude(String.valueOf(gps.getLat()));
                        messageDetail.setLocation(location);
                        Baidu baidu = new Baidu();
                        baidu.setLng(split1[1]);
                        baidu.setLat(split1[0]);
                        messageDetail.setBaidu(baidu);
                    }
                    if (split[i].contains("rcs-l=")) {
                        String replace = split[i].replace("rcs-l=", "");
                        String decode = URLDecoder.decode(replace, "utf-8");
                        LocationDetail locationDetail = new LocationDetail();
                        locationDetail.setName(decode);
                        locationDetail.setValue(decode);
                        locationDetail.setNames(new ArrayList<>());
                        messageDetail.setLocationDetail(locationDetail);
                    }
                }
                newObject.put("messageDetail", messageDetail);
                return newObject.toJSONString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return messageData;
    }


    private static void putMessageDetail(MessageType type, String url, String fileId, JSONObject jsonObject, String fileContentType,
                                         JSONObject newObject, String thumbnailId) {
        newObject.put("type", type.getCode());
        MessageDetail messageDetail = new MessageDetail();
        switch (type) {
            case PICTURE:
                messageDetail.setImgSrc("/chatbotApi/file/download/id?req=" + url);
                messageDetail.setPictureUrlId(fileId);
                break;
            case AUDIO:
                messageDetail.setAudioUrlId(fileId);
                break;
            case VIDEO:
                messageDetail.setImgSrc("/chatbotApi/file/download/id?req=" + url);
                messageDetail.setMainCoverId(thumbnailId);
                messageDetail.setVideoUrlId(fileId);
                break;
            default:
        }
        messageDetail.setName(fileId + "." + fileContentType);
        messageDetail.setSize(String.valueOf(jsonObject.getIntValue("fileSize")));
        newObject.put("messageDetail", messageDetail);
    }

    public Gps bd09_To_Gcj02(double bd_lat, double bd_lon) {
        double pi = 3.1415926535897932384626;
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * pi);
        double gg_lon = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return new Gps(gg_lon, gg_lat);
    }
}

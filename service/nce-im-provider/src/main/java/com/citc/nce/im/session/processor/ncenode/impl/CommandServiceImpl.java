package com.citc.nce.im.session.processor.ncenode.impl;

import com.alibaba.fastjson.JSONObject;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.dto.*;
import com.citc.nce.fileApi.AudioApi;
import com.citc.nce.fileApi.PictureApi;
import com.citc.nce.fileApi.PlatformApi;
import com.citc.nce.fileApi.VideoApi;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.filecenter.vo.FileInfo;
import com.citc.nce.filecenter.vo.FileInfoReq;
import com.citc.nce.filecenter.vo.UploadReq;
import com.citc.nce.filecenter.vo.UploadResp;
import com.citc.nce.im.robot.dto.message.MsgDto;
import com.citc.nce.im.session.constant.CustomCommandTypeEnum;
import com.citc.nce.im.session.processor.NodeProcessor;
import com.citc.nce.im.session.processor.bizModel.OrderModel;
import com.citc.nce.im.session.processor.ncenode.CommandService;
import com.citc.nce.im.util.dynamicfeign.DynamicClient;
import com.citc.nce.robot.RobotVariableApi;
import com.citc.nce.robot.api.OperateHostApi;
import com.citc.nce.robot.api.RobotCallPythonApi;
import com.citc.nce.robot.api.RobotProcessorApi;
import com.citc.nce.robot.vo.*;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>CommandServiceImpl</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2022/12/9 10:57
 */
@Service
@Slf4j
public class CommandServiceImpl implements CommandService {
    /**
     * 自定义指令用
     */
    @Value("${robotCommand.callBackUrl}")
    private String callBackUrl;

    @Autowired
    private RobotCallPythonApi robotCallPythonApi;

    @Autowired
    private FileApi fileApi;

    @Autowired
    private PlatformApi platformApi;

    @Qualifier("com.citc.nce.robot.api.RobotProcessorApi")
    @Autowired
    private RobotProcessorApi robotProcessorApi;

    @Autowired
    private OperateHostApi operateHostApi;

    @Autowired
    private VideoApi videoApi;

    @Autowired
    private AudioApi audioApi;

    @Autowired
    private PictureApi pictureApi;

    @Autowired
    private RobotVariableApi robotVariableApi;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private DynamicClient dynamicClient;

    /**
     * 轮询时间间隔 单位：毫秒
     */
    private static final Long POLLING_INTERVAL = 500L;
    /**
     * 轮询次数
     */
    private static final Long POLLING_COUNT = 30L;

    private static final Long OVER_TIME = 60L;

    private final List<String> pictureFormat = Arrays.asList("png", "jpg", "jpeg", "gif");
    private final List<String> audioFormat = Arrays.asList("mp3", "aac", "amr");
    private final List<String> videoFormat = Arrays.asList("mp4");

    private final String MSG_OVERTIME = "指令执行超时，请检查后重试...";
    private final String MSG_FAILURE = "指令执行失败，请检查后重试...";

    private FileInfo getFileType(String fileUrlId) {
        FileInfoReq fileInfoReq = new FileInfoReq();
        fileInfoReq.setUuid(fileUrlId);
        return fileApi.getFileInfo(fileInfoReq);
    }

    @Override
    public MaterialSubmitResp materialSubmit(MaterialSubmitReq req) {
        log.info("_________ materialSubmit is start");
        log.info("_________ materialSubmit req {}", req);
        MaterialSubmitResp res = new MaterialSubmitResp();
        String s = stringRedisTemplate.opsForValue().get(req.getRedisKey());
        if (StringUtils.isEmpty(s)) {
            res.setCode("1");
            res.setRedisKey(req.getRedisKey());
            return res;
        }
        stringRedisTemplate.opsForValue().set(req.getRedisKey() + "Execute", "-1", OVER_TIME, TimeUnit.SECONDS);

        UploadReq uploadReq = new UploadReq();
        BeanUtils.copyProperties(req, uploadReq);
        uploadReq.setSceneId("codeincodeservice");
        String listString = uploadReq.getList() + ",";
        String[] split = listString.split(",");
        List<UploadResp> uploadRespList = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            try {
                uploadReq.setList("[\"" + split[i] + "\"]");
                List<UploadResp> resps = fileApi.uploadFile(uploadReq);
                uploadRespList.addAll(resps);
            } catch (Exception e) {
                log.info("_________ materialSubmit 当前文件送审失败 file : {} operator ： {}", req.getFile(), split[i]);
                UploadResp uploadResp = new UploadResp();
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("当前文件送审失败,对应运营商").append(split[i]);
                uploadResp.setUrlId(stringBuffer.toString());
                uploadResp.setFileFormat("msg");
                uploadRespList.add(uploadResp);
            }
        }
        log.info("_________ materialSubmit fileApi.uploadFile uploadRespList {}", uploadRespList);

        try {
            List<UploadResp> respList = new ArrayList<>();
            for (UploadResp resp :
                    uploadRespList) {
                // todo
                // 送审图片
                savePicture(resp, uploadReq.getCreator());
                // 送审音频
                saveAudio(resp);
                // 送审视频
                saveVideo(resp);
                if (StringUtils.isEmpty(resp.getThumbnailTid())) {
                    resp.setThumbnailTid("");
                }
                respList.add(resp);
            }
            res.setCode("0");
            // 轮询回调结果
            String data = JsonUtils.obj2String(respList);
            stringRedisTemplate.opsForValue().set(req.getRedisKey(), data, OVER_TIME, TimeUnit.SECONDS);
        } catch (Exception e) {
            res.setCode("1");
        }
        res.setRedisKey(req.getRedisKey());
        log.info("_________ materialSubmit is end");
        return res;
    }

    public Map<String, String> setVariable(String redisKey) {
        Map<String, String> res = new HashMap<>();
        String s = stringRedisTemplate.opsForValue().get(redisKey + "Variable");
        HashMap redisMap = JsonUtils.string2Obj(s, HashMap.class);
        Iterator<Map.Entry<String, String>> iterator = redisMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            String nextKey = next.getKey();
            StringBuffer sb = new StringBuffer();
            sb.append("{{custom-").append(nextKey).append("}}");
            res.put(sb.toString(), next.getValue());
        }
        return res;
    }

    @Override
    public RobotCustomCommandResp variableList(RobotCustomCommandReq req) {
        RobotCustomCommandResp res = new RobotCustomCommandResp();
        log.info("_________ variableList req {}", req);
        String s = stringRedisTemplate.opsForValue().get(req.getRedisKey());
        if (StringUtils.isEmpty(s)) {
            res.setCode("1");
            res.setRedisKey(req.getRedisKey());
            return res;
        }
        stringRedisTemplate.opsForValue().set(req.getRedisKey() + "Execute", "-1", OVER_TIME, TimeUnit.SECONDS);
        RobotVariablePageReq variablePageReq = new RobotVariablePageReq();
        BeanUtils.copyProperties(req, variablePageReq);
        RobotVariableResp robotVariableResp = robotVariableApi.listAll(variablePageReq);
        String data = JsonUtils.obj2String(robotVariableResp);
        stringRedisTemplate.opsForValue().set(req.getRedisKey(), data, OVER_TIME, TimeUnit.SECONDS);
        res.setCode("0");
        res.setRedisKey(req.getRedisKey());
        return res;
    }

    @Override
    public RobotCustomCommandResp variableEditByName(RobotCustomCommandReq req) {
        RobotCustomCommandResp res = new RobotCustomCommandResp();
        log.info("_________ variableEditByName req {}", req);
        String s = stringRedisTemplate.opsForValue().get(req.getRedisKey());
        if (StringUtils.isEmpty(s)) {
            res.setCode("1");
            res.setRedisKey(req.getRedisKey());
            return res;
        }
        stringRedisTemplate.opsForValue().set(req.getRedisKey() + "Execute", "-1", OVER_TIME, TimeUnit.SECONDS);
        RobotVariableReq variablePageReq = new RobotVariableReq();
        BeanUtils.copyProperties(req, variablePageReq);
        robotVariableApi.editByName(variablePageReq);
        res.setCode("0");
        res.setRedisKey(req.getRedisKey());
        return res;
    }

    public void sendMsgByResult(String redisKey, String executeLoop) {
        setDataOrSendMsg(redisKey, "", executeLoop);
    }

    private void setDataOrSendMsg(String redisKey, String data, String executeLoop) {
        if (StringUtils.equals("0", executeLoop)) {
            if (StringUtils.isNotEmpty(data)) {
                stringRedisTemplate.opsForValue().set(redisKey, data, OVER_TIME, TimeUnit.SECONDS);
            }
        } else if (StringUtils.equals("1", executeLoop)) {
            log.info("###### 自定义指令 失败");
            executeMsg(redisKey, MSG_FAILURE);
            stringRedisTemplate.delete(redisKey + "Execute");
//            throw new BizException(CommandExp.FAILURE_ERROR);
        } else if (StringUtils.isEmpty(executeLoop)) {
            log.info("###### 自定义指令 超时");
            executeMsg(redisKey, MSG_OVERTIME);
            stringRedisTemplate.delete(redisKey + "Execute");
//            throw new BizException(CommandExp.OVERTIME_ERROR);
        }
    }

    public String executeLoop(String redisKey) {
        while (true) {
            String result = stringRedisTemplate.opsForValue().get(redisKey + "Execute");
            // 0:成功,1:失败
            if (StringUtils.equals("0", result)) {
                return "0";
            } else if (StringUtils.equals("1", result)) {
                return "1";
            } else if (StringUtils.isEmpty(result)) {
                return null;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(POLLING_INTERVAL);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.info("###### 自定义指令 轮询异常：", e);
                throw new RuntimeException(e);
            }
        }
    }

    private void saveVideo(UploadResp resp) {
        boolean isVideo = videoFormat.stream().anyMatch(format -> StringUtils.equals(format, resp.getFileFormat().toLowerCase()));
        if (isVideo) {
            log.info("_________ saveVideo is start");
            VideoReq videoReq = new VideoReq();
            videoReq.setVideoUrlId(resp.getUrlId());
            videoReq.setThumbnailTid("");
//            videoReq.setThumbnailTid(resp.getThumbnailTid());
            videoReq.setVideoDuration(resp.getFileDuration());
            videoReq.setVideoName(resp.getFileName());
            videoReq.setVideoFormat(resp.getFileFormat());
            videoReq.setVideoSize(resp.getFileSize());
            videoReq.setCovers(Arrays.asList(""));
            videoApi.saveVideo(videoReq);
            log.info("_________ saveVideo is end");
        }
    }

    private void saveAudio(UploadResp resp) {
        boolean isAudio = audioFormat.stream().anyMatch(format -> StringUtils.equals(format, resp.getFileFormat().toLowerCase()));
        if (isAudio) {
            log.info("_________ saveAudio is start");
            AudioReq audioReq = new AudioReq();
            audioReq.setAudioUrlId(resp.getUrlId());
            audioReq.setAudioDuration("20秒");
            audioReq.setAudioSize(resp.getFileSize());
            audioReq.setAudioName(resp.getFileName());
            audioReq.setOperator(resp.getOperator());
            audioApi.saveAudio(audioReq);
            log.info("_________ saveAudio is end");
        }
    }

    private void savePicture(UploadResp resp, String creator) {
        if (StringUtils.isNotEmpty(resp.getFileFormat())) {
            boolean isPicture = pictureFormat.stream().anyMatch(format -> StringUtils.equals(format, resp.getFileFormat().toLowerCase()));
            if (isPicture) {
                CustomCommandPictureReq pictureReq = new CustomCommandPictureReq();
                pictureReq.setPictureUrlId(resp.getUrlId());
                pictureReq.setThumbnailTid(resp.getThumbnailTid());
                pictureReq.setPictureName(resp.getFileName());
                pictureReq.setPictureFormat(resp.getFileFormat());
                pictureReq.setPictureSize(resp.getFileSize());
                pictureReq.setOperator(resp.getOperator());
                pictureReq.setFileId(resp.getFileTid());
                pictureReq.setCreator(creator);
                pictureApi.customCommandSave(pictureReq);
            }
        }
    }

    /**
     * 获取运营商的审核结果
     * 原本是把审核后的结果返回，用于拼装消息
     * 但实际是部分运营商是人工审核，无法实时返回
     * 现在此方法会被python调用，但实际不会返回结果
     *
     * @param
     * @return
     * @author zy.qiu
     * @createdTime 2023/4/24 10:22
     */
    @Override
    public RobotCustomCommandResp getParamForSendMsg(RobotCustomCommandReq req) {
        RobotCustomCommandResp res = new RobotCustomCommandResp();
        // 轮询回调结果
        res.setCode("0");
        res.setRedisKey(req.getRedisKey());
        return res;
    }


    @Override
    public String processCustomCommand(OrderModel orderModel, NodeProcessor nodeProcessor, String redisKey) throws Exception {
        log.info("###### 自定义指令 start");
        AtomicReference<String> fileUrl = new AtomicReference<>();
        AtomicInteger coustomType = new AtomicInteger(CustomCommandTypeEnum.CUSTOM_DEFAULT.getCode());
        UpMsgReq upMsgReq = nodeProcessor.getUpMsgReq();
        UpMsgReq msgReq = upMsgReq;
        String obj2String = JsonUtils.obj2String(upMsgReq);
        stringRedisTemplate.opsForValue().set(redisKey + "msgReq", obj2String, OVER_TIME, TimeUnit.SECONDS);
        stringRedisTemplate.opsForValue().set(redisKey + "Execute", "-1", OVER_TIME * 5, TimeUnit.SECONDS);
        // 指令需要使用到系统内的变量
        Map<String, String> stringMap = nodeProcessor.getSessionContext().getVar();
        Iterator<Map.Entry<String, String>> iterator = stringMap.entrySet().iterator();
        JSONObject redisMap = new JSONObject();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            String key = next.getKey().replace("{{custom-", "").replace("}}", "");
            redisMap.put(key, next.getValue());
        }

        stringRedisTemplate.opsForValue().set(redisKey + "Variable", redisMap.toJSONString(), OVER_TIME * 5, TimeUnit.SECONDS);

        // 当前状态 0,等待执行
        String dockerName = sendCustomCommand(orderModel, redisKey, msgReq, fileUrl.get(), coustomType.get(), msgReq.getAccountType());
        // 轮询回调结果
        String executeLoop = executeLoop(redisKey);

        // 删除docker容器
        DockerInfo req = new DockerInfo();
        req.setServerName(dockerName);
        operateHostApi.removeDocker(req);
//        setDataOrSendMsg(redisKey, data, executeLoop);
        log.info("###### 自定义指令 end");
        log.info("###### 自定义指令 sendCustomCommand delete redisKey");
        stringRedisTemplate.delete(redisKey);
        stringRedisTemplate.delete(redisKey + "msgReq");
        stringRedisTemplate.delete(redisKey + "Execute");
        return executeLoop;
    }

    @Override
    public String processCustomCommand(OrderModel orderModel, MsgDto msgDto, String rkp) throws Exception {
        log.info("---------------------自定义指令 start---------------------------");
        String obj2String = JsonUtils.obj2String(msgDto);
        stringRedisTemplate.opsForValue().set(rkp + "msgReq", obj2String, OVER_TIME, TimeUnit.SECONDS);
        stringRedisTemplate.opsForValue().set(rkp + "Execute", "-1", OVER_TIME * 5, TimeUnit.SECONDS);
        // 当前状态 0,等待执行
        String dockerName = sendCustomCommand(orderModel, rkp, msgDto, null, CustomCommandTypeEnum.CUSTOM_DEFAULT.getCode(), msgDto.getAccountType());
        // 轮询回调结果
        String executeLoop = executeLoop(rkp);
        // 删除docker容器
        DockerInfo req = new DockerInfo();
        req.setServerName(dockerName);
        operateHostApi.removeDocker(req);
        log.info("---------------------自定义指令 end---------------------------");
        stringRedisTemplate.delete(rkp);
        stringRedisTemplate.delete(rkp + "msgReq");
        stringRedisTemplate.delete(rkp + "Execute");
        return executeLoop;
    }

    private void executeMsg(String redisKey, String text) {
        String s1 = stringRedisTemplate.opsForValue().get(redisKey + "msgReq");
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(s1)) {
            UpMsgReq msgReq = JsonUtils.string2Obj(s1, UpMsgReq.class);
            // 判断发送的内容类型  type 1文本 2图片 3视频 4音频 5文件 6单卡 7多卡 8位置
            msgReq.setMessageData(text);
            msgReq.setMessageType(1);
            // 发送消息
            try {
                robotProcessorApi.javaSendMsg(msgReq);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                stringRedisTemplate.delete(redisKey + "msgReq");
            }
        }
    }

    /**
     * 方法未使用
     * <p>
     * <p>
     * private void dealWithSendFileExamine(AtomicInteger coustomType, UpMsgReq upMsgReq, UpMsgReq msgReq, List<Object> objList, int i) throws Exception {
     * if (CustomCommandTypeEnum.CUSTOM_SEND_FILE_EXAMINE.getCode() == coustomType.get()) {
     * Gson gson = new Gson();
     * String gsonMessage = gson.toJson(objList.get(i));
     * String s = gsonMessage.replace("[", "").replace("]", "");
     * JSONObject jsonObject = JSONObject.parseObject(s);
     * FileTidReq fileTidReq = new FileTidReq();
     * fileTidReq.setFileUrlId((String) jsonObject.get("urlId"));
     * // 异步获取送审的结果
     * fileTidReq.setOperator(upMsgReq.getAccountType());
     * FileAccept fileTid = null;
     * int count = 0;
     * while (count <= 10 && null == fileTid) {
     * count++;
     * fileTid = platformApi.getFileTid(fileTidReq);
     * if (null == fileTid) {
     * TimeUnit.MILLISECONDS.sleep(POLLING_INTERVAL);
     * } else {
     * break;
     * }
     * }
     * <p>
     * if (null != fileTid && StringUtils.isNotEmpty(fileTid.getFileId())) {
     * // 获取结果以后发送消息
     * // 判断发送的内容类型  type 1文本 2图片 3视频 4音频 5文件 6单卡 7多卡 8位置
     * msgReq.setMessageData(fileTid.getFileId());
     * msgReq.setMessageType(2);
     * // 发送消息
     * robotProcessorApi.javaSendMsg(msgReq);
     * }
     * }
     * }
     */

    private String sendCustomCommand(OrderModel orderModel, String redisKey, Object msgReq, String fileUrlString, int coustomType, String accountType) throws Exception {
        log.info("-------------------------------------自定义指令 sendCustomCommand is start");
        JSONObject paramMap = new JSONObject();
        // 指令名称
        paramMap.put("orderName", orderModel.getOrderName());
        // 说明
        paramMap.put("depiction", orderModel.getDepiction());
        // 指令内容
        paramMap.put("body", orderModel.getBodylist());
        // redisKey
        paramMap.put("redisKey", redisKey);

        HashMap<String, Object> map = new HashMap<>();
        // todo
        map.put("operator", accountType);
        map.put("loginUserId", orderModel.getCreator());
        map.put("pageNo", 1);
        map.put("pageSize", -1);
        map.put("UpMsgReq", msgReq);
        StringBuffer keyDict = new StringBuffer();
        keyDict.append(redisKey).append(",")
                .append(redisKey).append("msgReq").append(",")
                .append(redisKey).append("Execute").append(",")
                .append(redisKey).append("Variable");
        map.put("redisKeyDict", keyDict.toString());

        paramMap.put("scriptcmd", map);

        // 回调方法url
        String jsonString = paramMap.toJSONString();
        // 通过feign调用python服务
        log.info("-------------------------------------自定义指令 callPython: jsonString {}", jsonString);
        stringRedisTemplate.opsForValue().set(redisKey, jsonString, OVER_TIME, TimeUnit.SECONDS);
        JSONObject tempMap = new JSONObject();
        log.info("-------------------------------------自定义指令 callPython: redisKey {}", redisKey);
        tempMap.put("redisKey", redisKey);
        String s = tempMap.toJSONString();
//        robotCallPythonApi.callPython(s);
        String dockerName = callPython(s);
        return dockerName;
    }

    /**
     * 先通过OperateHost Java服务启动Python的docker容器
     * 再通过动态Feign调用Python服务
     *
     * @param s 请求报文
     */
    private synchronized String callPython(String s) {
        //1、启动docker容器
        DockerInfo dockerInfo = operateHostApi.startDocker();
        if (dockerInfo != null && !Strings.isNullOrEmpty(dockerInfo.getServerName())) {
            boolean isSuccess = false;
            int times = 0;
            while (!isSuccess && times < 6) {
                try {
                    Thread.sleep(1000);
                    //2、调用python服务
                    Object result = dynamicClient.executePostApi(dockerInfo.getServerName(), "/user/defined/directive", s);
                    log.info("-------------------callPython----ing-------------------执行结果:{}", result.toString());
                    isSuccess ="SUCCESS".equals(result);
                    times++;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("python容器启动失败：{}", e.getMessage());
                }
            }
            return dockerInfo.getServerName();
        }
        return null;
    }
}

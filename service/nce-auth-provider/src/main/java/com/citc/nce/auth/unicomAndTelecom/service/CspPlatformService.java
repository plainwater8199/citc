package com.citc.nce.auth.unicomAndTelecom.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.auth.accountmanagement.dao.AccountManagementDao;
import com.citc.nce.auth.accountmanagement.entity.AccountManagementDo;
import com.citc.nce.auth.accountmanagement.service.AccountManagementService;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.configure.CspUnicomAndTelecomConfigure;
import com.citc.nce.auth.constant.csp.common.CSPChatbotStatusEnum;
import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import com.citc.nce.auth.csp.account.entity.AccountManageDo;
import com.citc.nce.auth.csp.account.service.AccountManageService;
import com.citc.nce.auth.csp.chatbot.dao.ChatbotManageChangeDao;
import com.citc.nce.auth.csp.chatbot.dao.ChatbotManageDao;
import com.citc.nce.auth.csp.chatbot.dao.ChatbotManageWhiteListDao;
import com.citc.nce.auth.csp.chatbot.entity.ChatbotManageChangeDo;
import com.citc.nce.auth.csp.chatbot.entity.ChatbotManageDo;
import com.citc.nce.auth.csp.chatbot.entity.ChatbotManageWhiteListDo;
import com.citc.nce.auth.csp.contract.dao.ContractManageChangeDao;
import com.citc.nce.auth.csp.contract.dao.ContractManageDao;
import com.citc.nce.auth.csp.contract.entity.ContractManageChangeDo;
import com.citc.nce.auth.csp.contract.entity.ContractManageDo;
import com.citc.nce.auth.csp.contract.service.impl.ContractManageServiceImpl;
import com.citc.nce.auth.csp.csp.service.CspService;
import com.citc.nce.auth.unicomAndTelecom.dto.PlatformResult;
import com.citc.nce.auth.unicomAndTelecom.exp.UnicomAndTelecomExp;
import com.citc.nce.auth.unicomAndTelecom.req.*;
import com.citc.nce.auth.unicomAndTelecom.resp.ChatbotUploadResp;
import com.citc.nce.auth.utils.HttpsUtil;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.redis.config.RedisService;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.filecenter.vo.UploadResp;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class CspPlatformService {

    @Resource
    private RedisService redisService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    AccountManageService accountManageService;

    @Resource
    ContractManageDao contractManageDao;

    @Resource
    ContractManageChangeDao contractManageChangeDao;

    @Resource
    ChatbotManageDao chatbotManageDao;

    @Resource
    ChatbotManageChangeDao chatbotManageChangeDao;

    @Resource
    ChatbotManageWhiteListDao chatbotManageWhiteListDao;

    @Resource
    FileApi fileApi;

    @Autowired
    private CspUnicomAndTelecomConfigure cspUnicomAndTelecomConfigure;

    @Autowired
    private AccountManagementService accountManagementService;
    @Autowired
    private CspService cspService;
    @Autowired
    private AccountManagementDao accountManagementDao;


    /**
     * 新增电信联通客户信息
     *
     * @param req       参数
     * @param accessKey key
     * @param cspId     cspId
     * @return cspEcNo：csp客户识别码
     */
    public String addCspCustomer(CspCustomerReq req, String accessKey, String cspId, Integer isp) {
        String requestBody = JSONUtil.toJsonStr(req);
        String token = getToken(cspId, accessKey, isp);
        PlatformResult platformResult = executeMethod(accessKey, requestBody, cspUnicomAndTelecomConfigure.getAddCspCustomerUrl(),
                token, isp);
        JSONObject jsonObject = JsonUtils.string2Obj(platformResult.getData().toString(), JSONObject.class);
        return jsonObject.getString("cspEcNo");
    }

    /**
     * 变更客户信息
     *
     * @param req       参数
     * @param accessKey key
     * @param cspId     cspId
     */
    public void editCspCustomer(CspCustomerReq req, String accessKey, String cspId, Integer operatorCode) {
        String requestBody = JSONUtil.toJsonStr(req);
        String token = getToken(cspId, accessKey, operatorCode);
        executeMethod(accessKey, requestBody, cspUnicomAndTelecomConfigure.getEditCspCustomerUrl(), token, operatorCode);
    }

    /**
     * 注销客户信息
     *
     * @param accessKey key
     * @param cspId     cspId
     * @param cspEcNo   CSP客户识别码
     */
    public void deleteCspCustomer(String accessKey, String cspId, String cspEcNo, Integer operatorCode) {
        Map<String, String> param = new HashMap<>();
        param.put("cspEcNo", cspEcNo);
        String requestBody = JSONUtil.toJsonStr(param);
        String token = getToken(cspId, accessKey, operatorCode);
        executeMethod(accessKey, requestBody, cspUnicomAndTelecomConfigure.getDeleteCspCustomerUrl(), token, operatorCode);
    }

    /**
     * 新增chatbot接口
     *
     * @param req       chatbot信息
     * @param accessKey accessKey
     * @param cspId     cspId
     * @return Chatbot的唯一识别标识
     */
    public String addChatBot(ChatBotReq req, String accessKey, String cspId, Integer operatorCode) {
        String requestBody = JSONUtil.toJsonStr(req);
        String token = getToken(cspId, accessKey, operatorCode);
        PlatformResult platformResult = executeMethod(accessKey, requestBody, cspUnicomAndTelecomConfigure.getAddChatBotUrl(), token, operatorCode);
        JSONObject jsonObject = JsonUtils.string2Obj(platformResult.getData().toString(), JSONObject.class);
        return jsonObject.getString("accessTagNo");
    }

    /**
     * 变更chatbot信息接口
     *
     * @param req       chatbot信息
     * @param accessKey accessKey
     * @param cspId     cspId
     */
    public void updateChatBot(ChatBotReq req, String accessKey, String cspId, Integer operatorCode) {
        String requestBody = JSONUtil.toJsonStr(req);
        String token = getToken(cspId, accessKey, operatorCode);
        executeMethod(accessKey, requestBody, cspUnicomAndTelecomConfigure.getUpdateChatBotUrl(), token, operatorCode);
    }

    /**
     * 注销chatbot信息接口
     *
     * @param accessTagNo chatbot唯一标识
     * @param accessKey   accessKey
     * @param cspId       cspId
     */
    public void deleteChatBot(String accessTagNo, String accessKey, String cspId, Integer operatorCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("accessTagNo", accessTagNo);
        String requestBody = JSONUtil.toJsonStr(param);
        String token = getToken(cspId, accessKey, operatorCode);
        executeMethod(accessKey, requestBody, cspUnicomAndTelecomConfigure.getDeleteChatBotUrl(), token, operatorCode);
    }

    /**
     * 推出测试状态接口
     *
     * @param accessTagNo chatbot唯一标识
     * @param accessKey   accessKey
     * @param cspId       cspId
     */
    public void quitTestStatus(String accessTagNo, String accessKey, String cspId, String file, Integer operatorCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("accessTagNo", accessTagNo);
        param.put("file", file);
        String requestBody = JSONUtil.toJsonStr(param);
        String token = getToken(cspId, accessKey, operatorCode);
        executeMethod(accessKey, requestBody, cspUnicomAndTelecomConfigure.getQuitTestStatusUrl(), token, operatorCode);
    }

    /**
     * 变更chatbot状态接口
     *
     * @param accessTagNo  chatbot唯一标识
     * @param accessKey    accessKey
     * @param cspId        cspId
     * @param type         type 上下线标志，1:上线，2:下线，3测试
     * @param operatorCode 运营商编码
     */
    public void isOnlineUpdate(String accessTagNo, String accessKey, String cspId, Integer type, Integer operatorCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("accessTagNo", accessTagNo);
        param.put("type", type);
        String requestBody = JSONUtil.toJsonStr(param);
        String token = getToken(cspId, accessKey, operatorCode);
        executeMethod(accessKey, requestBody, cspUnicomAndTelecomConfigure.getIsOnlineUpdateUrl(), token, operatorCode);

    }

    /**
     * 开发者配置接口
     *
     * @param req       开发者信息
     * @param accessKey accessKey
     * @param cspId     cspId
     */
    public JSONObject updateDeveloper(DeveloperReq req, String accessKey, String cspId, Integer operatorCode) {
        String requestBody = JSONUtil.toJsonStr(req);
        String token = getToken(cspId, accessKey, operatorCode);
        PlatformResult platformResult = executeMethod(accessKey, requestBody, cspUnicomAndTelecomConfigure.getUpdateDeveloperUrl(), token, operatorCode);
        JSONObject jsonObject = JsonUtils.string2Obj(platformResult.getData().toString(), JSONObject.class);
        return jsonObject;
    }

    /**
     * 变更chatbot状态接口
     *
     * @param accessTagNo  chatbot唯一标识
     * @param phoneNumbers 白名单列表
     * @param cspId        cspId
     */
    public void whiteListPhone(List<String> phoneNumbers, String accessTagNo, String accessKey, String cspId, Integer operatorCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("phoneNumbers", phoneNumbers);
        param.put("accessTagNo", accessTagNo);
        String requestBody = JSONUtil.toJsonStr(param);
        String token = getToken(cspId, accessKey, operatorCode);
        executeMethod(accessKey, requestBody, cspUnicomAndTelecomConfigure.getWhiteListPhoneUrl(), token, operatorCode);
    }


    /**
     * 上传客户资料
     *
     * @param uploadReq 文件信息
     */
    @SneakyThrows
    public ChatbotUploadResp uploadContractFile(UploadReq uploadReq) {

        byte[] bytes = uploadReq.getFile().getBytes();
        PlatformResult platformResult = uploadFileToIsp(uploadReq, cspUnicomAndTelecomConfigure.getUploadChatBotFileUrl(), uploadReq.getOperatorCode());
        JSONObject jsonObject = JsonUtils.string2Obj(platformResult.getData().toString(), JSONObject.class);

        List<UploadResp> uploadRespList = getUploadResps(bytes, jsonObject, "filePath", uploadReq.getSceneId());
        ChatbotUploadResp res = new ChatbotUploadResp();
        res.setOperatorUrl(jsonObject.getString("filePath"));
        if (CollectionUtils.isNotEmpty(uploadRespList)) {
            res.setLocalUrl(uploadRespList.get(0).getUrlId());
        }
        return res;
    }


    /**
     * 上传chatbot资料接口
     *
     * @param uploadReq
     * @return
     */
    @SneakyThrows
    public ChatbotUploadResp uploadChatBotFile(UploadReq uploadReq) {
        byte[] bytes = uploadReq.getFile().getBytes();

        // 把文件上传到运营商
        PlatformResult platformResult = uploadFileToIsp(uploadReq, cspUnicomAndTelecomConfigure.getUploadChatBotFileUrl(), uploadReq.getOperatorCode());
        JSONObject jsonObject = JsonUtils.string2Obj(platformResult.getData().toString(), JSONObject.class);

        // 把文件上传到本地服务器
        String[] split = jsonObject.getString("filePath").split("\\.");
        com.citc.nce.filecenter.vo.UploadReq upload = new com.citc.nce.filecenter.vo.UploadReq();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        File resultFile = File.createTempFile(IdUtil.fastSimpleUUID(), "." + split[split.length - 1], Files.createTempDirectory("tempFile").toFile());
        resultFile.deleteOnExit();
        FileUtils.copyToFile(byteArrayInputStream, resultFile);
        upload.setFile(getMultipartFile(resultFile));
        upload.setSceneId(uploadReq.getSceneId());
        List<UploadResp> uploadRespList = fileApi.simpleUpload(upload);


        ChatbotUploadResp res = new ChatbotUploadResp();
        res.setOperatorUrl(jsonObject.getString("filePath"));
        if (CollectionUtils.isNotEmpty(uploadRespList)) {
            res.setLocalUrl(uploadRespList.get(0).getUrlId());
        }
        return res;
    }

    public static CommonsMultipartFile getMultipartFile(File file) {
        FileItem item = new DiskFileItemFactory().createItem("file"
                , MediaType.MULTIPART_FORM_DATA_VALUE
                , true
                , file.getName());
        try (InputStream input = new FileInputStream(file);
             OutputStream os = item.getOutputStream()) {
            // 流转移
            IOUtils.copy(input, os);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid file: " + e, e);
        }

        return new CommonsMultipartFile(item);
    }

    private List<UploadResp> getUploadResps(byte[] bytes, JSONObject jsonObject, String keyString, String sceneId) throws IOException {
        // 把文件上传到本地服务器
        String[] split = jsonObject.getString(keyString).split("\\.");
        com.citc.nce.filecenter.vo.UploadReq upload = new com.citc.nce.filecenter.vo.UploadReq();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        File resultFile = File.createTempFile(IdUtil.fastSimpleUUID(), "." + split[split.length - 1], Files.createTempDirectory("tempFile").toFile());
        resultFile.deleteOnExit();
        FileUtils.copyToFile(byteArrayInputStream, resultFile);
        upload.setFile(getMultipartFile(resultFile));
        upload.setSceneId(sceneId);
        return fileApi.simpleUpload(upload);
    }

    @SneakyThrows
    public ResponseEntity<byte[]> getFile(OperatorCodeReq operatorCodeReq) {
        try (CloseableHttpClient client = HttpsUtil.createSSLClientDefault();) {
            String exeUrl = "";
            if (operatorCodeReq.getOperatorCode() == 1) {
                //联通
                exeUrl = cspUnicomAndTelecomConfigure.getBaseUnicomUrl() + cspUnicomAndTelecomConfigure.getGetFileUrl();
            } else {
                //电信
                exeUrl = cspUnicomAndTelecomConfigure.getBaseTelecomUrl() + cspUnicomAndTelecomConfigure.getGetFileUrl();

            }
            URIBuilder uriBuilder = new URIBuilder(exeUrl);
            HttpPost httpPost = new HttpPost(uriBuilder.build());
            AccountManageDo accountManageDo = accountManageService.getCspAccount(operatorCodeReq.getOperatorCode(), SessionContextUtil.getUser().getUserId());
            buildHeader(accountManageDo.getCspPassword(), httpPost);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("fileUrl", operatorCodeReq.getFileUrl());
            String requestBody = JSONUtil.toJsonStr(paramMap);
            String token = getToken(accountManageDo.getCspAccount(), accountManageDo.getCspPassword(), operatorCodeReq.getOperatorCode());
            httpPost.addHeader("authorization", token);
            HttpEntity entity = new StringEntity(requestBody, ContentType.create("application/json", "utf-8"));
            httpPost.setEntity(entity);
            HttpResponse response = client.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            byte[] bytes = EntityUtils.toByteArray(httpEntity);
            return new ResponseEntity<>(bytes, HttpStatus.OK);
        } catch (Exception e) {
            log.error("getFile 失败", e);
            throw new BizException(500, "访问运营商服务失败");
        }
    }

    /**
     * 上传文件到运营商
     *
     * @param uploadReq    文件内容
     * @param url          需要上传位置
     * @param operatorCode 联通(1)或者电信
     * @return 上传结果
     */
    @NotNull
    private PlatformResult uploadFileToIsp(UploadReq uploadReq, String url, Integer operatorCode) throws URISyntaxException, IOException {
        try (CloseableHttpClient client = HttpsUtil.createSSLClientDefault();) {
            String exeUrl = Integer.valueOf(1).equals(operatorCode) ? cspUnicomAndTelecomConfigure.getBaseUnicomUrl() + url : cspUnicomAndTelecomConfigure.getBaseTelecomUrl() + url;
            URIBuilder uriBuilder = new URIBuilder(exeUrl);
            HttpPost httpPost = new HttpPost(uriBuilder.build());
            AccountManageDo accountManageDo = accountManageService.getCspAccount(uploadReq.getOperatorCode(), SessionContextUtil.getUser().getUserId());
            if (ObjectUtil.isEmpty(accountManageDo)) {
                throw new BizException(820501009, "该账号下无对应运营商csp账号信息，请先添加csp信息");
            }
            String token = getToken(accountManageDo.getCspAccount(), accountManageDo.getCspPassword(), uploadReq.getOperatorCode());
            httpPost.addHeader("authorization", token);
            buildHeader(accountManageDo.getCspPassword(), httpPost);
            httpPost.addHeader("uploadType", uploadReq.getUploadType() + "");
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", multipartFileToFile(uploadReq.getFile()));
            builder.setCharset(StandardCharsets.UTF_8);
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            HttpResponse response = client.execute(httpPost);
            return getPlatformResult(response);
        } catch (BizException b) {
            throw b;
        } catch (Exception e) {
            log.error("getFile 失败", e);
            throw new BizException(500, "访问运营商服务失败");
        }
    }


    public File multipartFileToFile(MultipartFile multiFile) {
        // 获取文件名
        String fileName = multiFile.getOriginalFilename();
        Assert.notNull(fileName, "filename can not be null");
        // 获取文件后缀
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        String uuid = IdUtil.fastUUID();
        try {
            File file = File.createTempFile(uuid, prefix);
            multiFile.transferTo(file);
            return file;
        } catch (Exception e) {
            log.error("临时文件生成失败", e);
            throw new BizException(UnicomAndTelecomExp.FILE_ERROR);
        }
    }

    /**
     * 获取token 接口
     *
     * @param cspId     cspId
     * @param accessKey accessKey
     * @return token
     */
    private String getAccessToken(String cspId, String accessKey, Integer operatorCode) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("cspId", cspId);//CSP客户编码（注册CSP时提供）
        paramMap.put("accessKey", accessKey);//CSP客户秘钥（注册CSP时提供）
        String requestBody = JSONUtil.toJsonStr(paramMap);
        PlatformResult platformResult = executeMethod(accessKey, requestBody, cspUnicomAndTelecomConfigure.getAccessTokenUrl(), null, operatorCode);
        if (platformResult.getCode() != 0) {
            throw new BizException(UnicomAndTelecomExp.TOKEN_ERROR);
        }
        JSONObject jsonObject = JsonUtils.string2Obj(platformResult.getData().toString(), JSONObject.class);
        return jsonObject.getString("accessToken");
    }

    @SneakyThrows
    private PlatformResult executeMethod(String accessKey, String requestBody, String url, String token, Integer operatorCode) {
        log.info("请求电信/联通平台请求数据：{}", requestBody);
        try (CloseableHttpClient client = HttpsUtil.createSSLClientDefault();) {
            String executeUrl = Integer.valueOf(1).equals(operatorCode) ? cspUnicomAndTelecomConfigure.getBaseUnicomUrl() + url : cspUnicomAndTelecomConfigure.getBaseTelecomUrl() + url;
            HttpPost httpPost = new HttpPost(executeUrl);
            buildHeader(accessKey, httpPost);
            log.info("token:{}", token);
            if (StringUtils.isNotEmpty(token)) {
                httpPost.addHeader("authorization", token);
            }
            return getReceiveData(client, httpPost, requestBody);
        } catch (BizException b) {
            throw b;
        } catch (Exception e) {
            log.error("getFile 失败", e);
            throw new BizException(500, "访问运营商服务失败");
        }
    }

    private void buildHeader(String accessKey, HttpPost httpPost) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeStamp = sdf.format(now);
        String random = RandomStringUtils.random(8, true, true);
        httpPost.addHeader("timestamp", timeStamp);
        String nonce = timeStamp + random;
        httpPost.addHeader("nonce", timeStamp + random);
        String signatureStr = accessKey + nonce + timeStamp;
        String signature = DigestUtils.md5Hex((signatureStr).getBytes());
        httpPost.addHeader("signature", signature);
    }

    /**
     * 获取联通或者电信 token
     *
     * @param cspId        cspId       //CSP客户编码（注册CSP时提供）
     * @param accessKey    访问秘钥    //CSP客户秘钥（注册CSP时提供）
     * @param operatorCode 联通或者电信（1）
     * @return token
     */
    private String getToken(String cspId, String accessKey, Integer operatorCode) {
        log.info("有业务来获取电信/联通token cspId:{}  accessKey:{}", cspId, accessKey);
        String tokenKey = "ISP:accessToken:" + operatorCode + "_" + cspId;
        String accessToken = redisService.getCacheObject(tokenKey);
        if (StringUtils.isNotEmpty(accessToken)) {
            log.info("使用redis——accessToken");
            return accessToken;
        }
        //重新生成token
        RLock lock = redissonClient.getLock("Redisson:" + tokenKey);
        try {
            if (lock.tryLock(10, 20, TimeUnit.SECONDS)) {
                String token = getAccessToken(cspId, accessKey, operatorCode);
                redisService.setCacheObject(tokenKey, token, 7000L, TimeUnit.SECONDS);
                log.info("登录获取新的——accessToken");
                return token;
            }
        } catch (InterruptedException e) {
            log.warn("Interrupted!", e);
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
        throw new BizException("无法登陆运营商（ISP）服务器");
    }

    private PlatformResult getReceiveData(CloseableHttpClient client, HttpPost httpPost, String requestBody) throws IOException {
        HttpEntity entity = new StringEntity(requestBody, ContentType.create("application/json", "utf-8"));
        httpPost.setEntity(entity);
        HttpResponse response = client.execute(httpPost);
        return getPlatformResult(response);
    }

    /**
     * 从resp中获取平台响应结果
     *
     * @param response 响应
     * @return PlatformResult
     */

    @NotNull
    private PlatformResult getPlatformResult(HttpResponse response) throws IOException {
        HttpEntity responseEntity = response.getEntity();
        String string = EntityUtils.toString(responseEntity);
        log.info("receive body is {}", string);
        PlatformResult platformResult = JSONObject.parseObject(string, PlatformResult.class);
        if (0 != platformResult.getCode()) {
            throw new BizException(820501009, ContractManageServiceImpl.ISP_ERROR + platformResult.getMessage());
        }
        return platformResult;
    }


    /**
     * 获取联通电信服务代码
     *
     * @param operatorCode 运营商类型
     * @return
     */
    public CodeResult getUnicomAndTelecomServiceCode(Integer operatorCode) {
        CodeResult codeResult = new CodeResult();
        AccountManageDo accountManageDo = accountManageService.getCspAccount(operatorCode, SessionContextUtil.getUser().getUserId());
        if (ObjectUtil.isEmpty(accountManageDo)) {
            return codeResult;
        }
        codeResult.setServiceCode(accountManageDo.getAgentCustomerNum());
        return codeResult;
    }

    /**
     * 注销合同
     *
     * @param req 合同id
     */
    @Transactional(rollbackFor = Exception.class)
    public void withdrawContract(OperatorCodeReq req) {
        ContractManageDo contractManageDo = contractManageDao.selectById(req.getId());
        String customerId = contractManageDo.getCustomerId();
        String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
        if (!contractManageDo.getCustomerId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }

        ChatbotManageDo chatbotManageDo = chatbotManageDao.selectOne("customer_id", customerId, "operator_code", contractManageDo.getOperatorCode());
        if (ObjectUtil.isNotEmpty(chatbotManageDo) && chatbotManageDo.getChatbotStatus() != 69) {
            throw new BizException(820103020, "当前合同下Chatbot尚未注销，请先注销Chatbot，再注销合同");
        }
        //注销待审核
        contractManageDo.setContractStatus(62);
        contractManageDao.updateById(contractManageDo);
        String cspEcNo = contractManageDo.getCustomerNum();
        AccountManageDo cspInfo = accountManageService.getCspAccount(req.getOperatorCode(), cspId);
        deleteCspCustomer(cspInfo.getCspPassword(), cspInfo.getCspAccount(), cspEcNo, req.getOperatorCode());
    }

    /**
     * 撤销注销
     *
     * @param req 合同id
     */
    @Transactional(rollbackFor = Exception.class)
    public void revokeCancellation(OperatorCodeReq req) {
        ContractManageDo contractManageDo = contractManageDao.selectById(req.getId());
        //通过当前用户id查查真正的cspId  （由于老数据cspId不是按照规则生成的）
        String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
        if (!contractManageDo.getCustomerId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }
        //注销待审核
        contractManageDo.setContractStatus(66);
        contractManageDao.updateById(contractManageDo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void revokeChangeContract(OperatorCodeReq req) {
        //将主表可用状态变为正常
        ContractManageDo contractManageDo = contractManageDao.selectById(req.getId());
        //通过当前用户id查查真正的cspId  （由于老数据cspId不是按照规则生成的）
        String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
        if (!contractManageDo.getCustomerId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }

        contractManageDo.setContractStatus(contractManageDo.getActualState());
        contractManageDao.updateById(contractManageDo);
        //将附表数据删除
        ContractManageChangeDo contractManageChangeDo = contractManageChangeDao.selectOne("contract_id", req.getId());
        contractManageChangeDo.setDeleted(1);
        contractManageChangeDao.updateById(contractManageChangeDo);
    }

    /**
     * 注销机器人
     */
    @Transactional(rollbackFor = Exception.class)
    public void withdrawChatBot(OperatorCodeReq req) {
        String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
        AccountManagementDo account = accountManagementDao.selectOne(
                Wrappers.<AccountManagementDo>lambdaQuery()
                        .eq(AccountManagementDo::getId, req.getId())
                        .eq(AccountManagementDo::getCspId, cspId)
        );
        if (Objects.isNull(account))
            throw new BizException("机器人账号不存在");
        if (!Objects.equals(account.getChatbotStatus(), CSPChatbotStatusEnum.STATUS_68_ONLINE.getCode()))
            throw new BizException("非上线状态不能注销机器人");
        ChatbotManageDo chatbotManageDo = chatbotManageDao.selectOne(
                new LambdaQueryWrapper<ChatbotManageDo>()
                        .eq(ChatbotManageDo::getChatbotAccountId, account.getChatbotAccountId())
        );
        //注销待审核
        chatbotManageDo.setActualState(chatbotManageDo.getChatbotStatus());
        chatbotManageDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_62_LOGOFF_WAIT_AUDIT.getCode());
        chatbotManageDao.updateById(chatbotManageDo);
        account.setChatbotStatus(CSPChatbotStatusEnum.STATUS_62_LOGOFF_WAIT_AUDIT.getCode());
        accountManagementDao.updateById(account);
        //注销chatbot
        String accessTagNo = chatbotManageDo.getAccessTagNo();

        AccountManageDo cspInfo = accountManageService.getCspAccount(req.getOperatorCode(), cspId);
        deleteChatBot(accessTagNo, cspInfo.getCspPassword(), cspInfo.getCspAccount(), req.getOperatorCode());
    }

    /**
     * 撤销注销机器人
     */
    @Transactional(rollbackFor = Exception.class)
    public void revokeCancellationChatBot(OperatorCodeReq req) {
        //将主表可用状态变为正常
        String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
        AccountManagementDo account = accountManagementDao.selectOne(
                Wrappers.<AccountManagementDo>lambdaQuery()
                        .eq(AccountManagementDo::getId, req.getId())
                        .eq(AccountManagementDo::getCspId, cspId)
        );
        if (Objects.isNull(account))
            throw new BizException("机器人账号不存在");
        if (!Objects.equals(account.getChatbotStatus(), CSPChatbotStatusEnum.STATUS_66_LOGOFF_AUDIT_NOT_PASS.getCode()))
            throw new BizException("非注销审核不通过状态不能撤销注销机器人");
        ChatbotManageDo chatbotManageDo = chatbotManageDao.selectOne(
                new LambdaQueryWrapper<ChatbotManageDo>()
                        .eq(ChatbotManageDo::getChatbotAccountId, account.getChatbotAccountId())
        );
        //注销待审核
        chatbotManageDo.setChatbotStatus(chatbotManageDo.getActualState());
        chatbotManageDo.setActualState(null);
        chatbotManageDao.updateById(chatbotManageDo);
        account.setChatbotStatus(chatbotManageDo.getChatbotStatus());
        accountManagementDao.updateById(account);
    }

    /**
     * 非移动机器人撤销变更
     *
     * @param req
     */
    public void revokeChangeChatBot(OperatorCodeReq req) {
        //将主表可用状态变为正常
        AccountManagementDo accountManagementDo = accountManagementDao.selectById(req.getId());
        if (accountManagementDo == null)
            throw new BizException(500, "机器人不存在");
        ChatbotManageDo chatbotManageDo = chatbotManageDao.selectOne("chatbot_account_id", accountManagementDo.getChatbotAccountId());
        chatbotManageDo.setChatbotStatus(chatbotManageDo.getActualState());
        chatbotManageDo.setActualState(null);
        chatbotManageDao.updateById(chatbotManageDo);

        accountManagementDo.setChatbotStatus(chatbotManageDo.getChatbotStatus());
        accountManagementDao.updateById(accountManagementDo);
        //删除变更
        chatbotManageChangeDao.delete(
                Wrappers.<ChatbotManageChangeDo>lambdaQuery()
                        .eq(ChatbotManageChangeDo::getChatbotAccountId, chatbotManageDo.getChatbotAccountId())
        );
    }

    /**
     * 申请上线
     *
     * @param req 信息
     */
    @Transactional
    public void applyOnlineChatBot(OperatorCodeReq req) {
        //将主表可用状态变为正常
        AccountManagementDo account = accountManagementDao.selectById(req.getId());
        if (Objects.isNull(account)) {
            throw new BizException("机器人账号不存在");
        }
        ChatbotManageDo chatbotManageDo = chatbotManageDao.selectOne(new LambdaQueryWrapper<ChatbotManageDo>()
                .eq(ChatbotManageDo::getChatbotAccountId, account.getChatbotAccountId())
                .eq(ChatbotManageDo::getDeleted, 0));
        if (Objects.isNull(chatbotManageDo)) {
            throw new BizException("机器人不存在");
        }
        //测试转上线待审核
        chatbotManageDo.setChatbotStatus(63);
        chatbotManageDo.setActualState(null);
        account.setChatbotStatus(63);
        chatbotManageDao.updateById(chatbotManageDo);
        accountManagementDao.updateById(account);
        //调用运营商
        String accessTagNo = chatbotManageDo.getAccessTagNo();
        String file = chatbotManageDo.getChatbotFileUrl();
        String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
        AccountManageDo cspInfo = accountManageService.getCspAccount(req.getOperatorCode(), cspId);
        quitTestStatus(accessTagNo, cspInfo.getCspPassword(), cspInfo.getCspAccount(), req.getFileUrl(), req.getOperatorCode());
    }

    /**
     * 撤销申请
     *
     * @param req 信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void revokeOnlineChatBot(OperatorCodeReq req) {
        AccountManagementDo accountManagementDo = accountManagementDao.selectOne(AccountManagementDo::getChatbotAccountId, req.getChatbotAccountId());
        if (accountManagementDo == null)
            throw new BizException("机器人不存在");
        if (Objects.equals(accountManagementDo.getAccountTypeCode(), CSPOperatorCodeEnum.CMCC.getCode()))
            throw new BizException("该运营商机器人不允许此操作");
        if (!Objects.equals(accountManagementDo.getChatbotStatus(), CSPChatbotStatusEnum.STATUS_67_TEST2ONLINE_AUDIT_NOT_PASS.getCode()))
            throw new BizException("操作非法，机器人状态错误");
        accountManagementDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_70_TEST.getCode());
        accountManagementDao.updateById(accountManagementDo);
        ChatbotManageDo chatbotManageDo = chatbotManageDao.selectOne(ChatbotManageDo::getChatbotAccountId, req.getChatbotAccountId());
        chatbotManageDo.setChatbotStatus(CSPChatbotStatusEnum.STATUS_70_TEST.getCode());
        chatbotManageDo.setActualState(null);
        chatbotManageDao.updateById(chatbotManageDo);
        accountManagementDao.update(
                new AccountManagementDo(),
                Wrappers.<AccountManagementDo>lambdaUpdate()
                        .eq(AccountManagementDo::getChatbotAccountId, chatbotManageDo.getChatbotAccountId())
                        .set(AccountManagementDo::getChatbotStatus, 70)
        );
    }

    public AccessInformation accessInformation(OperatorCodeReq req) {
        AccessInformation accessInformation = new AccessInformation();
        AccountManagementResp accountManagement = accountManagementService.getAccountManagementById(req.getChatbotId());
        if (null != accountManagement) {
            //通过当前用户id查查真正的cspId  （由于老数据cspId不是按照规则生成的）
            String cspId = cspService.obtainCspId(SessionContextUtil.getUser().getUserId());
            if (!accountManagement.getCustomerId().substring(0, 10).equals(cspId)) {
                throw new BizException("你操作的客户不属于你");
            }
            accessInformation.setAppID(accountManagement.getAppId());
            accessInformation.setSecretKey(accountManagement.getAppKey());
            accessInformation.setToken(accountManagement.getToken());
        }
        return accessInformation;
    }


    public void testChatBot(OperatorCodeReq req) {
        LambdaQueryWrapperX<ChatbotManageWhiteListDo> wrapperX = new LambdaQueryWrapperX<>();
        wrapperX.eq(ChatbotManageWhiteListDo::getChatbotId, req.getChatbotId())
                .eq(ChatbotManageWhiteListDo::getChatbotAccountId, req.getChatbotAccountId())

                .eq(ChatbotManageWhiteListDo::getDeleted, 0);
        ChatbotManageWhiteListDo chatbotManageWhiteListDo = chatbotManageWhiteListDao.selectOne(wrapperX);
        if (ObjectUtil.isNotEmpty(chatbotManageWhiteListDo)) {
            chatbotManageWhiteListDo.setWhiteList(req.getWhiteList());
            chatbotManageWhiteListDao.updateById(chatbotManageWhiteListDo);
        }
    }
}

package com.citc.nce.robot.req;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

/**
 * 文件名:MessageStatus
 * 创建者:zhujinyu
 * 创建时间:2024/3/1 15:11
 * 描述:
 */
@Data
public class FontdoMessageStatusReq {
    private String msgId;
    private String number;
    //发送任务ID，群发消息才有任务ID
    private String taskId;
    //消息类型，TEXT,MULTIMEDIA,SMS,MMS
    private String type;
    //PENDING 已提交,不会回调
    //SENT 已发送,不会回调
    //DELIVERED 已送达
    //DISPLAYED 已读
    //FALLBACK_SMS 回落短信
    //FALLBACK_MMS 回落视信
    //FAILED 失败
    private String status;
    private String error;

    //这其实是chatbotAccount
    private String appId;

    public String translateErrorToFailReason() {
        return errorDict.getOrDefault(error, error);
    }

    private static final Map<String, String> errorDict;

    @AllArgsConstructor
    private static class FontdoError {
        private String type;
        private String code;
        private String msg;
        private String description;
        private String cause;
        private String extra;
    }

    static {
        List<FontdoError> fontdoErrors = Arrays.asList(
                new FontdoError("接入层响应错误码", "10001", "Content-Type is null or illegal", "Content-Type信息为空或非法", "chatbot原因", "检查消息请求头中content-type字段是否携带正确"),
                new FontdoError("接入层响应错误码", "10002", "Authorization is null or illegal", "接入层鉴权失败", "chatbot原因", "1、检查Authorization 是否按照Basic BASE64(cspid:SHA256(cspToken+Date))生成，检查下cspid、cspToken和chatbotURI（即chatbotId+域名后缀）是否一致，Date的值是否为北京时间-8后的差值在5分钟内。 2、请检查cspToken是否正确，接入层接口的cspToken是直接用明文的cspToken，即csp通过运营平台接口或运营平台界面填入时的cspToken明文，如果运营平台展示或接口返回的是base64加密后的，则需要csp解密后使用； 3、请确认chatbot大区与调用接入层地址大区是否一致； 4、可以用联调工具包验证下，或者具体看下接口规范文档和联调工具里的header、body以及Pre-request Script是如何生成鉴权参数的。"),
                new FontdoError("接入层响应错误码", "10003", "Body is null or illegal", "Body内容为空或非法", "chatbot原因", "检查消息体内容是否为空或非法"),
                new FontdoError("接入层响应错误码", "10004", "Bad Request", "需要GET请求", "chatbot原因", "请求方法错误，需要GET请求"),
                new FontdoError("接入层响应错误码", "10005", "Bad Request", "需要POST请求", "chatbot原因", "请求方法错误，需要POST请求"),
                new FontdoError("接入层响应错误码", "10006", "Bad Request", "需要PUT请求", "chatbot原因", "请求方法错误，需要PUT请求"),
                new FontdoError("接入层响应错误码", "10007", "Bad Request", "需要DELETE请求", "chatbot原因", "请求方法错误，需要DELETE请求"),
                new FontdoError("接入层响应错误码", "10008", "Not Found", "未知的URL", "chatbot原因", "请求的URL不存在，检查请求地址是否正确"),
                new FontdoError("接入层响应错误码", "10009", "Ip Whitelist authentication failed", "不在IP白名单内", "chatbot原因", "请求的来源IP不在CSP的ip白名单内，需要联系CSP侧排查"),
                new FontdoError("接入层响应错误码", "10010", "Bad Request", "重复提交", "chatbot原因", "（自用型CSP）向接入层申请Token时，重复相同的两次请求会返回此报错，请勿重复提交申请"),
                new FontdoError("接入层响应错误码", "31002", "chatbotURI is illegal", "不合法的chatbotURI", "chatbot原因", "chatbotURI不合法，检查请求地址中chatbotURI是否填写正确"),
                new FontdoError("接入层响应错误码", "31003", "APPID is null or illegal", "不合法的APPID", "chatbot原因", "appid不合法，检查请求鉴权信息中的appid是否携带正确"),
                new FontdoError("接入层响应错误码", "31007", "Address is null", "手机号列表不能为空", "chatbot原因", "请求消息的被叫号码列表不能为空"),
                new FontdoError("接入层响应错误码", "31008", "Address is to long", "手机号数量超限（>100），应≤100", "chatbot原因", "请求消息的被叫号码列表不能多于100个"),
                new FontdoError("接入层响应错误码", "31009", "Date is overdue", "Date超期", "chatbot原因", "请求的鉴权信息中date时间超期，请更新到最新时间"),
                new FontdoError("接入层响应错误码", "32000", "senderAddress is null or illegal", "发送方地址为空或非法", "chatbot原因", "检查发送方地址senderAddress 字段填写是否正确"),
                new FontdoError("接入层响应错误码", "32001", "destinationAddress is null or illegal", "接收方地址为空或非法", "chatbot原因", "检查接收地址destinationAddress 字段填写是否正确"),
                new FontdoError("接入层响应错误码", "32003", "bodyText is null or illegal", "bodyText为空或非法", "chatbot原因", "检查消息体bodytext字段是否填写正确"),
                new FontdoError("接入层响应错误码", "32004", "msg decode fail", "消息体解码失败", "chatbot原因", "检查下发消息体是否符合xml格式规范"),
                new FontdoError("接入层响应错误码", "35006", "fileURL is null or illegal", "fileURL为空或不合法", "chatbot原因", "检查文件接口的fielURL是否已填写和填写正确"),
                new FontdoError("接入层响应错误码", "40000", "inReplyToContributionID is null or illegal", "交互消息下发未携带inReplyToContributionID或inReplyToContributionID不合规范", "chatbot原因", "检查交互消息下发时是否未携带inReplyToContributionID或inReplyToContributionID不合规范"),
                new FontdoError("接入层响应错误码", "40001", "inReplyToContributionID must be null", "群发消息下发inReplyToContributionID必须为空", "chatbot原因", "群发消息接口不能携带inReplyToContributionID，如有携带请去掉重试"),
                new FontdoError("接入层响应错误码", "40004", "Unauthorized", "接入层鉴权失败", "chatbot原因", "1、请csp检查chatbot状态是否为暂停状态或者已下线状态。。 注：chatbot状态变成暂停的原因，是因为直签客户或者代理商欠费了。续费成功后chatbot状态会更新为“下线”，客户点击chatbot上架即可重新上线。欠费暂停是BOSS发起的，需要集客大厅补交欠费，就可以恢复，通过集客大厅看下业务订购的状态，应该可以看到欠费情况。 2、请csp检查请求的接口地址是否正确，比如用正式环境chabtot请求接入层测试环境https://cmic-csp-cgw.cmicmaap.com时，就会报这个错误。 3、请csp检查请求接口地址路径后面的chatbotURI与消息体中senderAddress字段的chatbotURI是否一致。"),
                new FontdoError("接入层响应错误码", "40005", "Over limitation", "下发超流控（服务代码级别）", "chatbot原因", "检查所属服务代码是否已达下发量限制"),
                new FontdoError("接入层响应错误码", "40006", "Unauthorized", "接入层鉴权失败", "chatbot原因", "1、检查Authorization 是否按照Basic BASE64(cspid:SHA256(cspToken+Date))生成，检查下cspid、cspToken和chatbotURI（即chatbotId+域名后缀）是否一致，Date的值是否为北京时间-8后的差值在5分钟内。 2、请检查cspToken是否正确，接入层接口的cspToken是直接用明文的cspToken，即csp通过运营平台接口或运营平台界面填入时的cspToken明文，如果运营平台展示或接口返回的是base64加密后的，则需要csp解密后使用； 3、请确认chatbot大区与调用接入层地址大区是否一致； 4、可以用联调工具包验证下，或者具体看下接口规范文档和联调工具里的header、body以及Pre-request Script是如何生成鉴权参数的。"),
                new FontdoError("接入层响应错误码", "40007", "User Black list in platform", "下发号码被加入黑名单", "用户原因", "下发号码被加入黑名单，可联系客服排查"),
                new FontdoError("接入层响应错误码", "52003", "Server authentication error", "服务器认证错误", "平台原因", "接入层服务内部异常，需要联系客服具体排查原因"),
                new FontdoError("接入层响应错误码", "60001", "Server address error", "请求地址有误", "chatbot原因", "检查下发请求地址是否正确"),
                new FontdoError("DeliveryImpossible", "POL0001", "An error was been found in the MAAP. Error desc:Date is overdue. Error code is 9", "消息鉴权date字段过期", "chatbot原因", "检查下发消息鉴权信息中date字段时间"),
                new FontdoError("DeliveryImpossible", "POL0001", "An error was been found in the MAAP. Error desc:Outbound msg userId or senderAddress error. Error code is 43.", "消息senderAddress填写错误", "chatbot原因", "消息senderAddress填写错误，检查是否chatbot域名填写错误"),
                new FontdoError("DeliveryImpossible", "POL0001", "An error was been found in the MAAP. Error desc:User Blacklist authentication failed. Error code is 56.", "黑名单鉴权失败、chatbot被添加黑名单、用户号码被添加黑名单、调试状态未加白名单", "用户原因/chatbot原因", "检查下发号码是否在白名单中或者被添加进黑名单，可联系客服排查"),
                new FontdoError("DeliveryImpossible", "POL0001", "Business Authentication Failure", "业务鉴权失败（通常是鉴权参数配置问题）", "chatbot原因/大网原因", "联系客服具体排查"),
                new FontdoError("DeliveryImpossible", "POL0001", "Chatbot authentication failed", "chatbot鉴权失败", "chatbot原因/大网原因", "1、chatbot鉴权失败，检查chatbot状态是否为暂停 2、检查头域时间是否跟请求时间一致 3、检查content-length是否存在过大情况，与请求内容不相符"),
                new FontdoError("DeliveryImpossible", "POL0001", "Chatbot not in the white list", "下发号码不在调试白名单", "chatbot原因", "添加调试白名单后再尝试下发"),
                new FontdoError("DeliveryImpossible", "POL0001", "Chatbot not in the user whitelist", "下发号码不在调试白名单", "chatbot原因", "添加调试白名单后再尝试下发"),
                new FontdoError("DeliveryImpossible", "POL0001", "ChatbotDate check failed", "鉴权信息时间错误", "chatbot原因/大网原因", "联系客服具体排查"),
                new FontdoError("DeliveryImpossible", "POL0001", "limit feedback", "Chatbot配置禁止回落", "平台原因", "联系客服打开Chatbot允许回落视信/短信开关"),
                new FontdoError("DeliveryImpossible", "POL0001", "Robot status authentication failed", "Chatbot状态异常（已下线或暂停）", "chatbot原因", "检查Chatbot状态是否暂停或已下线"),
                new FontdoError("DeliveryImpossible", "POL0001", "Robot status authentication failed mandatory fields", "chatbot状态验证失败", "chatbot原因", "1、检查chatbotURI的域名后缀是否正确 2、核查chatbot是否在暂停/已下线/黑名单状态"),
                new FontdoError("DeliveryImpossible", "POL0001", "User Blacklist authentication failed(System-level)", "被叫用户在全局黑名单", "用户原因", "联系客服解除号码黑名单"),
                new FontdoError("DeliveryImpossible", "POL0001", "User Blacklist authentication failed. Error code is %1", "下发号码不在调试白名单或被加入黑名单", "用户原因/chatbot原因", "检查下发号码是否在白名单中或者被添加进黑名单，可联系客服排查"),
                new FontdoError("DeliveryImpossible", "POL0009", "An error was been found in the MAAP. Error desc:Over flow by day. Error code is 71.", "下发超流控（每日）", "chatbot原因", "反馈客服排查日发送量是否被限制，若是需要联系客户经理处理"),
                new FontdoError("DeliveryImpossible", "POL0009", "An error was been found in the MAAP. Error desc:Over flow by second. Error code is 70.", "下发超流控（每秒）", "chatbot原因", "调减每秒下发的号码数"),
                new FontdoError("DeliveryImpossible", "POL0009", "Chatbot status control fault", "chatbot状态验证失败", "chatbot原因", "核查chatbot是否在暂停/已下线/黑名单状态"),
                new FontdoError("DeliveryImpossible", "POL0009", "Over flow by month", "下发超流控（每月）", "chatbot原因", "反馈客服排查月发送量是否被限制，若是需要联系客户经理处理"),
                new FontdoError("DeliveryImpossible", "POL0009", "Over flow by second", "下发超流控（每秒）", "chatbot原因", "调减每秒下发的号码数"),
                new FontdoError("DeliveryImpossible", "POL0010", "Retries exceeded", "MAAP重试超过次数", "平台原因", "MAAP内部错误，联系客服具体排查"),
                new FontdoError("DeliveryImpossible", "POL0010", "Wait report timeout", "等待终端回执超时（下发缓存超过72小时）MaaP生成", "用户原因", "号码三天不在线，建议回落5G视信/短信发送或更换发送号码"),
                new FontdoError("DeliveryImpossible", "POL0011", "Chatbot down message check error", "Chatbot权限配置不允许下行", "平台原因", "联系客服打开Chatbot允许下发开关"),
                new FontdoError("DeliveryImpossible", "POL0011", "invalid content-type", "contentType携带错误", "chatbot原因", "检查下发消息体content-type字段是否符合规范"),
                new FontdoError("DeliveryImpossible", "POL0011", "The content-type for common file,or fallbackContentType is common file, the file-type or the file-size is empty", "文件消息未携带file-type或者file-size", "chatbot原因", "检查下发文件消息时是否缺少file-type或者file-size字段"),
                new FontdoError("DeliveryImpossible", "POL0011", "The content-type for suspension menu, bodyText message content type is invalid", "携带悬浮菜单时消息体格式错误", "chatbot原因", "检查下发消息体content-type字段是否符合规范"),
                new FontdoError("DeliveryImpossible", "POL2000", "After send message, don't recv reponse message or message is illegal.", "MaaP发送失败（一般都是回车换行格式非\r\n问题，需要具体排查）", "chatbot原因/大网原因", "检查下发消息体的换行符是否为/r/n"),
                new FontdoError("DeliveryImpossible", "POL2000", "Other errors", "MAAP返回其他错误", "大网原因", "1、排查同一个msgid同一个号码是否存在收到多个回执的情况，计费规则以第1条回执情况为准，终端是否成功收到，以是否有成功回执为准 2、MAAP内部错误，联系客服具体排查"),
                new FontdoError("DeliveryImpossible", "POL2000", "Status code is null, abnormal system timeout is the probable reason.", "MaaP发送失败（一般都是回车换行格式非\r\n问题，需要具体排查）", "chatbot原因/大网原因", "检查下发消息体的换行符是否为/r/n,联系客服具体排查"),
                new FontdoError("DeliveryImpossible", "SVC0002", "An error was been found in the MAAP. Error desc:file-info xml error in prolog. Error code is 126.", "下发报文xml格式错误", "chatbot原因", "检查下发消息体是否符合xml格式规范"),
                new FontdoError("DeliveryImpossible", "SVC0002", "An error was been found in the MAAP. Error desc:Message format address error by bill. Error code is 44.", "被叫号码格式错误（address字段）", "chatbot原因", "检查被叫号码字段格式是否正确"),
                new FontdoError("DeliveryImpossible", "SVC0002", "An error was been found in the MAAP. Error desc:Message format destinationAddress error. Error code is 35.", "被叫号码格式错误（destinationAddress 字段）", "chatbot原因", "检查被叫号码字段格式是否正确"),
                new FontdoError("DeliveryImpossible", "SVC0002", "An error was been found in the MAAP. Error desc:mmsContentLength field is error. Error code is 163.", "消息下发mmsContentLength字段填写错误", "chatbot原因", "检查消息下发mmsContentLength字段填写是否正确，是否超过2M"),
                new FontdoError("DeliveryImpossible", "SVC0002", "AO msg service capability invalid", "1、华为号码，属于用户原因，被叫没有开通5G消息，同时A2P消息没有带转短内容 （可以间接的理解成A2P消息的回落参数异常），也没有回落内容，只有UP2.4的内容，5GMC侧报错。 2、中兴，属于chatbot原因，消息里面没有带chatbotsa字段", "用户原因/chatbot原因", "1、华为号码，添加回落，检查终端状态是否在线 2、中兴号码，检查消息体是否携带chatbotsa字段"),
                new FontdoError("DeliveryImpossible", "SVC0002", "Body \"destinationAddress\" format error!", "被叫号码格式错误", "chatbot原因", "检查被叫号码字段格式是否正确"),
                new FontdoError("DeliveryImpossible", "SVC0002", "Duplicate addresses exist in the destination address list of multicast messages", "群发消息被叫号码列表中含有重复号码", "chatbot原因", "群发消息被叫号码列表中含有重复号码，需去重后再下发"),
                new FontdoError("DeliveryImpossible", "SVC0002", "dest user offline", "5G消息终端不在线", "用户原因", "检查终端的5G消息是否在线，终端重新打开开关后再尝试下发"),
                new FontdoError("DeliveryImpossible", "SVC0002", "Field length error", "字段长度错误", "chatbot原因", "检查下发消息体字段是否有超出长度限制"),
                new FontdoError("DeliveryImpossible", "SVC0002", "file info error", "文件信息错误", "chatbot原因", "上传文件格式不支持，或者上传文件声明格式与实际格式不符"),
                new FontdoError("DeliveryImpossible", "SVC0002", "Lack of destination number or address", "被叫号码格式错误", "chatbot原因", "检查被叫号码字段格式是否正确"),
                new FontdoError("DeliveryImpossible", "SVC0002", "Lack of mandatory fields or blank mandatory fields", "消息缺少必填字段或必填字段为空", "chatbot原因", "检查消息体是否带上所有必填字段； 号码格式填写为sip:+86XXXXXXXXXXX也有可能报这个错误"),
                new FontdoError("DeliveryImpossible", "SVC0002", "An error was been found in the MAAP. Error desc:Message Body Format Error. Error code is 20.", "消息格式编码错误", "chatbot原因", "检查消息体是否符合XML格式规范"),
                new FontdoError("DeliveryImpossible", "SVC0002", "Message Body Format Error", "消息格式编码错误", "chatbot原因", "检查消息体是否符合XML格式规范"),
                new FontdoError("DeliveryImpossible", "SVC0002", "Request message error", "消息体有问题", "chatbot原因", "检查下发消息体格式是否符合规范"),
                new FontdoError("DeliveryImpossible", "SVC0002", "Request message error, UE address format error", "被叫号码格式错误", "chatbot原因", "检查被叫号码字段格式是否正确"),
                new FontdoError("DeliveryImpossible", "SVC0002", "The corresponding bussiness type could not be found . Error code is 68", "消息业务类型不合法", "大网原因", "消息业务类型不正确，反馈客服排查"),
                new FontdoError("DeliveryImpossible", "SVC0004", "An error was been found in the MAAP. Error desc:Destination no route. Error code is 23.", "下发号码没有路由数据，如异网号码，携转号码", "用户原因", "检查下发号码是否为正常非携号转网的移动号码"),
                new FontdoError("DeliveryImpossible", "SVC0004", "Destination no route", "下发号码没有路由数据，如异网号码，携转号码", "用户原因", "检查下发号码是否为正常非携号转网的移动号码"),
                new FontdoError("DeliveryImpossible", "SVC0004", "Destination no route. Error code is %1", "下发号码没有路由数据，如异网号码（群发部分成功响应）", "用户原因", "检查下发号码是否为正常非携号转网的移动号码"),
                new FontdoError("DeliveryImpossible", "SVC0004", "send request to as fail", "maap发送至5gmc失败", "大网原因", "5GMC内部错误，联系客服具体排查"),
                new FontdoError("DeliveryImpossible", "SVC0004", "Fail to get robot by rotbot id", "chatbotid不存在或鉴权不通过", "", "检查chatbotid是否填写正确，Authorization鉴权信息是否正确"),
                new FontdoError("DeliveryImpossible", "SVC1001", "Bad Request", "下发终端失败", "终端原因", "下发终端失败，需反馈客服具体排查"),
                new FontdoError("DeliveryImpossible", "SVC1003", "Not Found", "下发终端失败（中兴）", "终端原因", "检查终端的5G消息是否在线，终端重新打开开关后再尝试下发"),
                new FontdoError("DeliveryImpossible", "SVC1004", "Request Timeout", "下发终端超时失败（终端原因）", "终端原因", "检查终端的5G消息是否在线，终端重新打开开关后再尝试下发"),
                new FontdoError("DeliveryImpossible", "SVC1005", "Busy Here", "下发终端繁忙失败（终端原因）", "终端原因", "下发终端失败，需反馈客服具体排查"),
                new FontdoError("DeliveryImpossible", "SVC1005", "Decline", "下发终端拒绝", "终端原因", "下发终端失败，需反馈客服具体排查"),
                new FontdoError("DeliveryImpossible", "SVC1005", "Not Acceptable Here", "下发终端失败", "终端原因", "检查终端的5G消息是否在线，终端重新打开开关后再尝试下发"),
                new FontdoError("DeliveryImpossible", "SVC1005", "Server Internal Error", "下发终端超时失败（终端原因）或大区内部服务错误原因", "终端原因/大网原因", "检查终端的5G消息是否在线，终端重新打开开关后再尝试下发，联系客服具体排查"),
                new FontdoError("DeliveryImpossible", "SVC1005", "Temporarily Unavailable", "下发终端临时不可达（终端原因）", "终端原因", "检查终端的5G消息是否在线，终端重新打开开关后再尝试下发，下发终端失败，联系客服具体排查"),
                new FontdoError("DeliveryImpossible", "SVC1006", "Unsupported Media Type", "终端解析文件消息体失败", "终端原因/chatbot原因", "检查下发消息体content-type字段是否符合规范"),
                new FontdoError("DeliveryImpossible", "SVC1500", "wait native rptmsg timeout", "下发等待终端回执超时（终端原因）", "终端原因", "5gmc下发给终端响应成功，但5分钟没收到回执，中兴5GMC报的错，确认终端是否支持5G消息UP2.4"),
                new FontdoError("DeliveryImpossible", "SVC1501", "no capability", "非UP2.45G终端", "终端原因", "终端不支持5G消息能力，更换终端或携带回落下发"),
                new FontdoError("DeliveryImpossible", "SVC2003", "An error was been found in the MAAP. Error desc:App id is error. Error code is 3", "接入层与大区之间消息鉴权Authorization中的appid填写错误", "平台原因/大网原因", "接入层与大区之间鉴权错误。具体需联系客服排查"),
                new FontdoError("DeliveryImpossible", "SVC3101", "An error was been found in the MAAP. Error desc:Security control authentication failed. Error code is 65.", "安全管控监权失败", "chatbot原因/用户原因", "消息安全审核不通过，请修改下发内容"),
                new FontdoError("DeliveryImpossible", "SVC3101", "Security control authentication failed", "安全管控鉴权失败", "chatbot原因/用户原因", "消息安全审核不通过，请修改下发内容"),
                new FontdoError("DeliveryImpossible", "SVC4001", "A2P message can not fall back to mms or sms", "终端5G消息不在线，消息允许回落，但未携带回落消息体", "chatbot原因/用户原因", "检查下发消息是否正确携带回落消息体；如果是中兴号码，请反馈客服联系大网排查"),
                new FontdoError("DeliveryImpossible", "SVC4001", "dest user offline", "5G消息终端不在线", "用户原因", "检查终端的5G消息是否在线，终端重新打开开关后再尝试下发"),
                new FontdoError("DeliveryImpossible", "SVC4001", "file transfer fail", "消息网元内部错误", "大网原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC4001", "query report relationship timeout", "等待终端回执超时（下发缓存超过72+1小时）5GMC生成", "用户原因", "号码三天不在线/关机，建议回落5G视信/短信或更换发送号码"),
                new FontdoError("DeliveryImpossible", "SVC4001", "msg expired", "等待终端回执超时（下发缓存超过72+1小时）5GMC生成", "用户原因", "号码三天不在线/关机，建议回落5G视信/短信或更换发送号码"),
                new FontdoError("DeliveryImpossible", "SVC4001", "msg manual del", "5GMC内部错误", "大网原因", "5GMC内部错误，联系客服具体排查"),
                new FontdoError("DeliveryImpossible", "SVC4001", "query user info fail", "用户号码当前是预销户状态或空号", "用户原因", "1、用户号码当前是预销户状态或空号 2、被叫号码未开通5G消息业务，建议回落5G视信/短信发送或更换发送号码"),
                new FontdoError("DeliveryImpossible", "SVC4001", "receiver not local area user in application platform send process", "号码非本大区所属用户", "大网原因", "号码局数据问题，联系客服具体排查"),
                new FontdoError("DeliveryImpossible", "SVC4001", "save message to message queue fail", "消息添加到队列失败", "用户原因", "5g消息大网网元内部错误，反馈客服具体排查，前期排查经验为大区升级操作切换中断服务时返回的错误码"),
                new FontdoError("DeliveryImpossible", "SVC4001", "service error", "下发终端失败（规范未定义的其他原因，需具体排查）", "大网原因", "5GMC下发终端失败，按前期排查经验为报文格式存在问题，建议检查后按需反馈客服具体排查"),
                new FontdoError("DeliveryImpossible", "SVC5201", "service is shutdown", "用户已停机", "用户原因", "用户已停机，更换号码下发"),
                new FontdoError("DeliveryImpossible", "SVC6001", "Deliver fail", "短信网元内部错误", "大网原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "HLR:data lacked", "短信网元内部错误", "大网原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "HLR:resource not available due to congestion", "短信网元内部错误，短信下发返回HLR消息等待队列满错误", "大网原因", "回落短信失败，前期派单排查经验：用户状态异常，如用户关机、用户无短信能力等"),
                new FontdoError("DeliveryImpossible", "SVC6001", "illegal device", "短信网元内部错误", "大网原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "message authentication failed", "回落短信失败，涉及大网网元间内部错误，排查经验一般是某省短信中心拉黑端口号。", "chatbot原因", "回落短信失败，涉及大网网元间内部错误，排查经验一般是某省短信中心拉黑端口号。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "MS error", "短信下发返回MS端错误", "终端原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "operation barred", "短信回执用户停机", "用户原因", "用户已停机，更换号码下发"),
                new FontdoError("DeliveryImpossible", "SVC6001", "peer node unreachable or no usable node", "短信网元内部错误", "大网原因", "短信网元内部错误，联系客服具体排查"),
                new FontdoError("DeliveryImpossible", "SVC6001", "routing failed", "短信网元内部错误", "大网原因", "号码局数据问题，联系客服具体排查"),
                new FontdoError("DeliveryImpossible", "SVC6001", "service barred", "用户停机", "用户原因", "用户已停机，更换号码下发"),
                new FontdoError("DeliveryImpossible", "SVC6001", "service error", "回落短信失败，涉及大网网元间内部错误，排查经验一般是某省短信中心拉黑端口号或拦截消息导致。建议用户修改短信内容测试，若依然失败，则为端口级拉黑。", "大网原因", "回落短信失败，涉及大网网元间内部错误，排查经验一般是某省短信中心拉黑端口号或拦截消息导致。建议用户修改短信内容测试，若依然失败，则为端口级拉黑。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "SMS not supported by MS", "短信网元内部错误", "大网原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-Deliver fail", "短信网元内部错误", "大网原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-failure because the subscriber is busy", "短信网元内部错误", "大网原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-GMSC receives abort message from VMSC", "短信网元内部错误", "大网原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-HLR:data error in route signal", "短信网元内部错误", "大网原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-HLR:no response", "短信网元内部错误", "大网原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-HLR:subscriber absent", "用户不在服务区", "用户原因", "用户不在服务区"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-HLR:the remote address not reachable", "短信网元内部错误", "大网原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-illegal device", "短信网元内部错误", "终端原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-message authentication failed", "回落短信失败，涉及大网网元间内部错误，排查经验一般是某省短信中心拉黑端口号或拦截消息导致。建议用户修改短信内容测试，若依然失败，则为端口级拉黑。", "大网原因", "回落短信失败，涉及大网网元间内部错误，排查经验一般是某省短信中心拉黑端口号或拦截消息导致。建议用户修改短信内容测试，若依然失败，则为端口级拉黑。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-MS error", "消息已转短，但短信下发因为终端问题未成功（目的终端发生某种不可知的故障，如终端损坏等）", "终端原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-MS response is overtime", "短信网元内部错误", "终端原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-MS store is full", "终端存储空间已满", "用户原因", "回落5G视信/短信手机内存满下发失败，请清理手机空间后重新下发"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-operation barred", "短信回执返回用户停机", "用户原因", "用户已停机，更换号码下发"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-Other failed to send by SMS", "短信网元内部错误/异网携号转网", "用户原因", "回落短信失败，涉及大网网元间内部错误，排查经验：短信中心查该用户无短信功能，拨测号码为停机，请发送方核实下发号码准确性。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-peer node unreachable or no usable node", "短信网元内部错误", "大网原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-sc_Congestion", "短信网元内部错误", "大网原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-service barred", "短信中心回用户信息不存在（停机）", "用户原因", "用户已停机，更换号码下发"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-session between GMSC and HLR failed", "短信网元内部错误", "大网原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-session between GMSC and VMSC failed", "短信网元内部错误", "大网原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-SMS release message exception", "短信网元内部错误", "大网原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-teleservice not supported", "停机", "用户原因", "用户已停机，更换号码下发"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-the network doesnot support SMS", "短信网元内部错误", "大网原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-Unknown Subscriber", "短信下发返回未知用户错误（比如空号或用户短信数据异常或终端原因）", "用户原因", "1、检查被叫号码是否空号或欠费停机等情况； 2、检查被叫号码是否能正常接收10086等其他端口下发的端口，若不能，则可能是被叫号码的短信数据异常，可通知被叫拨打10086客户重置业务数据。 3、短信下发终端临时报错，尝试重新下发 4、若排除上述情况，可联系客服排查。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-unknServCent", "短信网元内部错误", "大网原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-Unmarked Subscriber", "短信网元内部错误", "大网原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-VMSC system error received", "该问题一般归类为用户终端原因导致，建议用户重启终端尝试。", "用户原因", "该问题一般归类为用户终端原因导致，建议用户重启终端尝试。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-VMSC:illegal subscriber", "短信网元内部错误", "大网原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-VMSC:inconsistent version", "短信网元内部错误", "大网原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-VMSC:InvalidDestination", "短信网元内部错误", "大网原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-VMSC:no response", "短信网元内部错误，MSC无应答", "大网原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-VMSC:subscriber absent", "短信回执返回用户不在服务区错误。", "用户问题", "用户暂不在服务区，更换号码或过一段时间再重发"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-VMSC:subscriber absent", "用户不在服务区", "用户问题", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "sms report-VMSC:unexpected response received", "短信网元内部错误", "大网原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "teleservice not supported", "无效号码", "用户原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "the network doesnot support SMS", "短信网元内部错误", "大网原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "Unknown Subscriber", "短信下发返回未知用户错误（比如空号或用户短信数据异常或终端原因）", "用户原因", "1、检查被叫号码是否空号或欠费停机等情况； 2、检查被叫号码是否能正常接收10086等其他端口下发的端口，若不能，则可能是被叫号码的短信数据异常，可通知被叫拨打10086客户重置业务数据。 3、短信下发终端临时报错，尝试重新下发 4、若排除上述情况，可联系客服排查。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "VMSC system error received", "短信网元内部错误", "大网原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC6001", "VMSC:unexpected response received", "短信网元内部错误", "大网原因", "回落短信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC7001", "mms deferred report", "用户接收到彩信，但未成功下载，最终超时", "用户原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC7001", "mms push report-Deliver fail", "彩信投递失败", "大网原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC7001", "mms push report-failure because the subscriber is busy", "彩信投递失败-用户忙", "用户原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC7001", "mms push report-GMSC receives abort message from VMSC", "彩信网元错误", "大网原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC7001", "mms push report-HLR:resource not available due to congestion", "彩信内部网元错误", "大网原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC7001", "mms push report-HLR:subscriber absent", "非5G消息用户，终端在转彩后72小时内没响应，消息被丢弃", "用户原因/chatbot原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC7001", "mms push report-message authentication failed", "终端拉取素材认证失败", "大网原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC7001", "mms push report-message routing failed", "彩信回执路由失败", "大网原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC7001", "mms push report-MS error", "彩信Push下发返回MS端错误。", "终端原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC7001", "mms push report-MS store is full", "彩信Push下发返回手机内存满错误。", "终端原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC7001", "mms push report-operation barred", "用户停机", "用户原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC7001", "mms push report-Other failed to send by SMS", "彩信其他错误", "大网原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC7001", "mms push report-peer node unreachable or no usable node", "彩信Push下发返回无可用节点", "大网原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC7001", "mms push report-sc_Congestion", "彩信内部网元错误", "大网原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC7001", "mms push report-service barred", "彩信服务异常", "大网原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC7001", "mms push report-session between GMSC and HLR failed", "彩信内部网元错误", "大网原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC7001", "mms push report-session between GMSC and VMSC failed", "彩信内部网元错误", "大网原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC7001", "mms push report-teleservice not supported", "彩信Push下发返回电信业务不支持错误。", "用户原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC7001", "mms push report-Unknown Subscriber", "彩信下发返回未知用户错误（比如空号或用户短信数据异常或终端原因）", "用户原因", "1、检查被叫号码是否空号或欠费停机等情况； 2、检查被叫号码是否能正常接收10086等其他端口下发的端口，若不能，则可能是被叫号码的短信数据异常，可通知被叫拨打10086客户重置业务数据。 3、彩信下发终端临时报错，尝试重新下发 4、若排除上述情况，可联系客服排查。"),
                new FontdoError("DeliveryImpossible", "SVC7001", "mms push report-Unmarked Subscriber", "未定义用户", "用户原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC7001", "mms push report-VMSC system error received", "彩信网元错误", "大网原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC7001", "mms push report-VMSC:illegal subscriber", "彩信Push下发返回非法用户错误", "用户原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC7001", "mms push report-VMSC:no response", "彩信网元无响应", "大网原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC7001", "mms push report-VMSC:subscriber absent", "彩信Push下发返回用户不在服务区错误。", "用户原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("接入层响应错误码", "31011", "MessageId is illegal", "无效的消息ID", "", "消息ID填写不合法"),
                new FontdoError("接入层响应错误码", "31012", "MessageId is illegal", "消息ID与chatbot不匹配", "", ""),
                new FontdoError("DeliveryImpossible", "SVC7001", "mms push report-VMSC:unexpected response received", "彩信网元错误", "大网原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC7001", "mms rejected report", "彩信拒绝", "大网原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("DeliveryImpossible", "SVC7001", "mms timeout report", "回落彩信过期", "大网原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。"),
                new FontdoError("接入层响应错误码", "35002", "messageid is null", "消息ID不能为空", "", "撤回消息需携带消息ID"),
                new FontdoError("接入层响应错误码", "35003", "Messageid can’t cancel", "消息ID不可撤回", "", "消息已发送不可撤回"),
                new FontdoError("接入层响应错误码", "35005", "messageid decode error", "消息ID解析失败", "", "检查消息ID字段是否符合规范"),
                new FontdoError("DeliveryImpossible", "SVC7001", "mms unrecognized report", "无法识别的彩信体", "chatbot原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。 前期的排查经验，为彩信体无法正常识别，可以检查下彩信体或换个彩信体测试"),
                new FontdoError("DeliveryImpossible", "SVC7001", "service error", "彩信其他错误", "大网原因", "彩信下发终端失败，按前期排查经验为短信中心拦截消息导致"),
                new FontdoError("DeliveryImpossible", "SVC7111", "MMS receipt timeout", "转彩push下发成功，但是终端没有返回转彩的回执，等待回执超时(3天)", "用户原因/chatbot原因", "回落彩信失败，涉及大网网元间内部错误，待后台技术人员派单核查具体原因后答复。")
        );
        HashMap<String, String> dict = new HashMap<>(fontdoErrors.size());
        for (FontdoError fontdoError : fontdoErrors) {
            dict.put(fontdoError.code + ":" + fontdoError.msg, fontdoError.description);
        }
        errorDict = Collections.unmodifiableMap(dict);
    }
}
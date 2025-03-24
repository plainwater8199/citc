<!--
 * @Author: wangpeng
 * @LastEditors: wangpeng
 * @Date: 2024-07-15 15:10:09
 * @LastEditTime: 2024-07-17 09:48:51
 * @FilePath: \nce-citc\README.md
 * @Description: 
-->
# CITC.BackEnd 1.0.0
# 中讯邮电咨询设计院有限公司开发者服务平台技术开发服务项目文档

## 一、项目概述

1. **项目名称：**
   - 中讯邮电咨询设计院有限公司开发者服务平台技术开发服务项目
2. **项目管理资料**
   - 禅道项目地址：<http://124.70.80.244:10007/product-browse-17.html>
   - 项目文档地址：无
   - 验收材料地址：无
3. **技术栈**

```
1、nodejs 12 
2、vue-cli
3、mysql 5.7.37
4、redis
5、spring cloud 2021.0.9
6、jdk 1.8
7、spring-boot 2.6.15
8、spring-cloud-alibaba 2021.6.0
9、rocketMQ
10、mybatisplus
11、minio
12、swagger
13、docker
14、k8s
15、jenkis

4. **依赖情况**
   - 数据库类型 mysql ，地址见配置文件
   - 其他中间件 rocketMQ、seate
   - 其他服务名称 统一运营管理平台、5G消息应用开发平台
5. **联系人**
   - 名称 杨洋
   - 电话 18980557864
   - 邮箱 

## 二、项目文件树

```
nce-citc
├─ apis //应用服务接口层
│  ├─ nce-auth-api //用户相关应用接口服务
│  │  ├─ accountmanagement //用户的chatbot账号管理接口
│  │  ├─ adminUser //统一运营管理平台管理相关接口
│  │  ├─ cardstype //卡片样式相关接口
│  │  ├─ certificate //用户资历相关接口
│  │  ├─ common //CSP渠道和供应商枚举类
│  │  ├─ constant //auth服务的其它枚举类
│  │  ├─ contactbacklist //黑名单相关接口
│  │  ├─ contactgroup //联系人组相关接口
│  │  ├─ csp //csp相关接口
│  │  │  ├─ account //csp账号相关接口
│  │  │  ├─ agent //csp服务代码相关接口
│  │  │  ├─ chatbot //csp的chatbot相关接口
│  │  │  ├─ contract //csp的合同管理相关接口
│  │  │  ├─ csp //csp信息查询接口
│  │  │  ├─ customer //csp用户管理相关接口
│  │  │  ├─ dict //csp固定菜单相关接口
│  │  │  ├─ mediasms //视频短信模板管理相关接口
│  │  │  ├─ relationship //csp和csp客户关联相关接口
│  │  │  ├─ sms //短信相关接口
│  │  │  ├─ smsTemplate //短信模板相关接口
│  │  │  ├─ statistics //csp统计相关接口
│  │  │  ├─ videoSms //视频短信和视频短信模板相关接口
│  │  ├─ dyz //改变多因子状态相关接口
│  │  ├─ formmanagement //from表单相关接口
│  │  ├─ helpcenter //帮助中心相关接口
│  │  ├─ identification //个人认证和实名认证相关接口
│  │  ├─ invoice //发票管理相关接口
│  │  ├─ meal //合同套餐相关接口
│  │  ├─ merchant //商户中心相关接口
│  │  ├─ messageplan //消息套餐相关接口
│  │  ├─ messagetemplate //消息模板相关接口
│  │  ├─ moblie //chabot异步同步接口相关接口
│  │  ├─ onlineservice //在线客服组相关接口
│  │  ├─ orderRefund //预付费订单退款相关接口
│  │  ├─ postpay //后付费相关接口
│  │  ├─ prepayment //预付费订单相关接口
│  │  ├─ sharelink //分享链接相关接口
│  │  ├─ submitdata //数据提交相关接口
│  │  ├─ ticket //资质申请相关接口
│  │  ├─ unicomAndTelecom //电信和联通相关接口相关接口
│  │  ├─ user //门户用户相关接口
│  │  ├─ usermessage /用户消息管理相关接口
│  ├─ nce-authcenter-api //用户相关应用接口服务
│  │  ├─ auth //用户信息和认证相关接口
│  │  ├─ captcha //验证码相关接口
│  │  ├─ constant //相关枚举类
│  │  ├─ csp //csp用户登录、csp用户相关登录记录、csp基本信息查询、csp子账号，csp登录首页相关接口
│  │  ├─ dyzCallBack //多因子异步调用相关接口
│  │  ├─ identification //用户认证相关接口
│  │  ├─ largeModel //大模型相关接口
│  │  ├─ legalaffairs //法务模块相关接口
│  │  ├─ permission //csp客户权限获取相关接口
│  │  ├─ processrecord //操作记录相关接口
│  │  ├─ systemmsg //站内信相关接口
│  │  ├─ tempStorePerm //模板商城权限管理相关接口
│  │  ├─ userDataSyn //社区用户和多租户相关用户同步接口
│  │  ├─ userLoginRecord //用户中心--用户登录记录相关接口
│  │  ├─ Utils //工具类
│  ├─ nce-filecenter-api //文件管理服务
│  │  ├─ enums //文件管理相关枚举类
│  │  ├─ FileApi //文件上传相关接口
│  │  ├─ TempStoreMaterialApi //模板管理素材相关接口
│  ├─ nce-im-api //消息管理服务
│  │  ├─ mall //扩展商城相关接口
│  │  │  ├─ common //相关类定义
│  │  │  ├─ goods //扩展商城商品相关接口
│  │  │  ├─ order //扩展商城指令相关接口
│  │  │  ├─ process //扩展商城流程相关接口
│  │  │  ├─ snapshot //扩展商城快照相关接口
│  │  │  ├─ template //扩展商城模板相关接口
│  │  │  ├─ variable //扩展商城变量相关接口
│  │  ├─ tempStore //模板商城相关接口
│  │  │  ├─ GoodsApi //商品相关接口
│  │  │  ├─ ManageGoodsLogApi //商品管理记录相关接口
│  │  │  ├─ OrderAPi //订单相关接口
│  │  │  ├─ ResourcesAudioApi //音频资源相关接口
│  │  │  ├─ ResourcesFormApi //表单资源相关接口
│  │  │  ├─ ResourcesImgApi //图片资源相关接口
│  │  │  ├─ ResourcesImgGroupApi //资源分组相关接口
│  │  │  ├─ ResourcesVideoApi //视频资源相关接口
│  │  ├─ domain //相关域定义
│  │  ├─ enums //相关枚举类
│  │  ├─ req //相关请求定义
│  │  ├─ res //相关类定义
│  │  ├─ sms //消息发送相关类定义
│  │  ├─ tenant //多租户生成相关类定义
│  │  ├─ BroadcastApi //群发启动管理接口
│  │  ├─ DeliveryNoticeApi //消息发送异步通知
│  │  ├─ MassSegmentApi //号段查询
│  │  ├─ MessageApi //群发服务相关接口
│  │  ├─ OperateHostApi //docker服务操作接口
│  │  ├─ RichMediaNotifyApi //视频短信异步通知接口
│  │  ├─ RichMediaPlatformApi //视频消息发送接口
│  │  ├─ RobotCallBackApi //机器人自定义指令回调接口
│  │  ├─ RobotCallPythonApi //python服务调用接口
│  │  ├─ RobotGroupSendPlanDescApi //群发发送记录查询相关接口
│  │  ├─ RobotGroupSendPlansApi //群发计划查询接口
│  │  ├─ RobotGroupSendPlansDetailApi //群发计划详情查询接口
│  │  ├─ RobotProcessorApi //机器人发送接口
│  │  ├─ SendDetailsApi //发送详情接口
│  │  ├─ SmsPlatformApi //平台模板管理接口
│  ├─ nce-misc-api //基础服务
│  │  ├─ constant //相关枚举类
│  │  ├─ dictionary //字典表相关接口
│  │  ├─ email //邮件服务发送接口
│  │  ├─ legal //法务文件相关接口
│  │  ├─ msg //消息服务相关接口
│  │  ├─ operationlog //操作记录相关接口
│  │  ├─ record //操作记录相关接口
│  │  ├─ schedule //定时任务服务接口
│  │  ├─ shortUrl //短链服务相关接口
│  │  ├─ sms //短信发送相关接口
│  ├─ nce-rebot-files-api //角色管理
│  │  ├─ fileApi //文件服务相关接口
│  │  │  ├─ AudioApi //音频文件相关接口
│  │  │  ├─ ExamineResultApi //送审记录相关接口
│  │  │  ├─ FileDataApi //文件同步（无用）
│  │  │  ├─ GroupApi //文件组相关接口
│  │  │  ├─ PictureApi //图片文件相关接口
│  │  │  ├─ PlatformApi //平台送审相关接口
│  │  │  ├─ TenantApi //多租户同步相关接口
│  │  │  ├─ UpFileApi //素材文件相关接口
│  │  │  ├─ VideoApi //视频文件相关接口
│  ├─ nce-robot-api //机器人服务
│  │  ├─ aim //挂断服务相关接口
│  │  ├─ customcommand //定制需求管理相关接口
│  │  ├─ dataStatistics //数据统计相关接口
│  │  ├─ developer //开发者服务相关接口
│  │  ├─ module //组件服务相关接口
│  │  ├─ msgenum //相关枚举类
│  │  ├─ robot //机器人管理相关接口
│  │  ├─ tempStore //模板商城相关接口
│  │  ├─ tenant //多租户管理相关接口
├─ commons //基础服务
│  ├─ nce-common-core //基础配置
│  ├─ nce-common-facade //聚合层统一配置
│  ├─ nce-common-feignapi //feign配置
│  ├─ nce-common-fiegnserver //feign服务配置
│  ├─ nce-common-log //日志配置
│  ├─ nce-common-mybatis //mybatis配置
│  ├─ nce-common-openfeign //openfeign配置
│  ├─ nce-common-redis //redis配置
│  ├─ nce-common-seate //seate配置
│  ├─ nce-common-security //安全配置
│  ├─ nce-common-web //web相关配置
│  ├─ nce-common-xss //xss相关配置
├─ facade //聚合层服务
│  ├─ nce-5g-authcenter-facade //用户中心统一管理服务
│  │  ├─ auth //用户服务相关接口
│  │  ├─ captcha //验证码服务相关接口
│  │  ├─ common //文件上传相关接口（废弃）
│  │  ├─ config //相关拦截器配置
│  │  ├─ configure //服务器配置类
│  │  ├─ csp //csp相关服务接口
│  │  ├─ dyz //多因子服务相关接口
│  │  ├─ filter //相关过滤器配置
│  │  ├─ identification //认证服务相关接口
│  │  ├─ legalaffairs //法务文件相关接口
│  │  ├─ runner //数据同步
│  │  ├─ systemmsg //站内信服务
│  │  ├─ utils //相关工具类
│  │  ├─ AuthCenterWebFacadeApplication //服务启动类
│  ├─ nce-5g-boss-facade //统一运营管理平台
│  │  ├─ adminPlatform //管理平台管理员相关接口
│  │  ├─ aim //挂短相关接口
│  │  ├─ auth //用户Excel工具类
│  │  ├─ common //公共的异常枚举类
│  │  ├─ config //管理平台的配置类
│  │  ├─ configure //环境配置相关类
│  │  ├─ csp //管理平台中csp相关接口
│  │  ├─ customcommand //自定义指令需求管理相关接口
│  │  ├─ dataStatistics //数据统计相关接口
│  │  ├─ develop //开发者服务统计相关接口
│  │  ├─ dictionary //相关工具类
│  │  ├─ directCustomer //直连客户相关接口
│  │  ├─ email //邮件发送接口
│  │  ├─ enums //相关枚举类
│  │  ├─ exceptionController //权限资源异常处理相关接口
│  │  ├─ file //后台管理-文件服务
│  │  ├─ helpcenter //后台管理-帮助中心
│  │  ├─ filter //后台管理-相关过滤器
│  │  ├─ mall //后台管理-扩展商城-商品管理
│  │  ├─ meal //后台管理-套餐合同管理
│  │  ├─ record //后台管理-操作记录相关接口
│  │  ├─ template //后台管理-模板服务
│  │  ├─ tempStore //后台管理-模板商城相关接口
│  │  ├─ utils //后台管理-相关接口
│  │  ├─ BossWebFacadeApplication //后台管理-启动类
│  ├─ nce-5g-chatbot-web-facade //5g消息应用开发平台服务
│  │  ├─ auth //用户相关接口
│  │  ├─ base //登录用户信息参数解析
│  │  ├─ common //公共配置类
│  │  ├─ config //服务拦截器配置
│  │  ├─ configure //服务配置信息映射类
│  │  ├─ csp //csp用户管理接口
│  │  ├─ customcommand //自定义指令相关接口
│  │  ├─ datadictionary //字典表相关接口
│  │  ├─ dataStatistics //数据统计接口
│  │  ├─ developer //开发者相关接口
│  │  ├─ filter //服务过滤器相关配置
│  │  ├─ helpcenter //帮助中心相关接口
│  │  ├─ helper //接口验签和授权验证
│  │  ├─ invoice //发票配置
│  │  ├─ largeModel //大模型管理
│  │  ├─ mall //模板商城相关配置
│  │  ├─ meal //合同管理
│  │  ├─ messageplan //消息套餐相关接口
│  │  ├─ module //组件服务相关接口
│  │  ├─ orderRefund //退款管理
│  │  ├─ postpay //后付费相关接口
│  │  ├─ prepayment //预付费相关接口
│  │  ├─ rebot //素材文件相关接口
│  │  ├─ robot //机器人管理相关接口
│  │  ├─ shortUrl //短链相关接口
│  │  ├─ tempStore //模板商城相关接口
│  │  ├─ utils //相关工具类
│  │  ├─ ws //ws
│  │  ├─ ChatBotWebFacadeApplication //启动类
│  ├─ nce-5g-filecenter-facade //文件服务管理服务
│  │  ├─ config //服务拦截器配置
│  │  ├─ configure //服务配置信息映射类
│  │  ├─ file //文件相关接口
│  │  ├─ filter //服务过滤器配置
│  │  ├─ FileCenterFacadeApplication //启动类
├─service //服务层
│  ├─ nce-auth-provider //用户相关应用接口服务
│  │  ├─ accountmanagement //用户的chatbot账号管理接口
│  │  ├─ adminUser //统一运营管理平台管理相关接口
│  │  ├─ cardstype //卡片样式相关接口
│  │  ├─ certificate //用户资历相关接口
│  │  ├─ common //CSP渠道和供应商枚举类
│  │  ├─ constant //auth服务的其它枚举类
│  │  ├─ contactbacklist //黑名单相关接口
│  │  ├─ contactgroup //联系人组相关接口
│  │  ├─ csp //csp相关接口
│  │  │  ├─ account //csp账号相关接口
│  │  │  ├─ agent //csp服务代码相关接口
│  │  │  ├─ chatbot //csp的chatbot相关接口
│  │  │  ├─ contract //csp的合同管理相关接口
│  │  │  ├─ csp //csp信息查询接口
│  │  │  ├─ customer //csp用户管理相关接口
│  │  │  ├─ dict //csp固定菜单相关接口
│  │  │  ├─ mediasms //视频短信模板管理相关接口
│  │  │  ├─ relationship //csp和csp客户关联相关接口
│  │  │  ├─ sms //短信相关接口
│  │  │  ├─ smsTemplate //短信模板相关接口
│  │  │  ├─ statistics //csp统计相关接口
│  │  │  ├─ videoSms //视频短信和视频短信模板相关接口
│  │  ├─ dyz //改变多因子状态相关接口
│  │  ├─ formmanagement //from表单相关接口
│  │  ├─ helpcenter //帮助中心相关接口
│  │  ├─ identification //个人认证和实名认证相关接口
│  │  ├─ invoice //发票管理相关接口
│  │  ├─ meal //合同套餐相关接口
│  │  ├─ merchant //商户中心相关接口
│  │  ├─ messageplan //消息套餐相关接口
│  │  ├─ messagetemplate //消息模板相关接口
│  │  ├─ moblie //chabot异步同步接口相关接口
│  │  ├─ mq //消息队列相关
│  │  ├─ onlineservice //在线客服组相关接口
│  │  ├─ orderRefund //预付费订单退款相关接口
│  │  ├─ postpay //后付费相关接口
│  │  ├─ prepayment //预付费订单相关接口
│  │  ├─ sharelink //分享链接相关接口
│  │  ├─ submitdata //数据提交相关接口
│  │  ├─ ticket //资质申请相关接口
│  │  ├─ unicomAndTelecom //电信和联通相关接口相关接口
│  │  ├─ user //门户用户相关接口
│  │  ├─ usermessage /用户消息管理相关接口
│  │  ├─ utils //工具类
│  │  ├─ AuthApplication //启动类
│  ├─ nce-authcenter-provider //用户相关应用接口服务
│  │  ├─ admin //管理员用户相关接口
│  │  ├─ auth //用户信息和认证相关接口
│  │  ├─ captcha //验证码相关接口
│  │  ├─ config //服务拦截器配置
│  │  ├─ configure //服务配置信息映射类
│  │  ├─ constant //相关枚举类
│  │  ├─ csp //csp用户登录、csp用户相关登录记录、csp基本信息查询、csp子账号，csp登录首页相关接口
│  │  ├─ dyz //多因子异步调用相关接口
│  │  ├─ identification //用户认证相关接口
│  │  ├─ largeModel //大模型相关接口
│  │  ├─ legalaffairs //法务模块相关接口
│  │  ├─ permission //csp客户权限获取相关接口
│  │  ├─ processrecord //操作记录相关接口
│  │  ├─ systemmsg //站内信相关接口
│  │  ├─ tempStorePerm //模板商城权限管理相关接口
│  │  ├─ userDataSyn //社区用户和多租户相关用户同步接口
│  │  ├─ userLoginRecord //用户中心--用户登录记录相关接口
│  │  ├─ Utils //工具类
│  │  ├─ AuthCenterApplication //启动类
│  ├─ nce-filecenter-api //文件管理服务
│  │  ├─ config //服务拦截器配置
│  │  ├─ configure //服务配置信息映射类
│  │  ├─ controller //文件管理相关接口实现
│  │  ├─ converter //文件工具类
│  │  ├─ entity //数据库映射对象
│  │  ├─ exp //异常信息
│  │  ├─ mapper //数据库mapper配置
│  │  ├─ service //文件服务实现层
│  │  ├─ util //相关工具类
│  │  ├─ FileCenterApplication //启动类
│  ├─ nce-im-api //消息管理服务
│  │  ├─ broadcast //群发启动管理接口
│  │  ├─ client //消息发送
│  │  ├─ common //相关配置类
│  │  ├─ config //服务拦截器配置
│  │  ├─ configure //服务配置信息映射类
│  │  ├─ controller //服务接口
│  │  ├─ entity //数据实体对象
│  │  ├─ excle //excle处理服务
│  │  ├─ exp //异常消息
│  │  ├─ gateway //网关配置
│  │  ├─ group //群发服务配置
│  │  ├─ mall //扩展商城相关接口
│  │  │  ├─ common //相关类定义
│  │  │  ├─ goods //扩展商城商品相关接口
│  │  │  ├─ order //扩展商城指令相关接口
│  │  │  ├─ process //扩展商城流程相关接口
│  │  │  ├─ snapshot //扩展商城快照相关接口
│  │  │  ├─ template //扩展商城模板相关接口
│  │  │  ├─ variable //扩展商城变量相关接口
│  │  ├─ tempStore //模板商城相关接口
│  │  │  ├─ GoodsApi //商品相关接口
│  │  │  ├─ ManageGoodsLogApi //商品管理记录相关接口
│  │  │  ├─ OrderAPi //订单相关接口
│  │  │  ├─ ResourcesAudioApi //音频资源相关接口
│  │  │  ├─ ResourcesFormApi //表单资源相关接口
│  │  │  ├─ ResourcesImgApi //图片资源相关接口
│  │  │  ├─ ResourcesImgGroupApi //资源分组相关接口
│  │  │  ├─ ResourcesVideoApi //视频资源相关接口
│  │  ├─ mapper //数据库mapper配置类
│  │  ├─ mq //消息组件配置
│  │  ├─ msgenum //消息的相关枚举
│  │  ├─ nacos //nacos操作服务
│  │  ├─ richMedia //视频短信服务
│  │  ├─ robot //机器人服务
│  │  ├─ service //服务实现类
│  │  ├─ session //session处理
│  │  ├─ shortMsg //短信服务
│  │  ├─ util //工具类
│  │  ├─ ImApplication //启动服务
│  ├─ nce-misc-api //基础服务
│  │  ├─ common //相关配置类
│  │  ├─ dictionary //字典表相关接口
│  │  ├─ email //邮件服务发送接口
│  │  ├─ enums //相关枚举类
│  │  ├─ legal //法务文件相关接口
│  │  ├─ msg //消息服务相关接口
│  │  ├─ operationlog //操作记录相关接口
│  │  ├─ record //操作记录相关接口
│  │  ├─ schedule //定时任务服务接口
│  │  ├─ shortUrl //短链服务相关接口
│  │  ├─ sms //短信发送相关接口
│  │  ├─ utils //工具类
│  │  ├─ MiscApplication //启动服务
│  ├─ nce-rebot-files-api //角色管理
│  │  ├─ fileApi //文件服务相关接口
│  │  ├─ config //服务拦截器配置
│  │  ├─ configure //服务配置信息映射类
│  │  ├─ controller //服务接口
│  │  ├─ enums //相关枚举类
│  │  ├─ entity //数据库映射对象
│  │  ├─ exp //异常信息
│  │  ├─ mapper //数据库mapper配置
│  │  ├─ mq //消息队列配置
│  │  ├─ schedules //定时任务配置
│  │  ├─ service //相关实现
│  │  ├─ utils //工具类
│  │  ├─ MaterialApplication //启动类
│  ├─ nce-robot-api //机器人服务
│  │  ├─ aim //挂断服务相关接口
│  │  ├─ configure //服务配置信息映射类
│  │  ├─ custom //定制需求管理相关接口
│  │  ├─ dataStatistics //数据统计相关接口
│  │  ├─ developer //开发者服务相关接口
│  │  ├─ module //组件服务相关接口
│  │  ├─ robot //机器人管理相关接口
│  │  ├─ tempStore //模板商城相关接口
│  │  ├─ tenant //多租户管理相关接口
│  │  ├─ utils //相关工具类
│  │  ├─ RobotApplication //启动类
├─ pom.xml
├─ README.md
└─

```

## 三、项目启动说明

```
- 调试模式 npm run debug
- 开发模式 npm run dev
- 后端模块调试模式 npm run serve
- 生产模式 npm run start
- 前端启动 npm run dev
- 前端打包 build目录下执行 npm run buildDevModules -- --type dev --modules 模块1,模块2,模块3...模块n
- apidoc命令根目录执行
  apidoc -i app/controller -i lib/plugin -o app/public/apidoc_ -e node_modules -e extend -e middleware -e model -e service -e router -e utils -e config

```
## 四、版本更新说明

- v1.0.0

        1.项目搭建
        2.用户管理
        3.机器人
        4.chatbot创建
        5.机器人构建
        6.视频短信
        7.短信
        8.多租户
        9.开发者服务
        10.模板订单
        11.打卡订阅
        12.应用开发平台
        13.素材广场


## 五、其他说明
#### api文档
         http://localhost:8103/swagger-ui/index.html  -- 用户中心
         http://localhost:8100/swagger-ui/index.html  -- 5G消息应用平台
         http://localhost:8102/swagger-ui/index.html  -- 统一应用管理平台



#### 首页
        https://p-dev.5g-msg.com/login


#### 后台登录
        https://bg-dev.5g-msg.com:8003/login


### 备注测试服和测试服后管地址
  https://p-dev.5g-msg.com/login

  https://bg-dev.5g-msg.com:8003/login

### 构建
   http://124.70.80.244:10008/citc

      


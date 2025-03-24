package com.citc.nce.robot.req;

import com.alibaba.fastjson.JSONArray;
import com.citc.nce.robot.vo.VideoSmsResponse;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 参数名称	参数类型	是否必填	参数说明
 * success	boolean	是	请求是否成功
 * code	String	是	请求响应代码，详见10.响应码表
 * message	String	否	请求响应描述
 * result	JSONArray	是	请求结果集合
 * </p>
 * result集合中每个元素的参数说明：
 *
 * <p>
 * 参数名称	参数类型	是否必填	参数说明
 * vmsId	String	是	消息id
 * customId	String	否	客户消息id
 * mobile	String	是	手机号
 * state	Integer	是	状态：4成功5失败6超时
 * operCode	String	是	状态报告码
 * message	String	是	状态报告描述
 * submitTime	String	是	提交时间，格式:yyyy-MM-dd HH:mm:ss
 * reportTime	String	是	状态报告时间，格式:yyyy-MM-dd HH:mm:ss
 * extendedCode	String	否	扩展码
 * dataSource	String	否	来源
 * subPort	String	否	子端口
 * channelNumber	String	否	通道号码
 * </p>
 */
@Data
public class RichMediaResultArray {
    private Boolean success;
    private String code;
    private String message;
    private List<VideoSmsResponse> result;

    //变量替换后的模板内容
    private String contents;
}

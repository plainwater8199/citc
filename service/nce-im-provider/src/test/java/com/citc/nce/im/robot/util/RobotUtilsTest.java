package com.citc.nce.im.robot.util;

import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.dto.FileAccept;
import com.citc.nce.dto.FileTidReq;
import com.citc.nce.fileApi.PlatformApi;
import com.citc.nce.im.service.SendResultService;
import com.citc.nce.robot.req.TestSendMsgReq;
import com.citc.nce.robot.vo.directcustomer.Parameter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件名:RobotUtilsTest
 * 创建者:zhujinyu
 * 创建时间:2024/3/24 14:13
 * 描述:
 */
@SpringBootTest
public class RobotUtilsTest {
    @Resource
    private SendResultService sendResultService;
    @Resource
    private PlatformApi platformApi;

    @Test
    public void test(){
        List<Parameter> parameters = new ArrayList<>();
        RobotUtils.setVariableParam("1","110",
                "{\"robotProcessNodeList\":[{\"nodeType\":6,\"nodeName\":\"触发\",\"id\":\"abcd\",\"top\":\"100px\",\"left\":\"380px\",\"width\":\"80px\",\"color\":\"#fff\",\"type\":1,\"index\":1},{\"contentType\":0,\"contentBody\":[{\"type\":1,\"messageDetail\":{\"input\":{\"name\":\"{{question-ad54d6261-7965-4b86-8383-6b993384793d&提问1}}   \",\"value\":\"{{question-(ad54d6261-7965-4b86-8383-6b993384793d)}}   \",\"names\":[{\"id\":\"ad54d6261-7965-4b86-8383-6b993384793d\",\"type\":2,\"name\":\"提问1\"}],\"length\":4}}},{\"type\":1,\"messageDetail\":{\"input\":{\"name\":\"{{custom-375&g}}\",\"value\":\"{{custom-g}}\",\"names\":[{\"id\":\"375\",\"type\":2,\"name\":\"g\"}],\"length\":1}}},{\"type\":1,\"messageDetail\":{\"input\":{\"name\":\"{{sys-process&触发流程上行}}\",\"value\":\"{{sys-process}}\",\"names\":[{\"id\":\"sys-process\",\"type\":2,\"name\":\"触发流程上行\"}],\"length\":1}}}],\"buttonList\":[],\"nodeType\":2,\"index\":1,\"i\":1,\"nodeName\":\"发送消息1\",\"id\":\"a4539f3be-c249-4007-b8e1-bcfebec3f6f9\",\"top\":\"116px\",\"left\":\"569px\",\"width\":\"218px\",\"sucessId\":\"\",\"failId\":\"\",\"nodeLast\":\"\",\"verifyList\":[],\"orderList\":[],\"varsList\":[],\"subProcessList\":[],\"conditionList\":[],\"presonList\":[]}],\"robotProcessLineList\":[{\"toType\":\"6\",\"fromType\":\"2\",\"fromName\":\"触发\",\"toId\":\"a4539f3be-c249-4007-b8e1-bcfebec3f6f9\",\"fromId\":\"abcd\",\"toName\":\"发送消息1\",\"label\":\"连线名称\",\"lineId\":\"af255a4f8-f5b4-4d04-b2d9-181d9d079c8e\",\"index\":1}],\"sourceIds\":[],\"names\":[{\"id\":\"ad54d6261-7965-4b86-8383-6b993384793d\",\"type\":2,\"name\":\"提问1\"},{\"id\":\"375\",\"type\":2,\"name\":\"g\"},{\"id\":\"sys-process\",\"type\":2,\"name\":\"触发流程上行\"}]}"
                ,parameters);
    }

    @Test
    public void test1(){
        TestSendMsgReq testSendMsgReq = JsonUtils.string2Obj( "{\"chatbotId\":\"5455867534\",\"phoneNum\":18628075352,\"templateId\":547,\"variables\":\"1,2\"}",       TestSendMsgReq.class);
        sendResultService.testSendMessage(testSendMsgReq);
    }

    @Test
    void test2(){
        FileTidReq tidReq = new FileTidReq();
        tidReq.setOperator("电信");
        tidReq.setFileUrlId("c51c1de3ea6a4712a8a746227c53e49d");
        FileAccept fileAccept = platformApi.getFileTid(tidReq);
        System.out.println(fileAccept);
    }
}

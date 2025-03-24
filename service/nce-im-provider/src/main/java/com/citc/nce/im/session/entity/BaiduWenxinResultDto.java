package com.citc.nce.im.session.entity;

import lombok.Data;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/9/5 15:24
 */
@Data
public class BaiduWenxinResultDto {
    // 本轮对话的id
    private String id;
    private String object;
    private Long logId;
    // 时间戳
    private Long created;
    // 表示当前子句的序号，只有在流式接口模式下会返回该字段
    private Long sentenceId;
    // 表示当前子句是否是最后一句，只有在流式接口模式下会返回该字段
    private Boolean end;
    // 插件返回结果
    private String result;
    // 表示用户输入是否存在安全，是否关闭当前会话，清理历史会话信息
    // true：是，表示用户输入存在安全风险，建议关闭当前会话，清理历史会话信息
    // false：否，表示用户输入无安全风险
    private String needClearHistory;
    // 当need_clear_history为true时，此字段会告知第几轮对话有敏感信息，如果是当前问题，ban_round = -1
    private Long ban_round;
    // token统计信息，token数 = 汉字数+单词数*1.3 （仅为估算逻辑）
    private Usage usage;

    @Data
    private class Usage {
        // 问题tokens数
        private int promptTokens;
        // 回答tokens数
        private int completionTokens;
        // tokens总数
        private int totalTokens;
    }
}

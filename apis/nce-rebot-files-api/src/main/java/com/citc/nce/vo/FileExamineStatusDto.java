package com.citc.nce.vo;

import lombok.Data;

import java.util.List;

/**
 * 文件审核状态dto
 *
 * @author jcrenc
 * @since 2024/12/19 15:51
 */
@Data
public class FileExamineStatusDto {
    private String uuid;
    private List<ExamineResultVo> examineResults;
}

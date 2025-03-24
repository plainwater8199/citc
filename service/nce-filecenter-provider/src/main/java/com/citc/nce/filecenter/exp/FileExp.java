package com.citc.nce.filecenter.exp;

import com.citc.nce.common.core.exception.ErrorCode;

public interface FileExp {
    ErrorCode FILE_MISS_ERROR = new ErrorCode(820201001, "文件不存在");
    ErrorCode FILE_UPLOAD_ERROR = new ErrorCode(820201002, "文件上传失败");
    ErrorCode VIDEO_ANALYSIS_ERROR = new ErrorCode(820201003, "视频解析错误");
    ErrorCode FILE_DELETE_ERROR = new ErrorCode(820201004, "临时文件删除失败");
    ErrorCode FILE_CREATE_ERROR = new ErrorCode(820201005, "临时文件创建失败");
    ErrorCode ENCRYPTION_ERROR = new ErrorCode(820201006, "Sha解码失败");
    ErrorCode TOKEN_ERROR = new ErrorCode(820201007, "获取token失败");
    ErrorCode PLATFORM_ERROR = new ErrorCode(820201008, "文件上传失败");
    ErrorCode EXAMINE_ERROR = new ErrorCode(820201009, "送审文件下载失败");
    ErrorCode CLIENT_ERROR = new ErrorCode(820201010, "客户端创建失败");
    ErrorCode DELETE_ERROR = new ErrorCode(820201011, "平台侧文件删除失败");
    ErrorCode FORMAT_ERROR = new ErrorCode(820201012, "不允许上传该类型文件");
    ErrorCode FILE_SCENE_ERROR = new ErrorCode(820201013, "文件上传场景错误");
    ErrorCode DELETE_EXAMINE_ERROR = new ErrorCode(820201014, "文件正在审核中，请等待");
    ErrorCode FILE_SIZE_ERROR = new ErrorCode(820201015, "文件大小操作场景上传文件大小限制");
    ErrorCode FILE_FORMAT_FALSIFY = new ErrorCode(820201016, "文件格式被篡改");
    ErrorCode FILE_DURATION_ERROR = new ErrorCode(820201017, "文件时长超长");
}

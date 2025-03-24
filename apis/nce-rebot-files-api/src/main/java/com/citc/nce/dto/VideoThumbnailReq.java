package com.citc.nce.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author jcrenc
 * @since 2024/7/15 16:04
 */
@Data
public class VideoThumbnailReq {
    @NotNull
    private String videoUrlId;
    @NotNull
    private String thumbnailId;
}

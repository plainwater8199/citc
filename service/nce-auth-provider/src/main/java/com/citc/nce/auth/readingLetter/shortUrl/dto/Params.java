package com.citc.nce.auth.readingLetter.shortUrl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Params{
    private String custFlag;
    private String custId;
    private String customShortCode;
    private String customUrl;
}
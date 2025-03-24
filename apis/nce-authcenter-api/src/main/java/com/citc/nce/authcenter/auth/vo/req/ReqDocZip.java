package com.citc.nce.authcenter.auth.vo.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;

/**
 * @Author: zhujy
 * @Date: 2022/8/17 14:34
 * @Version: 1.0
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqDocZip {
  private String name;
  private InputStream inputStream;
}


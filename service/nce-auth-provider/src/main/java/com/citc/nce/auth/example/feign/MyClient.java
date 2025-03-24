/*
 * Copyright 2013-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.citc.nce.auth.example.feign;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.citc.nce.common.core.exception.BizException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author freeman
 */
@FeignClient(value = "MyClient", url = "http://localhost:8202"//,fallback =
)
@SentinelResource(exceptionsToIgnore = {BizException.class})
public interface MyClient {

    @GetMapping("/timeout/{t}")
    String timeout(@PathVariable("t") Integer t);

}

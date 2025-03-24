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

package com.citc.nce.auth.example.controller;

import com.citc.nce.common.core.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author freeman
 */
@RestController
@Slf4j
public class ApiController {

    @GetMapping("/default/{ok}")
    public String defaultConfig(@PathVariable boolean ok) {
        System.out.println("defaultConfig");
        if (ok) {
            return "ok";
        }
        throw new BizException(110, "fail");
    }

    @GetMapping("/timeout/{t}")
    String timeout(@PathVariable("t") Integer t) {
        System.out.println("defaultConfig");
        if (Integer.valueOf(1).equals(t)) {
            throw new BizException(110, "fail");
        }
        try {
            TimeUnit.SECONDS.sleep(t);
        } catch (InterruptedException e) {
            log.warn("Interrupted!", e);
            Thread.currentThread().interrupt();
        }
        return "api 返回的结果";

    }

    @GetMapping("/feign/{ok}")
    public String feignConfig(@PathVariable boolean ok) {
        System.out.println("feignConfig");
        if (ok) {
            return "ok";
        }
        throw new RuntimeException("fail");
    }

    @GetMapping("/feignMethod/{ok}")
    public String feignMethodConfig(@PathVariable boolean ok) {
        if (ok) {
            return "ok";
        }
        throw new RuntimeException("fail");
    }

}

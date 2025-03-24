package com.citc.nce.auth.utils.db;

import com.citc.nce.auth.utils.db.service.CheckDBService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class CheckDB implements ApplicationRunner {
    @Autowired
    private CheckDBService checkDBService;

    @Value("${checkDB.enable}")
    private boolean enable;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!enable) {
            return;
        }
        log.info("----------------数据库DB校验----------------");
        checkDBService.checkBDForService("classpath*:com/citc/nce/auth/**/domain/*.class");
        checkDBService.checkBDForService("classpath*:com/citc/nce/auth/**/entity/*.class");
    }
}

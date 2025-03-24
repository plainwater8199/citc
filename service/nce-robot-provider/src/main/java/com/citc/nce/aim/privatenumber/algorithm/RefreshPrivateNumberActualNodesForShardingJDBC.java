package com.citc.nce.aim.privatenumber.algorithm;
import com.citc.nce.aim.privatenumber.service.PrivateNumberTableManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@Component
@Slf4j
public class RefreshPrivateNumberActualNodesForShardingJDBC implements ApplicationRunner {

    @Resource
    PrivateNumberTableManagerService privateNumberTableManagerService;

    @Override
    public void run(ApplicationArguments args)  {

        //创建当前的表
        privateNumberTableManagerService.createAimPrivateNumberTable();

        // 刷新sharding的真实结点
        privateNumberTableManagerService.refreshShardingActualNodes(true);



    }
}

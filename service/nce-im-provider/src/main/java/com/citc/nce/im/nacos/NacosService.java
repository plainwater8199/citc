package com.citc.nce.im.nacos;

import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.im.configure.NacosConfigure;
import com.citc.nce.im.exp.NacosExp;
import com.citc.nce.im.exp.SendGroupExp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;
@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(NacosConfigure.class)
public class NacosService {

    private final NacosConfigure nacosConfigure;

    public List<String> queryNacosServices() {
        Properties properties = new Properties();
        properties.put("serverAddr", nacosConfigure.getServerAddr());
        properties.put("namespace", nacosConfigure.getNamespace());
        properties.put("username",nacosConfigure.getUsername());
        properties.put("password",nacosConfigure.getPassword());
        try{
            NamingService namingService = NamingFactory.createNamingService(properties);
            ListView<String> servicesOfServer = namingService.getServicesOfServer(1, 200);
            return servicesOfServer.getData();
        }catch (Exception e){
            log.error(e.getMessage());
            throw new BizException(NacosExp.NACOS_CONFIG_ERROR);
        }

    }

    public boolean checkDockerService(String serviceName) {
        Properties properties = new Properties();
        properties.put("serverAddr", nacosConfigure.getServerAddr());
        properties.put("namespace", nacosConfigure.getNamespace());
        properties.put("username",nacosConfigure.getUsername());
        properties.put("password",nacosConfigure.getPassword());
        try{
            NamingService namingService = NamingFactory.createNamingService(properties);
            Instance instance = namingService.selectOneHealthyInstance(serviceName);
            return instance != null && instance.isHealthy() && instance.isEnabled();
        }catch (Exception e){
            log.error(e.getMessage());
            throw new BizException(NacosExp.NACOS_CONFIG_ERROR);
        }
    }
}

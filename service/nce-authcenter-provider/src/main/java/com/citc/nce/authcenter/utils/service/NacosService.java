package com.citc.nce.authcenter.utils.service;

import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.ListView;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Properties;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(NacosConfigure.class)
public class NacosService {

    private final NacosConfigure nacosConfigure;

    public void queryNacosServices() {
        Properties properties = new Properties();
        properties.put("serverAddr", nacosConfigure.getServerAddr());
        properties.put("namespace", nacosConfigure.getNamespace());
        properties.put("username",nacosConfigure.getUsername());
        properties.put("password",nacosConfigure.getPassword());
        try{
            NamingService namingService = NamingFactory.createNamingService(properties);
            ListView<String> servicesOfServer = namingService.getServicesOfServer(1, 100);
            List<String> data1 = servicesOfServer.getData();
            for (String item : data1){
                System.out.println(item);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
}

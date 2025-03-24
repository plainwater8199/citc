package com.citc.nce.tempStore;


import com.citc.nce.tempStore.vo.UseTempVariableOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "rebot-service", contextId = "tempStore-service", url = "${robot:}")
public interface UseTempStoreApi {

    @PostMapping("/tempStoreUse/saveVariableOrder")
    public UseTempVariableOrder saveVariableOrder(@RequestBody UseTempVariableOrder data);

}

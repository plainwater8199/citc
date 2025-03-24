package com.citc.nce.robot.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryStatusReq implements Serializable {
    @NotEmpty
    private List<DeliveryInfo> deliveryInfoList;
}

package com.citc.nce.im.session.entity;

import lombok.Data;

/**
 * @Author: yangchuang
 * @Date: 2022/11/16 9:50
 * @Version: 1.0
 * @Description:
 */
@Data
public class Gps {
    private Double lon;
    private Double lat;

    public Gps(Double lon, Double lat) {
        this.lon = lon;
        this.lat = lat;
    }

    public Gps() {
    }
}

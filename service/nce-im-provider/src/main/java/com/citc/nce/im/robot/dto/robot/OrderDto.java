package com.citc.nce.im.robot.dto.robot;

import lombok.Data;


/**
 * create table robot_order
 * (
 * id                bigint auto_increment comment '表主键'
 * primary key,
 * robot_id          bigint            null comment '机器人id',
 * order_name        varchar(25)       not null comment '指令名称',
 * request_type      tinyint default 0 null comment '请求类型 0(GET)、1(POST)',
 * request_url       varchar(255)      null comment '请求地址',
 * header_list       varchar(512)      null comment '请求头',
 * request_body_type tinyint default 0 null comment '请求数据类型 0(FORM-DATA)、1(RAW)',
 * request_raw_type  tinyint           null comment '请求数据类型 0(TEXT)、1(JSON)、2(XML)',
 * body_list         text              null comment '请求数据',
 * response_type     tinyint default 0 null comment '响应参数类型 0(关闭)、1(打开)',
 * response_list     varchar(512)      null comment '响应参数列表',
 * depiction         varchar(50)       null,
 * order_type        tinyint default 0 null comment '请求类型 0(http)、1(自定义)',
 * );
 *
 * @author jcrenc
 * @since 2023/7/13 16:18
 */
@Data
public class OrderDto {
    private Long id;
    private String orderName;
    private Integer requestType;
    private String requestUrl;
    private String headerList;
    private Integer requestBodyType;
    private Integer requestRawType;
    private String bodyList; //当类型为http指令时为请求数据，自定义指令时为要执行的py脚本
    private Integer responseType;
    private String responseList;
    private String depiction;
    private Integer orderType;
    // 指令是否启用
    private Boolean active;
}

package com.citc.nce.im.session.processor.bizModel;

import lombok.Data;

import java.util.Date;

/*
* create table robot_order
(
    id                bigint auto_increment comment '表主键'
        primary key,
    robot_id          bigint            null comment '机器人id',
    order_name        varchar(25)       not null comment '指令名称',
    request_type      tinyint default 0 null comment '请求类型 0(GET)、1(POST)',
    request_url       varchar(255)      null comment '请求地址',
    header_list       varchar(512)      null comment '请求头',
    request_body_type tinyint default 0 null comment '请求数据类型 0(FORM-DATA)、1(RAW)',
    request_raw_type  tinyint           null comment '请求数据类型 0(TEXT)、1(JSON)、2(XML)',
    body_list         text              null comment '请求数据',
    response_type     tinyint default 0 null comment '响应参数类型 0(关闭)、1(打开)',
    response_list     varchar(512)      null comment '响应参数列表',
    creator           varchar(64)       not null comment '创建者',
    create_time       datetime          not null comment '创建时间',
    updater           varchar(64)       null comment '更新人',
    update_time       datetime          null comment '更新时间',
    deleted           tinyint default 0 null comment '是否删除 0(未删除)、1(已删除)',
    deleted_time      datetime          null comment '删除时间',
    depiction         varchar(50)       null,
    order_type        tinyint default 0 null comment '请求类型 0(http)、1(自定义)',
    request_url_name  longtext          null
);
*
* */
@Data
public class OrderModel {
    private Long id;
    private Long robotId;
    private String orderName;
    private Integer requestType;
    private String requestUrl;
    private String headerList;
    private Integer requestBodyType;
    private Integer requestRawType;
    private String bodylist;
    private Integer responseType;
    private String responseList;
    private String creator;
    private Date createTime;


    private String updater;
    private Date updateTime;
    private Integer deleted;
    private Date deletedTime;
    //描述
    private String depiction;

    private Integer orderType;

    // 指令是否启用
    private Boolean active;
}

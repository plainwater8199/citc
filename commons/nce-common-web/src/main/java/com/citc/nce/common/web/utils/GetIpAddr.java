package com.citc.nce.common.web.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public class GetIpAddr {

    private static final String IP_UTILS_FLAG = ",";
    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST_IP = "0:0:0:0:0:0:0:1";
    private static final String LOCALHOST_IP1 = "127.0.0.1";

    /**
     * 获取IP公网地址
     * <p>
     * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
     * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
     */
    /**
     * 获取ip
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = null;
        try {
            StringBuilder ipSB = new StringBuilder();

            ipSB.append("【X-Original-Forwarded-For】："+request.getHeader("X-Original-Forwarded-For"));
            ipSB.append("、【X-Forwarded-For】："+request.getHeader("X-Forwarded-For"));
            ipSB.append("、【x-forwarded-for】："+request.getHeader("x-forwarded-for"));
            ipSB.append("、【Proxy-Client-IP】："+request.getHeader("Proxy-Client-IP"));
            ipSB.append("、【WL-Proxy-Client-IP】："+request.getHeader("WL-Proxy-Client-IP"));
            ipSB.append("、【HTTP_CLIENT_IP】："+request.getHeader("HTTP_CLIENT_IP"));
            ipSB.append("、【HTTP_X_FORWARDED_FOR】："+request.getHeader("HTTP_X_FORWARDED_FOR"));
            ipSB.append("、【HTTP_X_FORWARDED】："+request.getHeader("HTTP_X_FORWARDED"));
            ipSB.append("、【HTTP_X_CLUSTER_CLIENT_IP】："+request.getHeader("HTTP_X_CLUSTER_CLIENT_IP"));
            ipSB.append("、【HTTP_VIA】："+request.getHeader("HTTP_VIA"));
            ipSB.append("、【X-Real-IP】："+request.getHeader("X-Real-IP"));
            ipSB.append("、【remoteAddr】："+request.getRemoteAddr());
            log.error(ipSB.toString());
            ip = request.getHeader("X-Real-IP");
            if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Original-Forwarded-For");
            }
            //以下两个获取在k8s中，将真实的客户端IP，放到了x-Original-Forwarded-For。而将WAF的回源地址放到了 x-Forwarded-For了。
            if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Forwarded-For");
            }
            //获取nginx等代理的ip
            if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("x-forwarded-for");
            }
            if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            //兼容k8s集群获取ip
            if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
                if (LOCALHOST_IP1.equalsIgnoreCase(ip) || LOCALHOST_IP.equalsIgnoreCase(ip)) {
                    //根据网卡取本机配置的IP
                    InetAddress iNet = null;
                    try {
                        iNet = InetAddress.getLocalHost();
                        ip = iNet.getHostAddress();
                    } catch (UnknownHostException e) {
                        log.error("getClientIp error: {}", e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.error("IPUtils ERROR ", e);
        }
        //使用代理，则获取第一个IP地址
        if (!StringUtils.isEmpty(ip) && ip.indexOf(IP_UTILS_FLAG) > 0) {
            ip = ip.substring(0, ip.indexOf(IP_UTILS_FLAG));
        }

        return ip;

    }

}

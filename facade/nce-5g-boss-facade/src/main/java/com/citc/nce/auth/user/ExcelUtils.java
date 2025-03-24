package com.citc.nce.auth.user;

import com.alibaba.excel.EasyExcel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * @Author: ylzouf
 * @Date: 2022/8/16 17:14
 * @Version 1.0
 * @Description:
 */
@Slf4j
public class ExcelUtils {
    /**
     * 表头进行自动扩展的导出
     *
     * @param os        文件输出流
     * @param clazz     Excel实体映射类
     * @param data      导出数据
     * @param sheetName sheet名称
     * @return
     */
    @SneakyThrows
    public static void writeExcel(OutputStream os, Class clazz, List<?> data, String sheetName) {
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(os);
            EasyExcel.write(bos, clazz)
                    .sheet(sheetName)
                    .doWrite(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ResponseEntity下载文件
     *
     * @param fileName
     * @param byteOutPutStream
     */
    @SneakyThrows
    public static ResponseEntity<byte[]> downloadExcel(String fileName, ByteArrayOutputStream byteOutPutStream) {
        //下载文件
        try {
            HttpHeaders headers = new HttpHeaders();
            String fileNameURL = URLEncoder.encode(fileName, "UTF-8");
            headers.add("Content-disposition", "attachment;filename=" + fileNameURL + ";" + "filename*=utf-8''" + fileNameURL);
            ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(byteOutPutStream.toByteArray(), headers, HttpStatus.OK);
            return responseEntity;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != byteOutPutStream) {
                byteOutPutStream.flush();
                byteOutPutStream.close();
            }
        }
        return null;
    }
}

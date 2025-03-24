package com.citc.nce.authcenter.Utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.metadata.WriteSheet;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;


/**
 * @Author: zhujy
 * @Date: 2022/8/17 14:34
 * @Version: 1.0
 * @Description:
 */
public class ExcelUtils {

    /**
     * 功能描述: 默认读取第一个sheet
     *
     * @param inputStream
     * @param headClass
     * @param readListener
     * @return void
     * @author xuhao
     */
    public static void importExcel(InputStream inputStream, Class headClass, ReadListener readListener) {
        importExcel(inputStream, headClass, readListener, 0, 1);
    }

    public static void importExcel(InputStream inputStream, Class headClass, ReadListener readListener, Integer sheetNo,
                                   Integer headRowNumber) {
        ExcelReader excelReader = null;
        try {
            excelReader = EasyExcel.read(inputStream, headClass, readListener).build();
            ReadSheet readSheet = EasyExcel.readSheet(sheetNo).headRowNumber(headRowNumber).build();
            excelReader.read(readSheet);
        } finally {
            if (excelReader != null) {
                // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
                excelReader.finish();
            }
        }
    }

    /**
     * 功能描述: 读取所有sheet
     *
     * @param inputStream
     * @param headClass
     * @param readListener
     * @return void
     * @author xuhao
     */
    public static void importAllSheetExcel(InputStream inputStream, Class headClass, ReadListener readListener,
                                           Integer headRowNumber) {
        ExcelReader excelReader = null;
        try {
            excelReader = EasyExcel.read(inputStream, headClass, readListener).headRowNumber(headRowNumber).build();
            excelReader.readAll();
        } finally {
            if (excelReader != null) {
                // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
                excelReader.finish();
            }
        }
    }


    /**
     * 功能描述: 导出简单excel
     *
     * @param headClass
     * @param data
     * @param sheetName
     * @return byte[]
     * @author xuhao
     */
    public static <T> byte[] exportExcel(Class headClass, List<T> data, String sheetName) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.write(os, headClass).build();
            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();
            excelWriter.write(data, writeSheet);
        } finally {
            if (excelWriter != null) {
                // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
                excelWriter.finish();
            }
            if (os != null) {
                os.close();
            }
        }
        return os.toByteArray();

    }

    /**
     * 功能描述: 导出多sheet的excel
     *
     * @param headClass
     * @param mulData
     * @return byte[]
     * @author xuhao
     */
    public static <T> byte[] exportMulSheetExcel(Class headClass, Map<String, List<T>> mulData) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.write(os, headClass).build();
            WriteSheet writeSheet = null;
            for (Map.Entry<String, List<T>> entry : mulData.entrySet()) {
                writeSheet = EasyExcel.writerSheet(entry.getKey()).build();
                // 分页去数据库查询数据 这里可以去数据库查询每一页的数据
                List data = entry.getValue();
                excelWriter.write(data, writeSheet);
            }
        } finally {
            if (excelWriter != null) {
                // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
                excelWriter.finish();
            }
            if (os != null) {
                os.close();
            }
        }
        return os.toByteArray();
    }


    /**
     * 功能描述: 根据resources的template下面的模板生成excel
     *
     * @param data
     * @param templateFileName
     * @return byte[]
     * @author xuhao
     */
    public static <T> byte[] exportExcelByTemplate(List<T> data, String templateFileName) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ExcelWriter excelWriter = null;
        try {
            ClassPathResource classPathResource = new ClassPathResource(
                    "templates" + File.separator + templateFileName);
            excelWriter = EasyExcel.write(os).withTemplate(classPathResource.getInputStream()).build();
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            excelWriter.fill(data, writeSheet);
        } finally {
            if (excelWriter != null) {
                // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
                excelWriter.finish();
            }
            if (os != null) {
                os.close();
            }
        }
        return os.toByteArray();
    }
}

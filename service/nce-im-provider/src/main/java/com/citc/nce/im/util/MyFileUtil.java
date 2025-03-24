package com.citc.nce.im.util;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.nio.file.Files;

/**
 * 流转化为文件工具类
 */
public class MyFileUtil {

    public static File createTmpFile(InputStream inputStream, File tmpDirFile,String fileFormat) throws IOException {
        File resultFile = File.createTempFile(IdUtil.fastSimpleUUID(), "." + fileFormat, tmpDirFile);
        resultFile.deleteOnExit();
        FileUtils.copyToFile(inputStream, resultFile);
        return resultFile;
    }

    public static File bytesToFile(byte[] bytes,String fileFormat) throws IOException {
        return createTmpFile(new ByteArrayInputStream(bytes), Files.createTempDirectory("tempFile").toFile(), fileFormat);
    }

    public static CommonsMultipartFile getMultipartFile(File file) {
        FileItem item = new DiskFileItemFactory().createItem("file"
                , MediaType.MULTIPART_FORM_DATA_VALUE
                , true
                , file.getName());
        try (InputStream input = new FileInputStream(file);
             OutputStream os = item.getOutputStream()) {
            // 流转移
            IOUtils.copy(input, os);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid file: " + e, e);
        }

        return new CommonsMultipartFile(item);
    }

}

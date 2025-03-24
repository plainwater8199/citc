package com.citc.nce.filecenter.util;

import cn.hutool.core.util.IdUtil;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.filecenter.exp.FileExp;
import com.citc.nce.filecenter.vo.FileInfo;
import com.google.common.base.Strings;
import groovy.util.logging.Log4j;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaInfo;
import ws.schild.jave.MultimediaObject;

import java.io.*;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.*;

public class FileUtil {
    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    private static final SecureRandom random = new SecureRandom();

    public static ByteArrayOutputStream obtainOPS(RandomAccessFile randomFile, int requestSize, int start) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int needSize = requestSize;
        randomFile.seek(start);
        while(needSize > 0){
            int len = randomFile.read(buffer);
            if(needSize < buffer.length){
                bos.write(buffer, 0, needSize);
            } else {
                bos.write(buffer, 0, len);
                if(len < buffer.length){
                    break;
                }
            }
            needSize -= buffer.length;
        }
        randomFile.close();
        bos.flush();
        return bos;
    }

    public static void updateHttpHeasers(HttpHeaders headers, Map<String, Long> indexMap, Long contentLength) {
        long requestStart = indexMap.get("requestStart");
        long requestEnd = indexMap.get("requestEnd");
        long length;
        if(requestEnd > 0){
            length = requestEnd - requestStart + 1;
            headers.add("Content-length",length+"");
            headers.add("Content-Range", "bytes " + requestStart + "-" + requestEnd + "/" + contentLength);
        }else{
            length = contentLength - requestStart;
            headers.add("Content-length",length+"");
            headers.add("Content-Range", "bytes "+ requestStart + "-" + (contentLength - 1) + "/" + contentLength);
        }
    }

    public static Map<String, Long> obtainIndexMap(String range) {
        Map<String, Long> indexMap = new HashMap<>();
        long requestStart = 0;
        long requestEnd = 0;
        String[] ranges = range.split("=");
        if(ranges.length > 1){
            String[] rangeDatas = ranges[1].split("-");
            requestStart = Integer.parseInt(rangeDatas[0]);
            if(rangeDatas.length > 1){
                requestEnd = Integer.parseInt(rangeDatas[1]);
            }
        }
        indexMap.put("requestStart",requestStart);
        indexMap.put("requestEnd",requestEnd);
        return  indexMap;
    }

    public static HttpHeaders obtainHttpHeaders(FileInfo fileInfo) throws UnsupportedEncodingException {
        HttpHeaders headers = new HttpHeaders();
        String fileName = fileInfo.getFileName();
        String fileFormat = fileInfo.getFileFormat();
        String fileNameURL = URLEncoder.encode(fileName, "UTF-8");
        headers.add("Content-disposition", "attachment;filename=" + fileNameURL);
        headers.add("Accept-Ranges", "bytes");
        headers.add("ETag", fileName);
        headers.add("Last-Modified", new Date().toString());
        headers.add("Content-Type", "video/"+fileFormat.toLowerCase(Locale.ROOT));
        return  headers;
    }

    public static Integer[] obtainStart(String range) {
        int start = 0;
        int end = 0;
        int requestSize ;
        if(range != null && range.startsWith("bytes=")){
            String[] values = range.split("=")[1].split("-");
            start = Integer.parseInt(values[0]);
            if(values.length > 1){
                end = Integer.parseInt(values[1]);
            }
        }
        if(end != 0 && end > start){
            requestSize = end - start + 1;
        } else {
            requestSize = Integer.MAX_VALUE;
        }
        return new Integer[]{start,requestSize};
    }
    //校验文件魔法值是不是pdf的魔法值
    public static boolean checkFormatIsNotPDF(File file) {
        byte[] magicNumberForExtension = getMagicNumberForExtension("pdf");
        if(file ==null)
        {
            throw new BizException("校验文件类型的文件为空");
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] magicNumber = new byte[magicNumberForExtension.length];
            int bytesRead = fis.read(magicNumber);
            if (bytesRead < magicNumberForExtension.length) {
                return false; // 文件太小
            }
            return !Arrays.equals(Arrays.copyOf(magicNumber, magicNumberForExtension.length), magicNumberForExtension);
        } catch (Exception e) {
            FileUtil.log.error("文件格式校验失败！");
        }
        return false;
    }


    public static boolean checkFormat(File file, String format) {
        if(!Arrays.asList("svg","css").contains(format)){// svg css 没有魔法数，无法校验
            byte[] magicNumberForExtension = getMagicNumberForExtension(format);
            boolean isError = false;
            if (magicNumberForExtension.length > 0 && file != null) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] magicNumber = new byte[magicNumberForExtension.length];
                    int bytesRead = fis.read(magicNumber);
                    if (bytesRead < magicNumberForExtension.length) {
                        return false; // 文件太小
                    }
                    isError = Arrays.equals(Arrays.copyOf(magicNumber, magicNumberForExtension.length), magicNumberForExtension);
                    //如果是png,jpg,jpeg,gif,svg,webp这些格式，魔法数是png也返回成功，因为缩略图会有印象
                    if(!isError && Arrays.asList("jpg","jpeg","gif","svg","webp","bmp").contains(format)){
                        return checkFormat(file,"png");
                    }
                    //mp3有两种魔法数，如果上面校验失败，则校验下面的魔法数
                    if(!isError && "mp3".equals(format)){
                        byte[] magicNumberForExtensionForMP3 = new byte[]{(byte) 0xFF};
                        isError = Arrays.equals(Arrays.copyOf(magicNumber, magicNumberForExtensionForMP3.length), magicNumberForExtensionForMP3);
                    }
                    //tiff有两种魔法数，如果上面校验失败，则校验下面的魔法数
                    if(!isError && "tiff".equals(format)){
                        byte[] magicNumberForExtensionForTiff = new byte[]{(byte) 0x4D, (byte) 0x4D, (byte) 0x00, (byte) 0x2A};
                        isError = Arrays.equals(Arrays.copyOf(magicNumber, magicNumberForExtensionForTiff.length), magicNumberForExtensionForTiff);
                    }
                } catch (Exception e) {
                    FileUtil.log.error("文件格式校验失败！");
                }
            }
            return isError;
        }else{
            return true;
        }
    }

    public static File convertInputStreamToFile(MultipartFile file) {
        try{
            String fileName = "/temp_file_"+getRandomNum(10);
            // 创建一个临时文件来存储InputStream的内容
            File tempFile = File.createTempFile(fileName, null);
//            file.transferTo(tempFile);

            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(file.getBytes());
            } catch (IOException e) {
                throw new IOException("Failed to convert MultipartFile to File", e);
            }

            return tempFile;
        }catch (Exception e){
            FileUtil.log.error("临时文件生成失败");
        }
        return null;
    }


    private static Integer getRandomNum(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Number of digits must be greater than 0");
        }
        int lowerBound = (int) Math.pow(10, count - 1D);
        int upperBound = (int) Math.pow(10, count) - 1;
        return lowerBound + random.nextInt(upperBound - lowerBound + 1);
    }

    private static byte[] getMagicNumberForExtension(String extension) {
        switch (extension.toLowerCase()) {
            case "jpg":
            case "jpeg":
                return new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
            case "png":
                return new byte[]{(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47};
            case "webp":
            case "avi":
                return new byte[]{(byte) 0x52, (byte) 0x49, (byte) 0x46, (byte) 0x46};
            case "svg":
                // SVG 无固定魔法数
                return new byte[0];
            case "gif":
                return new byte[]{(byte) 0x47, (byte) 0x49, (byte) 0x46, (byte) 0x38};
            case "pdf":
                return new byte[]{(byte) 0x25, (byte) 0x50, (byte) 0x44, (byte) 0x46};
            case "mp4":
            case "3gp":
                return new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00};
            case "wmv":
                return new byte[]{(byte) 0x30, (byte) 0x26, (byte) 0xB2, (byte) 0x75};
            case "ppt":
            case "doc":
            case "xls":
                return new byte[]{(byte) 0xD0, (byte) 0xCF, (byte) 0x11, (byte) 0xE0};
            case "zip":
            case "docx":
            case "pptx":
            case "xlsx":
                return new byte[]{(byte) 0x50, (byte) 0x4B, (byte) 0x03, (byte) 0x04};
            case "rar":
                return new byte[]{(byte) 0x52, (byte) 0x61, (byte) 0x72, (byte) 0x21};
            case "bmp":
                return new byte[]{(byte) 0x42, (byte) 0x4D};
            case "tiff":
                return new byte[]{(byte) 0x49, (byte) 0x49, (byte) 0x2A, (byte) 0x00}; // 或者 (byte) 0x4D, (byte) 0x4D, (byte) 0x00, (byte) 0x2A
            case "mp3":
                return new byte[]{(byte) 0x49, (byte) 0x44};
            case "aac":
                return new byte[]{(byte) 0xFF};
            case "amr":
                return new byte[]{(byte) 0x23, (byte) 0x21, (byte) 0x41, (byte) 0x4D};
            default:
                return new byte[]{};
        }
    }

    public static String getFileExtension(String fileName) {
        if(!Strings.isNullOrEmpty(fileName)){
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
                return fileName.substring(dotIndex + 1);
            }
        }
        return ""; // 无有效扩展名
    }

    public static long getFileDuration(File file, String format) {
        if (file != null) {
            List<String> videoFormatList = Arrays.asList("mp4","3gp","webm","mp3");
            if(videoFormatList.contains(format)){
                try {
                    MultimediaObject multimediaObject = new MultimediaObject(file);
                    MultimediaInfo info = multimediaObject.getInfo();
                    return info.getDuration();
                } catch (Exception e) {
                    FileUtil.log.error("获取文件时长错误！");
                }
            }else{
                return 1L;
            }
        }
        return 99999999999L;
    }

}

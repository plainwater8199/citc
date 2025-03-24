package com.citc.nce.rebot.util;

import com.citc.nce.filecenter.vo.FileInfo;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileUtil {
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

    public static List<String> getVideoFormatList() {
        List<String> videoFormats = new ArrayList<>();
        videoFormats.add("mp4");
        videoFormats.add("3gp");
        videoFormats.add("webm");
        return videoFormats;
    }

}

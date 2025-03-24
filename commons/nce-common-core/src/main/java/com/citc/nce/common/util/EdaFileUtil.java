package com.citc.nce.common.util;

import com.citc.nce.common.core.exception.BizException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

/**
 *
 * @author bydud
 * @since 2024/4/12
 */
@Slf4j
public class EdaFileUtil {
    public static final String EMPTY = "";

    private EdaFileUtil() {
    }

    public static String readToString(String src) throws IOException {
        File file = new File(src);
        if (!file.exists()) return EMPTY;
        StringBuilder sb = new StringBuilder();
        List<String> list = Files.readAllLines(file.toPath());
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) {
                sb.append("\r\n");
            }
        }
        return sb.toString();
    }

    /**
     * 把字符串写到制定目录下的指定文件中去
     *
     * @param path     文件存放路径
     * @param fileName 文件名称
     * @param content  文件内容
     * @return
     */
    public static boolean writeStringToFile(String path, String fileName, String content, Boolean append) {
        File file = new File(path, fileName);
        createParentPath(file.getParentFile());
        try (FileOutputStream fileOutputStream = new FileOutputStream(file, append);
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
             BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {
            bufferedWriter.write(content);
            bufferedWriter.flush();
            return true;
        } catch (IOException e) {
            log.info("writeStringToFile", e);
        }
        return false;
    }

    /**
     * 创建文件夹以及父级文件夹 mkdir -p
     *
     * @param pathDir
     */
    public static void createParentPath(String pathDir) {
        createParentPath(new File(pathDir));
    }

    /**
     * 创建文件夹以及父级文件夹
     *
     * @param file
     */
    public static void createParentPath(File file) {
        if (null != file && !file.exists()) {
            // 创建文件夹
            if (file.mkdirs()) {
                // 递归创建父级目录
                createParentPath(file);
            } else {
                throw new BizException(500, "创建目录失败");
            }
        }
    }
}

package com.citc.nce.filecenter.util;

import cn.hutool.core.util.ObjectUtil;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.filecenter.exp.FileExp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

@Slf4j
public class MyFileUtil {

    private static final String path = "/usr/temp";

    public static File createTmpFile(InputStream inputStream, String name, String ext) throws IOException {
        File directory = new File(path);
        if (!directory.exists()) {
            boolean mkdir = directory.mkdirs();
            if (!mkdir) {
                System.out.println("创建目录失败！");
            }
        }
        File resultFile = new File(path + File.separator + name + "." + ext);
        if (!resultFile.exists()) {
            boolean createFile = resultFile.createNewFile();
            if (!createFile) {
                System.out.println("创建文件失败！");
            }
        }
        if (ObjectUtil.isNotEmpty(inputStream)) {
            FileUtils.copyToFile(inputStream, resultFile);
        }
        return resultFile;
    }

    public static File bytesToFile(byte[] bytes, String fileType, String fileName) throws IOException {
        return createTmpFile(new ByteArrayInputStream(bytes), fileName, fileType);
    }

    /**
     * 生成原图的缩略图，每次压缩到0.5倍像素直至缩略图小于10kb
     * 1. 始终生成jpeg格式的缩略图
     * 2. 在服务器上创建了文件，使用完应该删除
     *
     * @param file 源文件
     * @return 缩略图文件
     */
    public static File buildPictureThumbnail(MultipartFile file, String fileName) {
        File result = null;
        if (file != null && !file.isEmpty()) {
            // 压缩到小于指定文件大小10k
            double targetSize = 10 * 1024D;
            try {
                //从MultipartFile 中获取 byte[]
                byte[] bytes = file.getBytes();
                while (bytes.length > targetSize) {
                    float reduceMultiple = 0.5f;
                    bytes = resizeImage(bytes, reduceMultiple);
                }
                result = bytesToFile(bytes, "jpeg", fileName);
                return result;
            } catch (IOException e) {
                //抛出异常
                log.error("保存图片失败", e);
                throw new BizException(FileExp.FILE_CREATE_ERROR);
            }
        }
        return null;
    }

    /**
     * 压缩图片到指定大小
     *
     * @param srcImgData
     * @param reduceMultiple 每次压缩比率
     * @return
     * @throws IOException
     */
    public static byte[] resizeImage(byte[] srcImgData, float reduceMultiple) throws IOException {
        if (srcImgData == null || srcImgData.length == 0) {
            throw new IllegalArgumentException("The source image data is null or empty.");
        }

        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(srcImgData));
        if (bi == null) {
            throw new IOException("Failed to decode image data.");
        }

        int width = (int) (bi.getWidth() * reduceMultiple); // 源图宽度
        int height = (int) (bi.getHeight() * reduceMultiple); // 源图高度
        Image image = bi.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = tag.getGraphics();
        g.setColor(Color.RED);
        g.drawImage(image, 0, 0, null); // 绘制处理后的图
        g.dispose();
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ImageIO.write(tag, "JPEG", bOut);
        return bOut.toByteArray();
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

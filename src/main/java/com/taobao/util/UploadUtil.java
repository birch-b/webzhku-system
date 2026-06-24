package com.taobao.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * 文件上传工具类
 */
public class UploadUtil {

    /**
     * 获取上传目录的真实路径
     */
    public static String getUploadPath(HttpServletRequest request, String subDir) {
        String realPath = request.getServletContext().getRealPath("/upload/" + subDir);
        File dir = new File(realPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return realPath;
    }

    /**
     * 生成唯一文件名
     */
    public static String generateFileName(String originalFilename) {
        String ext = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            ext = originalFilename.substring(dotIndex);
        }
        String datePath = new SimpleDateFormat("yyyyMMdd").format(new Date());
        return datePath + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12) + ext;
    }

    /**
     * 获取文件相对路径
     */
    public static String getRelativePath(String subDir, String fileName) {
        return "/upload/" + subDir + "/" + fileName;
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 创建文件上传处理器
     */
    public static ServletFileUpload createUploadHandler(int maxSize) {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(1024 * 1024); // 1MB内存阈值
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setFileSizeMax(maxSize * 1024 * 1024L); // 单个文件最大
        upload.setSizeMax(maxSize * 1024 * 1024L * 10); // 总请求最大
        upload.setHeaderEncoding("UTF-8");
        return upload;
    }
}

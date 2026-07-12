package com.taobao.util;

import org.apache.commons.fileupload.FileItem;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class FileUploadUtil {
    private static final String BASE_UPLOAD_DIR = "/upload";

    public static String upload(FileItem item, HttpServletRequest req, String subDir) throws Exception {
        if (item == null || item.getName() == null || item.getName().isEmpty()) return null;
        if (item.getSize() <= 0) return null;

        // 处理文件后缀：无后缀或文件名异常时使用 ".tmp" 兜底，避免 substring(-1) 抛越界
        String name = item.getName().trim();
        // 某些浏览器带完整路径（C:\Users\xxx\xxx.png），只取文件名
        int slash = Math.max(name.lastIndexOf('\\'), name.lastIndexOf('/'));
        if (slash >= 0) name = name.substring(slash + 1);
        String ext;
        int dot = name.lastIndexOf('.');
        if (dot <= 0 || dot == name.length() - 1) {
            ext = ".tmp"; // 无后缀兜底
        } else {
            ext = name.substring(dot).toLowerCase();
        }

        String filename = UUID.randomUUID().toString() + ext;
        String uploadDir = BASE_UPLOAD_DIR + "/" + (subDir != null ? subDir : "default");
        String realPath = req.getServletContext().getRealPath(uploadDir);
        File dir = new File(realPath);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("无法创建上传目录: " + dir.getAbsolutePath());
        }
        File file = new File(dir, filename);
        try (InputStream is = item.getInputStream()) {
            java.nio.file.Files.copy(is, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        return uploadDir + "/" + filename;
    }

    public static String upload(FileItem item, HttpServletRequest req) throws Exception {
        return upload(item, req, "default");
    }
}

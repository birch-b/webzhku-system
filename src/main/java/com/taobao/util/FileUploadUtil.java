package com.taobao.util;

import org.apache.commons.fileupload.FileItem;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.InputStream;
import java.util.UUID;

public class FileUploadUtil {
    private static final String BASE_UPLOAD_DIR = "/upload";

    public static String upload(FileItem item, HttpServletRequest req, String subDir) throws Exception {
        if (item == null || item.getName() == null || item.getName().isEmpty()) return null;
        String ext = item.getName().substring(item.getName().lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + ext;
        String uploadDir = BASE_UPLOAD_DIR + "/" + (subDir != null ? subDir : "default");
        String realPath = req.getServletContext().getRealPath(uploadDir);
        File dir = new File(realPath);
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, filename);
        try (InputStream is = item.getInputStream()) {
            java.nio.file.Files.copy(is, file.toPath());
        }
        return uploadDir + "/" + filename;
    }

    public static String upload(FileItem item, HttpServletRequest req) throws Exception {
        return upload(item, req, "default");
    }
}

package com.taobao.servlet.shop.servlet;

import com.taobao.util.DBUtil;
import com.taobao.util.FileUploadUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;
import java.util.List;

@WebServlet("/shop/info/*")
public class ShopInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM shop WHERE user_id = ?");
            ps.setLong(1, userId); ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                req.setAttribute("shopId", rs.getLong("id")); req.setAttribute("shopName", rs.getString("shop_name"));
                req.setAttribute("shopCategory", rs.getString("shop_category")); req.setAttribute("shopDesc", rs.getString("description"));
                req.setAttribute("shopAvatar", rs.getString("avatar")); req.setAttribute("shopStatus", rs.getInt("status"));
            }
            req.getRequestDispatcher("/shop/shop_info.jsp").forward(req, resp);
        } catch (Exception e) { e.printStackTrace(); req.getRequestDispatcher("/shop/shop_info.jsp").forward(req, resp); }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Long userId = (Long) req.getSession().getAttribute("userId");
        String shopName = "", shopCategory = "", description = "", avatar = "";
        boolean uploadOk = true;
        String uploadError = null;
        try {
            // 先读旧头像：如果本次没上传新文件，则保留 DB 里的旧头像（不覆盖）
            try (Connection conn0 = DBUtil.getConnection()) {
                PreparedStatement ps0 = conn0.prepareStatement("SELECT avatar FROM shop WHERE user_id = ?");
                ps0.setLong(1, userId); ResultSet rs0 = ps0.executeQuery();
                if (rs0.next()) { String old = rs0.getString("avatar"); if (old != null) avatar = old; }
            } catch (Exception ignored) {}

            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setSizeMax(5 * 1024 * 1024); // 5MB
            List<FileItem> items = upload.parseRequest(req);
            for (FileItem item : items) {
                if (item.isFormField()) {
                    switch (item.getFieldName()) {
                        case "shopName": shopName = item.getString("UTF-8"); break;
                        case "shopCategory": shopCategory = item.getString("UTF-8"); break;
                        case "description": description = item.getString("UTF-8"); break;
                    }
                } else {
                    // 只处理字段名是 avatar 的文件上传，忽略其它未知文件字段
                    if ("avatar".equals(item.getFieldName())) {
                        String url = FileUploadUtil.upload(item, req, "shop");
                        if (url != null && !url.isEmpty()) avatar = url;
                    }
                }
            }
        } catch (org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException e) {
            uploadOk = false; uploadError = "头像文件过大，请上传 5MB 以内的图片";
        } catch (Exception e) {
            e.printStackTrace();
            uploadOk = false; uploadError = "头像上传失败：" + e.getMessage();
        }

        // 上传失败时不继续写库，把错误提示带回前端
        if (!uploadOk) {
            req.setAttribute("error", uploadError);
            // 回显表单字段，避免用户重填
            req.setAttribute("shopId", null);
            req.setAttribute("shopName", shopName);
            req.setAttribute("shopCategory", shopCategory);
            req.setAttribute("shopDesc", description);
            req.setAttribute("shopAvatar", avatar);
            req.getRequestDispatcher("/shop/shop_info.jsp").forward(req, resp);
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            StringBuilder sql = new StringBuilder("UPDATE shop SET shop_name = ?, shop_category = ?, description = ?, avatar = ? WHERE user_id = ?");
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            ps.setString(1, shopName);
            ps.setString(2, shopCategory);
            ps.setString(3, description);
            ps.setString(4, avatar);
            ps.setLong(5, userId);
            ps.executeUpdate();

            // ===== DB 更新成功后同步刷新 session（保证 header.jsp 导航栏立刻显示新店铺名 / 新头像） =====
            HttpSession s = req.getSession();
            s.setAttribute("shopName", shopName);
            s.setAttribute("shopAvatar", avatar);

            resp.sendRedirect(req.getContextPath() + "/shop/info/view?msg=updated");
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "更新失败");
            req.getRequestDispatcher("/shop/shop_info.jsp").forward(req, resp);
        }
    }
}

package com.taobao.shop.servlet;

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
        try {
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
                    String url = FileUploadUtil.upload(item, req, "shop");
                    if (url != null && !url.isEmpty()) avatar = url;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }

        try (Connection conn = DBUtil.getConnection()) {
            StringBuilder sql = new StringBuilder("UPDATE shop SET shop_name = ?, shop_category = ?, description = ?");
            if (!avatar.isEmpty()) sql.append(", avatar = ?");
            sql.append(" WHERE user_id = ?");
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            ps.setString(1, shopName); ps.setString(2, shopCategory); ps.setString(3, description);
            if (!avatar.isEmpty()) ps.setString(4, avatar);
            ps.setLong(avatar.isEmpty() ? 4 : 5, userId);
            ps.executeUpdate();
            resp.sendRedirect(req.getContextPath() + "/shop/info/view?msg=updated");
        } catch (Exception e) {
            e.printStackTrace(); req.setAttribute("error", "更新失败");
            req.getRequestDispatcher("/shop/shop_info.jsp").forward(req, resp);
        }
    }
}

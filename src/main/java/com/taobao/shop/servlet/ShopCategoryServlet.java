package com.taobao.shop.servlet;

import com.taobao.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/shop/category/*")
public class ShopCategoryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps0 = conn.prepareStatement("SELECT id FROM shop WHERE user_id = ?");
            ps0.setLong(1, userId); ResultSet rs0 = ps0.executeQuery();
            long shopId = rs0.next() ? rs0.getLong("id") : 0;
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM category WHERE shop_id = ? ORDER BY sort_order");
            ps.setLong(1, shopId); ResultSet rs = ps.executeQuery();
            List<String[]> categories = new ArrayList<>();
            while (rs.next()) {
                categories.add(new String[]{String.valueOf(rs.getLong("id")), rs.getString("name"),
                    String.valueOf(rs.getLong("parent_id")), String.valueOf(rs.getInt("sort_order")),
                    String.valueOf(rs.getInt("status"))});
            }
            req.setAttribute("categories", categories); req.setAttribute("shopId", shopId);
            req.getRequestDispatcher("/shop/category_list.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace(); req.setAttribute("error", "查询分类失败");
            req.getRequestDispatcher("/shop/category_list.jsp").forward(req, resp);
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        long shopId = Long.parseLong(req.getParameter("shopId"));
        try (Connection conn = DBUtil.getConnection()) {
            switch (action != null ? action : "") {
                case "add":
                    PreparedStatement ps = conn.prepareStatement("INSERT INTO category (shop_id, name, parent_id, sort_order) VALUES (?, ?, ?, ?)");
                    ps.setLong(1, shopId); ps.setString(2, req.getParameter("name"));
                    ps.setLong(3, Long.parseLong(req.getParameter("parentId")));
                    ps.setInt(4, Integer.parseInt(req.getParameter("sortOrder")));
                    ps.executeUpdate(); break;
                case "update":
                    PreparedStatement ps2 = conn.prepareStatement("UPDATE category SET name = ?, sort_order = ?, status = ? WHERE id = ?");
                    ps2.setString(1, req.getParameter("name"));
                    ps2.setInt(2, Integer.parseInt(req.getParameter("sortOrder")));
                    ps2.setInt(3, Integer.parseInt(req.getParameter("status")));
                    ps2.setLong(4, Long.parseLong(req.getParameter("id")));
                    ps2.executeUpdate(); break;
                case "delete":
                    PreparedStatement ps3 = conn.prepareStatement("DELETE FROM category WHERE id = ? AND shop_id = ?");
                    ps3.setLong(1, Long.parseLong(req.getParameter("id"))); ps3.setLong(2, shopId);
                    ps3.executeUpdate(); break;
            }
            resp.sendRedirect(req.getContextPath() + "/shop/category/list");
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }
}

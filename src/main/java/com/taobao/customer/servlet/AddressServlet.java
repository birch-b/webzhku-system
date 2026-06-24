package com.taobao.customer.servlet;

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

@WebServlet("/customer/address/*")
public class AddressServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM address WHERE user_id = ? ORDER BY is_default DESC");
            ps.setLong(1, userId); ResultSet rs = ps.executeQuery();
            List<String[]> addresses = new ArrayList<>();
            while (rs.next()) {
                addresses.add(new String[]{String.valueOf(rs.getLong("id")), rs.getString("receiver_name"),
                    rs.getString("phone"),
                    rs.getString("province") + rs.getString("city") + rs.getString("district") + rs.getString("detail"),
                    String.valueOf(rs.getInt("is_default"))});
            }
            req.setAttribute("addresses", addresses);
            req.getRequestDispatcher("/customer/address_list.jsp").forward(req, resp);
        } catch (Exception e) { e.printStackTrace(); req.getRequestDispatcher("/customer/address_list.jsp").forward(req, resp); }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Long userId = (Long) req.getSession().getAttribute("userId");
        String action = req.getParameter("action");
        try (Connection conn = DBUtil.getConnection()) {
            switch (action != null ? action : "") {
                case "add":
                    PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO address (user_id, receiver_name, phone, province, city, district, detail, is_default) VALUES (?,?,?,?,?,?,?,?)");
                    ps.setLong(1, userId); ps.setString(2, req.getParameter("receiverName"));
                    ps.setString(3, req.getParameter("phone")); ps.setString(4, req.getParameter("province"));
                    ps.setString(5, req.getParameter("city")); ps.setString(6, req.getParameter("district"));
                    ps.setString(7, req.getParameter("detail"));
                    int isDefault = "1".equals(req.getParameter("isDefault")) ? 1 : 0;
                    ps.setInt(8, isDefault); ps.executeUpdate();
                    if (isDefault == 1) {
                        PreparedStatement ps2 = conn.prepareStatement("UPDATE address SET is_default=0 WHERE user_id=? AND id!=LAST_INSERT_ID()");
                        ps2.setLong(1, userId); ps2.executeUpdate();
                    }
                    break;
                case "setDefault":
                    PreparedStatement ps3 = conn.prepareStatement("UPDATE address SET is_default=0 WHERE user_id=?");
                    ps3.setLong(1, userId); ps3.executeUpdate();
                    PreparedStatement ps4 = conn.prepareStatement("UPDATE address SET is_default=1 WHERE id=? AND user_id=?");
                    ps4.setLong(1, Long.parseLong(req.getParameter("id"))); ps4.setLong(2, userId); ps4.executeUpdate();
                    break;
                case "delete":
                    PreparedStatement ps5 = conn.prepareStatement("DELETE FROM address WHERE id=? AND user_id=?");
                    ps5.setLong(1, Long.parseLong(req.getParameter("id"))); ps5.setLong(2, userId); ps5.executeUpdate();
                    break;
            }
            resp.sendRedirect(req.getContextPath() + "/customer/address/list");
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }
}

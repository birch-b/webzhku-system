package com.taobao.customer.servlet;

import com.taobao.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

/**
 * 收货地址管理Servlet
 * 处理地址的增删改查、设为默认
 */
@WebServlet("/address/*")
public class AddressServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/list";
        switch (pathInfo) {
            case "/list":
                listAddresses(req, resp);
                break;
            case "/edit":
                showEditForm(req, resp);
                break;
            default:
                listAddresses(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        if (action == null) action = "";
        switch (action) {
            case "add":
                addAddress(req, resp);
                break;
            case "edit":
                editAddress(req, resp);
                break;
            case "delete":
                deleteAddress(req, resp);
                break;
            case "setDefault":
                setDefault(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/address/list");
        }
    }

    /**
     * 地址列表
     */
    private void listAddresses(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, receiver_name, phone, province, city, district, detail, is_default FROM address WHERE user_id = ? ORDER BY is_default DESC, create_time DESC");
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            java.util.List<String[]> addresses = new java.util.ArrayList<>();
            while (rs.next()) {
                addresses.add(new String[]{
                        String.valueOf(rs.getLong("id")),
                        rs.getString("receiver_name"),
                        rs.getString("phone"),
                        rs.getString("province") + rs.getString("city") + rs.getString("district") + rs.getString("detail"),
                        String.valueOf(rs.getInt("is_default")),
                        rs.getString("province"),
                        rs.getString("city"),
                        rs.getString("district"),
                        rs.getString("detail")
                });
            }
            req.setAttribute("addresses", addresses);
            req.getRequestDispatcher("/customer/address_list.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "加载地址列表失败");
            req.getRequestDispatcher("/customer/address_list.jsp").forward(req, resp);
        }
    }

    /**
     * 显示编辑表单（同时加载地址列表）
     */
    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/address/list");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            // 先加载地址列表
            PreparedStatement ps0 = conn.prepareStatement(
                    "SELECT id, receiver_name, phone, province, city, district, detail, is_default FROM address WHERE user_id = ? ORDER BY is_default DESC, create_time DESC");
            ps0.setLong(1, userId);
            ResultSet rs0 = ps0.executeQuery();
            java.util.List<String[]> addresses = new java.util.ArrayList<>();
            while (rs0.next()) {
                addresses.add(new String[]{
                        String.valueOf(rs0.getLong("id")),
                        rs0.getString("receiver_name"),
                        rs0.getString("phone"),
                        rs0.getString("province") + rs0.getString("city") + rs0.getString("district") + rs0.getString("detail"),
                        String.valueOf(rs0.getInt("is_default")),
                        rs0.getString("province"),
                        rs0.getString("city"),
                        rs0.getString("district"),
                        rs0.getString("detail")
                });
            }
            req.setAttribute("addresses", addresses);

            // 加载要编辑的地址
            long addrId = Long.parseLong(idParam);
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM address WHERE id = ? AND user_id = ?");
            ps.setLong(1, addrId);
            ps.setLong(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                req.setAttribute("addrId", rs.getLong("id"));
                req.setAttribute("receiverName", rs.getString("receiver_name"));
                req.setAttribute("phone", rs.getString("phone"));
                req.setAttribute("province", rs.getString("province"));
                req.setAttribute("city", rs.getString("city"));
                req.setAttribute("district", rs.getString("district"));
                req.setAttribute("detail", rs.getString("detail"));
                req.setAttribute("isDefault", rs.getInt("is_default"));
                req.setAttribute("editMode", true);
            }
            req.getRequestDispatcher("/customer/address_list.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/address/list");
        }
    }

    /**
     * 新增地址
     */
    private void addAddress(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        String receiverName = req.getParameter("receiverName");
        String phone = req.getParameter("phone");
        String province = req.getParameter("province");
        String city = req.getParameter("city");
        String district = req.getParameter("district");
        String detail = req.getParameter("detail");
        int isDefault = "1".equals(req.getParameter("isDefault")) ? 1 : 0;

        if (receiverName == null || receiverName.trim().isEmpty() ||
                phone == null || phone.trim().isEmpty() ||
                detail == null || detail.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/address/list?error=empty");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 如果设为默认，先清除其他默认
                if (isDefault == 1) {
                    PreparedStatement ps0 = conn.prepareStatement("UPDATE address SET is_default = 0 WHERE user_id = ? AND is_default = 1");
                    ps0.setLong(1, userId);
                    ps0.executeUpdate();
                }
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO address (user_id, receiver_name, phone, province, city, district, detail, is_default) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
                ps.setLong(1, userId);
                ps.setString(2, receiverName);
                ps.setString(3, phone);
                ps.setString(4, province);
                ps.setString(5, city);
                ps.setString(6, district);
                ps.setString(7, detail);
                ps.setInt(8, isDefault);
                ps.executeUpdate();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
            resp.sendRedirect(req.getContextPath() + "/address/list?msg=added");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/address/list?error=fail");
        }
    }

    /**
     * 编辑地址
     */
    private void editAddress(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        long addrId = Long.parseLong(req.getParameter("id"));
        String receiverName = req.getParameter("receiverName");
        String phone = req.getParameter("phone");
        String province = req.getParameter("province");
        String city = req.getParameter("city");
        String district = req.getParameter("district");
        String detail = req.getParameter("detail");
        int isDefault = "1".equals(req.getParameter("isDefault")) ? 1 : 0;

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (isDefault == 1) {
                    PreparedStatement ps0 = conn.prepareStatement("UPDATE address SET is_default = 0 WHERE user_id = ? AND is_default = 1");
                    ps0.setLong(1, userId);
                    ps0.executeUpdate();
                }
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE address SET receiver_name=?, phone=?, province=?, city=?, district=?, detail=?, is_default=? WHERE id=? AND user_id=?");
                ps.setString(1, receiverName);
                ps.setString(2, phone);
                ps.setString(3, province);
                ps.setString(4, city);
                ps.setString(5, district);
                ps.setString(6, detail);
                ps.setInt(7, isDefault);
                ps.setLong(8, addrId);
                ps.setLong(9, userId);
                ps.executeUpdate();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
            resp.sendRedirect(req.getContextPath() + "/address/list?msg=updated");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/address/list?error=fail");
        }
    }

    /**
     * 删除地址
     */
    private void deleteAddress(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        long addrId = Long.parseLong(req.getParameter("id"));
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM address WHERE id = ? AND user_id = ?");
            ps.setLong(1, addrId);
            ps.setLong(2, userId);
            ps.executeUpdate();
            resp.sendRedirect(req.getContextPath() + "/address/list?msg=deleted");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/address/list?error=fail");
        }
    }

    /**
     * 设为默认地址
     */
    private void setDefault(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        long addrId = Long.parseLong(req.getParameter("id"));
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 先清除所有默认
                PreparedStatement ps0 = conn.prepareStatement("UPDATE address SET is_default = 0 WHERE user_id = ?");
                ps0.setLong(1, userId);
                ps0.executeUpdate();
                // 设置指定地址为默认
                PreparedStatement ps1 = conn.prepareStatement("UPDATE address SET is_default = 1 WHERE id = ? AND user_id = ?");
                ps1.setLong(1, addrId);
                ps1.setLong(2, userId);
                ps1.executeUpdate();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
            resp.sendRedirect(req.getContextPath() + "/address/list?msg=defaultSet");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/address/list?error=fail");
        }
    }
}

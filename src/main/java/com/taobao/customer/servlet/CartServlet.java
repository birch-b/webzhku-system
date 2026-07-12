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

@WebServlet("/cart/*")
public class CartServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null) {
            switch (pathInfo) {
                case "/delete": deleteCart(req, resp); return;
                case "/selectAll": selectAll(req, resp); return;
                case "/toggle": toggleSelect(req, resp); return;
            }
        }
        Long userId = (Long) req.getSession().getAttribute("userId");
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT ci.*, p.name, p.price, p.cover_image, p.stock, p.status AS pstatus FROM cart_item ci LEFT JOIN product p ON ci.product_id = p.id WHERE ci.user_id = ? ORDER BY ci.create_time DESC");
            ps.setLong(1, userId); ResultSet rs = ps.executeQuery();
            List<String[]> cartItems = new ArrayList<>(); double totalAmount = 0;
            while (rs.next()) {
                int qty = rs.getInt("quantity"); double price = rs.getDouble("price");
                int selected = rs.getInt("selected");
                if (selected == 1) totalAmount += qty * price;
                cartItems.add(new String[]{String.valueOf(rs.getLong("id")), String.valueOf(rs.getLong("product_id")),
                        rs.getString("name"), rs.getString("price"), String.valueOf(qty),
                        String.valueOf(qty * price), rs.getString("cover_image"), String.valueOf(selected),
                        String.valueOf(rs.getInt("stock")), String.valueOf(rs.getInt("pstatus"))});
            }
            req.setAttribute("cartItems", cartItems); req.setAttribute("totalAmount", totalAmount);
            req.getRequestDispatcher("/cart.jsp").forward(req, resp);
        } catch (Exception e) { e.printStackTrace(); req.getRequestDispatcher("/cart.jsp").forward(req, resp); }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        switch (pathInfo != null ? pathInfo : "") {
            case "/add": addToCart(req, resp); break;
            case "/update": updateCart(req, resp); break;
            case "/delete": deleteCart(req, resp); break;
            case "/selectAll": selectAll(req, resp); break;
            case "/toggle": toggleSelect(req, resp); break;
            default: doGet(req, resp);
        }
    }

    private void addToCart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        long productId = Long.parseLong(req.getParameter("productId"));
        int quantity = 1;
        try { quantity = Integer.parseInt(req.getParameter("quantity")); } catch (Exception e) {}
        try (Connection conn = DBUtil.getConnection()) {
            // 检查商品状态和库存
            PreparedStatement psCheck = conn.prepareStatement("SELECT status, stock FROM product WHERE id = ?");
            psCheck.setLong(1, productId); ResultSet rsCheck = psCheck.executeQuery();
            if (rsCheck.next()) {
                if (rsCheck.getInt("status") != 1) {
                    resp.sendRedirect(req.getContextPath() + "/product/detail?id=" + productId + "&msg=offshelf");
                    return;
                }
                if (quantity > rsCheck.getInt("stock")) quantity = rsCheck.getInt("stock");
            }
            PreparedStatement ps1 = conn.prepareStatement("SELECT id, quantity FROM cart_item WHERE user_id=? AND product_id=?");
            ps1.setLong(1, userId); ps1.setLong(2, productId); ResultSet rs = ps1.executeQuery();
            if (rs.next()) {
                PreparedStatement ps2 = conn.prepareStatement("UPDATE cart_item SET quantity = quantity + ? WHERE id = ?");
                ps2.setInt(1, quantity); ps2.setLong(2, rs.getLong("id")); ps2.executeUpdate();
            } else {
                PreparedStatement ps3 = conn.prepareStatement(
                        "INSERT INTO cart_item (user_id, product_id, quantity, selected, create_time) VALUES (?, ?, ?, 1, NOW())");
                ps3.setLong(1, userId); ps3.setLong(2, productId); ps3.setInt(3, quantity); ps3.executeUpdate();
            }
            resp.sendRedirect(req.getContextPath() + "/cart/list");
        } catch (Exception e) { e.printStackTrace(); resp.sendRedirect(req.getContextPath() + "/cart/list"); }
    }

    private void updateCart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long cartId = Long.parseLong(req.getParameter("id"));
        int quantity = Integer.parseInt(req.getParameter("quantity"));
        try (Connection conn = DBUtil.getConnection()) {
            if (quantity <= 0) {
                PreparedStatement ps = conn.prepareStatement("DELETE FROM cart_item WHERE id = ?");
                ps.setLong(1, cartId); ps.executeUpdate();
            } else {
                PreparedStatement ps = conn.prepareStatement("UPDATE cart_item SET quantity = ? WHERE id = ?");
                ps.setInt(1, quantity); ps.setLong(2, cartId); ps.executeUpdate();
            }
            resp.sendRedirect(req.getContextPath() + "/cart/list");
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }

    private void deleteCart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long cartId = Long.parseLong(req.getParameter("id"));
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM cart_item WHERE id = ?");
            ps.setLong(1, cartId); ps.executeUpdate();
            resp.sendRedirect(req.getContextPath() + "/cart/list");
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }

    private void selectAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        int selected = Integer.parseInt(req.getParameter("selected"));
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE cart_item SET selected = ? WHERE user_id = ?");
            ps.setInt(1, selected); ps.setLong(2, userId); ps.executeUpdate();
            resp.sendRedirect(req.getContextPath() + "/cart/list");
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }

    private void toggleSelect(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long cartId = Long.parseLong(req.getParameter("id"));
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE cart_item SET selected = IF(selected=1, 0, 1) WHERE id = ?");
            ps.setLong(1, cartId); ps.executeUpdate();
            resp.sendRedirect(req.getContextPath() + "/cart/list");
        } catch (Exception e) { e.printStackTrace(); resp.sendError(500); }
    }
}

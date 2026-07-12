package com.taobao.servlet.customer.servlet;

import com.taobao.service.AddressService;
import com.taobao.service.impl.AddressServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 收货地址管理Servlet
 * 仅做请求分发、参数接收、权限判断、调用业务层、页面跳转，无JDBC/SQL
 */
@WebServlet("/address/*")
public class AddressServlet extends HttpServlet {
    private final AddressService addressService = new AddressServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/list";
        }
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

    // 地址列表
    private void listAddresses(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        try {
            List<Map<String, Object>> addresses = addressService.listAddressByUserId(userId);
            req.setAttribute("addresses", addresses);
        } catch (RuntimeException e) {
            e.printStackTrace();
            req.setAttribute("error", "加载地址列表失败");
        }
        req.getRequestDispatcher("/customer/address_list.jsp").forward(req, resp);
    }

    // 编辑回显页面
    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/address/list");
            return;
        }
        try {
            List<Map<String, Object>> addresses = addressService.listAddressByUserId(userId);
            req.setAttribute("addresses", addresses);

            long addrId = Long.parseLong(idParam);
            Map<String, Object> addr = addressService.getAddressByIdAndUserId(addrId, userId);
            if (addr != null) {
                req.setAttribute("addrId", addr.get("id"));
                req.setAttribute("receiverName", addr.get("receiverName"));
                req.setAttribute("phone", addr.get("phone"));
                req.setAttribute("province", addr.get("province"));
                req.setAttribute("city", addr.get("city"));
                req.setAttribute("district", addr.get("district"));
                req.setAttribute("detail", addr.get("detail"));
                req.setAttribute("isDefault", addr.get("isDefault"));
                req.setAttribute("editMode", true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/address/list");
            return;
        }
        req.getRequestDispatcher("/customer/address_list.jsp").forward(req, resp);
    }

    // 新增地址
    private void addAddress(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Long userId = (Long) session.getAttribute("userId");
        String receiverName = req.getParameter("receiverName");
        String phone = req.getParameter("phone");
        String province = req.getParameter("province");
        String city = req.getParameter("city");
        String district = req.getParameter("district");
        String detail = req.getParameter("detail");
        int isDefault = "1".equals(req.getParameter("isDefault")) ? 1 : 0;

        if (receiverName == null || receiverName.trim().isEmpty()
                || phone == null || phone.trim().isEmpty()
                || detail == null || detail.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/address/list?error=empty");
            return;
        }
        try {
            addressService.addAddress(userId, receiverName, phone, province, city, district, detail, isDefault);
            resp.sendRedirect(req.getContextPath() + "/address/list?msg=added");
        } catch (RuntimeException e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/address/list?error=fail");
        }
    }

    // 修改地址
    private void editAddress(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Long userId = (Long) session.getAttribute("userId");
        long addrId = Long.parseLong(req.getParameter("id"));
        String receiverName = req.getParameter("receiverName");
        String phone = req.getParameter("phone");
        String province = req.getParameter("province");
        String city = req.getParameter("city");
        String district = req.getParameter("district");
        String detail = req.getParameter("detail");
        int isDefault = "1".equals(req.getParameter("isDefault")) ? 1 : 0;
        try {
            addressService.updateAddress(addrId, userId, receiverName, phone, province, city, district, detail, isDefault);
            resp.sendRedirect(req.getContextPath() + "/address/list?msg=updated");
        } catch (RuntimeException e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/address/list?error=fail");
        }
    }

    // 删除地址
    private void deleteAddress(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Long userId = (Long) session.getAttribute("userId");
        long addrId = Long.parseLong(req.getParameter("id"));
        try {
            addressService.deleteAddress(addrId, userId);
            resp.sendRedirect(req.getContextPath() + "/address/list?msg=deleted");
        } catch (RuntimeException e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/address/list?error=fail");
        }
    }

    // 设置默认地址
    private void setDefault(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Long userId = (Long) session.getAttribute("userId");
        long addrId = Long.parseLong(req.getParameter("id"));
        try {
            addressService.setDefaultAddress(addrId, userId);
            resp.sendRedirect(req.getContextPath() + "/address/list?msg=defaultSet");
        } catch (RuntimeException e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/address/list?error=fail");
        }
    }
}
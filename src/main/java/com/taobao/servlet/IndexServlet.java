package com.taobao.servlet;

import com.taobao.service.AnnouncementService;
import com.taobao.service.CategoryService;
import com.taobao.service.ProductService;
import com.taobao.service.ShopService;
import com.taobao.service.impl.AnnouncementServiceImpl;
import com.taobao.service.impl.CategoryServiceImpl;
import com.taobao.service.impl.ProductServiceImpl;
import com.taobao.service.impl.ShopServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 首页数据加载Servlet
 * 仅接收参数、调用业务层、转发页面，无任何SQL与数据库操作
 */
@WebServlet("/index")
public class IndexServlet extends HttpServlet {
    private final AnnouncementService announcementService = new AnnouncementServiceImpl();
    private final CategoryService categoryService = new CategoryServiceImpl();
    private final ProductService productService = new ProductServiceImpl();
    private final ShopService shopService = new ShopServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String categoryName = req.getParameter("category");

        try {
            List<Map<String, Object>> announcements = announcementService.listPublishedAnnouncement();
            req.setAttribute("announcements", announcements);

            List<Map<String, Object>> categories = categoryService.listRootCategory();
            req.setAttribute("categories", categories);

            List<Map<String, Object>> products = productService.listProductByCategory(categoryName);
            req.setAttribute("products", products);
            req.setAttribute("selectedCategory", categoryName);

            String role = (String) req.getSession().getAttribute("userRole");
            if ("shopkeeper".equals(role)) {
                Long userId = (Long) req.getSession().getAttribute("userId");
                String shopName = shopService.getShopNameByOwnerId(userId);
                req.setAttribute("shopName", shopName);
            }

        } catch (RuntimeException e) {
            e.printStackTrace();
            req.setAttribute("errorMsg", "首页数据加载异常");
        }

        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}

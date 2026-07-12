package com.taobao.servlet;

import com.taobao.service.AnnouncementService;
import com.taobao.service.CategoryService;
import com.taobao.service.ProductService;
import com.taobao.service.impl.AnnouncementServiceImpl;
import com.taobao.service.impl.CategoryServiceImpl;
import com.taobao.service.impl.ProductServiceImpl;

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
    // 注入三层业务对象
    private final AnnouncementService announcementService = new AnnouncementServiceImpl();
    private final CategoryService categoryService = new CategoryServiceImpl();
    private final ProductService productService = new ProductServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String categoryName = req.getParameter("category");

        try {
            // 1. 调用业务层获取公告
            List<Map<String, Object>> announcements = announcementService.listPublishedAnnouncement();
            req.setAttribute("announcements", announcements);

            // 2. 调用业务层获取分类
            List<Map<String, Object>> categories = categoryService.listRootCategory();
            req.setAttribute("categories", categories);

            // 3. 调用业务层获取商品
            List<Map<String, Object>> products = productService.listProductByCategory(categoryName);
            req.setAttribute("products", products);
            req.setAttribute("selectedCategory", categoryName);
        } catch (RuntimeException e) {
            e.printStackTrace();
            // 统一异常处理，可传递错误提示到页面
            req.setAttribute("errorMsg", "首页数据加载异常");
        }

        // 页面转发
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
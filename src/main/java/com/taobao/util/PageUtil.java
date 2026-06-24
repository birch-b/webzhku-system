package com.taobao.util;

import javax.servlet.http.HttpServletRequest;

public class PageUtil {
    public static int getPage(HttpServletRequest req) {
        try { return Integer.parseInt(req.getParameter("page")); } catch (Exception e) { return 1; }
    }
    public static int getPageSize(HttpServletRequest req) {
        try { return Integer.parseInt(req.getParameter("pageSize")); } catch (Exception e) { return 20; }
    }
}

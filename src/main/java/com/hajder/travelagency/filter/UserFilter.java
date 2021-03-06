package com.hajder.travelagency.filter;

import com.hajder.travelagency.entity.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.hajder.travelagency.filter.UserFilter.ADMIN_PATH;
import static com.hajder.travelagency.filter.UserFilter.SUPER_ADMIN_PATH;
import static com.hajder.travelagency.filter.UserFilter.USER_PATH;

/**
 * Filtering /admin/* and /super-admin/* paths for unauthorized entries.
 * Created by Piotr on 19.12.2016.
 * @see javax.servlet.Filter
 * @author Piotr Hajder
 */
@WebFilter(filterName = "UserFilter", urlPatterns = { ADMIN_PATH + "/*", SUPER_ADMIN_PATH + "/*", USER_PATH + "/*" })
public class UserFilter implements Filter {
    static final String ADMIN_PATH = "/admin";
    static final String SUPER_ADMIN_PATH = "/super-admin";
    static final String USER_PATH = "/user";
    private static final String ADMIN_LOGIN = "/admin-login.xhtml";
    private static final String USER_LOGIN = "/user-login.xhtml";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();

        User user = (session != null) ? (User) session.getAttribute("user") : null;
        String ctx = request.getRequestURI();

        if(user != null) {
            if(ctx.startsWith(USER_PATH) && !user.isAdmin()) {
                filterChain.doFilter(request, response);
            } else if(ctx.startsWith(SUPER_ADMIN_PATH) ? user.isSuperAdmin() : user.isAdmin()) {
                filterChain.doFilter(request, response);
            } else {
                response.sendError(403);
            }
        } else {
            String path = ctx.startsWith(ADMIN_PATH) ? ADMIN_LOGIN : USER_LOGIN;
            response.sendRedirect(request.getContextPath() + path);
        }
    }

    @Override
    public void destroy() {
    }
}


package com.hotel;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Controls access to protected pages by checking the administrator session.
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * Allows a request when an administrator is logged in.
     * Otherwise, redirects the browser to the login page.
     *
     * @param request the incoming HTTP request
     * @param response the HTTP response to be sent
     * @param handler the handler selected for the request
     * @return true if access is allowed; false if the request is redirected
     * @throws Exception if request processing fails
     */
    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        // Avoid creating a new session just to check authentication.
        HttpSession session = request.getSession(false);

        if (session != null
                && session.getAttribute("loggedInAdmin") != null) {
            return true;
        }

        response.sendRedirect(request.getContextPath() + "/login");
        return false;
    }
}

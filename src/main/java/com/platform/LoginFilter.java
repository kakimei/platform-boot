package com.platform;

import com.platform.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@Component
@WebFilter(urlPatterns = "/platform/*", filterName = "loginFilter")
@Order(2)
public class LoginFilter implements Filter {

	private static final String LOGIN_URI = "/platform/user/login";
	private static final String REGISTER_URI = "/platform/user/register";
	@Autowired
	private UserService userService;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
		throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)servletRequest;
		if(REGISTER_URI.equals(request.getRequestURI()) || LOGIN_URI.equals(request.getRequestURI())){
			filterChain.doFilter(servletRequest, servletResponse);
			return;
		}
		String token = request.getParameter("token");
		String user = userService.check(token);
		if(StringUtils.isNotBlank(user)){
			request.setAttribute("user", user);
			filterChain.doFilter(servletRequest, servletResponse);
		}else{
			throw new ServletException("user login failed or expired.");
		}
	}

	@Override
	public void destroy() {

	}
}
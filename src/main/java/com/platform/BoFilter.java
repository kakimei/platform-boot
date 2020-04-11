package com.platform;

import com.platform.bo.userinfo.service.BoUserService;
import com.platform.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@WebFilter(urlPatterns = "/bo/*", filterName = "boFilter")
@Order(3)
public class BoFilter implements Filter {

	private static final String LOGIN_URI = "/platform/bo/user/login";

	private BoUserService boUserService;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
		boUserService = applicationContext.getBean(BoUserService.class);
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
		throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) servletResponse;
//		response.setHeader("Access-Control-Allow-Origin", "*");
		HttpServletRequest request = (HttpServletRequest)servletRequest;
		if(LOGIN_URI.equals(request.getRequestURI())){
			filterChain.doFilter(servletRequest, servletResponse);
			return;
		}
		String boUserToken = request.getParameter("boUserToken");
		String boUserName = request.getParameter("boUser");
		String boUser = boUserService.check(boUserToken, boUserName);
		if(StringUtils.isNotBlank(boUser)){
			request.setAttribute("boUser", boUser);
			filterChain.doFilter(servletRequest, servletResponse);
		}else{
			throw new ServletException("bo user login failed or expired.");
		}
	}

	@Override
	public void destroy() {

	}
}

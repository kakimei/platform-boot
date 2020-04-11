package com.platform;

import com.platform.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@WebFilter(urlPatterns = "/*", filterName = "loginFilter")
@Order(2)
public class LoginFilter implements Filter {

	private static final String LOGIN_URI = "/platform/user/login";
	private static final String REGISTER_URI = "/platform/user/register";
	private static final String WX_TOKEN = "/platform/weixin/userInfo/get";
	private static final String BO_URI = "/platform/bo";

	private UserService userService;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
		userService = applicationContext.getBean(UserService.class);
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
		throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)servletRequest;
		if(REGISTER_URI.equals(request.getRequestURI()) ||
			LOGIN_URI.equals(request.getRequestURI()) ||
			WX_TOKEN.equals(request.getRequestURI()) ||
			request.getRequestURI().startsWith(BO_URI)){
			filterChain.doFilter(servletRequest, servletResponse);
			return;
		}
		log.info("------------request url------------:"+request.getRequestURL().toString());
		log.info("------------parameters------------:");
		Map<String, String[]> parameterMap = servletRequest.getParameterMap();
		for(Map.Entry<String, String[]> entry : parameterMap.entrySet()){
			log.info("{} : {}", entry.getKey(), entry.getValue());
		}
		log.info("------------parameters end------------:");
		String openId = request.getParameter("openid");
		log.info("------------openId--------------:"+openId);
		String user = userService.check(openId);
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

package com.italia.municipality.lakesebu.controller;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(filterName = "AuthFilter", urlPatterns={"*.xhtml"})
public class AuthorizationFilter implements Filter{

	public AuthorizationFilter(){}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException{}
	
	@Override
	public void doFilter(ServletRequest request, 
			ServletResponse response, 
			FilterChain chain) throws IOException, ServletException{
		try{
			HttpServletRequest reqt = (HttpServletRequest)request;
			HttpServletResponse resp = (HttpServletResponse) response;
			HttpSession session = reqt.getSession(false);
			
			String reqURI = reqt.getRequestURI();
			if(reqURI.indexOf("/portal.xhtml")>=0 || reqURI.indexOf("/login.xhtml")>=0
					|| reqURI.indexOf("/loginda.xhtml")>=0 
					|| reqURI.indexOf("/loginlic.xhtml")>=0
					|| reqURI.indexOf("/logingso.xhtml")>=0
					|| reqURI.indexOf("/dilg.xhtml")>=0 
							|| reqURI.indexOf("/loginac.xhtml")>=0
									|| reqURI.indexOf("/citizenreg.xhtml")>=0
											|| reqURI.indexOf("/mobile.xhtml")>=0
													|| reqURI.indexOf("/loginper.xhtml")>=0
					|| (session != null && session.getAttribute("username") !=null)
					|| reqURI.indexOf("/public/")>=0
					|| reqURI.contains("javax.faces.resource")){
				chain.doFilter(request, response);
			}else{
				resp.sendRedirect(reqt.getContextPath() + "/marxmind/portal.xhtml");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void destroy(){}
}

/*
 * Created on 2005-4-5
 */
package com.pabula.fw.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author dekn
 */
public class CharacterEncodingFilter implements Filter {
	private String encoding = null;
	protected FilterConfig filterConfig = null;

	public void init(FilterConfig filterConfig) throws ServletException {
		//System.err.println("=========== CharactherEncodingFilter loaded ==========");
		this.filterConfig = filterConfig;
	}

	public void doFilter(ServletRequest request, ServletResponse response,
						 FilterChain chain) throws IOException, ServletException {
		//System.err.println("============ CharachterEncodingFilter ==============");

		encoding = getUserEncoding(); // 当前用户的编码方式，是用户配置的一个值
		if (encoding == null) {
			encoding = request.getCharacterEncoding(); // 浏览器返回的编码方式
		}
		if (encoding == null) {
			encoding = filterConfig.getInitParameter("encoding"); // 应用程序的编码方式
		}
		if (encoding != null) {
			//System.err.println("--------------------------------");
			request.setCharacterEncoding(encoding); // 这是servlet
			// 2.3新加的方法，专门在设置request编码方式
			//response.setContentType("\"text/html;charset=gb2312\""); // 设置response编码方式
		}


		String urlSearchStr = ((HttpServletRequest)request).getQueryString();

		if(null != urlSearchStr && !urlSearchStr.trim().equals("")){
			if(!URLCheck(urlSearchStr)){
				System.err.println("URL中包含非法字符，请求失败  " + urlSearchStr);
				response.setCharacterEncoding(getUserEncoding());
				response.getOutputStream().println("URL中包含非法字符，请求失败\r\n");
				response.getOutputStream().println("Copyright 2004 - 2005 www.CMS4J.com，All Rights Reserved");
				return;
			}
		}




		chain.doFilter(request, response);

	}

	/**
	 * 检测URL中的非法字符
	 * @param checkStr
	 * @return
	 */
	public boolean URLCheck(String checkStr){
		boolean isOK = true;

		if(checkStr.indexOf("'") > -1 ||
				checkStr.indexOf("\"") > -1 ||
				checkStr.indexOf("$") > -1 ||
				checkStr.indexOf(">") > -1 ||
				checkStr.indexOf("<") > -1 ||
				checkStr.indexOf("(") > -1 ||
				checkStr.indexOf(")") > -1 ||
				checkStr.indexOf("+") > -1){
			isOK = false;
		}

		return isOK;
	}


	public void destroy() {
		this.encoding = null;
		this.filterConfig = null;
	}

	private String getUserEncoding() {
		return "GBK";
	}


}

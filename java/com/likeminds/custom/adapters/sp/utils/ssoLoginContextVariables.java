package com.likeminds.custom.adapters.sp.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class ssoLoginContextVariables {

	private Cookie[] cookies;
	private String requestUrl = null;
	private String cancelUrl = null;
	private String errText = null;
	private String Path = null;
	private String LangCode = null;

	public ssoLoginContextVariables(HttpServletRequest request) {
		cookies = request.getCookies();
		requestUrl = request.getParameter("requestUrl");
		cancelUrl = request.getParameter("cancelUrl");
		LangCode = request.getParameter("langCode");

		errText = request.getParameter("errText");
		Path = request.getPathTranslated();
	}

	public String getLangCode() {
		return LangCode;
	}

	public Cookie[] getCookies() {
		return cookies;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public String getCancelUrl() {
		return cancelUrl;
	}

	public String getErrText() {
		return errText;
	}

	public String getPath() {
		return Path;
	}

}

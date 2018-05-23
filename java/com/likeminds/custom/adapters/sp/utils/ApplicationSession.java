package com.likeminds.custom.adapters.sp.utils;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.cache.LoadingCache;

import oracle.apps.fnd.common.LangInfo;
import oracle.apps.fnd.ext.common.AppsSession;
import oracle.apps.fnd.ext.common.CookieStatus;
import oracle.apps.fnd.ext.common.State;

public class ApplicationSession {
	private String StatusMessage = null;
	private HttpServletRequest req;
	private HttpServletResponse res;
	private String icxCookieName = null;
	private String icxCookieValue = null;
	private CookieStatus SSOStatus = CookieStatus.VALID;
	private ssoLoginContextVariables contextVars = null;
	private State state;
	private EBSInstance Ebsserv;
	private LoadingCache<String, String> profile = EBSServerProfile.getLoadingCache();
	private static List LangsInstalled;

	public void setCurrentState(State state) {
		this.state = state;
	}

	public State getCurrentState() {
		return state;
	}

	public ApplicationSession(ssoLoginContextVariables ssoContext, HttpServletRequest request,
			HttpServletResponse response) {
		req = request;
		res = response;
		contextVars = ssoContext;
	}

	public AppsSession getSession(EBSInstance Ebsserv) {
		this.Ebsserv = Ebsserv;
		AppsSession appsSession = null;
		icxCookieName = Ebsserv.getICXCookieName();
		icxCookieValue = getCookieValue(icxCookieName);
		CookieStatus ICXCookieStatus = CookieStatus.INVALID;
		if (isCookieValid(icxCookieValue)) {
			ICXCookieStatus = AppsSession.getAppsSessionStatus(Ebsserv.getConnection(), icxCookieValue,
					(String) Ebsserv.getGuestUserName());
			appsSession = new AppsSession(Ebsserv.getConnection(), Ebsserv.getApplicationServerID(), icxCookieValue);
		}
		if (appsSession != null) {
			appsSession.setCurrentState(State.getInstance(ICXCookieStatus, SSOStatus));
		}
		return appsSession;
	}

	private String getCookieValue(String cookieName) {
		return ApplicationSession.getCookieValue(contextVars.getCookies(), cookieName);
	}

	private boolean isCookieValid(String CookieValue) {
		return !(CookieValue == null || CookieValue.equals("-1") || CookieValue.equals(""));
	}

	public static String getCookieValue(Cookie[] cookies, String cookieName) {
		if (cookies == null || cookieName == null) {
			return null;
		}
		String CookieValue = null;
		if (cookies != null) {
			for (int i = 0; i < cookies.length; ++i) {
				if (!cookieName.equals(cookies[i].getName()))
					continue;
				CookieValue = cookies[i].getValue();
			}
		}
		return CookieValue;
	}

	public void ssologin(String ssoUserName, String ssoGuid) throws ExecutionException {

		// Fetching EBS profile for system & this user.

		res.setHeader("Cache-Control", "no-cache");
		res.setHeader("Pragma", "no-cache");
		res.setDateHeader("Expires", 0);
		this.killCookie("EBSAuthCookie");

		String LocalLogin = profile.get(EBSSQLConstants.CONST_LocalLogin);
		System.out.println("Local Login :" + LocalLogin);
		String MaintenanceMode = profile.get(EBSSQLConstants.CONST_MaintenanceMode);

		if ("LOCAL".equals(LocalLogin) || "Y".equals(MaintenanceMode)) {
			StatusMessage = "Unable to Perform SSO, Please Check EBS server Mode";
			return;
		}
		// Find linked Accounts for the GUID

		LinkedAccounts linkedUser = new LinkedAccounts(Ebsserv.getConnection(), ssoGuid);
		String username = linkedUser.getLinkedUsername();
		if (username == null) {
			System.out.println("No Linked Users or linked user Status is " + linkedUser.getException());
			// Proceed to Link Auto-Link the user from SSO.
			String AutoLinking = profile.get(EBSSQLConstants.CONST_AutoLink);
			if (AutoLinking != null && ("Y".equals(AutoLinking) || "CREATE_LINK".equals(AutoLinking))) {
				// User Preferences is set not to allow Auto Linking.
				boolean isUserLinkable = linkedUser.isUserLinkable(Ebsserv.getConnection(), ssoUserName);
				if (isUserLinkable) {
					linkedUser.createLink(Ebsserv.getConnection(), ssoUserName, ssoGuid);
					linkedUser = new LinkedAccounts(Ebsserv.getConnection(), ssoGuid);
					username = linkedUser.getLinkedUsername();
					// Checking if the user is linked and able to be fetched
				}
			}
			if (username == null) {
				// Proceed to Manually Link the user
				// Create a Guest Session to login

				// perform redirect to the Manual linking URL
				/*
				 * String string4 = linkedUser.getLinkingURL(Ebsserv.getConnection(),
				 * ssoUserName); if (logger.isLoggable(Level.FINE)) {
				 * logger.fine("autoLinkEnabled=Y block- no hasLinkableAppsUser --sending to " +
				 * string4); } httpServletResponse.sendRedirect(string4);
				 */
				return;
			}

		}
		String AutoLinking = profile.get(EBSSQLConstants.CONST_AutoLink);
		System.out.println("Auto Linking :" + AutoLinking);

	}

	private void createCookie(String cookieName, String cookieValue, String Path, String Domain, boolean HTTPOnly,
			boolean Secure, int MaxAge) {
		// To replace existing Cookie
		Cookie cookie = readCookie(cookieName);
		if (cookie != null) {
			cookie.setValue(cookieValue);
			cookie.setMaxAge(MaxAge);
		} else {
			cookie = new Cookie(cookieName, cookieValue);
			cookie.setPath(Path);
			cookie.setMaxAge(MaxAge);
			cookie.setHttpOnly(HTTPOnly);
			cookie.setSecure(Secure);
		}
		res.addCookie(cookie);
	}

	private void killCookie(String cookieName) {
		Cookie cookie = readCookie(cookieName);
		if (cookie != null) {
			cookie.setMaxAge(0);
			res.addCookie(cookie);
		} else {
			System.out.println("Unable to find cookie in Request");
		}
	}

	public Cookie readCookie(String cookieName) {
		Cookie[] cookies = contextVars.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				if (cookieName.equals(cookie.getName())) {
					return cookie;
				}
			}
		}
		return null;
	}

	private AppsSession createGuestSession() {
		/*
		 * String string =
		 * OAMLanguageCookie.selectLanguage(appsHttpServletRequestWrapper, null, true);
		 * boolean bl = this.createNewSession("GUEST", string); if (!bl) { AppsSession
		 * appsSession = null; return appsSession; } AppsSession appsSession =
		 * (AppsSession)this.getAppsSession();
		 * this.addSessionCookie(httpServletResponse); if
		 * (logger.isLoggable(Level.FINE)) { logger.fine("Created GUEST session :" +
		 * this.getAppsSession().getUserName() + " " +
		 * this.getAppsSession().getSessionId() + " langCode=" + string); } AppsSession
		 * appsSession2 = appsSession; return appsSession2; finally { try {
		 * appsHttpServletRequestWrapper.server.releaseConnection(); } catch (Exception
		 * exception) {} }
		 */
		return null;
	}

	private AppsSession createSession(String username, String Language) {

		return null;
	}

	private String getLanguageCode(String ssoUsername) {
		String LangCode = null;

		// Check if EBSAuthOL cookie is present. (Use that if set)

		LangCode = readCookie("EBSAuthOL").getValue();
		if (LangCode != null && LangCode.length() > 0) {
			killCookie("EBSAuthOL");
			return LangCode;
		}

		// Get from Parameter (LangCode)
		LangCode = contextVars.getLangCode();
		if (LangCode != null && LangCode.length() > 0)
			return LangCode;

		// Use language from Browser (if not )
		String browserLanguage = req.getHeader("Accept-Language");
		LangCode = matchLanginstalled(browserLanguage);
		if (LangCode != null && LangCode.length() > 0)
			return LangCode;

		// Get Language from User Profile

		// Get Language from Site

		// Get Language from Base install.

		List<LangInfo> langs = ebsLangs.getInstance().getLangs();
		if (langs == null) {
			ebsLangs.getInstance().setLangs(Ebsserv.getEBSInstance().getInstalledLangInfos());
		}
		return LangCode;
	}

	private String matchLanginstalled(String browserLanguage) {

		if (browserLanguage == null) {
			return null;
		}
		String topMatchLanguage = null;
		float currentFloat = 0;
		String[] browserLangs = browserLanguage.split(",");
		for (int i = 0; i < browserLangs.length; ++i) {
			float f;
			String[] lanugage = browserLangs[i].split(";");
			String string5 = lanugage[0];
			if (lanugage.length > 1) {
				try {
					if (lanugage[1].startsWith("q"))
						lanugage[1] = lanugage[1].split("=")[1];
					f = Float.parseFloat(lanugage[1]);
				} catch (NumberFormatException numberFormatException) {
					f = 1.0f;
				}
			} else {
				f = 1.0f;
			}

			if (!EBSSQLConstants.html2OracleLangMapping.containsKey(string5))
				continue;
			String langcode = EBSSQLConstants.html2OracleLangMapping.get(string5);
			// Is the language installed on EBS ?

			if (!Ebsserv.getEBSInstance().isLangInstalled(langcode) || currentFloat > f)
				continue;
			topMatchLanguage = langcode;
			currentFloat = f;
		}
		return topMatchLanguage;
	}

	public static void main(String[] args) {
		String lang = ApplicationSession.matchLanginstalled("zh,en;q=0.9,af;q=0.8,an;q=0.7");
		System.out.println(lang);
	}
}

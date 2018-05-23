package com.likeminds.custom.adapters.sp;

import javax.sql.DataSource;

public class EBSAdapterConfigData {

	private String LogoutURL;
	private String PfBase;
	private DataSource JdbcStore;
	private String ApplicationID;
	private String RegisteredContext;
	private String CookieDomain;

	public String getLogoutURL() {
		return LogoutURL;
	}

	public void setLogoutURL(String logoutURL) {
		LogoutURL = logoutURL;
	}

	public String getPfBase() {
		return PfBase;
	}

	public void setPfBase(String pfBase) {
		PfBase = pfBase;
	}

	public DataSource getJdbcStore() {
		return JdbcStore;
	}

	public void setJdbcStore(DataSource jdbcStore) {
		JdbcStore = jdbcStore;
	}

	public String getApplicationID() {
		return ApplicationID;
	}

	public void setApplicationID(String applicationID) {
		ApplicationID = applicationID;
	}

	public String getRegisteredContext() {
		return RegisteredContext;
	}

	public void setRegisteredContext(String registeredContext) {
		RegisteredContext = registeredContext;
	}

	public String getCookieDomain() {
		return CookieDomain;
	}

	public void setCookieDomain(String cookieDomain) {
		CookieDomain = cookieDomain;
	}

}

package com.likeminds.custom.adapters.sp;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sourceid.saml20.adapter.AuthnAdapterDescriptor;
import org.sourceid.saml20.adapter.AuthnAdapterException;
import org.sourceid.saml20.adapter.attribute.AttributeValue;
import org.sourceid.saml20.adapter.conf.Configuration;
import org.sourceid.saml20.adapter.gui.AdapterConfigurationGuiDescriptor;
import org.sourceid.saml20.adapter.sp.authn.SpAuthenticationAdapter;
import org.sourceid.saml20.adapter.sp.authn.SsoContext;
import org.sourceid.saml20.adapter.state.SessionStateSupport;
import org.sourceid.websso.servlet.adapter.HandlerRegistry;

import com.google.common.cache.LoadingCache;
import com.likeminds.custom.adapters.sp.utils.ApplicationSession;
import com.likeminds.custom.adapters.sp.utils.EBSInstance;
import com.likeminds.custom.adapters.sp.utils.EBSSQLConstants;
import com.likeminds.custom.adapters.sp.utils.EBSServerProfile;
import com.likeminds.custom.adapters.sp.utils.ssoLoginContextVariables;
import com.likeminds.custom.adapters.sp.webapp.handler.EBSSSOLoginAuthenticationHandler;

import oracle.apps.fnd.ext.common.AppsSession;
import oracle.apps.fnd.ext.common.State;
import oracle.jdbc.OracleConnection;

public class EBSAuthnAdapter implements SpAuthenticationAdapter {
	private static boolean initialized;
	private static EBSSSOLoginAuthenticationHandler EBSSSOLogin;
	public static final String ATTR_NAME_USERID = "userId";

	private static final String ADAPTER_NAME = "EBS Authentication Adapter 1.0";
	private static final String ADAPTER_DESCRIPTION = "This Adapter leverages EBS SDK to create an Application session with EBS and redirect for SSO ";
	public static final String ENDPOINT_FOR_SSOLOGIN = "/ebsauthn/ssologin";
	public static final String ENDPOINT_FOR_MOBILE_LOGIN = "/ebsauthn/login";

	private String SESSION_KEY = "SSOLOGIN_CONTEXT:SESSION:";

	private EBSSpAdapterConfigurator EBSSpAdapterConfig = new EBSSpAdapterConfigurator();
	private EBSAdapterConfigData ConfigData;
	protected AuthnAdapterDescriptor adapterDescriptor;

	public EBSAuthnAdapter() {
		HashSet attrNames = new HashSet();
		attrNames.add("Username");
		attrNames.add("Guid");
		AdapterConfigurationGuiDescriptor adapterConfGuiDesc = this.EBSSpAdapterConfig
				.getAdapterConfigurationGuiDescriptor(ADAPTER_DESCRIPTION);
		this.adapterDescriptor = new AuthnAdapterDescriptor(this, ADAPTER_NAME, attrNames, true, adapterConfGuiDesc);

	}

	public Serializable createAuthN(SsoContext ssoContext, HttpServletRequest request, HttpServletResponse response,
			String resumePath) throws AuthnAdapterException, IOException {
		SessionStateSupport sessionStateSupport = new SessionStateSupport();
		Map subjectAttributes = ssoContext.getSubjectAttrs();
		String USERNAME = ((AttributeValue) subjectAttributes.get("Username")).getValue();
		String GUID = ((AttributeValue) subjectAttributes.get("Guid")).getValue();

		if (USERNAME != null && USERNAME.length() != 0 && GUID != null && GUID.length() != 0) {
			ssoLoginContextVariables ctxVars = (ssoLoginContextVariables) sessionStateSupport.getAttribute(SESSION_KEY,
					request, response);

			try {
				ApplicationSession appSession = new ApplicationSession(ctxVars, request, response);
				OracleConnection conn = ConfigData.getJdbcStore().getConnection()
						.unwrap(oracle.jdbc.OracleConnection.class);
				AppsSession session = appSession.getSession(new EBSInstance(conn, ConfigData.getApplicationID()));
				if (session == null) {
					appSession.setCurrentState(State.VALID_SSO_INVALID_ICX);
				}
				switch (appSession.getCurrentState()) {
				case VALID_SSO_INVALID_ICX:
					appSession.ssologin(USERNAME, GUID);
					break;
				case VALID_SSO_EXPIRED_ICX:
					break;
				case VALID_SSO_EXPIRED_GUEST:
					break;
				case VALID_SSO_VALID_ICX:
					break;
				case INVALID_SSO_EXPIRED_GUEST:
					break;
				case INVALID_SSO_EXPIRED_ICX:
					break;
				case INVALID_SSO_INVALID_ICX:
					break;
				case INVALID_SSO_VALID_GUEST:
					break;
				case INVALID_SSO_VALID_ICX:
					break;
				case UNKNOWN_SSO_EXPIRED_GUEST:
					break;
				case UNKNOWN_SSO_EXPIRED_ICX:
					break;
				case UNKNOWN_SSO_INVALID_ICX:
					break;
				case UNKNOWN_SSO_VALID_GUEST:
					break;
				case UNKNOWN_SSO_VALID_ICX:
					break;
				case VALID_SSO_VALID_GUEST:
					break;
				default:
					break;
				}

				System.out.println(session.getCurrentState());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new AuthnAdapterException("SQL Exception Connecting with EBS DB");
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		} else {
			throw new AuthnAdapterException("No Subject available from SSO. Authentication Failed.");
		}
	}

	public AuthnAdapterDescriptor getAdapterDescriptor() {
		return this.adapterDescriptor;
	}

	static synchronized void initHandlers() {
		if (!initialized) {
			EBSSSOLogin = new EBSSSOLoginAuthenticationHandler();
			HandlerRegistry.registerHandler("/ebsauth/ssologin", EBSSSOLogin);
			initialized = true;
		}

	}

	static {
		initHandlers();
	}

	@Override
	public void configure(Configuration config) {
		// TODO Auto-generated method stub
		try {
			ConfigData = EBSSpAdapterConfig.configureData(config);
			EBSServerProfile.setDataStore(ConfigData.getJdbcStore());
			LoadingCache<String, String> profile = EBSServerProfile.getLoadingCache();
			try {
				profile.getAll(EBSSQLConstants.preloaded_profiles);
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Failed to Execute Prefetch");
			}
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean logoutAuthN(Serializable arg0, HttpServletRequest arg1, HttpServletResponse arg2, String arg3)
			throws AuthnAdapterException, IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String lookupLocalUserId(HttpServletRequest arg0, HttpServletResponse arg1, String arg2, String arg3)
			throws AuthnAdapterException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	private void redirectToTarget(HttpServletRequest request, HttpServletResponse response, String ssoTarget,
			String resumePath) throws AuthnAdapterException {
		try {
			StringBuffer url = new StringBuffer(ssoTarget);
			if (url == null || url.length() == 0) {
				throw new AuthnAdapterException("Unable to find the URL to redirect");
			}
			response.sendRedirect(url.toString());
		} catch (IOException ioe) {
			throw new AuthnAdapterException("Unable to redirect", (Throwable) ioe);
		}
	}
}

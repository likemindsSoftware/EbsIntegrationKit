package com.likeminds.custom.adapters.sp;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.sourceid.saml20.adapter.conf.Configuration;
import org.sourceid.saml20.adapter.gui.AdapterConfigurationGuiDescriptor;
import org.sourceid.saml20.adapter.gui.FieldDescriptor;
import org.sourceid.saml20.adapter.gui.JdbcDatastoreFieldDescriptor;
import org.sourceid.saml20.adapter.gui.TextFieldDescriptor;
import org.sourceid.saml20.adapter.gui.validation.FieldValidator;
import org.sourceid.saml20.adapter.gui.validation.impl.HttpURLValidator;
import org.sourceid.saml20.adapter.gui.validation.impl.RequiredFieldValidator;
import org.sourceid.saml20.domain.JdbcDataSource;
import org.sourceid.saml20.domain.mgmt.DataSourceManager;
import org.sourceid.saml20.domain.mgmt.MgmtFactory;

public class EBSSpAdapterConfigurator {
	private static final RequiredFieldValidator REQUIRED_VALIDATOR = new RequiredFieldValidator();
	private static final HttpURLValidator HTTP_URL_VALIDATOR = new HttpURLValidator();

	public static final String FIELD_LOGOUT = "EBS Logout URL";
	public static final String FIELD_PFBASE = "PF Base URL";
	public static final String FIELD_JDBC_STORE = "EBS DB Store";
	public static final String FIELD_APPLICATION_ID = "Application ID";
	public static final String FIELD_REGISTERED_CONTEXT_SSOLOGIN = "Agent Context for ssologin Handler";
	public static final String FIELD_EBS_COOKIE_DOMAIN = "Cookie Domain";

	public static final String DESC_LOGOUT = "Enter the absolute logout URL of EBS instance for configuring SLO";
	public static final String DESC_PFBASE = "Base url of Ping federate configured in the system config.";
	public static final String DESC_JDBC_STORE = "Oracle Database source where the system we lookup the FND table";
	public static final String DESC_APPLICATION_ID = "Application ID for EBS instance. Usually available on the DBC file or DB";
	public static final String DESC_REGISTERED_CONTEXT_SSOLOGIN = "URL context to used for registering as EBS agent on profile options. default: ebsauthn for a agent url https://<SP URL>/ext/ebsauthn/ssologin";
	public static final String DESC_EBS_COOKIE_DOMAIN = "Cookie domain where the EBS resides. ";

	private final String DEFAULT_REGISTERED_CONTEXT_SSOLOGIN = "ebsauthn";

	protected AdapterConfigurationGuiDescriptor getAdapterConfigurationGuiDescriptor(String description) {
		AdapterConfigurationGuiDescriptor adapterConfGuiDesc = new AdapterConfigurationGuiDescriptor(description);
		TextFieldDescriptor ApplicationIDField = new TextFieldDescriptor(FIELD_APPLICATION_ID, DESC_APPLICATION_ID);
		ApplicationIDField.addValidator((FieldValidator) REQUIRED_VALIDATOR);
		adapterConfGuiDesc.addField((FieldDescriptor) ApplicationIDField);

		JdbcDatastoreFieldDescriptor JDBCStoreField = new JdbcDatastoreFieldDescriptor(FIELD_JDBC_STORE,
				DESC_JDBC_STORE);
		JDBCStoreField.addValidator(REQUIRED_VALIDATOR);
		adapterConfGuiDesc.addField((FieldDescriptor) JDBCStoreField);

		TextFieldDescriptor pfBaseUrl = new TextFieldDescriptor(FIELD_PFBASE, DESC_PFBASE);
		pfBaseUrl.addValidator((FieldValidator) REQUIRED_VALIDATOR);
		pfBaseUrl.addValidator((FieldValidator) HTTP_URL_VALIDATOR);
		adapterConfGuiDesc.addField((FieldDescriptor) pfBaseUrl);

		TextFieldDescriptor logoutURLField = new TextFieldDescriptor(FIELD_LOGOUT, DESC_LOGOUT);
		logoutURLField.addValidator((FieldValidator) HTTP_URL_VALIDATOR, true);
		adapterConfGuiDesc.addField((FieldDescriptor) logoutURLField);

		TextFieldDescriptor ContextField = new TextFieldDescriptor(FIELD_REGISTERED_CONTEXT_SSOLOGIN,
				DESC_REGISTERED_CONTEXT_SSOLOGIN);
		ContextField.addValidator((FieldValidator) REQUIRED_VALIDATOR);
		ContextField.setDefaultValue(DEFAULT_REGISTERED_CONTEXT_SSOLOGIN);
		adapterConfGuiDesc.addField((FieldDescriptor) ContextField);

		TextFieldDescriptor CookieDomainField = new TextFieldDescriptor(FIELD_EBS_COOKIE_DOMAIN,
				DESC_EBS_COOKIE_DOMAIN);
		CookieDomainField.addValidator((FieldValidator) REQUIRED_VALIDATOR);
		adapterConfGuiDesc.addField((FieldDescriptor) CookieDomainField);

		return adapterConfGuiDesc;
	}

	protected EBSAdapterConfigData configureData(Configuration configuration) throws NamingException {
		EBSAdapterConfigData configData = new EBSAdapterConfigData();
		configData.setLogoutURL(configuration.getFieldValue(FIELD_LOGOUT));
		configData.setApplicationID(configuration.getFieldValue(FIELD_APPLICATION_ID));
		configData.setCookieDomain(configuration.getFieldValue(FIELD_EBS_COOKIE_DOMAIN));
		configData.setPfBase(configuration.getFieldValue(FIELD_PFBASE));
		configData.setRegisteredContext(configuration.getFieldValue(FIELD_REGISTERED_CONTEXT_SSOLOGIN));

		DataSourceManager dmgr = MgmtFactory.getDataSourceManager();
		JdbcDataSource jdbcSourece = dmgr.getJdbcDataSource(configuration.getFieldValue(FIELD_JDBC_STORE));
		InitialContext initialContext = new InitialContext();
		DataSource dataSource = (DataSource) initialContext.lookup(jdbcSourece.getJndiName());
		configData.setJdbcStore(dataSource);
		return configData;
	}
}

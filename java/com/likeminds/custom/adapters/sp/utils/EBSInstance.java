package com.likeminds.custom.adapters.sp.utils;

import java.sql.Connection;
import java.sql.SQLException;

import oracle.apps.fnd.ext.common.EBiz;

public class EBSInstance {
	EBiz EBSServer = null;
	Connection sqlConnection = null;
	String ApplicationServerID = null;

	public EBSInstance(Connection connection, String ApplicationServerID) {
		try {
			sqlConnection = connection;
			EBSServer = new EBiz(connection, ApplicationServerID);
			this.ApplicationServerID = ApplicationServerID;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		return sqlConnection;
	}

	public EBiz getEBSInstance() {
		return EBSServer;
	}

	public String getICXCookieName() {
		return EBSServer.getIcxCookieName();
	}

	public String getApplicationServerID() {
		return ApplicationServerID;
	}

	public String getGuestUserName() {
		return EBSServer.getGuestUserName();
	}
}

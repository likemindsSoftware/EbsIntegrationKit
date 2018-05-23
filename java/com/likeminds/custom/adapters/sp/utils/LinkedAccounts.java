package com.likeminds.custom.adapters.sp.utils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import oracle.apps.fnd.ext.jdbc.utils.ListMapProcessor;
import oracle.apps.fnd.ext.jdbc.utils.QueryRunner;
import oracle.apps.fnd.ext.jdbc.utils.RSProcessor;

public class LinkedAccounts {

	private ArrayList<LinkedAccounts.User> ValidLinkedAccounts = new ArrayList<LinkedAccounts.User>();
	private static final String EXPIRED = "Account Expired";
	private static final String PENDING = "Account Pending";
	private String errText = null;
	private User linkedaccount = null;

	public String getException() {
		return errText;
	}

	public LinkedAccounts(Connection conn, String Guid) {
		QueryRunner queryRunner = new QueryRunner();
		ListMapProcessor listMapProcessor = new ListMapProcessor();
		String sqlQuery = EBSSQLConstants.SQL_userstatus;
		try {
			ArrayList<Map> records = queryRunner.query((RSProcessor) listMapProcessor, conn, sqlQuery,
					new Object[] { Guid });
			if (records != null && !records.isEmpty()) {
				for (Map user : records) {
					User userObj = new User();
					Object object;
					userObj.setUserName((String) user.get("USER_NAME"));
					userObj.setIsDefault(((String) user.get("DEFAULT_USER_FLAG")).equals("Yes"));
					String flag = (String) user.get("start_date_pending_flag");
					if (flag != null && flag.equals("Y")) {
						errText = PENDING;
						continue;
					}
					flag = (String) user.get("expiry_end_date_flag");
					if (flag != null && flag.equals("Y")) {
						errText = EXPIRED;
						continue;
					}
					ValidLinkedAccounts.add(userObj);
				}
				if (ValidLinkedAccounts.size() == 0) {
					linkedaccount = null;
				} else if (ValidLinkedAccounts.size() > 1) {
					for (User appsUser : this.ValidLinkedAccounts) {
						if (!appsUser.isDefault)
							continue;
						linkedaccount = appsUser;
					}
					if (linkedaccount == null)
						linkedaccount = ValidLinkedAccounts.get(0);
				} else {
					linkedaccount = ValidLinkedAccounts.get(0);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getLinkedUsername() {
		if (linkedaccount != null)
			return linkedaccount.getUserName();
		else
			return null;
	}

	private class User {
		boolean isDefault = false;
		String UserName = null;

		public void setIsDefault(Boolean isDefault) {
			this.isDefault = isDefault;
		}

		public String getUserName() {
			return UserName;
		}

		public void setUserName(String userName) {
			UserName = userName;
		}

	}

	public static boolean isUserLinkable(Connection connection, String username) {
		// TODO Auto-generated method stub

		CallableStatement callableStatement = null;
		try {
			callableStatement = connection.prepareCall(EBSSQLConstants.SQL_ssouserlinkcheck);
			callableStatement.setString(1, username.toUpperCase());
			callableStatement.registerOutParameter(2, 12);
			callableStatement.registerOutParameter(3, 12);
			callableStatement.registerOutParameter(4, 12);
			callableStatement.executeQuery();
			String userID = callableStatement.getString(2);
			String autoLinkable = callableStatement.getString(3);
			String LocalLogin = callableStatement.getString(4);

			if (userID == null || "NO".equalsIgnoreCase(autoLinkable) || "LOCAL".equalsIgnoreCase(LocalLogin)) {
				return false;
			}
		} catch (SQLException sQLException) {
			sQLException.printStackTrace();
			System.out.println("Error Executing SQL statement ");
		}
		return true;
	}

	public static void createLink(Connection connection, String ssoUserName, String ssoGuid) {
		// TODO Auto-generated method stub
		CallableStatement callableStatement = null;
		try {
			callableStatement = connection.prepareCall(EBSSQLConstants.SQL_updateguid);
			callableStatement.setString(1, ssoGuid.toUpperCase());
			callableStatement.setString(2, ssoUserName.toUpperCase());
			callableStatement.executeQuery();
		} catch (SQLException sQLException) {
			sQLException.printStackTrace();
			System.out.println("Error Executing SQL statement ");
		}
	}

	public static String getUserProfile(Connection connection, String username, String ProfileKey) {
		CallableStatement callableStatement = null;
		try {
			callableStatement = connection.prepareCall(EBSSQLConstants.SQL_userProfile);
			callableStatement.setString(1, username.toUpperCase());
			callableStatement.setString(2, ProfileKey.toUpperCase());
			callableStatement.registerOutParameter(3, 12);
			callableStatement.executeQuery();
			String profileVales = callableStatement.getString(3);

			if (profileVales != null && !"".equalsIgnoreCase(profileVales)) {
				return profileVales;
			}
		} catch (SQLException sQLException) {
			sQLException.printStackTrace();
			System.out.println("Error Executing SQL statement ");
		}
		return null;
	}

	public static String getLinkingUrl(Connection connection, String ssoUserName) {

		return null;
	}
}

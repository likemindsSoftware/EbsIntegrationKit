package com.likeminds.custom.adapters.sp.utils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class EBSServerProfile {

	private static DataSource jdbcSource;

	public static void setDataStore(DataSource jdbcSource) {
		EBSServerProfile.jdbcSource = jdbcSource;
	}

	private static LoadingCache<String, String> profileCache;

	static {
		LoadingCache<String, String> profileCache = CacheBuilder.newBuilder().maximumSize(100)
				.expireAfterWrite(10, TimeUnit.MINUTES).build(new CacheLoader<String, String>() {
					@Override
					public String load(String profileKey) throws Exception {
						return getEBSProfileValue(profileKey);
					}
				});
	}

	public static LoadingCache<String, String> getLoadingCache() {
		return profileCache;
	}

	public static String getEBSProfileValue(String Key) {
		Connection conn;
		try {
			conn = EBSServerProfile.jdbcSource.getConnection();
			return EBSServerProfile.getProfile(Key, conn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "-1";
	}

	private static String getProfile(String ProfileName, Connection connection) throws SQLException {

		if (connection == null) {
			throw new NullPointerException("No Active Connection Passed");
		}
		CallableStatement callableStatement = null;
		try {
			String string2;
			connection.commit();
			callableStatement = connection.prepareCall(EBSSQLConstants.SQL_profile);
			callableStatement.setString(1, ProfileName);
			callableStatement.registerOutParameter(2, 12);
			callableStatement.execute();
			if ("null".equalsIgnoreCase(string2 = callableStatement.getString(2))) {
				string2 = null;
			}
			return string2;
		} catch (SQLException sQLException) {
			throw sQLException;
		} finally {
			if (callableStatement != null) {
				try {
					callableStatement.close();
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}
	}

}

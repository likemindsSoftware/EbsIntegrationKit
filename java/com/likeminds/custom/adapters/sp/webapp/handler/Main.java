package com.likeminds.custom.adapters.sp.webapp.handler;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.likeminds.custom.adapters.sp.utils.LinkedAccounts;

//import com.likeminds.custom.adapters.sp.utils.LinkedAccounts;

public class Main {
	public static void main(String[] args) throws Exception {// throws SQLException {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection("jdbc:oracle:thin:@ebs.likemindsconsulting.com:1521:VIS",
					"apps", "apps");
			LinkedAccounts accounts = new LinkedAccounts(con, "4222C8D69B87453EAC55B730F8E6F487");
			LinkedAccounts.createLink(con, "vikram", "4F03A3D7D3DFFA764D27606FF3773311");
			// LinkedAccounts accounts = new LinkedAccounts(con,
			// "4F03A3D7D3DFFA764D27606FF3773311");
			System.out.println(LinkedAccounts.getUserProfile(con, "vikram", "icx_language"));
			con.close();
			System.out.println(accounts.getLinkedUsername());

		} catch (NoSuchAlgorithmException e) {
			String eMessage = "deobfuscate: Specified algorithm cannot be found";
			throw new Exception(eMessage, e);
		} catch (IllegalBlockSizeException e) {
			String eMessage = "deobfuscate: Illegal block size";
			throw new Exception(eMessage, e);
		} catch (BadPaddingException e) {
			String eMessage = "deobfuscate: Bad padding";
			throw new Exception(eMessage, e);
		} catch (NoSuchPaddingException e) {
			String eMessage = "deobfuscate: Wrong padding";
			throw new Exception(eMessage, e);
		} catch (InvalidAlgorithmParameterException e) {
			String eMessage = "deobfuscate: Invalid algorithm parameter";
			throw new Exception(eMessage, e);
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

/*
 * **************************************************
 *  Copyright (C) 2017 Ping Identity Corporation
 *  All rights reserved.
 *
 *  The contents of this file are subject to the terms of the
 *  Ping Identity Corporation SDK Developer Guide.
 *
 *  Ping Identity Corporation
 *  1001 17th St Suite 100
 *  Denver, CO 80202
 *  303.468.2900
 *  http://www.pingidentity.com
 * ****************************************************
 */

package com.likeminds.custom.adapters.sp.webapp.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sourceid.saml20.adapter.state.SessionStateSupport;
import org.sourceid.websso.servlet.adapter.Handler;

import com.likeminds.custom.adapters.sp.utils.ssoLoginContextVariables;

public class EBSSSOLoginAuthenticationHandler implements Handler {
	public void handle(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String SESSION_KEY = "SSOLOGIN_CONTEXT:SESSION:";
		ssoLoginContextVariables loginContext = new ssoLoginContextVariables(req);
		SessionStateSupport sessionStateSupport = new SessionStateSupport();
		// SESSION_KEY += loginContext.getPath();
		sessionStateSupport.setAttribute(SESSION_KEY, loginContext, req, resp, false);
		resp.sendRedirect("https://localhost:9031/sp/startSSO.ping?IdpAdapterId=Test&SpSessionAuthnAdapterId=EBSAuth");
	}
}

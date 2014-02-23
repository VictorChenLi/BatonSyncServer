/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baton.syncserver.infrastructure.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baton.syncserver.infrastructure.database.Datastore;
import com.baton.syncserver.infrastructure.exception.ServiceException;
import com.baton.syncserver.usermanage.service.UserManageServices;
import com.baton.syncserver.usermanage.service.UserManageServicesImpl;

/**
 * Servlet that unregisters a device, whose registration id is identified by
 * {@link #PARAMETER_REG_ID}.
 * <p>
 * The client app should call this servlet everytime it receives a
 * {@code com.google.android.c2dm.intent.REGISTRATION} with an
 * {@code unregistered} extra.
 */
@SuppressWarnings("serial")
public class LoginServlet extends BaseServlet {

	private UserManageServices userManageService = new UserManageServicesImpl();

	private static final String GCM_REGID = "gcm_regid";
	private static final String EMAIL = "email";
	private static final String PASSWORD = "password";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException {
		String gcm_regid = getParameter(req, GCM_REGID);
		String email = getParameter(req, EMAIL);
		String password = getParameter(req, PASSWORD);
		try {
			userManageService.UserLogin(gcm_regid, email, password);
		} catch (ServiceException e) {
			this.setException(resp, e);
		}
		setSuccess(resp);
	}

}

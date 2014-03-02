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
 * Servlet that registers a device, whose registration id is identified by
 * {@link #PARAMETER_REG_ID}.
 *
 * <p>
 * The client app should call this servlet everytime it receives a
 * {@code com.google.android.c2dm.intent.REGISTRATION C2DM} intent without an
 * error or {@code unregistered} extra.
 */
@SuppressWarnings("serial")
public class RegisterServlet extends BaseServlet {
	
	private UserManageServices userManageService=new UserManageServicesImpl();
	
  private static final String GCM_REGID = "gcm_regid";
  private static final String NICK_NAME = "nick_name";
  private static final String EMAIL = "email";
  private static final String PASSWORD = "password";
  private static final String FIRST_NAME = "f_name";
  private static final String LAST_NAME = "l_name";
  private static final String USER_TYPE = "user_type";

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException {
    String gcm_regid = getParameter(req, GCM_REGID);
    String nick_name = getParameter(req, NICK_NAME);
    String email = getParameter(req, EMAIL);
    String password = getParameter(req, PASSWORD);
    String f_name = getParameter(req, FIRST_NAME);
    String l_name = getParameter(req, LAST_NAME);
    String user_type = getParameter(req,USER_TYPE);
//    String user_type = "Student";
    try {
		userManageService.UserRegister(gcm_regid, nick_name, email, password, f_name, l_name,user_type);
	} catch (ServiceException e) {
		this.setException(resp, e);
	} 
    setSuccess(resp);
  }

}

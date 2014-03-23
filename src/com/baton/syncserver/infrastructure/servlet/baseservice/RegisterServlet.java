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
package com.baton.syncserver.infrastructure.servlet.baseservice;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baton.publiclib.model.usermanage.UserProfile;
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

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
	  logger.info("Receive the register request");
	  super.doPost(req, resp);
    String gcm_regid = getParameter(req, UserProfile.GCMID_WEB_STR);
    String login_id = getParameter(req, UserProfile.LOGINID_WEB_STR);
    String email = getParameter(req, UserProfile.EMAIL_WEB_STR);
    String password = getParameter(req, UserProfile.PASSWORD_WEB_STR);
    String f_name = getParameter(req, UserProfile.FNAME_WEB_STR);
    String l_name = getParameter(req, UserProfile.LNAME_WEB_STR);
    String user_type = getParameter(req,UserProfile.USERTYPE_WEB_STR);
//    String user_type = "Student";
    try {
		userManageService.UserRegister(gcm_regid, login_id, email, password, f_name, l_name,user_type);
	} catch (ServiceException e) {
		e.printStackTrace();
		this.setException(resp, e);
	} 
    setSuccess(resp);
  }

}

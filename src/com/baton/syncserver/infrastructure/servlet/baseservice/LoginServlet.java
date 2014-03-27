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

import org.apache.log4j.Logger;

import com.baton.publiclib.infrastructure.exception.ServiceException;
import com.baton.publiclib.model.classmanage.ClassLesson;
import com.baton.publiclib.model.classmanage.VirtualClass;
import com.baton.publiclib.model.usermanage.UserProfile;
import com.baton.publiclib.utility.JsonHelper;
//import com.baton.syncserver.classmanage.model.VirtualClass;
import com.baton.syncserver.infrastructure.database.Datastore;
//import com.baton.syncserver.usermanage.model.UserProfile;
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

	private static Logger logger = Logger.getLogger(LoginServlet.class);
	
	private UserManageServices userManageService = new UserManageServicesImpl();

	private static final String GCM_REGID = UserProfile.GCMID_WEB_STR;
	private static final String EMAIL = UserProfile.EMAIL_WEB_STR;
	private static final String STUDENT_LOGINID = UserProfile.LOGINID_WEB_STR;
	private static final String PASSWORD = UserProfile.PASSWORD_WEB_STR;
	private static final String CLASSROOM = VirtualClass.CLASSROOM_NAME_WEB_STR;
	private static final String TEACHER_LOGINID = UserProfile.TEACHER_LOGINID_WEB_STR;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.info("Receive the login request");
		super.doPost(req, resp);
		String gcm_regid = getParameter(req, GCM_REGID);
//		String email = getParameter(req, EMAIL);
		String student_login_id = getParameter(req, STUDENT_LOGINID);
		String password = getParameter(req, PASSWORD);
		String classroom = getParameter(req, CLASSROOM);
		String teacher_login_id = getParameter(req, TEACHER_LOGINID);
		
		ClassLesson curLesson = null ;
		try {
			curLesson = userManageService.UserLogin(gcm_regid, student_login_id, password, classroom, teacher_login_id);
		} catch (ServiceException e) {
			e.printStackTrace();
			this.setException(resp, e);
		}
		setSuccess(resp,JsonHelper.serialize(curLesson));
	}

}

package com.baton.syncserver.infrastructure.servlet.baseservice;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import com.baton.publiclib.model.classmanage.VirtualClass;
import com.baton.publiclib.model.usermanage.UserProfile;
//import com.baton.syncserver.classmanage.model.VirtualClass;
import com.baton.syncserver.classmanage.service.ClassManageServices;
import com.baton.syncserver.classmanage.service.ClassManageServicesImpl;
import com.baton.syncserver.usermanage.dbAccess.UserManageDBAccess;
import com.baton.syncserver.usermanage.dbAccess.UserManageDBAccessImpl;
//import com.baton.syncserver.usermanage.model.UserProfile;
import com.baton.syncserver.usermanage.service.UserManageServices;
import com.baton.syncserver.usermanage.service.UserManageServicesImpl;

public class SwitchLessonServlet extends BaseServlet {
	private ClassManageServices classManageImpl = new ClassManageServicesImpl();
	private UserManageDBAccess userManageDBImpl = new UserManageDBAccessImpl();
	
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.info("Receive the switch lesson request");
		super.doPost(req, resp);
		String classroom_name = getParameter(req, VirtualClass.CLASSROOM_NAME_WEB_STR);
		String loginId = getParameter(req,UserProfile.LOGINID_WEB_STR);
		UserProfile user = userManageDBImpl.queryUserProfileByLoginId(loginId);
		classManageImpl.switchClassLesson(classroom_name, user.getUid());
		this.setSuccess(resp);
	}
	
}

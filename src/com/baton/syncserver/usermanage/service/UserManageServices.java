package com.baton.syncserver.usermanage.service;

import com.baton.publiclib.infrastructure.exception.ServiceException;
import com.baton.publiclib.model.classmanage.ClassLesson;

public interface UserManageServices {
	public boolean UserRegister(String gcm_regid, String nick_name,
			String email, String password, String f_name, String l_name,
			String user_type) throws ServiceException;

	public ClassLesson UserLogin(String gcm_regid, String email, String password, String class_name, String teacher_loginId)
			throws ServiceException;
}

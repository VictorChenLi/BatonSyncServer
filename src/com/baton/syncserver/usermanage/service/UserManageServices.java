package com.baton.syncserver.usermanage.service;

import com.baton.syncserver.infrastructure.exception.ServiceException;

public interface UserManageServices {
	public boolean UserRegister(String gcm_regid, String nick_name, String email, String password, String f_name, String l_name) throws ServiceException;
	
	public boolean UserLogin(String gcm_regid, String email, String password);
}

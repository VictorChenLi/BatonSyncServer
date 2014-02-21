package com.baton.syncserver.usermanage.service;

import com.baton.syncserver.infrastructure.exception.ErrorCode;
import com.baton.syncserver.infrastructure.exception.ServiceException;
import com.baton.syncserver.usermanage.dbAccess.UserManageDBAccess;
import com.baton.syncserver.usermanage.dbAccess.UserManageDBAccessImpl;
import com.baton.syncserver.usermanage.model.UserProfile;

public class UserManageServicesImpl implements UserManageServices {

	private UserManageDBAccess userManageDBImpl = new UserManageDBAccessImpl();
	
	@Override
	public boolean UserRegister(String gcm_regid, String nick_name,
			String email, String password, String f_name, String l_name) throws ServiceException {
		if(null!=userManageDBImpl.queryUserProfile(email))
			throw new ServiceException(ErrorCode.Email_Occupied_Msg,ErrorCode.Email_Occupied);
		UserProfile user = new UserProfile(gcm_regid,nick_name,email,password,f_name,l_name);
		if(false == userManageDBImpl.insertUserProfile(user))
			throw new ServiceException(ErrorCode.DB_Common_Error,ErrorCode.DB_Common_Error_Msg);
		return true;
		
	}

	@Override
	public boolean UserLogin(String gcm_regid, String email, String password) {
		// TODO Auto-generated method stub
		return false;
	}

}

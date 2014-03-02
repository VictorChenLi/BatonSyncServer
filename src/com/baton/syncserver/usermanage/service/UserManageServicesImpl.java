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
			String email, String password, String f_name, String l_name, String user_type) throws ServiceException {
		if(null!=userManageDBImpl.queryUserProfile(email))
			throw new ServiceException(ErrorCode.Email_Occupied_Msg,ErrorCode.Email_Occupied);
		UserProfile user = new UserProfile(gcm_regid,nick_name,email,password,f_name,l_name,user_type);
		if(false == userManageDBImpl.insertUserProfile(user))
			throw new ServiceException(ErrorCode.DB_Common_Error,ErrorCode.DB_Common_Error_Msg);
		return true;
		
	}

	@Override
	public boolean UserLogin(String gcm_regid, String email, String password) throws ServiceException {
		UserProfile user = userManageDBImpl.queryUserProfile(email);
		if(null == user)
			throw new ServiceException(ErrorCode.Email_Not_Exist_Msg,ErrorCode.Email_Not_Exist_Msg);
		if(!user.getPassword().equals(password))
			throw new ServiceException(ErrorCode.Password_Error_Msg,ErrorCode.Password_Error);
		if(!user.getGcm_regid().equals(gcm_regid))
		{
			user.setGcm_regid(gcm_regid);
			userManageDBImpl.updateUserProfile(user);
		}
		return true;
	}

}

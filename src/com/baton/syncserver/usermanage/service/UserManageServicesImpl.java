package com.baton.syncserver.usermanage.service;

import com.baton.publiclib.infrastructure.exception.ErrorCode;
import com.baton.publiclib.infrastructure.exception.ServiceException;
import com.baton.publiclib.model.classmanage.ClassLesson;
import com.baton.publiclib.model.usermanage.LoginSession;
import com.baton.publiclib.model.usermanage.UserProfile;
import com.baton.syncserver.classmanage.dbAccess.ClassManageDBAccess;
import com.baton.syncserver.classmanage.dbAccess.ClassManageDBAccessImpl;
import com.baton.syncserver.classmanage.service.ClassManageServices;
import com.baton.syncserver.classmanage.service.ClassManageServicesImpl;
import com.baton.syncserver.ticketmanage.service.TicketManageServices;
import com.baton.syncserver.ticketmanage.service.TicketManageServicesImpl;
import com.baton.syncserver.usermanage.dbAccess.UserManageDBAccess;
import com.baton.syncserver.usermanage.dbAccess.UserManageDBAccessImpl;
//import com.baton.syncserver.usermanage.model.UserProfile;

public class UserManageServicesImpl implements UserManageServices {

	private UserManageDBAccess userManageDBImpl = new UserManageDBAccessImpl();
	private ClassManageDBAccess classManageDBImpl = new ClassManageDBAccessImpl();
	private ClassManageServices classManageServiceImpl = new ClassManageServicesImpl();
	private TicketManageServices ticketManageServiceImpl = new TicketManageServicesImpl();
	
	@Override
	public boolean UserRegister(String gcm_regid, String nick_name,
			String email, String password, String f_name, String l_name, String user_type) throws ServiceException {
		if(null!=userManageDBImpl.queryUserProfileByEmail(email))
			throw new ServiceException(ErrorCode.Email_Occupied_Msg,ErrorCode.Email_Occupied);
		if(null!=userManageDBImpl.queryUserProfileByLoginId(nick_name))
			throw new ServiceException(ErrorCode.LoginId_Occupied_Msg,ErrorCode.LoginId_Occupied);
		UserProfile user = new UserProfile(gcm_regid,nick_name,email,password,f_name,l_name,user_type);
		if(false == userManageDBImpl.insertUserProfile(user))
			throw new ServiceException(ErrorCode.DB_Common_Error_Msg,ErrorCode.DB_Common_Error);
		return true;
		
	}

	@Override
	public ClassLesson UserLogin(String gcm_regid, String loginId, String password,String classroom, String teacher_login_id) throws ServiceException {
		UserProfile user = userManageDBImpl.queryUserProfileByLoginId(loginId);
		UserProfile teacher = userManageDBImpl.queryUserProfileByLoginId(teacher_login_id);
		
		//field check
		if(null==teacher)
			throw new ServiceException(ErrorCode.Teacher_Not_Exist_Msg,ErrorCode.Teacher_Not_Exist);
		if(null == user)
			throw new ServiceException(ErrorCode.LoginId_Not_Exist_Msg,ErrorCode.LoginId_Not_Exist);
		if(!user.getPassword().equals(password))
			throw new ServiceException(ErrorCode.Password_Error_Msg,ErrorCode.Password_Error);
		
		ClassLesson lesson=null;
		
		if(UserProfile.USERTYPE_STUDENT.equals(user.getUser_type()))
		{
			lesson = classManageDBImpl.queryCurVirtualClassLesson(teacher.getUid(), classroom);
			if(null==lesson)
				throw new ServiceException(ErrorCode.Class_Not_Exist_Msg,ErrorCode.Class_Not_Exist);
			//return lesson;
		}
		if(UserProfile.USERTYPE_TEACHER.equals(user.getUser_type()))
		{
			lesson = classManageDBImpl.queryCurVirtualClassLesson(user.getUid(), classroom);
			
			if(null==lesson)
			{
				lesson = classManageServiceImpl.createClassRoom(classroom, user.getUid());
			}
			
		}
		//TODO: gcm thing could be removed, since it's not used anymore
		if(!user.getGcm_regid().equals(gcm_regid))
		{
			user.setGcm_regid(gcm_regid);
			userManageDBImpl.updateUserProfile(user);
		}
		
		//update login_session table
		
		LoginSession ls = userManageDBImpl.queryLoginSession(user.getUid(), lesson.getLid(), gcm_regid);
		if(null == ls){
			System.out.println("no exist login session");
			//inactive other sessions under the same uid and lid
			userManageDBImpl.inactiveLoginSession(user.getUid(),lesson.getLid());
			//insert this new one
			LoginSession newLs = new LoginSession(lesson.getLid(),user.getUid(),user.getUser_type(),user.getLogin_id(),null,LoginSession.LOGIN_STATUS_ACTIVE,gcm_regid);
			userManageDBImpl.insertLoginSession(newLs);
			// send the class participation to all the student's device
			ticketManageServiceImpl.notifyAllClassParticipation(lesson.getLid());
		}else{
			System.out.println("exist login session with this gcm_id");
			//inactive other sessions under the same uid and lid
			userManageDBImpl.inactiveLoginSession(user.getUid(),lesson.getLid(),gcm_regid);
			//active this sessions
			userManageDBImpl.activeLoginSession(ls);
		}
		
		return lesson;
	}

}

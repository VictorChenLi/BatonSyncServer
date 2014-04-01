package com.baton.syncserver.usermanage.dbAccess;

import java.util.List;

import com.baton.publiclib.model.usermanage.LoginSession;
import com.baton.publiclib.model.usermanage.UserProfile;

//import com.baton.syncserver.usermanage.model.UserProfile;

public interface UserManageDBAccess {

	/*For User Profile Management*/
	public static final String SELECT_USER_PROFILE = "select * from user_profile ";

	public static final String INSERT_USER_PROFILE = "insert into user_profile(gcm_regid, login_id, email, password,f_name,l_name,user_type,created_at) "
			+ "VALUES (?,?,?,?,?,?,?,DEFAULT)";
	
	public static final String UPDATE_USER_PROFILE_BY_UID = "update user_profile set gcm_regid=?, login_id=?, email=?, password=?, f_name=?, l_name=?,user_type=? where uid=?";
	/*end for User Profile Management*/
	
	/*For User Login Session Management*/
	public static final String SELECT_LOGIN_SESSION = "select * from login_session ";
	
	public static final String INSERT_LOGIN_SESSION = "insert into login_session(lid,uid,user_type,user_login_id,login_time,session_status,gcm_regid) "
			+ "VALUES (?,?,?,?,DEFAULT,?,?)";
	
	public static final String ACTIVE_LOGIN_STATUS = "update login_session set login_time=CURRENT_TIMESTAMP, session_status='"+LoginSession.LOGIN_STATUS_ACTIVE+"' ";
	public static final String INACTIVE_LOGIN_STATUS = "update login_session set login_time=login_time, session_status='"+LoginSession.LOGIN_STATUS_INACTIVE+"' ";
	/*End for User Login Session Management*/
	
	/*For User Profile Management*/
	public UserProfile queryUserProfileByUId(int id);

	public UserProfile queryUserProfileByEmail(String email);

	public List<UserProfile> queryUserProfileList();

	public boolean insertUserProfile(UserProfile user);
	
	public boolean updateUserProfile(UserProfile user);	
	/**@deprecated*/
	public UserProfile queryUserProfileByGCM(String gcm_regid);
	
	public UserProfile queryUserProfileByLoginId(String login_id);
	/*end for User Profile Management*/
	
	
	/*For User Login Session Management*/
	public LoginSession queryLoginSession(int uid, int lid, String gcm_regid);

	public List<LoginSession> queryLoginSessionList();

	public boolean insertLoginSession(LoginSession session);
	
	/**
	 * given a session, update its status to active
	 */
	public boolean activeLoginSession(LoginSession session);	

	public boolean inactiveLoginSession(int uid, int lid);

	public boolean inactiveLoginSession(int uid, int lid, String gcm_regid);

	public List<LoginSession> queryActiveLoginSession(int lid, String userType);

	/*end for Login Session Management*/
}

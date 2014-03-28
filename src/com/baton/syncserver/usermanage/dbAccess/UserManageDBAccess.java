package com.baton.syncserver.usermanage.dbAccess;

import java.util.List;

import com.baton.publiclib.model.usermanage.UserProfile;

//import com.baton.syncserver.usermanage.model.UserProfile;

public interface UserManageDBAccess {

	public static final String SELECT_USER_PROFILE = "select * from user_profile ";

	public static final String INSERT_USER_PROFILE = "insert into user_profile(gcm_regid, login_id, email, password,f_name,l_name,user_type,created_at) "
			+ "VALUES (?,?,?,?,?,?,?,DEFAULT)";
	
	public static final String UPDATE_USER_PROFILE_BY_UID = "update user_profile set gcm_regid=?, login_id=?, email=?, password=?, f_name=?, l_name=?,user_type=? where uid=?";

	public UserProfile queryUserProfileByUId(int id);

	public UserProfile queryUserProfileByEmail(String email);

	public List<UserProfile> queryUserProfileList();

	public boolean insertUserProfile(UserProfile user);
	
	public boolean updateUserProfile(UserProfile user);
	
	/**
	 * @deprecated
	 */
	public UserProfile queryUserProfileByGCM(String gcm_regid);
	
	public UserProfile queryUserProfileByLoginId(String login_id);
}

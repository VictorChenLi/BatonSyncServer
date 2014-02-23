package com.baton.syncserver.usermanage.dbAccess;

import java.util.List;

import com.baton.syncserver.usermanage.model.UserProfile;

public interface UserManageDBAccess {

	public static final String SELECTSQL = "select * from user_profile ";

	public static final String INSERTSQL = "insert into user_profile(gcm_regid, nick_name, email, password,f_name,l_name,created_at) "
			+ "VALUES (?,?,?,?,?,?,DEFAULT)";
	
	public static final String UPDATESQL = "update user_profile set gcm_regid=?, nick_name=?, email=?, password=?, f_name=?, l_name=? where uid=?";

	public UserProfile queryUserProfile(int id);

	public UserProfile queryUserProfile(String email);

	public List<UserProfile> queryUserProfileList();

	public boolean insertUserProfile(UserProfile user);
	
	public boolean updateUserProfile(UserProfile user);
}

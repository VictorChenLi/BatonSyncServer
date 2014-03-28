package com.baton.syncserver.usermanage.dbAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.baton.publiclib.model.usermanage.UserProfile;
import com.baton.syncserver.infrastructure.database.BaseDBAccess;
import com.baton.syncserver.infrastructure.database.DTable;
//import com.baton.syncserver.usermanage.model.UserProfile;

public class UserManageDBAccessImpl implements UserManageDBAccess {
	

	@Override
	public UserProfile queryUserProfileByUId(int id) {
		String strSqlWhere = BaseDBAccess.getSqlAndWhereString(new String[]{UserProfile.UID_DB_STR},new String[]{String.valueOf(id)});
		return queryUserProfileList(UserManageDBAccess.SELECT_USER_PROFILE,strSqlWhere)!=null?queryUserProfileList(UserManageDBAccess.SELECT_USER_PROFILE,strSqlWhere).get(0):null;
	}

	@Override
	public UserProfile queryUserProfileByEmail(String email) {
		String strSqlWhere = BaseDBAccess.getSqlAndWhereString(new String[]{UserProfile.EMAIL_DB_STR},new String[]{email});
		return queryUserProfileList(UserManageDBAccess.SELECT_USER_PROFILE,strSqlWhere)!=null?queryUserProfileList(UserManageDBAccess.SELECT_USER_PROFILE,strSqlWhere).get(0):null;
	}
	
	public UserProfile queryUserProfileByGCM(String gcm_regid)
	{
		String strSqlWhere = BaseDBAccess.getSqlAndWhereString(new String[]{UserProfile.GCMID_DB_STR},new String[]{gcm_regid});
		return queryUserProfileList(UserManageDBAccess.SELECT_USER_PROFILE,strSqlWhere)!=null?queryUserProfileList(UserManageDBAccess.SELECT_USER_PROFILE,strSqlWhere).get(0):null;
	}
	
	@Override
	public List<UserProfile> queryUserProfileList()
	{
		return queryUserProfileList(UserManageDBAccess.SELECT_USER_PROFILE,"");
	}
	
	private List<UserProfile> queryUserProfileList(String strSql, String strWhere)
	{
		List<UserProfile> userList = new ArrayList<UserProfile>();
		strSql+=strWhere;
		DTable results = BaseDBAccess.getSQLResult(strSql);
		if(null==results||0==results.getRowLength())
			return null;
		for(int i =0;i<results.getRowLength();i++)
		{
			Map<String,Object> curRow = results.getRow(i);
			UserProfile user = new UserProfile(curRow);
			userList.add(user);
		}
		return userList;
	}

	@Override
	public boolean insertUserProfile(UserProfile user) {
		return BaseDBAccess.runSQL(UserManageDBAccess.INSERT_USER_PROFILE, user.getUserData());
	}

	@Override
	public boolean updateUserProfile(UserProfile user) {
		List<String> varList = user.getUserData();
		varList.add(String.valueOf(user.getUid()));
		return BaseDBAccess.runSQL(UserManageDBAccess.UPDATE_USER_PROFILE_BY_UID,varList);
	}

	@Override
	public UserProfile queryUserProfileByLoginId(String login_id) {
		String strSqlWhere = BaseDBAccess.getSqlAndWhereString(new String[]{UserProfile.LOGINID_DB_STR},new String[]{login_id});
		return queryUserProfileList(UserManageDBAccess.SELECT_USER_PROFILE,strSqlWhere)!=null?queryUserProfileList(UserManageDBAccess.SELECT_USER_PROFILE,strSqlWhere).get(0):null;
	}
	
}

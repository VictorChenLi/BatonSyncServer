package com.baton.syncserver.usermanage.dbAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.baton.syncserver.infrastructure.database.BaseDBAccess;
import com.baton.syncserver.infrastructure.database.DTable;
import com.baton.syncserver.usermanage.model.UserProfile;

public class UserManageDBAccessImpl implements UserManageDBAccess {
	

	@Override
	public UserProfile queryUserProfile(int id) {
		String strSqlWhere = BaseDBAccess.getSqlAndWhereString(new String[]{"uid"},new String[]{String.valueOf(id)});
		return queryUserProfileList(UserManageDBAccess.SELECTSQL,strSqlWhere)!=null?queryUserProfileList(UserManageDBAccess.SELECTSQL,strSqlWhere).get(0):null;
	}

	@Override
	public UserProfile queryUserProfile(String email) {
		String strSqlWhere = BaseDBAccess.getSqlAndWhereString(new String[]{"email"},new String[]{email});
		return queryUserProfileList(UserManageDBAccess.SELECTSQL,strSqlWhere)!=null?queryUserProfileList(UserManageDBAccess.SELECTSQL,strSqlWhere).get(0):null;
	}
	
	public UserProfile queryUserProfileByGCM(String gcm_regid)
	{
		String strSqlWhere = BaseDBAccess.getSqlAndWhereString(new String[]{"gcm_regid"},new String[]{gcm_regid});
		return queryUserProfileList(UserManageDBAccess.SELECTSQL,strSqlWhere)!=null?queryUserProfileList(UserManageDBAccess.SELECTSQL,strSqlWhere).get(0):null;
	}
	
	@Override
	public List<UserProfile> queryUserProfileList()
	{
		return queryUserProfileList(UserManageDBAccess.SELECTSQL,"");
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
		return BaseDBAccess.runSQL(UserManageDBAccess.INSERTSQL, user.getUserData());
	}

	@Override
	public boolean updateUserProfile(UserProfile user) {
		List<String> varList = user.getUserData();
		varList.add(String.valueOf(user.getUid()));
		return BaseDBAccess.runSQL(UserManageDBAccess.UPDATESQL,varList);
	}
	
}

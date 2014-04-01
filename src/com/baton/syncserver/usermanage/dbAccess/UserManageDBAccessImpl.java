package com.baton.syncserver.usermanage.dbAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.baton.publiclib.model.usermanage.LoginSession;
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

	@Override
	public LoginSession queryLoginSession(int uid, int lid,String gcm_regid) {
		String strSqlWhere = BaseDBAccess.getSqlAndWhereString(new String[]{LoginSession.UID_DB_STR,LoginSession.LID_DB_STR,LoginSession.GCMREGID_DB_STR},new String[]{String.valueOf(uid),String.valueOf(lid),gcm_regid});
		return queryLoginSessionList(UserManageDBAccess.SELECT_LOGIN_SESSION,strSqlWhere)!=null?queryLoginSessionList(UserManageDBAccess.SELECT_LOGIN_SESSION,strSqlWhere).get(0):null;
	}
	
	@Override
	public List<LoginSession> queryActiveLoginSession(int lid, String userType) {
		String strSqlWhere = BaseDBAccess.getSqlAndWhereString(new String[]{LoginSession.LID_DB_STR,LoginSession.USERTYPE_DB_STR,LoginSession.SESSIONSTATUS_DB_STR},new String[]{String.valueOf(lid),userType,LoginSession.LOGIN_STATUS_ACTIVE});
		return queryLoginSessionList(UserManageDBAccess.SELECT_LOGIN_SESSION,strSqlWhere);
	}
	
	private List<LoginSession> queryLoginSessionList(String strSql, String strWhere)
	{
		List<LoginSession> sessionList = new ArrayList<LoginSession>();
		strSql+=strWhere;
		DTable results = BaseDBAccess.getSQLResult(strSql);
		if(null==results||0==results.getRowLength())
			return null;
		for(int i =0;i<results.getRowLength();i++)
		{
			Map<String,Object> curRow = results.getRow(i);
			LoginSession session = new LoginSession(curRow);
			sessionList.add(session);
		}
		return sessionList;
	}

	@Override
	public List<LoginSession> queryLoginSessionList() {
		return queryLoginSessionList(UserManageDBAccess.SELECT_LOGIN_SESSION,"");
	}

	@Override
	public boolean insertLoginSession(LoginSession session) {
		return BaseDBAccess.runSQL(UserManageDBAccess.INSERT_LOGIN_SESSION, session.getLoginSessionInsertParams());
	}

	/**
	 * given a session, update its status to active
	 * @param session
	 * @return
	 */
	@Override
	public boolean activeLoginSession(LoginSession session) {
		String strSqlWhere = BaseDBAccess.getSqlAndWhereString(new String[]{LoginSession.SESSIONID_DB_STR},new String[]{String.valueOf(session.getSession_id())});
		return updateLoginSessionStatus(UserManageDBAccess.ACTIVE_LOGIN_STATUS,strSqlWhere);
	}
	
	@Override
	public boolean inactiveLoginSession(int uid, int lid) {
		String strSqlWhere = BaseDBAccess.getSqlAndWhereString(new String[]{LoginSession.UID_DB_STR,LoginSession.LID_DB_STR},new String[]{String.valueOf(uid),String.valueOf(lid)});
		return updateLoginSessionStatus(UserManageDBAccess.INACTIVE_LOGIN_STATUS,strSqlWhere);
	}

	/**
	 * inactive all login sessions under the same uid and lid with GCM ID different from the gcm_regid in parameter
	 */
	@Override
	public boolean inactiveLoginSession(int uid, int lid, String other_than_gcm_regid) {
		String strSqlWhere = BaseDBAccess.getSqlAndWhereString(new String[]{LoginSession.UID_DB_STR,LoginSession.LID_DB_STR},new String[]{String.valueOf(uid),String.valueOf(lid)});
		strSqlWhere = strSqlWhere + " and "+ LoginSession.GCMREGID_DB_STR + " <> '" + other_than_gcm_regid+"'";
		return updateLoginSessionStatus(UserManageDBAccess.INACTIVE_LOGIN_STATUS,strSqlWhere);
	}

	private boolean updateLoginSessionStatus(String strSql, String strWhere)
	{
		strSql = strSql + " "+ strWhere;
		System.out.println("updateLoginSessionStatus sql:"+ strSql);
		return BaseDBAccess.runSQL(strSql);
	}
	
}

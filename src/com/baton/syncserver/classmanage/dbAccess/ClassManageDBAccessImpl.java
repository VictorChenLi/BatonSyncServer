package com.baton.syncserver.classmanage.dbAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.baton.syncserver.classmanage.model.ClassParticipate;
import com.baton.syncserver.classmanage.model.VirtualClass;
import com.baton.syncserver.infrastructure.database.BaseDBAccess;
import com.baton.syncserver.infrastructure.database.DTable;
import com.baton.syncserver.usermanage.dbAccess.UserManageDBAccess;

public class ClassManageDBAccessImpl implements ClassManageDBAccess {

	@Override
	public void insertVirtualClass(VirtualClass vClass) {
		BaseDBAccess.runSQL(INSERTSQL, vClass.getUserData());
	}

	@Override
	public VirtualClass queryVirtualClass(int teacherId, String className) {
		String strSqlWhere = BaseDBAccess.getSqlAndWhereString(new String[]{VirtualClass.CLASSROOM_NAME_DB,VirtualClass.TEACHER_ID_DB},new String[]{className,String.valueOf(teacherId)});
		return queryVirtualClassList(UserManageDBAccess.SELECTSQL,strSqlWhere)!=null?queryVirtualClassList(UserManageDBAccess.SELECTSQL,strSqlWhere).get(0):null;
	}

	public List<VirtualClass> queryVirtualClassList(String strSql, String strWhere)
	{
		List<VirtualClass> classList = new ArrayList<VirtualClass>();
		strSql+=strWhere;
		DTable results = BaseDBAccess.getSQLResult(strSql);
		if(null==results||0==results.getRowLength())
			return null;
		for(int i =0;i<results.getRowLength();i++)
		{
			Map<String,Object> curRow = results.getRow(i);
			VirtualClass clas = new VirtualClass(curRow);
			classList.add(clas);
		}
		return classList;
	}

	@Override
	public void insertClassParticipate(int cid, int studentid) {
		List<String> colValues = new ArrayList<String>();
		colValues.add(String.valueOf(cid));
		colValues.add(String.valueOf(studentid));
		BaseDBAccess.runSQL(INSERTSQL_CP, colValues);
	}

	@Override
	public List<ClassParticipate> queryClassParticipate(int cid) {
		List<ClassParticipate> participates = new ArrayList<ClassParticipate>();
		String strSqlWhere = BaseDBAccess.getSqlAndWhereString(new String[]{"cid"},new String[]{String.valueOf(cid)});
		String strSql = ClassManageDBAccess.SELECTSQL_CP+strSqlWhere;
		DTable results = BaseDBAccess.getSQLResult(strSql);
		if(null==results||0==results.getRowLength())
			return null;
		for(int i =0;i<results.getRowLength();i++)
		{
			Map<String,Object> curRow = results.getRow(i);
			ClassParticipate cp = new ClassParticipate(curRow);
			participates.add(cp);
		}
		return participates;
	}
}

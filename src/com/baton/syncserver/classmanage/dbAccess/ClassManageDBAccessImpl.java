package com.baton.syncserver.classmanage.dbAccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;








import com.baton.publiclib.model.classmanage.ClassLesson;
import com.baton.publiclib.model.classmanage.ClassParticipate;
import com.baton.publiclib.model.classmanage.VirtualClass;
import com.baton.publiclib.model.ticketmanage.TalkTicketForDisplay;
import com.baton.publiclib.model.ticketmanage.Ticket;
import com.baton.publiclib.model.usermanage.UserProfile;
//import com.baton.syncserver.classmanage.model.ClassLesson;
//import com.baton.syncserver.classmanage.model.ClassParticipate;
//import com.baton.syncserver.classmanage.model.VirtualClass;
import com.baton.syncserver.infrastructure.database.BaseDBAccess;
import com.baton.syncserver.infrastructure.database.DTable;
import com.baton.syncserver.ticketmanage.dbAccess.TicketManageDBAccess;
import com.baton.syncserver.ticketmanage.dbAccess.TicketManageDBAccessImpl;
//import com.baton.syncserver.ticketmanage.model.Ticket;
import com.baton.syncserver.usermanage.dbAccess.UserManageDBAccess;
import com.baton.syncserver.usermanage.dbAccess.UserManageDBAccessImpl;
//import com.baton.syncserver.usermanage.model.UserProfile;

public class ClassManageDBAccessImpl implements ClassManageDBAccess {
	
	private TicketManageDBAccess ticketDBImple = new TicketManageDBAccessImpl();
	private UserManageDBAccess userDBImpl = new UserManageDBAccessImpl();

	@Override
	public void insertVirtualClass(VirtualClass vClass) {
		BaseDBAccess.runSQL(INSERTSQL_VC, vClass.getUserData());
	}

	@Override
	public VirtualClass queryVirtualClass(int teacherId, String className) {
		String strSqlWhere = BaseDBAccess.getSqlAndWhereString(new String[]{VirtualClass.CLASSROOM_NAME_DB,VirtualClass.TEACHER_ID_DB},new String[]{className,String.valueOf(teacherId)});
		return queryVirtualClassList(ClassManageDBAccess.SELECTSQL_VC,strSqlWhere)!=null?queryVirtualClassList(ClassManageDBAccess.SELECTSQL_VC,strSqlWhere).get(0):null;
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

//	@Override
//	public void insertClassParticipate(int cid, int studentid) {
//		List<String> colValues = new ArrayList<String>();
//		colValues.add(String.valueOf(cid));
//		colValues.add(String.valueOf(studentid));
//		BaseDBAccess.runSQL(INSERTSQL_CP, colValues);
//	}

	@Override
	public List<TalkTicketForDisplay> queryLessonParticipate(int lid) {
		List<Ticket> ticketList = ticketDBImple.queryTicketListByLesson(lid);
		Map<Integer,TalkTicketForDisplay> tMap = new HashMap<Integer,TalkTicketForDisplay>();
		for(Ticket ticket : ticketList)
		{
			TalkTicketForDisplay displayTicket;
			if(tMap.containsKey(ticket.getUid()))
			{
				displayTicket = tMap.get(ticket.getUid());
			}
			else
			{
				UserProfile user = userDBImpl.queryUserProfileByUId(ticket.getUid());
				displayTicket = new TalkTicketForDisplay(user.getLogin_id(),user.getUid(),lid);
			}
			if(ticket.getTicket_status().equals(Ticket.TICKETSTATUS_RAISING))
			{
				displayTicket.setParticipate_intent(ticket.getTicketContent());
				displayTicket.setStartTimeStamp(ticket.getTimeStamp());
			}
			if(ticket.getTicket_status().equals(Ticket.TICKETSTATUS_RESPOND))
				displayTicket.setResponse_times(displayTicket.getResponse_times()+1);
			displayTicket.setParticipate_times(displayTicket.getParticipate_times()+1);
			tMap.put(ticket.getUid(), displayTicket);
		}
		List<TalkTicketForDisplay> displayTicketList = new ArrayList<TalkTicketForDisplay>();
		for(Map.Entry<Integer, TalkTicketForDisplay> entry : tMap.entrySet())
		{
			displayTicketList.add(entry.getValue());
		}
		return displayTicketList;
	}

	@Override
	public ClassLesson queryCurVirtualClassLesson(int teacherId,
			String className) {
		VirtualClass vclass = this.queryVirtualClass(teacherId, className);
		if(null==vclass)
			return null;
		String strSqlWhere = BaseDBAccess.getSqlAndWhereString(new String[]{VirtualClass.CID_DB_STR,ClassLesson.LESSONSTATUS_DB_STR},new String[]{String.valueOf(vclass.getCid()),ClassLesson.LESSONSTATUS_OPEN});
		String strSql = ClassManageDBAccess.SELECTSQL_CL;
		return this.queryClassLessonList(strSql, strSqlWhere)!=null?this.queryClassLessonList(strSql, strSqlWhere).get(0):null;
	}
	
	public List<ClassLesson> queryClassLessonList(String strSql, String strWhere)
	{
		List<ClassLesson> lessonList = new ArrayList<ClassLesson>();
		strSql+=strWhere;
		DTable results = BaseDBAccess.getSQLResult(strSql);
		if(null==results||0==results.getRowLength())
			return lessonList;
		for(int i =0;i<results.getRowLength();i++)
		{
			Map<String,Object> curRow = results.getRow(i);
			ClassLesson lesson = new ClassLesson(curRow);
			lessonList.add(lesson);
		}
		return lessonList;
	}

	@Override
	public List<ClassLesson> queryClassLessonList(int teacherId, String className) {
		VirtualClass vclass = this.queryVirtualClass(teacherId, className);
		String strSqlWhere = BaseDBAccess.getSqlAndWhereString(new String[]{VirtualClass.CID_DB_STR},new String[]{String.valueOf(vclass.getCid())});
		String strSql = ClassManageDBAccess.SELECTSQL_CL + strSqlWhere;
		return this.queryClassLessonList(strSql, strSqlWhere);
	}

	@Override
	public void insertClassLesson(ClassLesson lesson) {
		BaseDBAccess.runSQL(INSERTSQL_CL, lesson.getUserData());
	}

	@Override
	public void updataClassLesson(ClassLesson lesson) {
		List<String> varList = lesson.getUserData();
		varList.add(String.valueOf(lesson.getLid()));
		BaseDBAccess.runSQL(ClassManageDBAccess.UPDATESQL_CL,varList);
	}

	@Override
	public ClassLesson queryCurVirtualClassLesson(String teacherloginId,
			String className) {
		UserProfile teacher = userDBImpl.queryUserProfileByLoginId(teacherloginId);
		return this.queryCurVirtualClassLesson(teacher.getUid(), className);
	}

	@Override
	public ClassLesson queryLessonByLid(int lid) {
		String strSqlWhere = BaseDBAccess.getSqlAndWhereString(new String[]{ClassLesson.LESSONID_DB_STR},new String[]{String.valueOf(lid)});
		String strSql = ClassManageDBAccess.SELECTSQL_CL + strSqlWhere;
		ClassLesson lesson=null;
		DTable results = BaseDBAccess.getSQLResult(strSql);
		if(null==results||0==results.getRowLength())
			return lesson;
		for(int i =0;i<results.getRowLength();i++)
		{
			Map<String,Object> curRow = results.getRow(i);
			lesson = new ClassLesson(curRow);
		}
		return lesson;
	}

	@Override
	public VirtualClass queryVirtualClass(int cid) {
		String strSqlWhere = BaseDBAccess.getSqlAndWhereString(new String[]{VirtualClass.CID_DB_STR},new String[]{String.valueOf(cid)});
		String strSql = ClassManageDBAccess.SELECTSQL_VC + strSqlWhere;
		VirtualClass vClass=null;
		DTable results = BaseDBAccess.getSQLResult(strSql);
		if(null==results||0==results.getRowLength())
			return vClass;
		for(int i =0;i<results.getRowLength();i++)
		{
			Map<String,Object> curRow = results.getRow(i);
			vClass = new VirtualClass(curRow);
		}
		return vClass;
	}
	

}

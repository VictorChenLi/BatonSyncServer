package com.baton.syncserver.classmanage.dbAccess;

import java.util.List;

import com.baton.publiclib.model.classmanage.ClassLesson;
import com.baton.publiclib.model.classmanage.ClassParticipate;
import com.baton.publiclib.model.classmanage.VirtualClass;
import com.baton.publiclib.model.ticketmanage.TalkTicketForDisplay;

//import com.baton.syncserver.classmanage.model.ClassLesson;
//import com.baton.syncserver.classmanage.model.ClassParticipate;
//import com.baton.syncserver.classmanage.model.VirtualClass;

public interface ClassManageDBAccess {
	public static final String INSERTSQL_VC = "insert into virtual_class (classroom_name,teacher_id)"
			+ " Values (?,?)";
//	public static final String INSERTSQL_CP = "insert into [class_participate] (cid,student_id)"
//			+ " Values (?,?)";
	public static final String INSERTSQL_CL = "insert into class_lesson (cid,lesson_name,lesson_description,start_time,end_time,status)"
			+ " Values (?,?,?,?,?,?)";
	public static final String SELECTSQL_VC = "select * from virtual_class ";
//	public static final String SELECTSQL_CP = "select * from class_participate";
	public static final String SELECTSQL_CL = "select * from class_lesson ";
	public static final String UPDATESQL_CL = "update class_lesson set cid=?, lesson_name=?, lesson_description=?, start_time=?, end_time=?, status=? where lid=?";

	public void insertVirtualClass(VirtualClass vClass);

	public VirtualClass queryVirtualClass(int teacherId, String className);

	public ClassLesson queryCurVirtualClassLesson(int teacherId,
			String className);
	
	public ClassLesson queryCurVirtualClassLesson(String teacherloginId,
			String className);

	public void insertClassLesson(ClassLesson lesson);

	public List<ClassLesson> queryClassLessonList(int teacherId,
			String className);

	public void updataClassLesson(ClassLesson lesson);

//	public void insertClassParticipate(int cid, int studentid);

	public List<TalkTicketForDisplay> queryLessonParticipate(int lid);
	
	public ClassLesson queryLessonByLid(int lid);
	
	public VirtualClass queryVirtualClass(int cid);
}

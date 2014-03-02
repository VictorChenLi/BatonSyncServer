package com.baton.syncserver.classmanage.dbAccess;

import java.util.List;

import com.baton.syncserver.classmanage.model.ClassParticipate;
import com.baton.syncserver.classmanage.model.VirtualClass;

public interface ClassManageDBAccess {
	public static final String INSERTSQL="insert into [virtual_class] (classroom_name,teacher_id)"
			+ " Values (?,?)";
	public static final String INSERTSQL_CP="insert into [class_participate] (cid,student_id)"
			+ " Values (?,?)";
	public static final String SELECTSQL="select * from virtual_class";
	public static final String SELECTSQL_CP="select * from class_participate";
	
	public void insertVirtualClass(VirtualClass vClass);
	
	public VirtualClass queryVirtualClass(int teacherId, String className);
	
	public void insertClassParticipate(int cid, int studentid);
	
	public List<ClassParticipate> queryClassParticipate(int cid);
}

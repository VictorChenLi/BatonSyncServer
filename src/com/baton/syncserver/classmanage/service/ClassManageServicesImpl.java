package com.baton.syncserver.classmanage.service;

import java.util.List;

import com.baton.publiclib.model.classmanage.ClassLesson;
import com.baton.publiclib.model.classmanage.ClassParticipate;
import com.baton.publiclib.model.classmanage.VirtualClass;
import com.baton.publiclib.model.ticketmanage.TalkTicketForDisplay;
import com.baton.publiclib.utility.TimeHelper;
import com.baton.syncserver.classmanage.dbAccess.ClassManageDBAccess;
import com.baton.syncserver.classmanage.dbAccess.ClassManageDBAccessImpl;
//import com.baton.syncserver.classmanage.model.ClassLesson;
//import com.baton.syncserver.classmanage.model.ClassParticipate;
//import com.baton.syncserver.classmanage.model.VirtualClass;
//import com.baton.syncserver.infrastructure.utility.TimeHelper;

public class ClassManageServicesImpl implements ClassManageServices {

	private ClassManageDBAccess classManageDBImpl = new ClassManageDBAccessImpl();
	
	
	@Override
	public ClassLesson createClassRoom(String classroom_name, int teacher_id) {

		VirtualClass vClass = classManageDBImpl.queryVirtualClass(teacher_id, classroom_name);
		if(null==vClass)
		{
			vClass = new VirtualClass(classroom_name,teacher_id);
			classManageDBImpl.insertVirtualClass(vClass);
			vClass = classManageDBImpl.queryVirtualClass(teacher_id, classroom_name);
		}
		ClassLesson lesson = new ClassLesson(0, vClass.getCid(), null, null, TimeHelper.getStrTimeFromMillis(System.currentTimeMillis()), null, ClassLesson.LESSONSTATUS_OPEN);
		classManageDBImpl.insertClassLesson(lesson);
		return classManageDBImpl.queryCurVirtualClassLesson(teacher_id, classroom_name);
	}

	@Override
	public List<TalkTicketForDisplay> queryClassParticipationData(String classroom_name, int teacher_id) {
		ClassLesson lesson = classManageDBImpl.queryCurVirtualClassLesson(teacher_id, classroom_name);
		return classManageDBImpl.queryLessonParticipate(lesson.getLid());
	}
	
	@Override
	public List<TalkTicketForDisplay> queryClassParticipationData(int lid)
	{
		return classManageDBImpl.queryLessonParticipate(lid);
	}
	@Override
	public void startClassLesson(String classroom_name, int teacher_id) {
		// TODO Auto-generated method stub
	}

	@Override
	public void endClassLesson(String classroom_name, int teacher_id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void switchClassLesson(String classroom_name, int teacher_id) {
		ClassLesson lesson = classManageDBImpl.queryCurVirtualClassLesson(teacher_id, classroom_name);
		lesson.setStatus(ClassLesson.LESSONSTATUS_CLOSE);
		lesson.setEnd_time(TimeHelper.getStrTimeFromMillis(System.currentTimeMillis()));
		classManageDBImpl.updataClassLesson(lesson);
		//create a new lesson
		lesson.setEnd_time("");
		lesson.setStart_time(TimeHelper.getStrTimeFromMillis(System.currentTimeMillis()));
		lesson.setStatus(ClassLesson.LESSONSTATUS_OPEN);
		classManageDBImpl.insertClassLesson(lesson);
		
	}

	@Override
	public void queryStudentParticipationData(int student_id, int lid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ClassLesson queryCurClassLesson(String classroom_name, int teacher_id) {
		return classManageDBImpl.queryCurVirtualClassLesson(teacher_id, classroom_name);
	}

	@Override
	public ClassLesson queryCurClassLesson(int lid) {
		
		return null;
	}

}

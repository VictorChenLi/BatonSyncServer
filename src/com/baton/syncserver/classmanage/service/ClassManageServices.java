package com.baton.syncserver.classmanage.service;

import java.util.List;

import com.baton.publiclib.model.classmanage.ClassLesson;
import com.baton.publiclib.model.classmanage.ClassParticipate;
import com.baton.publiclib.model.ticketmanage.TalkTicketForDisplay;

//import com.baton.syncserver.classmanage.model.ClassParticipate;

public interface ClassManageServices {
	
	public ClassLesson createClassRoom(String classroom_name, int teacher_id);
	
	public List<TalkTicketForDisplay> queryClassParticipationData(String classroom_name, int teacher_id);
	
	public List<TalkTicketForDisplay> queryClassParticipationData(int lid);
	
	public void queryStudentParticipationData(int student_id, int lid);
	
	public void startClassLesson(String classroom_name,int teacher_id);
	
	public void endClassLesson(String classroom_name, int teacher_id);
	
	public void switchClassLesson(String classroom_name, int teacher_id);
	
	public ClassLesson queryCurClassLesson(String classroom_name, int teacher_id);
	
	public ClassLesson queryCurClassLesson(int lid);

}

package com.baton.syncserver.ticketmanage.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;

import com.baton.publiclib.infrastructure.exception.ErrorCode;
import com.baton.publiclib.infrastructure.exception.ServiceException;
import com.baton.publiclib.model.classmanage.ClassLesson;
import com.baton.publiclib.model.classmanage.ClassParticipate;
import com.baton.publiclib.model.classmanage.VirtualClass;
import com.baton.publiclib.model.ticketmanage.Ticket;
import com.baton.publiclib.model.usermanage.LoginSession;
import com.baton.publiclib.model.usermanage.UserProfile;
import com.baton.publiclib.utility.JsonHelper;
import com.baton.publiclib.utility.TimeHelper;
import com.baton.syncserver.classmanage.dbAccess.ClassManageDBAccess;
import com.baton.syncserver.classmanage.dbAccess.ClassManageDBAccessImpl;
import com.baton.syncserver.infrastructure.utility.GCMHelper;
//import com.baton.syncserver.usermanage.model.UserProfile;
import com.baton.syncserver.ticketmanage.dbAccess.TicketManageDBAccess;
import com.baton.syncserver.ticketmanage.dbAccess.TicketManageDBAccessImpl;
//import com.baton.syncserver.ticketmanage.model.Ticket;
import com.baton.syncserver.usermanage.dbAccess.UserManageDBAccess;
import com.baton.syncserver.usermanage.dbAccess.UserManageDBAccessImpl;

public class TicketManageServicesImpl implements TicketManageServices {

	private UserManageDBAccess userManageDBImpl = new UserManageDBAccessImpl();
	private TicketManageDBAccess ticketManageDBImpl = new TicketManageDBAccessImpl();
	private ClassManageDBAccess classManageDBImpl = new ClassManageDBAccessImpl();
	protected final Logger logger = Logger.getLogger(getClass().getName());
	
	GCMHelper gcmHelper;
	
	@Override
	public Boolean SendTicket(ServletConfig config, String loginId, String gcm_regid, String ticketType,
			String ticketContent, String timeStamp, int lid) throws ServiceException {
		UserProfile student = userManageDBImpl.queryUserProfileByLoginId(loginId);
		
		/**20140330 modified by fiona save or update ticket in database*/
		List<Ticket> currentRaisingTicketList = ticketManageDBImpl.queryTicket(lid, student.getUid(), new String[]{Ticket.TICKETSTATUS_RAISING});
		Ticket curTicket = null;
		String serverTime = TimeHelper.getStrTimeFromMillis(System.currentTimeMillis());
		if(currentRaisingTicketList==null || currentRaisingTicketList.size()==0){
			/**modified by fiona 2014/4/8 - time stamp the ticket with the server time*/
			//no raising ticket under this student in this lesson, insert this ticket
			curTicket = new Ticket(student.getUid(),ticketType,ticketContent,serverTime,lid,Ticket.TICKETSTATUS_RAISING);
			ticketManageDBImpl.insertTicket(curTicket);
		}else{
			//get this ticket and update it
			curTicket = currentRaisingTicketList.get(0);
			curTicket.setTicketContent(ticketContent);
			ticketManageDBImpl.updateTicket(curTicket);
		}
		/**end modify*/
		
		/**send the ticket to teacher device*/
		gcmHelper = new GCMHelper(config);
		
		List<String> teacherDevice = new ArrayList<String>();
		ClassLesson lesson = classManageDBImpl.queryLessonByLid(lid);
		if(null==lesson)
			throw new ServiceException(ErrorCode.Class_Not_Exist_Msg,ErrorCode.Class_Not_Exist);
		VirtualClass vClass = classManageDBImpl.queryVirtualClass(lesson.getCid());
		UserProfile teacher = userManageDBImpl.queryUserProfileByUId(vClass.getTeacher_id());
		if(null!=teacher)
			teacherDevice.add(teacher.getGcm_regid());
		else
			throw new ServiceException(ErrorCode.Teacher_Not_Exist_Msg,ErrorCode.Teacher_Not_Exist);
		Map<String,String> contentMapForTeacher = new HashMap<String,String>();
		contentMapForTeacher.put(Ticket.UID_WEB_STR, String.valueOf(student.getUid()));
		contentMapForTeacher.put(UserProfile.LOGINID_WEB_STR, student.getLogin_id());
		contentMapForTeacher.put(Ticket.TICKETTYPE_WEB_STR, ticketType);
		contentMapForTeacher.put(Ticket.TICKETCONTENT_WEB_STR, ticketContent);
		contentMapForTeacher.put(Ticket.TIMESTAMP_WEB_STR, serverTime);
		gcmHelper.asyncSend(teacherDevice, contentMapForTeacher);
		
		
		/**20140331 modified by fiona
		 * send all of the corrent tickets to every student devices*/
		//get the active student logins in this lesson
//		Map<String,String> contentMapForStudent = new HashMap<String,String>();
//		List<LoginSession> lsList = userManageDBImpl.queryActiveLoginSession(lid, UserProfile.USERTYPE_STUDENT);
//		if(lsList!=null && lsList.size()!=0){
//			List<String> studentDevices = new ArrayList<String>();
//			for(LoginSession ls : lsList){
//				studentDevices.add(ls.getGcm_regid());
//			}
//			//get all the current un-discarded tickets in this lesson 
//			List<Ticket> currentTickets = ticketManageDBImpl.queryTicket(lid, new String[]{Ticket.TICKETSTATUS_RAISING,Ticket.TICKETSTATUS_RESPOND});
//			List<Ticket> resTickets = new ArrayList<Ticket>();
//			Hashtable<Integer,Boolean> multiTicket = new Hashtable<Integer,Boolean>();
//			for(Ticket t:currentTickets){
//				//one ticket for one uid, no need to put multiple tickets of one uid now
//				if(!multiTicket.containsKey(t.getUid())){
//					resTickets.add(t);
//					multiTicket.put(t.getUid(), true);
//				}
//			}
//			if(resTickets!=null && resTickets.size()!=0){
//				//if there is at least one un-discarded ticket, send them to the student devices!
//				contentMapForStudent.put(Ticket.TICKET_LIST_WEB_STR, JsonHelper.serialize(resTickets));
//				gcmHelper.asyncSend(studentDevices, contentMapForStudent);				
//			}
//		}
		
		/**end modification*/
		
		// unify the data structure, just send the ClassParticipate list to the students
		this.notifyAllClassParticipation(lid,config);
		return true;
	}

	@Override
	public List<Ticket> QueryTicketData(int lid) {
		return ticketManageDBImpl.queryTicketListByLesson(lid);
	}

	@Override
	public void StoreTicketList(List<Ticket> ticketList) {
		for(Ticket ticket : ticketList)
		{
			ticketManageDBImpl.insertTicket(ticket);
		}
	}

	@Override
	public List<ClassParticipate> QueryClassBuddiesList(int lid) {
		List<ClassParticipate> response=new ArrayList<ClassParticipate>() ;
		List<LoginSession> loginUserList = userManageDBImpl.queryActiveLoginSession(lid,UserProfile.USERTYPE_STUDENT);
		logger.info("Current active student login session number: " + loginUserList.size() );
		for(LoginSession ls : loginUserList){
			int uid = ls.getUid();
			UserProfile student = userManageDBImpl.queryUserProfileByUId(uid);
			List<Ticket> tickets = ticketManageDBImpl.queryTicket(lid,uid,new String[]{Ticket.TICKETSTATUS_RAISING,Ticket.TICKETSTATUS_RESPOND});
			if(tickets==null)
				tickets = new ArrayList<Ticket>();
			ClassParticipate cp = new ClassParticipate(lid,student,tickets);
			response.add(cp);
		}
		return response;
	}
	
	@Override
	public void notifyAllClassParticipation(int lid, ServletConfig config)
	{
		Map<String,String> contentMapForStudent = new HashMap<String,String>();
		List<LoginSession> lsList = userManageDBImpl.queryActiveLoginSession(lid, UserProfile.USERTYPE_STUDENT);
		if(lsList!=null && lsList.size()!=0){
			List<String> studentDevices = new ArrayList<String>();
			for(LoginSession ls : lsList){
				studentDevices.add(ls.getGcm_regid());
			}
		
			List<ClassParticipate> currentClassBuddies = this.QueryClassBuddiesList(lid);
			if(null!=currentClassBuddies&&currentClassBuddies.size()!=0)
			{
				contentMapForStudent.put(Ticket.TICKET_LIST_WEB_STR, JsonHelper.serialize(currentClassBuddies));
				if(gcmHelper==null)
					gcmHelper = new GCMHelper(config);
				gcmHelper.asyncSend(studentDevices, contentMapForStudent);
			}
		}
	}
	

}

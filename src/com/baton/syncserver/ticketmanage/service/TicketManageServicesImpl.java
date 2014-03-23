package com.baton.syncserver.ticketmanage.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;

import com.baton.publiclib.model.classmanage.ClassLesson;
import com.baton.publiclib.model.classmanage.VirtualClass;
import com.baton.publiclib.model.ticketmanage.Ticket;
import com.baton.publiclib.model.usermanage.UserProfile;
import com.baton.syncserver.classmanage.dbAccess.ClassManageDBAccess;
import com.baton.syncserver.classmanage.dbAccess.ClassManageDBAccessImpl;
import com.baton.syncserver.infrastructure.utility.GCMHelper;
//import com.baton.syncserver.ticketmanage.model.Ticket;
import com.baton.syncserver.usermanage.dbAccess.UserManageDBAccess;
import com.baton.syncserver.usermanage.dbAccess.UserManageDBAccessImpl;
//import com.baton.syncserver.usermanage.model.UserProfile;
import com.baton.syncserver.ticketmanage.dbAccess.*;

public class TicketManageServicesImpl implements TicketManageServices {

	private UserManageDBAccess userManageDBImpl = new UserManageDBAccessImpl();
	private TicketManageDBAccess ticketManageDBImpl = new TicketManageDBAccessImpl();
	private ClassManageDBAccess classManageDBImpl = new ClassManageDBAccessImpl();
	
	@Override
	public Boolean SendTicket(ServletConfig config, String loginId, String gcm_regid, String ticketType,
			String ticketContent, String timeStamp, int lid) {
		UserProfile user = userManageDBImpl.queryUserProfileByLoginId(loginId);
		// current solution is we don't need to store the ticket during the class
//		Ticket ticket = new Ticket(user.getUid(),ticketType,ticketContent,timeStamp,lid,Ticket.TICKETSTATUS_RAISING);
//		ticketManageDBImpl.insertTicket(ticket);
		GCMHelper gcmHelper = new GCMHelper(config);
		List<String> devices = new ArrayList<String>();
		// TODO for the spiral two, we don't have classroom, so we send msg to all teacher tablet
		ClassLesson lesson = classManageDBImpl.queryLessonByLid(lid);
		VirtualClass vClass = classManageDBImpl.queryVirtualClass(lesson.getCid());
		UserProfile teacher = userManageDBImpl.queryUserProfile(vClass.getTeacher_id());
		if(null!=teacher)
			devices.add(teacher.getGcm_regid());
		
		Map<String,String> contentMap = new HashMap<String,String>();
		contentMap.put(Ticket.UID_WEB_STR, String.valueOf(user.getUid()));
		contentMap.put(UserProfile.LOGINID_WEB_STR, user.getLogin_id());
		contentMap.put(Ticket.TICKETTYPE_WEB_STR, ticketType);
		contentMap.put(Ticket.TICKETCONTENT_WEB_STR, ticketContent);
		contentMap.put(Ticket.TIMESTAMP_WEB_STR, timeStamp);
		gcmHelper.asyncSend(devices, contentMap);
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

}

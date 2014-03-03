package com.baton.syncserver.ticketmanage.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;

import com.baton.syncserver.infrastructure.utility.GCMHelper;
import com.baton.syncserver.ticketmanage.model.Ticket;
import com.baton.syncserver.usermanage.dbAccess.UserManageDBAccess;
import com.baton.syncserver.usermanage.dbAccess.UserManageDBAccessImpl;
import com.baton.syncserver.usermanage.model.UserProfile;
import com.baton.syncserver.ticketmanage.dbAccess.*;

public class TicketManageServicesImpl implements TicketManageServices {

	private UserManageDBAccess userManageDBImpl = new UserManageDBAccessImpl();
	private TicketManageDBAccess ticketManageDBImpl = new TicketManageDBAccessImpl();
	
	@Override
	public Boolean SendTicket(ServletConfig config, String email, String gcm_regid, String ticketType,
			String ticketContent, String timeStamp) {
		UserProfile user = userManageDBImpl.queryUserProfile(email);
		Ticket ticket = new Ticket(user.getUid(),ticketType,ticketContent,timeStamp);
		ticketManageDBImpl.insertTicket(ticket);
		GCMHelper gcmHelper = new GCMHelper(config);
		List<String> devices = new ArrayList<String>();
		// TODO for the spiral two, we don't have classroom, so we send msg to all teacher tablet
		for(UserProfile teacher: userManageDBImpl.queryUserProfileList())
		{
			if(teacher.getUser_type().equals("Teacher"))
				devices.add(teacher.getGcm_regid());
		}
		Map<String,String> contentMap = new HashMap<String,String>();
		contentMap.put(Ticket.UID_WEB_STR, String.valueOf(user.getUid()));
		contentMap.put(UserProfile.NICKNAME_WEB_STR, user.getNick_name());
		contentMap.put(Ticket.TICKETTYPE_WEB_STR, ticketType);
		contentMap.put(Ticket.TICKETCONTENT_WEB_STR, ticketContent);
		contentMap.put(Ticket.TIMESTAMP_WEB_STR, timeStamp);
		gcmHelper.asyncSend(devices, contentMap);
		return true;
	}

}

package com.baton.syncserver.infrastructure.servlet.baseservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baton.publiclib.model.classmanage.ClassLesson;
import com.baton.publiclib.model.classmanage.ClassParticipate;
import com.baton.publiclib.model.ticketmanage.Ticket;
import com.baton.publiclib.model.usermanage.LoginSession;
import com.baton.publiclib.model.usermanage.UserProfile;
import com.baton.publiclib.utility.JsonHelper;
import com.baton.syncserver.ticketmanage.dbAccess.TicketManageDBAccess;
import com.baton.syncserver.ticketmanage.dbAccess.TicketManageDBAccessImpl;
import com.baton.syncserver.ticketmanage.service.TicketManageServices;
import com.baton.syncserver.ticketmanage.service.TicketManageServicesImpl;
import com.baton.syncserver.usermanage.dbAccess.UserManageDBAccess;
import com.baton.syncserver.usermanage.dbAccess.UserManageDBAccessImpl;

@SuppressWarnings("serial")
public class GetLessonBuddies extends BaseServlet {

	private UserManageDBAccess userManageDBImpl = new UserManageDBAccessImpl();
	private TicketManageDBAccess ticketManageDBImpl = new TicketManageDBAccessImpl();
	private TicketManageServices ticketManageServicesImpl = new TicketManageServicesImpl();
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doPost(req, resp);
		logger.info("Receive the GetLessonBuddies request");
		
		List<ClassParticipate> response= null;
		
		String lessonId = getParameter(req,ClassLesson.LESSONID_WEB_STR);
		int lid = Integer.valueOf(lessonId);
		response = ticketManageServicesImpl.QueryClassBuddiesList(lid);
		this.setSuccess(resp,JsonHelper.serialize(response));
	}
	
	
}

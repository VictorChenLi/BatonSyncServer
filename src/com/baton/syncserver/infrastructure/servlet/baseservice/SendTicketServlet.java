package com.baton.syncserver.infrastructure.servlet.baseservice;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;





import com.baton.publiclib.infrastructure.exception.ServiceException;
import com.baton.publiclib.model.classmanage.ClassLesson;
import com.baton.publiclib.model.ticketmanage.Ticket;
import com.baton.publiclib.model.usermanage.UserProfile;
//import com.baton.syncserver.classmanage.model.ClassLesson;
//import com.baton.syncserver.ticketmanage.model.Ticket;
import com.baton.syncserver.ticketmanage.service.TicketManageServices;
import com.baton.syncserver.ticketmanage.service.TicketManageServicesImpl;
//import com.baton.syncserver.usermanage.model.UserProfile;
import com.baton.syncserver.usermanage.service.UserManageServices;
import com.baton.syncserver.usermanage.service.UserManageServicesImpl;

public class SendTicketServlet extends BaseServlet {

	private TicketManageServices ticketManageService = new TicketManageServicesImpl();
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		logger.info("Receive the send ticket request");
		super.doPost(req, resp);
		String gcm_regid = getParameter(req, UserProfile.GCMID_WEB_STR);
		String loginId = getParameter(req,UserProfile.LOGINID_WEB_STR);
		String ticketType = getParameter(req, Ticket.TICKETTYPE_WEB_STR);
		String ticketContent = getParameter(req, Ticket.TICKETCONTENT_WEB_STR);
		String timeStamp = getParameter(req, Ticket.TIMESTAMP_WEB_STR);
		int LessonId = Integer.valueOf(getParameter(req,ClassLesson.LESSONID_WEB_STR));
		
		try {
			ticketManageService.SendTicket(getServletConfig(), loginId, gcm_regid, ticketType, ticketContent, timeStamp,LessonId);
		} catch (ServiceException e) {
			e.printStackTrace();
			this.setException(resp, e);
		}
		setSuccess(resp);
	}
}

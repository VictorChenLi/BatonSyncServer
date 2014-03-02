package com.baton.syncserver.infrastructure.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baton.syncserver.ticketmanage.model.Ticket;
import com.baton.syncserver.ticketmanage.service.TicketManageServices;
import com.baton.syncserver.ticketmanage.service.TicketManageServicesImpl;
import com.baton.syncserver.usermanage.model.UserProfile;
import com.baton.syncserver.usermanage.service.UserManageServices;
import com.baton.syncserver.usermanage.service.UserManageServicesImpl;

public class SendTicketServlet extends BaseServlet {

	private TicketManageServices ticketManageService = new TicketManageServicesImpl();
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		String gcm_regid = getParameter(req, UserProfile.GCMID_WEB_STR);
		String ticketType = getParameter(req, Ticket.TICKETTYPE_WEB_STR);
		String ticketContent = getParameter(req, Ticket.TICKETCONTENT_WEB_STR);
		String timeStamp = getParameter(req, Ticket.TIMESTAMP_WEB_STR);
		
		ticketManageService.SendTicket(getServletConfig(), gcm_regid, ticketType, ticketContent, timeStamp);
		setSuccess(resp);
	}
}

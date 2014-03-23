package com.baton.syncserver.infrastructure.servlet.baseservice;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baton.publiclib.model.classmanage.ClassLesson;
import com.baton.publiclib.model.ticketmanage.Ticket;
import com.baton.publiclib.utility.JsonHelper;
import com.baton.syncserver.ticketmanage.service.TicketManageServices;
import com.baton.syncserver.ticketmanage.service.TicketManageServicesImpl;

public class UploadTicketData extends BaseServlet {

	private TicketManageServices ticketManageImpl = new TicketManageServicesImpl();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.info("Receive the upload ticket request");
		super.doPost(req, resp);
		String ticketListStr = getParameter(req,Ticket.TICKET_LIST_WEB_STR);
		List<Ticket> ticketList = JsonHelper.deserializeList(ticketListStr,Ticket.class);
		ticketManageImpl.StoreTicketList(ticketList);
		this.setSuccess(resp);
	}
	
	
}

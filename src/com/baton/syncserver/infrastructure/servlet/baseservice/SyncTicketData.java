package com.baton.syncserver.infrastructure.servlet.baseservice;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baton.publiclib.model.classmanage.ClassLesson;
import com.baton.publiclib.model.ticketmanage.TalkTicketForDisplay;
import com.baton.publiclib.model.ticketmanage.Ticket;
import com.baton.publiclib.utility.JsonHelper;
import com.baton.syncserver.classmanage.service.ClassManageServices;
import com.baton.syncserver.classmanage.service.ClassManageServicesImpl;
import com.baton.syncserver.ticketmanage.service.TicketManageServices;
import com.baton.syncserver.ticketmanage.service.TicketManageServicesImpl;
@SuppressWarnings("serial")
public class SyncTicketData extends BaseServlet {

	private ClassManageServices classManageImpl = new ClassManageServicesImpl();
	private TicketManageServices ticketManageImpl = new TicketManageServicesImpl();
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doPost(req, resp);
		logger.info("Receive the sync ticket request");
		String lessonId = getParameter(req,ClassLesson.LESSONID_WEB_STR);
		List<Ticket> ticketList = ticketManageImpl.QueryTicketData(Integer.valueOf(lessonId));
		List<TalkTicketForDisplay> displayTicketList = classManageImpl.queryClassParticipationData(Integer.valueOf(lessonId));
		this.setSuccess(resp,JsonHelper.serialize(ticketList)+"&"+JsonHelper.serialize(displayTicketList));
	}
	
	
}

package com.baton.syncserver.ticketmanage.service;

import java.util.List;

import javax.servlet.ServletConfig;

import com.baton.publiclib.infrastructure.exception.ServiceException;
import com.baton.publiclib.model.ticketmanage.Ticket;

public interface TicketManageServices {
	
	public Boolean SendTicket(ServletConfig config, String email, String gcm_regid,
			String ticketType, String ticketContent, String timeStamp, int lid) throws ServiceException;
	
	public List<Ticket> QueryTicketData(int lid);
	
	public void StoreTicketList(List<Ticket> ticketList);

}

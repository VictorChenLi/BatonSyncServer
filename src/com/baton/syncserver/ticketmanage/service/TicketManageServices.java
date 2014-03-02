package com.baton.syncserver.ticketmanage.service;

import javax.servlet.ServletConfig;

public interface TicketManageServices {
	
	public Boolean SendTicket(ServletConfig config, String gcm_regid,
			String ticketType, String ticketContent, String timeStamp);
	
}

package com.baton.syncserver.ticketmanage.dbAccess;

import java.util.List;

import com.baton.publiclib.model.ticketmanage.Ticket;

//import com.baton.syncserver.ticketmanage.model.Ticket;

public interface TicketManageDBAccess {
	public static final String SELECTSQL = "select * from ticket ";

	public static final String INSERTSQL = "insert into ticket(uid, ticketType, ticketContent, timeStamp,lid, ticket_status) "
			+ "VALUES (?,?,?,?,?,?)";
	
	public static final String UPDATESQL = "update ticket set uid=?, ticketType=?, ticketContent=?, timeStamp=?,lid=? , ticket_status=? where tid=?";

	public Ticket queryTicketById(int tid);
	
	public List<Ticket> queryTicketListByLesson(int lid);

	public List<Ticket> queryTicketList();

	public boolean insertTicket(Ticket ticket);
	
	public boolean updateTicket(Ticket ticket);
}

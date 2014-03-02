package com.baton.syncserver.ticketmanage.dbAccess;

import java.util.List;

import com.baton.syncserver.ticketmanage.model.Ticket;

public interface TicketManageDBAccess {
	public static final String SELECTSQL = "select * from ticket ";

	public static final String INSERTSQL = "insert into ticket(uid, ticketType, ticketContent, timeStamp) "
			+ "VALUES (?,?,?,?)";
	
	public static final String UPDATESQL = "update ticket set uid=?, ticketType=?, ticketContent=?, timeStamp=? where tid=?";

	public Ticket queryTicketById(int tid);

	public List<Ticket> queryTicketList();

	public boolean insertTicket(Ticket ticket);
	
	public boolean updateTicket(Ticket ticket);
}

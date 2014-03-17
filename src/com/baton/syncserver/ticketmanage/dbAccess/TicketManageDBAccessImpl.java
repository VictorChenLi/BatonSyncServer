package com.baton.syncserver.ticketmanage.dbAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



import com.baton.publiclib.model.classmanage.ClassLesson;
import com.baton.publiclib.model.ticketmanage.Ticket;
//import com.baton.syncserver.classmanage.model.ClassLesson;
import com.baton.syncserver.infrastructure.database.BaseDBAccess;
import com.baton.syncserver.infrastructure.database.DTable;
//import com.baton.syncserver.ticketmanage.model.Ticket;

public class TicketManageDBAccessImpl implements TicketManageDBAccess {

	@Override
	public Ticket queryTicketById(int tid) {
		String strSqlWhere = BaseDBAccess.getSqlAndWhereString(new String[]{"tid"},new String[]{String.valueOf(tid)});
		return queryTicketList(TicketManageDBAccess.SELECTSQL,strSqlWhere)!=null?queryTicketList(TicketManageDBAccess.SELECTSQL,strSqlWhere).get(0):null;
	}

	@Override
	public List<Ticket> queryTicketList() {
		return queryTicketList(TicketManageDBAccess.SELECTSQL,"");
	}
	
	private List<Ticket> queryTicketList(String strSql, String strWhere)
	{
		List<Ticket> ticketList = new ArrayList<Ticket>();
		strSql+=strWhere;
		DTable results = BaseDBAccess.getSQLResult(strSql);
		if(null==results||0==results.getRowLength())
			return ticketList;
		for(int i =0;i<results.getRowLength();i++)
		{
			Map<String,Object> curRow = results.getRow(i);
			Ticket ticket = new Ticket(curRow);
			ticketList.add(ticket);
		}
		return ticketList;
	}

	@Override
	public boolean insertTicket(Ticket ticket) {
		return BaseDBAccess.runSQL(TicketManageDBAccess.INSERTSQL, ticket.getUserData());
	}

	@Override
	public boolean updateTicket(Ticket ticket) {
		List<String> varList = ticket.getUserData();
		varList.add(String.valueOf(ticket.getTid()));
		return BaseDBAccess.runSQL(TicketManageDBAccess.UPDATESQL,varList);
	}

	@Override
	public List<Ticket> queryTicketListByLesson(int lid) {
		String strSqlWhere = BaseDBAccess.getSqlAndWhereString(new String[]{ClassLesson.LESSONID_DB_STR},new String[]{String.valueOf(lid)});
		return queryTicketList(TicketManageDBAccess.SELECTSQL,strSqlWhere);
	}

}

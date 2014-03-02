package com.baton.syncserver.classmanage.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassParticipate {
	public static final String STUDENT_ID_DB_STR ="student_id";
	
	private int cid;
	private int student_id;
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	public int getStudent_id() {
		return student_id;
	}
	public void setStudent_id(int student_id) {
		this.student_id = student_id;
	}
	public ClassParticipate(int cid, int student_id) {
		super();
		this.cid = cid;
		this.student_id = student_id;
	}
	public ClassParticipate(Map<String, Object> data)
	{
		super();
		this.cid=Integer.valueOf(data.get(VirtualClass.CID_DB_STR).toString());
		this.student_id=Integer.valueOf(data.get(ClassParticipate.STUDENT_ID_DB_STR).toString());
	}
	
	public List<String> getUserData()
	{
		List<String> userData = new ArrayList<String>();
		userData.add(String.valueOf(this.cid));
		userData.add(String.valueOf(this.student_id));
		return userData;
	}
}

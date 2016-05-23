package com.paulavasile.dineunite.data;

public class NewsInfo {
	
	public static final int TYPE_VALID_SCAN = 0;
	public static final int TYPE_INVALID_SCAN = 1;
	public static final int TYPE_REDEEMED = 2;
	public static final int TYPE_UNREDEEMED = 3;
	
	public int id;
	public int type;
	public int eid;
	public int tid;	
	public String barcode;
	public String status;
	public String date;	
	public String reason;
	public String tmname;
	public int needsynced;
	
	public NewsInfo() {
	}
}

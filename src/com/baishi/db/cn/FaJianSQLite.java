package com.baishi.db.cn;

public class FaJianSQLite {
	public String  BillCode ;
	public String  NextSite ;
	public String  VehicleNo ;
	public String  ScanTime ;
	public String ScanDate;
	public String X_Auth_User;
	public String X_Auth_Site;
	public int index;
	public FaJianSQLite() {
		// TODO Auto-generated constructor stub
	}
	
	public String getX_Auth_User() {
		return X_Auth_User;
	}

	public void setX_Auth_User(String x_Auth_User) {
		X_Auth_User = x_Auth_User;
	}

	public String getX_Auth_Site() {
		return X_Auth_Site;
	}

	public void setX_Auth_Site(String x_Auth_Site) {
		X_Auth_Site = x_Auth_Site;
	}

	public FaJianSQLite(String billCode, String nextSite, String vehicleNo,
			String scanTime, String scanDate,String x_Auth_User,String x_Auth_Site) {
		super();
		BillCode = billCode;
		NextSite = nextSite;
		VehicleNo = vehicleNo;
		ScanTime = scanTime;
		ScanDate = scanDate;
		X_Auth_User = x_Auth_User;
		X_Auth_Site = x_Auth_Site;
	}

	public String getBillCode() {
		return BillCode;
	}
	public void setBillCode(String billCode) {
		BillCode = billCode;
	}
	public String getNextSite() {
		return NextSite;
	}
	public String getScanDate() {
		return ScanDate;
	}
	public void setScanDate(String scanDate) {
		ScanDate = scanDate;
	}
	public void setNextSite(String nextSite) {
		NextSite = nextSite;
	}
	public String getVehicleNo() {
		return VehicleNo;
	}
	public void setVehicleNo(String vehicleNo) {
		VehicleNo = vehicleNo;
	}
	public String getScanTime() {
		return ScanTime;
	}
	public void setScanTime(String scanTime) {
		ScanTime = scanTime;
	}
	public void setIndex(int pox){
		index = pox;
	}
	public int getIndex(){
		return index;
	}
	
	
}

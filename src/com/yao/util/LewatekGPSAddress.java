package com.yao.util;

public class LewatekGPSAddress {
	
	private String countryName;
	private String countryNameCode;
	private String AdministrativeAreaName;
	private String locatityName;
	private String dependentLocalityName;
	
	//设置CoubtryName
	public void setCountryName(String string) {
		// TODO Auto-generated method stub
		this.countryName=string;
	}

	public void setCountryNameCode(String string) {
		// TODO Auto-generated method stub
		this.countryNameCode=string;
	}

	public void setAdministrativeAreaName(String string) {
		// TODO Auto-generated method stub
		this.AdministrativeAreaName=string;
	}

	public void setLocalityName(String string) {
		// TODO Auto-generated method stub
		this.locatityName=string;
	}

	public void setDependentLocalityName(String string) {
		// TODO Auto-generated method stub
		this.dependentLocalityName=string;
	}
	
	public String getLoacation(){
		StringBuffer sb=new StringBuffer();
		sb.append(countryName).append("\n")
			.append(countryNameCode).append("\n")
			.append(AdministrativeAreaName).append("\n")
			.append(locatityName).append("\n")
			.append(dependentLocalityName);
		return sb.toString();
	}
}

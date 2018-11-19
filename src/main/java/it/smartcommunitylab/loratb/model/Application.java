package it.smartcommunitylab.loratb.model;

import org.springframework.data.annotation.Id;

public class Application {
	@Id
	private String id;
	private String appId;
	private String name;
	private String organizationID;
	private String serviceProfileID;
	private String serviceProfileName;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getServiceProfileName() {
		return serviceProfileName;
	}
	public void setServiceProfileName(String serviceProfileName) {
		this.serviceProfileName = serviceProfileName;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getOrganizationID() {
		return organizationID;
	}
	public void setOrganizationID(String organizationID) {
		this.organizationID = organizationID;
	}
	public String getServiceProfileID() {
		return serviceProfileID;
	}
	public void setServiceProfileID(String serviceProfileID) {
		this.serviceProfileID = serviceProfileID;
	}
}

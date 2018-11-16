package it.smartcommunitylab.loratb.ext.tb;

import org.springframework.data.annotation.Id;

public class TbDevice {
	@Id
	private String id;
	private String tenantId;
	private String customerId;
	private String name;
	private String type;
	private String credentialsId;
	private String credentialsType;
	private String credentialsValue;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCredentialsId() {
		return credentialsId;
	}
	public void setCredentialsId(String credentialsId) {
		this.credentialsId = credentialsId;
	}
	public String getCredentialsType() {
		return credentialsType;
	}
	public void setCredentialsType(String credentialsType) {
		this.credentialsType = credentialsType;
	}
	public String getCredentialsValue() {
		return credentialsValue;
	}
	public void setCredentialsValue(String credentialsValue) {
		this.credentialsValue = credentialsValue;
	}
	
}

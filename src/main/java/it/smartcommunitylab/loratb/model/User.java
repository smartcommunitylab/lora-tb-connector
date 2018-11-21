package it.smartcommunitylab.loratb.model;

import org.springframework.data.annotation.Id;

public class User {
	@Id
	private String id;
	private String tbId;
	private String tbTenantId;
	private String tbEmail;
	private String tbName;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTbId() {
		return tbId;
	}
	public void setTbId(String tbId) {
		this.tbId = tbId;
	}
	public String getTbTenantId() {
		return tbTenantId;
	}
	public void setTbTenantId(String tbTenantId) {
		this.tbTenantId = tbTenantId;
	}
	public String getTbEmail() {
		return tbEmail;
	}
	public void setTbEmail(String tbEmail) {
		this.tbEmail = tbEmail;
	}
	public String getTbName() {
		return tbName;
	}
	public void setTbName(String tbName) {
		this.tbName = tbName;
	}
	
}

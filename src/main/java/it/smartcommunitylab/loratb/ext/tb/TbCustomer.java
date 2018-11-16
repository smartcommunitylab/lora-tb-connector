package it.smartcommunitylab.loratb.ext.tb;

import org.springframework.data.annotation.Id;

public class TbCustomer {
	@Id
	private String id;
	private String tenantId;
	private String name;
		
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
	@Override
	public String toString() {
		return tenantId + "/" + id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}

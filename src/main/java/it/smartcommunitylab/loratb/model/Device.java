package it.smartcommunitylab.loratb.model;

import org.springframework.data.annotation.Id;

public class Device {
	@Id
	private String id;
	private String name;
	private String type;
	private String tbId;
	private String tbTenantId;
	private String tbCredentialsId;
	private String tbCredentialsType;
	private String loraApplicationId;
	private String loraApplicationName;
	private String loraDevEUI;
	private String loraProfileId;
	private String loraProfileName;
	private Double loraStatusBattery;
	
	public String getTbTenantId() {
		return tbTenantId;
	}
	public void setTbTenantId(String tenantId) {
		this.tbTenantId = tenantId;
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
	public String getTbCredentialsId() {
		return tbCredentialsId;
	}
	public void setTbCredentialsId(String tbCredentialsId) {
		this.tbCredentialsId = tbCredentialsId;
	}
	public String getTbCredentialsType() {
		return tbCredentialsType;
	}
	public void setTbCredentialsType(String tbCredentialsType) {
		this.tbCredentialsType = tbCredentialsType;
	}
	public String getLoraApplicationId() {
		return loraApplicationId;
	}
	public void setLoraApplicationId(String loraApplicationId) {
		this.loraApplicationId = loraApplicationId;
	}
	public String getLoraDevEUI() {
		return loraDevEUI;
	}
	public void setLoraDevEUI(String loraDevEUI) {
		this.loraDevEUI = loraDevEUI;
	}
	public String getTbId() {
		return tbId;
	}
	public void setTbId(String tbId) {
		this.tbId = tbId;
	}
	public String getLoraProfileId() {
		return loraProfileId;
	}
	public void setLoraProfileId(String loraProfileId) {
		this.loraProfileId = loraProfileId;
	}
	public String getLoraProfileName() {
		return loraProfileName;
	}
	public void setLoraProfileName(String loraProfileName) {
		this.loraProfileName = loraProfileName;
	}
	public Double getLoraStatusBattery() {
		return loraStatusBattery;
	}
	public void setLoraStatusBattery(Double loraStatusBattery) {
		this.loraStatusBattery = loraStatusBattery;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLoraApplicationName() {
		return loraApplicationName;
	}
	public void setLoraApplicationName(String loraApplicationName) {
		this.loraApplicationName = loraApplicationName;
	}
	
}

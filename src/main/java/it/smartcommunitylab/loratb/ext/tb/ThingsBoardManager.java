package it.smartcommunitylab.loratb.ext.tb;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.smartcommunitylab.loratb.model.Customer;
import it.smartcommunitylab.loratb.model.Device;
import it.smartcommunitylab.loratb.model.ExtLogin;
import it.smartcommunitylab.loratb.utils.HTTPUtils;

@Component
public class ThingsBoardManager {
	private static final transient Logger logger = LoggerFactory.getLogger(ThingsBoardManager.class);
	
	@Value("${tb.endpoint}")
	private String endpoint;

	@Value("${tb.user}")
	private String user;

	@Value("${tb.password}")
	private String password;
	
	@Value("${tb.limit}")
	private int limit;
	
	@Value("${tb.header}")
	private String headerKey;
	
	private ObjectMapper mapper = null;
	
	private String token;
	
	@PostConstruct
	public void init() {
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
		mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}
	
	private boolean isTokenExpired() {
		return (token == null);
	}
	
	public String getToken() throws Exception {
		String address = endpoint + "api/auth/login";
		ExtLogin login = new ExtLogin();
		login.setUsername(user);
		login.setPassword(password);
		String json = HTTPUtils.post(address, login, null, null, null, null);
		JsonNode node = mapper.readTree(json);
		return node.get("token").asText();
	}
	
	public List<Customer> getCustomers() throws Exception {
		List<Customer> result = new ArrayList<>();
		if(isTokenExpired()) {
			token = getToken();
		}
		String address = endpoint + "api/customers?limit=" + limit;
		boolean hasNext = false;
		do {
			String json = HTTPUtils.get(address, token, headerKey, null, null);
			JsonNode rootNode = mapper.readTree(json);
			JsonNode dataNode = rootNode.get("data");
			if(dataNode.isArray()) {
				for (JsonNode customerNode : dataNode) {
					String id = customerNode.get("id").get("id").asText();
					String tenantId = customerNode.get("tenantId").get("id").asText();
					String name = customerNode.get("name").asText();
					Customer tbCustomer = new Customer();
					tbCustomer.setId(id);
					tbCustomer.setTenantId(tenantId);
					tbCustomer.setName(name);
					result.add(tbCustomer);
				}
			}
			hasNext = rootNode.get("hasNext").asBoolean();
		} while (hasNext);
		if(logger.isInfoEnabled()) {
			logger.info("getCustomers:" + result.size());
		}
		return result;
	}
	
	public List<Device> getDevicesByCustomer(String customerId) throws Exception {
		List<Device> result = new ArrayList<>();
		if(isTokenExpired()) {
			token = getToken();
		}
		String address = endpoint + "api/customer/" + customerId + "/devices?limit=" + limit;
		boolean hasNext = false;
		do {
			String json = HTTPUtils.get(address, token, headerKey, null, null);
			JsonNode rootNode = mapper.readTree(json);
			JsonNode dataNode = rootNode.get("data");
			if(dataNode.isArray()) {
				for (JsonNode deviceNode : dataNode) {
					String id = deviceNode.get("id").get("id").asText();
					String tenantId = deviceNode.get("tenantId").get("id").asText();
					String name = deviceNode.get("name").asText();
					String type = deviceNode.get("type").asText();
					
					String addressCred = endpoint + "api/device/" + id + "/credentials";
					String jsonCred = HTTPUtils.get(addressCred, token, headerKey, null, null);
					JsonNode credNode = mapper.readTree(jsonCred);
					String credentialsType = credNode.get("credentialsType").asText();
					String credentialsId = credNode.get("credentialsId").asText();
					
					Device device = new Device();
					device.setTbId(id);
					device.setTbTenantId(tenantId);
					device.setName(name);
					device.setType(type);
					device.setTbCredentialsId(credentialsId);
					device.setTbCredentialsType(credentialsType);
					result.add(device);
				}
			}
			hasNext = rootNode.get("hasNext").asBoolean();
		} while (hasNext);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getDevicesByCustomer[%s]:%s", customerId, result.size()));
		}
		return result;
	}
	
	public Device addDevice(String tenantId, String name, String type) throws Exception {
		if(isTokenExpired()) {
			token = getToken();
		}
		
		ObjectNode tenantNode = mapper.createObjectNode();
		tenantNode.put("id", tenantId);
		tenantNode.put("entityType", "TENANT");
		
		ObjectNode deviceNode = mapper.createObjectNode();
		deviceNode.put("name", name);
		deviceNode.put("type", type);
		deviceNode.put("id", (String) null);
		deviceNode.set("tenantId", tenantNode);
		
		String json = mapper.writeValueAsString(deviceNode);
		
		String address = endpoint + "api/device";
		String jsonResponse = HTTPUtils.post(address, json, token, headerKey, null, null);
		JsonNode rootNode = mapper.readTree(jsonResponse);
		
		String id = rootNode.get("id").get("id").asText();
		
		String addressCred = endpoint + "api/device/" + id + "/credentials";
		String jsonCred = HTTPUtils.get(addressCred, token, headerKey, null, null);
		JsonNode credNode = mapper.readTree(jsonCred);
		String credentialsType = credNode.get("credentialsType").asText();
		String credentialsId = credNode.get("credentialsId").asText();
		
		Device newDevice = new Device();
		newDevice.setTbId(id);
		newDevice.setTbTenantId(tenantId);
		newDevice.setName(name);
		newDevice.setTbCredentialsId(credentialsId);
		newDevice.setTbCredentialsType(credentialsType);
		
		return newDevice;
	}
	
	public void sendTelemetry(Device device, JsonNode payload, long timestamp) throws Exception {
		if(isTokenExpired()) {
			token = getToken();
		}
		ObjectNode telemetryNode = mapper.createObjectNode();
		telemetryNode.put("ts", timestamp);
		telemetryNode.set("values", payload);
		String json = mapper.writeValueAsString(telemetryNode);
		
		String address = endpoint + "api/v1/" + device.getTbCredentialsId() + "/telemetry";
		HTTPUtils.post(address, json, token, headerKey, null, null);
	}

}

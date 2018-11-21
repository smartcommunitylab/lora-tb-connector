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
import it.smartcommunitylab.loratb.model.User;
import it.smartcommunitylab.loratb.utils.HTTPUtils;
import it.smartcommunitylab.loratb.utils.Utils;

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
	
	private long tokenExp;
	
	@PostConstruct
	public void init() {
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
		mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}
	
	private synchronized boolean isTokenExpired() {
		if(token == null) {
			return true;
		}
		return (System.currentTimeMillis() > tokenExp);
	}
	
	public synchronized String getToken() throws Exception {
		String address = endpoint + "api/auth/login";
		ExtLogin login = new ExtLogin();
		login.setUsername(user);
		login.setPassword(password);
		String json = HTTPUtils.post(address, login, null, null, null, null);
		JsonNode node = mapper.readTree(json);
		String jwtToken = node.get("token").asText();
		String jwtBody = Utils.getJWTBody(jwtToken);
		JsonNode nodeBody = mapper.readTree(jwtBody);
		tokenExp = nodeBody.get("exp").asLong() * 1000;
		return jwtToken;
	}
	
	public User getUser() throws Exception {
		if(isTokenExpired()) {
			token = getToken();
		}
		String address = endpoint + "api/auth/user";
		String json = HTTPUtils.get(address, token, headerKey, null, null);
		JsonNode rootNode = mapper.readTree(json);
		String id = rootNode.get("id").get("id").asText();
		String tenantId = rootNode.get("tenantId").get("id").asText();
		String name = rootNode.get("name").asText();
		String email = rootNode.get("email").asText();
		
		User user = new User();
		user.setTbId(id);
		user.setTbTenantId(tenantId);
		user.setTbName(name);
		user.setTbEmail(email);
		return user;
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
	
	public Device getDeviceById(String deviceId) throws Exception {
		if(isTokenExpired()) {
			token = getToken();
		}
		String address = endpoint + "api/device/" + deviceId;
		String jsonDevice = HTTPUtils.get(address, token, headerKey, null, null);
		JsonNode deviceNode = mapper.readTree(jsonDevice);
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
		return device;
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
	
	public Device addDevice(String tenantId, Device device) throws Exception {
		if(isTokenExpired()) {
			token = getToken();
		}
		
		ObjectNode tenantNode = mapper.createObjectNode();
		tenantNode.put("id", tenantId);
		tenantNode.put("entityType", "TENANT");
		
		ObjectNode deviceNode = mapper.createObjectNode();
		deviceNode.put("name", device.getName());
		deviceNode.put("type", device.getType());
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
		
		ObjectNode attributesNode = mapper.createObjectNode();
		attributesNode.put("loraApplicationId", device.getLoraApplicationId());
		attributesNode.put("loraDevEUI", device.getLoraDevEUI());
		String jsonAttr = mapper.writeValueAsString(attributesNode);
		
		String addressAttr = endpoint + "api/v1/" + credentialsId + "/attributes";
		HTTPUtils.post(addressAttr, jsonAttr, token, headerKey, null, null);
		
		Device newDevice = new Device();
		newDevice.setTbId(id);
		newDevice.setTbTenantId(tenantId);
		newDevice.setName(device.getName());
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

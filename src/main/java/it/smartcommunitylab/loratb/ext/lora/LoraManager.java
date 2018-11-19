package it.smartcommunitylab.loratb.ext.lora;

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

import it.smartcommunitylab.loratb.model.Application;
import it.smartcommunitylab.loratb.model.Device;
import it.smartcommunitylab.loratb.model.ExtLogin;
import it.smartcommunitylab.loratb.utils.HTTPUtils;

@Component
public class LoraManager {
	private static final transient Logger logger = LoggerFactory.getLogger(LoraManager.class);
	
	@Value("${lora.endpoint}")
	private String endpoint;

	@Value("${lora.user}")
	private String user;

	@Value("${lora.password}")
	private String password;
	
	@Value("${lora.limit}")
	private int limit;
	
	@Value("${lora.header}")
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
		String address = endpoint + "api/internal/login";
		ExtLogin login = new ExtLogin();
		login.setUsername(user);
		login.setPassword(password);
		String json = HTTPUtils.post(address, login, null, null, null, null);
		JsonNode node = mapper.readTree(json);
		return node.get("jwt").asText();
	}
	
	public List<Application> getApplications() throws Exception {
		List<Application> result = new ArrayList<>();
		if(isTokenExpired()) {
			token = getToken();
		}
		String address = endpoint + "api/applications?limit=" + limit;
		boolean hasNext = false;
		do {
			String json = HTTPUtils.get(address, token, headerKey, null, null);
			JsonNode rootNode = mapper.readTree(json);
			JsonNode dataNode = rootNode.get("result");
			if(dataNode.isArray()) {
				for (JsonNode appNode : dataNode) {
					Application application = mapper.treeToValue(appNode, Application.class);
					application.setAppId(appNode.get("id").asText());
					result.add(application);
				}
			}
		} while (hasNext);
		if(logger.isInfoEnabled()) {
			logger.info("getToken:" + result.size());
		}
		return result;
	}
	
	public List<Device> getDevicesByApp(String appId) throws Exception {
		List<Device> result = new ArrayList<>();
		if(isTokenExpired()) {
			token = getToken();
		}
		String address = endpoint + "api/applications/" + appId + "/devices?limit=" + limit;
		boolean hasNext = false;
		do {
			String json = HTTPUtils.get(address, token, headerKey, null, null);
			JsonNode rootNode = mapper.readTree(json);
			JsonNode dataNode = rootNode.get("result");
			if(dataNode.isArray()) {
				for (JsonNode deviceNode : dataNode) {
					String loraDevEUI = deviceNode.get("devEUI").asText();
					String loraApplicationId = deviceNode.get("applicationID").asText();
					String name = deviceNode.get("name").asText();
					String loraProfileId = deviceNode.get("deviceProfileID").asText();
					String loraProfileName = deviceNode.get("deviceProfileName").asText();
					Double loraStatusBattery = deviceNode.get("deviceStatusBattery").asDouble();
					
					Device device = new Device();
					device.setLoraDevEUI(loraDevEUI);
					device.setLoraApplicationId(loraApplicationId);
					device.setName(name);
					device.setLoraProfileId(loraProfileId);
					device.setLoraProfileName(loraProfileName);
					device.setLoraStatusBattery(loraStatusBattery);
					result.add(device);
				}
			}
		} while (hasNext);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getDevicesByApp[%s]:%s", appId, result.size()));
		}
		return result;
	}
}

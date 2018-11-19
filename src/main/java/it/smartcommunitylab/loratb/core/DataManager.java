package it.smartcommunitylab.loratb.core;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import it.smartcommunitylab.loratb.ext.lora.LoraManager;
import it.smartcommunitylab.loratb.ext.tb.ThingsBoardManager;
import it.smartcommunitylab.loratb.model.Application;
import it.smartcommunitylab.loratb.model.Customer;
import it.smartcommunitylab.loratb.model.Device;
import it.smartcommunitylab.loratb.repository.ApplicationRepository;
import it.smartcommunitylab.loratb.repository.CustomerRepository;
import it.smartcommunitylab.loratb.repository.DeviceRepository;
import it.smartcommunitylab.loratb.utils.Utils;

@Component
public class DataManager implements MqttMessageListener {
	private static final transient Logger logger = LoggerFactory.getLogger(DataManager.class);
			
	@Autowired
	private ThingsBoardManager tbManager;
	
	@Autowired
	private LoraManager loraManager;
	
	@Autowired
	private MqttManager mqttManager;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private DeviceRepository deviceRepository;
	
	@Autowired
	private ApplicationRepository applicationRepository;
	
	private ObjectMapper mapper = null;
	
	@PostConstruct
	public void init() throws Exception {
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
		mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mqttManager.setMessageListener(this);
		mqttManager.init();
	}

	@Override
	public void onMessage(String topic, String message) {
		try {
			JsonNode rootNode = mapper.readTree(message);
			String devEUI = rootNode.get("devEUI").asText();
			String appId = rootNode.get("applicationID").asText();
			Device device = deviceRepository.findByLoraDevEUI(appId, devEUI);
			if(device == null) {
				if(logger.isInfoEnabled()) {
					logger.info(String.format("onMessage - device not found: %s / %s", appId, devEUI));
				}
				return;
			}
			// check if exists in tb
			if(Utils.isNotEmpty(device.getTbTenantId()) &&
					Utils.isNotEmpty(device.getTbId())) {
				//send telemetry
				long timestamp = rootNode.get("timestamp").asLong();
				JsonNode objectNode = rootNode.get("object");
				tbManager.sendTelemetry(device, objectNode, timestamp);
			} else {
				if(logger.isInfoEnabled()) {
					logger.info(String.format("onMessage - device not connected to TB: %s / %s", appId, devEUI));
				}
			}
		} catch (Exception e) {
			if(logger.isInfoEnabled()) {
				logger.info(String.format("onMessage exception:%s", e.getMessage()));
			}
		}
	}
	
	private String getTbTenantId() {
		List<Customer> list = customerRepository.findAll();
		if(list.size() > 0) {
			return list.get(0).getTenantId();
		}
		return null;
	}
	
	public void storeTbCustomers() throws Exception {
		List<Customer> customers = tbManager.getCustomers();
		customerRepository.deleteAll();
		customerRepository.saveAll(customers);
	}
	
	public void storeTbDevices() throws Exception {
		List<Customer> customers = customerRepository.findAll();
		for(Customer customer : customers) {
			List<Device> devices = tbManager.getDevicesByCustomer(customer.getId());
			for (Device device : devices) {
				Device deviceDb = deviceRepository.findByTbId(device.getTbTenantId(), device.getTbId());
				if(deviceDb == null) {
					deviceRepository.save(device);
				} else {
					deviceDb.setName(device.getName());
					deviceDb.setType(device.getType());
					deviceDb.setTbCredentialsId(device.getTbCredentialsId());
					deviceDb.setTbCredentialsType(device.getTbCredentialsType());
					deviceRepository.save(deviceDb);
				}
			}
		}
	}
	
	public void storeLoraApplications() throws Exception {
		List<Application> applications = loraManager.getApplications();
		applicationRepository.deleteAll();
		applicationRepository.saveAll(applications);
	}
	
	public void storeLoraDevices() throws Exception {
		List<Application> applications = applicationRepository.findAll();
		for (Application application : applications) {
			List<Device> devices = loraManager.getDevicesByApp(application.getAppId());
			for (Device device : devices) {
				Device deviceDb = deviceRepository.findByLoraDevEUI(device.getLoraApplicationId(), 
						device.getLoraDevEUI());
				if(deviceDb == null) {
					deviceRepository.save(device);
				} else {
					deviceDb.setName(device.getName());
					deviceDb.setLoraProfileId(device.getLoraProfileId());
					deviceDb.setLoraProfileName(device.getLoraProfileName());
					deviceDb.setLoraStatusBattery(device.getLoraStatusBattery());
					deviceRepository.save(deviceDb);
				}
			}
		}
	}
	
	public void allignLoraDevices() throws Exception {
		List<Device> devices = deviceRepository.findAll();
		for (Device device : devices) {
			// check lora device
			if(Utils.isNotEmpty(device.getLoraApplicationId()) &&
					Utils.isNotEmpty(device.getLoraDevEUI())) {
				// check if exists in tb
				if(Utils.isNotEmpty(device.getTbTenantId()) &&
						Utils.isNotEmpty(device.getTbId())) {
					continue;
				}
				Device tbDevice = tbManager.addDevice(getTbTenantId(), 
						device.getName(), device.getLoraProfileName());
				device.setTbId(tbDevice.getTbId());
				device.setTbTenantId(tbDevice.getTbTenantId());
				device.setTbCredentialsId(tbDevice.getTbCredentialsId());
				device.setTbCredentialsType(tbDevice.getTbCredentialsType());
				deviceRepository.save(device);
			}
		}
	}

}

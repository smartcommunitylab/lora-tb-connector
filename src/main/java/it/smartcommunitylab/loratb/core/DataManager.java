package it.smartcommunitylab.loratb.core;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.loratb.ext.lora.LoraManager;
import it.smartcommunitylab.loratb.ext.tb.ThingsBoardManager;
import it.smartcommunitylab.loratb.model.Application;
import it.smartcommunitylab.loratb.model.Customer;
import it.smartcommunitylab.loratb.model.Device;
import it.smartcommunitylab.loratb.repository.ApplicationRepository;
import it.smartcommunitylab.loratb.repository.CustomerRepository;
import it.smartcommunitylab.loratb.repository.DeviceRepository;

@Component
public class DataManager {
	@Autowired
	private ThingsBoardManager tbManager;
	
	@Autowired
	private LoraManager loraManager;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private DeviceRepository deviceRepository;
	
	@Autowired
	private ApplicationRepository applicationRepository;
	
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
}

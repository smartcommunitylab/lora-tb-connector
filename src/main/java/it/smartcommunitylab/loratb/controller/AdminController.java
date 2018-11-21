package it.smartcommunitylab.loratb.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.loratb.core.DataManager;

@RestController
public class AdminController {
	private static final transient Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	@Autowired
	private DataManager dataManager;
	
	@GetMapping(value = "/admin/init")
	public void initDataset() throws Exception {
		if(logger.isInfoEnabled()) {
			logger.info("initDataset");
		}
		// get TB user
		if(logger.isInfoEnabled()) {
			logger.info("initDataset: get TB user");
		}
		dataManager.storeTbUser();
		// get Lora applications
		if(logger.isInfoEnabled()) {
			logger.info("initDataset: get Lora applications");
		}
		dataManager.storeLoraApplications();
		// get Lora devices
		if(logger.isInfoEnabled()) {
			logger.info("initDataset: get Lora devices");
		}
		dataManager.storeLoraDevices();
		// align Lora devices
		if(logger.isInfoEnabled()) {
			logger.info("initDataset: align Lora devices");
		}
		dataManager.alignLoraDevices();
	}
	
	@GetMapping(value = "/admin/lora/device/refresh")
	public void alignLoraDevices() throws Exception {
		if(logger.isInfoEnabled()) {
			logger.info("alignLoraDevices: align Lora devices");
		}
		dataManager.storeLoraApplications();
		dataManager.storeLoraDevices();
		dataManager.alignLoraDevices();
	}
}

package it.smartcommunitylab.loratb.core;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.loratb.ext.tb.TbCustomer;
import it.smartcommunitylab.loratb.ext.tb.TbDevice;
import it.smartcommunitylab.loratb.ext.tb.ThingsBoardManager;
import it.smartcommunitylab.loratb.repository.TbCustomerRepository;
import it.smartcommunitylab.loratb.repository.TbDeviceRepository;

@Component
public class DataManager {
	@Autowired
	private ThingsBoardManager tbManager;
	
	@Autowired
	private TbCustomerRepository tbCustomerRepository;
	
	@Autowired
	private TbDeviceRepository tbDeviceRepository;
	
	public void alignCustomers() throws Exception {
		List<TbCustomer> customers = tbManager.getCustomers();
		tbCustomerRepository.deleteAll();
		tbCustomerRepository.saveAll(customers);
	}
	
	public void alignDevices() throws Exception {
		List<TbCustomer> customers = tbCustomerRepository.findAll();
		for(TbCustomer customer : customers) {
			List<TbDevice> devices = tbManager.getDevicesByCustomer(customer.getId());
			tbDeviceRepository.saveAll(devices);
		}
	}
}

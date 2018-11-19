package it.smartcommunitylab.loratb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.loratb.model.Device;

@Repository
public interface DeviceRepository extends MongoRepository<Device, String> {
	@Query(value="{loraApplicationId:?0, loraDevEUI:?1}")
	Device findByLoraDevEUI(String appId, String devEUI);
	
	@Query(value="{tbTenantId:?0, tbId:?1}")
	Device findByTbId(String tenantId, String deviceId);
	
}

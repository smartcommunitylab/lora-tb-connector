package it.smartcommunitylab.loratb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.loratb.model.Application;

@Repository
public interface ApplicationRepository extends MongoRepository<Application, String> {
	@Query(value="{appId:?0}")
	Application findByAppId(String appId);
}

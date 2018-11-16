package it.smartcommunitylab.loratb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.loratb.ext.tb.TbCustomer;

@Repository
public interface TbCustomerRepository extends MongoRepository<TbCustomer, String> {

}

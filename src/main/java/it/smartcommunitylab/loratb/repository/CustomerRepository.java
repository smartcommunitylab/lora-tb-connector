package it.smartcommunitylab.loratb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.loratb.model.Customer;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {

}

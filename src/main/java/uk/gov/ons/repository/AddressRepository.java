package uk.gov.ons.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.gov.ons.entities.Address;

@Repository
public interface AddressRepository extends ReactiveCrudRepository<Address, String> {
	
	Flux<Address> findByTokensAddressAllContaining(String search);
	
	Mono<Address> findById(String id);
}

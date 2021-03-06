package uk.gov.ons.repository.skinny;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.gov.ons.entities.HybridAddressSkinny;

@Repository
public interface HybridAddressSkinnyRepository extends ReactiveCrudRepository<HybridAddressSkinny, String> {
	
	Flux<HybridAddressSkinny> findByLpiNagAllContaining(String search);
	
	Mono<HybridAddressSkinny> findById(String id);
}

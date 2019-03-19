package learn.springframework.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import learn.springframework.domain.UnitOfMeasure;

public interface UnitOfMeasureRepository extends CrudRepository<UnitOfMeasure, Long> {
	Optional<UnitOfMeasure> findByDescription(String description);
}

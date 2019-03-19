package learn.springframework.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import learn.springframework.domain.Category;

public interface CategoryRepository extends CrudRepository<Category, Long> {
	Optional<Category> findByDescription(String description);
}

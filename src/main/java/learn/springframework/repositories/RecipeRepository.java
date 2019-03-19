package learn.springframework.repositories;

import org.springframework.data.repository.CrudRepository;

import learn.springframework.domain.Recipe;

public interface RecipeRepository extends CrudRepository<Recipe, Long> {

}

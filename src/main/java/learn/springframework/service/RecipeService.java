package learn.springframework.service;

import java.util.Set;

import learn.springframework.commands.RecipeCommand;
import learn.springframework.domain.Recipe;

public interface RecipeService {
	Set<Recipe> getRecipes();

	Recipe findById(Long id);
	
	RecipeCommand saveRecipeCommand(RecipeCommand command);

	RecipeCommand findCommandById(Long id);

	void deleteById(Long id);
}

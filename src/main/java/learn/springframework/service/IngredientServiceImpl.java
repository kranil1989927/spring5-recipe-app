package learn.springframework.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import learn.springframework.commands.IngredientCommand;
import learn.springframework.converters.IngredientCommandToIngredient;
import learn.springframework.converters.IngredientToIngredientCommand;
import learn.springframework.domain.Ingredient;
import learn.springframework.domain.Recipe;
import learn.springframework.repositories.RecipeRepository;
import learn.springframework.repositories.UnitOfMeasureRepository;

@Service
public class IngredientServiceImpl implements IngredientService {
	
	private final IngredientToIngredientCommand ingredientToIngredientCommand;
	private final IngredientCommandToIngredient ingredientCommandToIngredient;
	private final RecipeRepository recipeRepository;
	private final UnitOfMeasureRepository unitOfMeasureRepository;

	public IngredientServiceImpl(IngredientToIngredientCommand ingredientToIngredientCommand, IngredientCommandToIngredient ingredientCommandToIngredient,
			RecipeRepository recipeRepository, UnitOfMeasureRepository unitOfMeasureRepository) {
		this.ingredientToIngredientCommand = ingredientToIngredientCommand;
		this.ingredientCommandToIngredient = ingredientCommandToIngredient;
		this.recipeRepository = recipeRepository;
		this.unitOfMeasureRepository = unitOfMeasureRepository;
	}

	@Override
	public IngredientCommand findByRecipeIdAndIngredientId(Long recipeId, Long ingredientId) {
		Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);

		if (!recipeOptional.isPresent()) {
			// TODO : Implement Error Handling
		}

		Recipe recipe = recipeOptional.get();

		Optional<IngredientCommand> ingredientCommandOptional = recipe.getIngredients().stream().filter(ingredient -> ingredient.getId().equals(ingredientId))
				.map(ingredient -> ingredientToIngredientCommand.convert(ingredient)).findFirst();

		if (!ingredientCommandOptional.isPresent()) {
			// TODO : Implement Error Handling
		}
		return ingredientCommandOptional.get();
	}

	@Override
	@Transactional
	public IngredientCommand saveIngredientCommand(IngredientCommand command) {

		Optional<Recipe> recipeOptional = recipeRepository.findById(command.getRecipeId());

		if (!recipeOptional.isPresent()) {
			return new IngredientCommand();
		} else {
			Recipe recipe = recipeOptional.get();
			
			Optional<Ingredient> ingredientOptional = recipe
					.getIngredients()
					.stream()
					.filter(ingredient -> ingredient.getId().equals(command.getId()))
					.findFirst();
			
			if(ingredientOptional.isPresent()){
				Ingredient ingredientFound = ingredientOptional.get();
				ingredientFound.setDescription(command.getDescription());
				ingredientFound.setAmount(command.getAmount());
				ingredientFound.setUom(unitOfMeasureRepository
						.findById(command.getUom().getId())
						.orElseThrow(() -> new RuntimeException("UOM not found")));
			} else {
				Ingredient ingredient = ingredientCommandToIngredient.convert(command);
				recipe.addIngredient(ingredient);
			}
			
			Recipe savedRecipe = recipeRepository.save(recipe);
			
			Optional<Ingredient> savedIngredientOptional = savedRecipe.getIngredients().stream()
				.filter(recipeIngredient -> recipeIngredient.getId().equals(command.getId()))
				.findFirst();
			
			if(!savedIngredientOptional.isPresent()){
				savedIngredientOptional = savedRecipe.getIngredients().stream()
						.filter(recipeIngredients -> recipeIngredients.getDescription().equals(command.getDescription()))
						.filter(recipeIngredients -> recipeIngredients.getAmount().equals(command.getAmount()))
						.filter(recipeIngredients -> recipeIngredients.getUom().getId().equals(command.getUom().getId()))
						.findFirst();
			}
			
			return ingredientToIngredientCommand.convert(savedIngredientOptional.get());
		}
	}

	@Override
	public void deleteById(Long recipeId, Long idToDelete) {
		Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
		
		if(recipeOptional.isPresent()){
			
			Recipe recipe = recipeOptional.get();
			
			Optional<Ingredient> ingredientOptional = recipe.getIngredients().stream()
					.filter(ingredient -> ingredient.getId().equals(idToDelete))
					.findFirst();
			
			if(ingredientOptional.isPresent()){
				Ingredient ingredientDelete = ingredientOptional.get();
				ingredientDelete.setRecipe(null);
				
				recipe.getIngredients().remove(ingredientOptional.get());
				recipeRepository.save(recipe);
			}
			
		} else {
			// "Recipe Id Not found. Id:" + recipeId
		}
	}
}

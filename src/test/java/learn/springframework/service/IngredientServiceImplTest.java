package learn.springframework.service;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import learn.springframework.commands.IngredientCommand;
import learn.springframework.converters.IngredientCommandToIngredient;
import learn.springframework.converters.IngredientToIngredientCommand;
import learn.springframework.converters.UnitOfMeasureCommandToUnitOfMeasure;
import learn.springframework.converters.UnitOfMeasureToUnitOfMeasureCommand;
import learn.springframework.domain.Ingredient;
import learn.springframework.domain.Recipe;
import learn.springframework.repositories.RecipeRepository;
import learn.springframework.repositories.UnitOfMeasureRepository;

public class IngredientServiceImplTest {

	private final IngredientToIngredientCommand ingredientToIngredientCommand;
	private final IngredientCommandToIngredient ingredientCommandToIngredient;

	@Mock
	RecipeRepository recipeRepository;
	
	@Mock
    UnitOfMeasureRepository unitOfMeasureRepository;

	IngredientService ingredientService;

	// init converters
	public IngredientServiceImplTest() {
		this.ingredientToIngredientCommand = new IngredientToIngredientCommand(new UnitOfMeasureToUnitOfMeasureCommand());
		this.ingredientCommandToIngredient = new IngredientCommandToIngredient(new UnitOfMeasureCommandToUnitOfMeasure());
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		ingredientService = new IngredientServiceImpl(ingredientToIngredientCommand, ingredientCommandToIngredient, recipeRepository, unitOfMeasureRepository);
	}

	@Test
	public void findByRecipeIdAndId() throws Exception {
	}

	@Test
	public void findByRecipeIdAndReceipeIdHappyPath() throws Exception {
		// given
		Recipe recipe = new Recipe();
		recipe.setId(1L);

		Ingredient ingredient1 = new Ingredient();
		ingredient1.setId(1L);

		Ingredient ingredient2 = new Ingredient();
		ingredient2.setId(1L);

		Ingredient ingredient3 = new Ingredient();
		ingredient3.setId(3L);

		recipe.addIngredient(ingredient1);
		recipe.addIngredient(ingredient2);
		recipe.addIngredient(ingredient3);
		Optional<Recipe> recipeOptional = Optional.of(recipe);

		when(recipeRepository.findById(anyLong())).thenReturn(recipeOptional);

		// then
		IngredientCommand ingredientCommand = ingredientService.findByRecipeIdAndIngredientId(1L, 3L);

		// when
		assertEquals(Long.valueOf(3L), ingredientCommand.getId());
		assertEquals(Long.valueOf(1L), ingredientCommand.getRecipeId());
		verify(recipeRepository, times(1)).findById(anyLong());
	}
	
	@Test
	public void testSaveRecipeCommand() throws Exception {
		IngredientCommand command = new IngredientCommand();
		command.setId(3L);
		command.setRecipeId(2L);

		Optional<Recipe> recipeOptional = Optional.of(new Recipe());

		Recipe savedRecipe = new Recipe();
		savedRecipe.addIngredient(new Ingredient());
		savedRecipe.getIngredients().iterator().next().setId(3L);

		when(recipeRepository.findById(anyLong())).thenReturn(recipeOptional);
		when(recipeRepository.save(any())).thenReturn(savedRecipe);

		IngredientCommand saveIngredientCommand = ingredientService.saveIngredientCommand(command);

		assertEquals(Long.valueOf(3L), saveIngredientCommand.getId());
		verify(recipeRepository, times(1)).findById(anyLong());
		verify(recipeRepository, times(1)).save(any(Recipe.class));
	}
	
	@Test
	public void testDeleteById() throws Exception {
		
		Recipe recipe = new Recipe();
		recipe.setId(1L);
		
		Ingredient ingredient = new Ingredient();
		ingredient.setId(3L);
		recipe.addIngredient(ingredient);
		ingredient.setRecipe(recipe);
		
		Optional<Recipe> recipeOptional = Optional.of(recipe );
		when(recipeRepository.findById(anyLong())).thenReturn(recipeOptional);
		ingredientService.deleteById(1L, 3L);
		
		verify(recipeRepository, times(1)).findById(anyLong());
		verify(recipeRepository, times(1)).save(any(Recipe.class));
	}
}

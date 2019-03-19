package learn.springframework.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import learn.springframework.commands.RecipeCommand;
import learn.springframework.converters.RecipeCommandToRecipe;
import learn.springframework.converters.RecipeToRecipeCommand;
import learn.springframework.domain.Recipe;
import learn.springframework.exceptions.NotFoundException;
import learn.springframework.repositories.RecipeRepository;

public class RecipeServiceImplTest {

	RecipeServiceImpl recipeService;

	@Mock
	RecipeRepository recipeRepository;

	@Mock
	RecipeToRecipeCommand recipeToRecipeCommand;

	@Mock
	RecipeCommandToRecipe recipeCommandToRecipe;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		recipeService = new RecipeServiceImpl(recipeRepository, recipeCommandToRecipe, recipeToRecipeCommand);
	}

	@Test
	public void getRecipesTest() throws Exception {
		Recipe recipe = new Recipe();
		Set<Recipe> recipeData = new HashSet<>();
		recipeData.add(recipe);

		when(recipeService.getRecipes()).thenReturn(recipeData);

		Set<Recipe> recipes = recipeService.getRecipes();
		assertEquals(recipes.size(), recipeData.size());
		verify(recipeRepository, times(1)).findAll();
		verify(recipeRepository, never()).findById(anyLong());
	}

	@Test
	public void getRecipeByIdTest() throws Exception {
		Recipe recipe = new Recipe();
		recipe.setId(1L);

		Optional<Recipe> recipeOptional = Optional.of(recipe);
		when(recipeRepository.findById(anyLong())).thenReturn(recipeOptional);

		Recipe recipeReturned = recipeService.findById(1L);

		assertNotNull("Null recipe returned", recipeReturned);
		verify(recipeRepository, times(1)).findById(anyLong());
		verify(recipeRepository, never()).findAll();
	}
	
	@Test(expected = NotFoundException.class)
	public void getRecipeByIdTestNotFound() throws Exception {
		Optional<Recipe> recipeOptional = Optional.empty();

		when(recipeRepository.findById(anyLong())).thenReturn(recipeOptional);

		Recipe recipeReturned = recipeService.findById(1L);
	}
	
	@Test
	public void getRecipeCommandByIdTest() throws Exception {
		Recipe recipe = new Recipe();
		recipe.setId(1L);
		Optional<Recipe> recipeOptional = Optional.of(recipe);

		when(recipeRepository.findById(anyLong())).thenReturn(recipeOptional);

		RecipeCommand recipeCommand = new RecipeCommand();
		recipeCommand.setId(1L);

		when(recipeToRecipeCommand.convert(any())).thenReturn(recipeCommand);

		RecipeCommand commandById = recipeService.findCommandById(1L);

		assertNotNull("Null recipe returned", commandById);
		verify(recipeRepository, times(1)).findById(anyLong());
		verify(recipeRepository, never()).findAll();
	}

	@Test
	public void testDeleteById() throws Exception {
		Long idToDelete = Long.valueOf(2L);
		recipeService.deleteById(idToDelete);
		verify(recipeRepository, times(1)).deleteById(anyLong());
	}
}

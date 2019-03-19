package learn.springframework.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import learn.springframework.commands.RecipeCommand;
import learn.springframework.domain.Recipe;
import learn.springframework.exceptions.NotFoundException;
import learn.springframework.service.RecipeService;

public class RecipeControllerTest {

	@Mock
	RecipeService recipeService;

	RecipeController recipeController;
	
	MockMvc mockMvc;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		recipeController = new RecipeController(recipeService);
		mockMvc = MockMvcBuilders.standaloneSetup(recipeController)
				.setControllerAdvice(new ControllerExceptionHandler())
				.build();
	}

	@Test
	public void testGetRecipe() throws Exception {
		Recipe recipe = new Recipe();
		recipe.setId(1L);

		when(recipeService.findById(anyLong())).thenReturn(recipe);
		
		 mockMvc.perform(get("/recipe/1/show"))
         .andExpect(status().isOk())
         .andExpect(view().name("recipe/show"))
         .andExpect(model().attributeExists("recipe"));
	}
	
	@Test
	public void testGetNewRecipeForm() throws Exception{
		RecipeCommand recipeCommand = new RecipeCommand();
		recipeCommand.setId(2L);
		
		when(recipeService.saveRecipeCommand(any(RecipeCommand.class))).thenReturn(recipeCommand);
		
		mockMvc.perform(get("/recipe/new"))
			.andExpect(status().isOk())
			.andExpect(view().name("recipe/recipeform"))
			.andExpect(model().attributeExists("recipe"));;
	}
	
	@Test
    public void testPostNewRecipeForm() throws Exception {
        RecipeCommand command = new RecipeCommand();
        command.setId(2L);

        when(recipeService.saveRecipeCommand(any())).thenReturn(command);

        mockMvc.perform(post("/recipe")
	                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	                .param("id", "")
	                .param("description", "some string")
	                .param("directions", "some directions")
        		)
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/recipe/2/show"));
    }

    @Test
    public void testPostNewRecipeFormValidationFail() throws Exception {
        RecipeCommand command = new RecipeCommand();
        command.setId(2L);

        when(recipeService.saveRecipeCommand(any())).thenReturn(command);

        mockMvc.perform(post("/recipe")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "")
                .param("cookTime", "3000")

        )
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("recipe"))
                .andExpect(view().name("recipe/recipeform"));
    }
    
	@Test
    public void testGetUpdateView() throws Exception {
        RecipeCommand command = new RecipeCommand();
        command.setId(2L);

        when(recipeService.findCommandById(anyLong())).thenReturn(command);

        mockMvc.perform(get("/recipe/1/update"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/recipeform"))
                .andExpect(model().attributeExists("recipe"));
    }
	
	@Test
	public void testDeleteAction() throws Exception{
		mockMvc.perform(get("/recipe/1/delete"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/"));
			
		verify(recipeService, times(1)).deleteById(anyLong());
	}
	
	@Test
	public void testGetRecipeNotFound() throws Exception {
		when(recipeService.findById(anyLong())).thenThrow(NotFoundException.class);

		mockMvc.perform(get("/recipe/1/show"))
			.andExpect(status().isNotFound())
			.andExpect(view().name("404error"));
	}
	
	@Test
	public void testGetRecipeNumberFormat() throws Exception {
		mockMvc.perform(get("/recipe/myFavRecipe/show"))
			.andExpect(status().isBadRequest())
			.andExpect(view().name("400error"));
	}
}

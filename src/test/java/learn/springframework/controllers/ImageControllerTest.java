package learn.springframework.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import learn.springframework.commands.RecipeCommand;
import learn.springframework.service.ImageService;
import learn.springframework.service.RecipeService;

public class ImageControllerTest {

	@Mock
	ImageService imageService;

	@Mock
	RecipeService recipeService;

	MockMvc mockMvc;

	ImageController imageController;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		imageController = new ImageController(recipeService, imageService);
		mockMvc = MockMvcBuilders.standaloneSetup(imageController)
				.setControllerAdvice(new ControllerExceptionHandler())
				.build();
	}

	@Test
	public void getImageForm() throws Exception {
		RecipeCommand recipeCommand = new RecipeCommand();
		recipeCommand.setId(1L);
		
		when(recipeService.findCommandById(anyLong())).thenReturn(recipeCommand);
		
		mockMvc.perform(get("/recipe/1/image"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("recipe"));
		
		verify(recipeService, times(1)).findCommandById(anyLong());
	}

	@Test
	public void handleImagePost() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("imagefile", "testing.txt", "text/plain", "Image_Upload".getBytes());
		
		mockMvc.perform(multipart("/recipe/1/image").file(multipartFile))
			.andExpect(status().is3xxRedirection())
			.andExpect(header().string("Location", "/recipe/1/show"));
		
		verify(imageService, times(1)).saveImageFile(anyLong(), any());
	}
	
	@Test
	public void renderImageFromDB() throws Exception {

		RecipeCommand command = new RecipeCommand();
		command.setId(1L);

		String s = "fake image text";
		Byte[] byteBoxed = new Byte[s.getBytes().length];

		int i = 0;
		for (byte primByte : s.getBytes()) {
			byteBoxed[i++] = primByte;
		}

		command.setImage(byteBoxed);
		when(recipeService.findCommandById(anyLong())).thenReturn(command);
		
		MockHttpServletResponse response = mockMvc.perform(get("/recipe/1/recipeimage"))
				.andExpect(status().isOk())
				.andReturn().getResponse();
		
		byte[] responseBytes = response.getContentAsByteArray();
		assertEquals(s.getBytes().length, responseBytes.length);
	}

	@Test
	public void getGetImageNumberFormatException() throws Exception {
		mockMvc.perform(get("/recipe/myFavRecipe/recipeimage"))
		.andExpect(status().isBadRequest())
		.andExpect(view().name("400error"));
	}
}

package learn.springframework.controllers;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import learn.springframework.commands.RecipeCommand;
import learn.springframework.exceptions.NotFoundException;
import learn.springframework.service.RecipeService;

@Controller
public class RecipeController {

	private final RecipeService recipeService;

	public RecipeController(RecipeService recipeService) {
		this.recipeService = recipeService;
	}

	@GetMapping("recipe/{id}/show")
	public String showById(@PathVariable String id, Model model) {
		model.addAttribute("recipe", recipeService.findById(Long.valueOf(id)));
		return "recipe/show";
	}

	@GetMapping("recipe/new")
	public String newRecipe(Model model) {
		model.addAttribute("recipe", new RecipeCommand());
		return "recipe/recipeform";
	}

	@GetMapping("recipe/{id}/update")
	public String updateRecipe(@PathVariable String id, Model model){
		model.addAttribute("recipe", recipeService.findCommandById(Long.valueOf(id)));
		return  "recipe/recipeform";
	}
	
	@PostMapping("recipe")
	public String saveOrUpdate(@Valid @ModelAttribute("recipe") RecipeCommand command, BindingResult bindingResult) {
		
		if(bindingResult.hasErrors()){
			bindingResult.getAllErrors().forEach(objError -> {
				System.out.println(objError.toString());
			});
			return  "recipe/recipeform";
		}
		RecipeCommand savedCommand = recipeService.saveRecipeCommand(command);

		return "redirect:/recipe/" + savedCommand.getId()+"/show";
	}
	
	@GetMapping("recipe/{id}/delete")
	public String deleteById(@PathVariable String id){
		recipeService.deleteById(Long.valueOf(id));
		return  "redirect:/";
	}
	
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NotFoundException.class)
	public ModelAndView handleNotFound(Exception ex) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("404error");
		modelAndView.addObject("exception", ex);
		return modelAndView;
	}
}

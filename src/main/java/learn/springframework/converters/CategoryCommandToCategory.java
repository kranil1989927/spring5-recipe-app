package learn.springframework.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import learn.springframework.commands.CategoryCommand;
import learn.springframework.domain.Category;
@Component
public class CategoryCommandToCategory implements Converter<CategoryCommand, Category> {

	@Override
	public Category convert(CategoryCommand source) {
		if (source == null) {
			return null;
		}

		final Category category = new Category();
		category.setId(source.getId());
		category.setDescription(source.getDescription());
		return category;
	}

}

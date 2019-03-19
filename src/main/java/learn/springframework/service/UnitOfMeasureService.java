package learn.springframework.service;

import java.util.Set;

import learn.springframework.commands.UnitOfMeasureCommand;

public interface UnitOfMeasureService {
	Set<UnitOfMeasureCommand> listAllUoms();
}
